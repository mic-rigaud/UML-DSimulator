package tuml.interpreter.parsercombinator

import java.util.Collection
import java.util.Map
import java.util.Set
import org.eclipse.xtend.lib.annotations.Data

class GrammarLanguage extends ParserCombinator {
	static var Parser<Grammar> parser
	def static getParser() {
		if(parser == null) {
			val ident = terminal("([A-Za-z_]\\w*)")
			val term = terminal('"([^"]*)"').create(Symbol)
			var regex = terminal("/((?:[^/\\\\]|\\\\.)*)/").create(Terminal)
			val exp = new WrappingParser
			val primitiveExp = term.or(ident.create(RuleCall)).or(regex).or(isymb("(").then(exp).then(isymb(")")))
			val priority0 =
				(
					// TODO: elemsAsSet?
					primitiveExp.then(symb("*").or(symb("+").or(symb("?"))).then(symb("#").multSet(0, 1)).then(isymb(",").then(primitiveExp).multSet(0, 1))).create(Repetition)
				).or(
					primitiveExp.then(isymb("!")).create(Drop)
				).or(
					primitiveExp
				)
			val priority1 = priority0.then(priority0.create(Sequence).mult(0, -1))
			val priority2 = priority1.then(
					isymb("{").then(ident).then(isymb("(")).then(
						ident.multSet(0, -1, isymb(","))
					).then(isymb(")")).then(isymb("}")).create(Creation)
				).or(priority1)
			var priority3 = priority2.then(isymb("|").then(priority2).create(Alternative).mult(0, -1))
			exp.set(priority3)
			val productionRule = ident.then(isymb(":=")).then(exp).then(isymb(";")).create(ProductionRule)
			parser = productionRule.multSet(1, -1).create(Grammar) as Parser as Parser<Grammar>
		}
		parser
	}

	@Data
	abstract static class GrammarNode {}

	@Data
	static class Grammar extends GrammarNode {
		val Set<ProductionRule> productionRules

		def eval() {
			val rules = productionRules.toMap[it.name].mapValues[new ParserCombinator.WrappingParser<Object>].immutableCopy
			productionRules.forEach[productionRule |
				rules.get(productionRule.name).set(productionRule.body.eval(rules))
			]
			rules.get(productionRules.get(0).name)
		}
	}

	@Data
	static class ProductionRule extends GrammarNode {
		String name
		Expression body
	}

	@Data
	abstract static class Expression extends GrammarNode {
		def Parser<Object> eval(Map<String, ? extends Parser<Object>> rules)
	}

	@Data
	static class Symbol extends Expression {
		String value

		override Parser<Object> eval(Map<String, ? extends Parser<Object>> rules) {
			new ParserCombinator.SymbolParser(value, false)
		}
	}

	@Data
	static class Terminal extends Expression {
		String value

		override Parser<Object> eval(Map<String, ? extends Parser<Object>> rules) {
			new ParserCombinator.TerminalParser(value)
		}
	}

	@Data
	static class RuleCall extends Expression {
		String ruleName

		override Parser<Object> eval(Map<String, ? extends Parser<Object>> rules) {
			val ret = rules.get(ruleName)
			if(ret == null) {
				throw new RuntimeException('''error: rule «ruleName» not found''')
			}
			ret
		}
	}

	@Data
	static class Alternative extends Expression {
		Expression left
		Expression right

		override Parser<Object> eval(Map<String, ? extends Parser<Object>> rules) {
			new ParserCombinator.OrParser(left.eval(rules), right.eval(rules))
		}
	}

	@Data
	static class Sequence extends Expression {
		Expression left
		Expression right

		override Parser<Object> eval(Map<String, ? extends Parser<Object>> rules) {
			new ParserCombinator.ThenParser(left.eval(rules), right.eval(rules))
		}
	}

	@Data
	static class Repetition extends Expression {
		val Expression expression
		val String op
		val Collection<String> asSet
		val Collection<Expression> separator

		override Parser<Object> eval(Map<String, ? extends Parser<Object>> rules) {
			val mult = switch(op) {
				case "?": 0 -> 1
				case "*": 0 -> -1
				case "+": 1 -> -1
				default: throw new RuntimeException('''error: unsupported repetition operator: «op»''')
			}
			val sep = if(separator.empty) {null} else {separator.findFirst[true].eval(rules)}
			new ParserCombinator.MultParser(expression.eval(rules), mult.key, mult.value, !asSet.empty, sep)
		}
	}

	@Data
	static class Drop extends Expression {
		val Expression expression

		override Parser<Object> eval(Map<String, ? extends Parser<Object>> rules) {
			new ParserCombinator.DroppingParser(expression.eval(rules))
		}
	}

	@Data
	static class Creation extends Expression {
		val Expression expression
		val String nodeType
		val Set<String> argumentNames

		override Parser<Object> eval(Map<String, ? extends Parser<Object>> rules) {
			// TODO: handling creation
			new ParserCombinator.NodeCreatingParser(expression.eval(rules), nodeType, argumentNames)
		}
	}
}
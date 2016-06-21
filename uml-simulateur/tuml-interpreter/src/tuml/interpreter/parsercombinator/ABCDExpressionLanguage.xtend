package tuml.interpreter.parsercombinator

import java.util.function.Function
import org.eclipse.xtend.lib.annotations.Data

class ABCDExpressionLanguage extends ParserCombinator {
	abstract static class Exp {
		def Object eval(Function<String, Object> env)
	}

	static class True extends Exp {
		override toString() {"true"}

		override eval(Function<String, Object> env) {
			true
		}
	}
	static class False extends Exp {
		override toString() {"false"}

		override eval(Function<String, Object> env) {
			false
		}
	}
	@Data
	static class Var extends Exp {
		val String name override toString() {name}

		override eval(Function<String, Object> env) {
			env.apply(name)
		}
	}
	@Data
	static class Int extends Exp {
		val int value override toString() {"" + value}
		new(String value) {
			this.value = Integer.parseInt(value)
		}

		override eval(Function<String, Object> env) {
			value
		}
	}
	@Data
	static class UnaryExp extends Exp {
		val String op val Exp operand

		override eval(Function<String, Object> env) {
			switch(op) {
				case "not": {
					!operand.eval(env) as Boolean
				}
				case "-": {
					-operand.eval(env) as Integer
				}
				case "+": {
					operand.eval(env) as Integer
				}
				default: {
					throw new RuntimeException('''Unsupported unary operator: «op»''')
				}
			}
		}
	}
	@Data
	static class BinaryExp extends Exp {
		val Exp left val String op val Exp right
		
		override eval(Function<String, Object> env) {
			switch(op) {
				case "or": {
					left.eval(env) as Boolean || right.eval(env) as Boolean
				}
				case "and": {
					left.eval(env) as Boolean && right.eval(env) as Boolean
				}
				// TODO: nor, nand, xor
				case "==": {
					left.eval(env) == right.eval(env)
				}
				case "!=": {
					left.eval(env) != right.eval(env)
				}
				// TODO: <, <=, >, >=
				case "-": {
					left.eval(env) as Integer - right.eval(env) as Integer
				}
				case "+": {
					left.eval(env) as Integer + right.eval(env) as Integer
				}
				case "*": {
					left.eval(env) as Integer * right.eval(env) as Integer
				}
				case "/": {
					left.eval(env) as Integer / right.eval(env) as Integer
				}
				case "%": {
					left.eval(env) as Integer % right.eval(env) as Integer
				}
				default: {
					throw new RuntimeException('''Unsupported binary operator: «op»''')
				}
			}
		}
	}

	static def kw(String s) {
//		symb(s)
		// to make sure "nota" is not understood as "not a"
		terminal('''(«s»)\b''')
	}

	static var Parser<Exp> expressionParser
	def static getParser() {
		if(expressionParser == null) {
			val ident = terminal("([A-Za-z_]\\w*)")
			val integer = terminal("([0-9]+)")
			val expression = new WrappingParser
			val primitiveExpression =	(
											ident.create(Var)
										).or(
											// should be ikw, but not necessary since we are after ident rule
											isymb("false").create(False)
										).or(
											// idem
											isymb("true").create(True)
										).or(
											integer.create(Int)
										).or(
											isymb("(").then(expression).then(isymb(")"))
										)
			val priority0 = (
								(
									kw("not")
								).or(
									symb("-")
								).or(
									symb("+")
								).then(primitiveExpression.create(UnaryExp)))
							.or(primitiveExpression)
			val priority1 = priority0.then(
								(
									(
										symb("*")
									).or(
										symb("/")
									).or(
										symb("%")
									)
								).then(priority0).create(BinaryExp)
								.mult(0, -1)
							)
			val priority2 = priority1.then(
								(
									(
										symb("+")
									).or(
										symb("-")
									)
								).then(priority1).create(BinaryExp)
								.mult(0, -1)
							)
			val priority3 = priority2.then(
								(
									(
										symb("<")
									).or(
										symb("<=")
									).or(
										symb(">")
									).or(
										symb(">=")
									)
								).then(priority2).create(BinaryExp)
								.mult(0, -1)
							)
			val priority4 = priority3.then(
								(
									(
										symb("==")
									).or(
										symb("!=")
									)
								).then(priority3).create(BinaryExp)
								.mult(0, -1)
							)
			val priority5 = priority4.then(
								(
									(
										kw("or")
									).or(
										kw("and")
									).or(
										kw("nor")
									).or(
										kw("nand")
									).or(
										kw("xor")
									)
								).then(priority4).create(BinaryExp)
								.mult(0, -1)
							)
			expression.set(priority5)

			expressionParser = expression as Parser as Parser<Exp>
		}
		return expressionParser
	}
}
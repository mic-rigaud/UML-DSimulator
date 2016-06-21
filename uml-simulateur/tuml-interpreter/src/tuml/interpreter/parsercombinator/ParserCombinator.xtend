package tuml.interpreter.parsercombinator

import java.lang.reflect.Constructor
import java.util.ArrayList
import java.util.Collection
import java.util.LinkedHashSet
import java.util.regex.Pattern
import org.eclipse.xtend.lib.annotations.Data
import tuml.interpreter.parsercombinator.GrammarLanguage.Grammar

class ParserCombinator {

	def static void main(String[] args) {
//		ParserCombinator.testSimpleGrammars
//		System.exit(1)
//		val parser = ABCDExpressionLanguage.parser as Parser
//		testParser(parser, "not (a or and b)")

		simpleTests
		testGrammars
		testABCDExpression
	}

	def static testGrammar(String grammar, String...programs) {
		val parser = GrammarLanguage.parser
		val parsed = testParser(parser, grammar) as Grammar

//		val p = parsed.productionRules.get(10).body.eval(Collections.emptyMap)
//		println(p.parseInternal("/([0-9]+)/", new Nil))
//		System.exit(1)
//		println(Pattern.compile("^\\/((?:[^\\/])*)\\/$").matcher("/([0-9]+)/"))

		val parsedParser = parsed.eval
		    //programs.forEach[program | testParser(parsedParser, program)]
	}

	def static testGrammars() {
		ParserCombinator.testSimpleGrammars
		testKM3Grammar
		testMetaGrammar
	}

	def static testSimpleGrammars() {
		testGrammar('a := "A";', "A")
		testGrammar('a := "A" | "B";', "A", "B")
		// TODO[wordBoundary]: add word boundary after symbols? but only for keywords... or always put ident first => cannot always work (see ABCD expression language)
		testGrammar('a := "A" "B";', "AB", "A B")
		testGrammar('a := "A" "B" | c; c := "C";', "AB", "C")

		// TODO[trailingSeparator]: last comma accepted
		// TODO[nonUniqueRepetitions]: all as are collapsed into one because we use a Set to represent *#
		testGrammar('a := "a"*#,(","!);', "a,a,a,a,")

		// DONE[enforceMultiplicities]: ? works as *
		try {
			testGrammar('a := "a"?#;', "aa")
		} catch(Exception e) {
			// we should get an error
			e.printStackTrace(System.out)
		}
		// DONE[enforceMultiplicities]: + works as *
		try {
			testGrammar('a := "a"+#;', "")
		} catch(Exception e) {
			// we should get an error
			e.printStackTrace(System.out)
		}
	}

	static val km3Grammar = '
		metamodel := package*# {Metamodel(packages)};
		package := "package"! ident "{"! classifier*# "}"! {Package(name,classifiers)};
		classifier := class | dataType;
		class := "class"! ident ("extends"! ident)?# "{"! structuralFeature*# "}"! {Class(name,supertype,structuralFeatures)};
		structuralFeature := attribute | reference;
		attribute := "attribute"! ident multiplicity ":"! ident ";"! {Attribute(name,multiplicity,typeName)};
		reference := "reference"! ident multiplicity "container"?# ":"! ident ("oppositeOf"! ident)?# ";"! {Reference(name,multiplicity,isContainer,typeName,opposite)};
		multiplicity := ("["! ("*"! {AnyNumber()} | int "-"! int {Bounded(lower, upper)}) "]"!)?# "ordered"?# "unique"?# {Multiplicity(number,isOrdered,isUnique)};
		dataType := "dataType"! ident ";"! {DataType(name)};
		int := /([0-9]+)/;
		ident := /([A-Za-z_]\\w*)/;
	'

	def static testKM3Grammar() {
		// DONE: ignored symbols, creations, multiSet
		// DONE: improved parsing error reporting? keeping longest match?
		// DONE: empty alternatives? => or simply use ?#
		testGrammar(km3Grammar,
			"
				package KM3 {	-- test
					class ModelElement {
						attribute name : String;
					}

					class Package extends ModelElement {
						reference classifiers[*] ordered container : Classifier;
					}

					class Classifier extends ModelElement {}

					class Class extends Classifier {
						reference supertype[0-1] : Class;
						reference structuralFeatures[*] ordered container : StructuralFeature oppositeOf owner;
					}
	
					class StructuralFeature extends ModelElement {
						reference owner : Class oppositeOf structuralFeatures;

						attribute lower : Integer;
						attribute upper : Integer;
						attribute isOrdered : Boolean;
					}

					class Attribute extends StructuralFeature {}

					class Reference extends StructuralFeature {
						attribute isContainer : Boolean;
						reference opposite : Reference;
					}

					class DataType extends Classifier {}
				}

				package PrimitiveTypes {
					dataType Boolean;
					dataType Integer;
					dataType String;
				}
			",
			// TODO[wordBoundary]: make sure identifiers starting with keywords are not recognized as such
			// e.g., by using similar construct to kw() in ABCDExpressionLanguage...
			// but in meta grammar language rather than API?
			"
				package A {	-- test
					class B {
						attribute name orderedunique : String;
					}
				}
			"
		)
	}

	def static testMetaGrammar() {
		// TODO[wordBoundary]: same problem as with KM3 grammar about keywords at beginning of identifiers
		val metaGrammar = '
			grammar := productionRule*;
			productionRule := ident ":="! expression ";"! {ProductionRule(name,body)};
			expression := priority3;
			priority3 := priority2 ("|"! priority2 {Alternative(left, right)})*;
			priority2 := priority1 ("{"! ident "("! ident*#,(","!) ")"! "}"! {Creation(wrapped,typeName,argNames)})?;
			priority1 := priority0 (priority0 {Sequence(left,right)})*;
			priority0 := primitiveExp ("*" | "+" | "?") "#"?# (","! primitiveExp)?# {Repetition(wrapped,op,asSet,separator)} | primitiveExp "!"! {Drop(wrapped)} | primitiveExp;
			primitiveExp := ident | terminal | regex | "("! expression ")"!;
			ident := /([A-Za-z_]\\w*)/;
			terminal := /"([^"]*)"/;
			regex := /\\/((?:[^\\/\\\\]|\\\\.)*)\\//;
		'	// TODO: add post-processing lambda to Terminals so that they can notably unescape?
		testGrammar(metaGrammar, "a := A;", km3Grammar, metaGrammar)
	}

	def static simpleTests() {
		try {
			testParser(symb("a"), "ab")
		} catch(Exception e) {
			// we should get an error
			e.printStackTrace(System.out)
		}
		testParser(symb("a").then(symb("b")), "ab")
		testParser(symb("a").or(symb("b")), "a")
		testParser(symb("a").or(symb("b")), "b")
		try {
			testParser(symb("a").or(symb("b")), "c")
		} catch(Exception e) {
			// we should get an error
			e.printStackTrace(System.out)
		}
		try {
			testParser(symb("a").or(symb("b")).mult(0, -1), "abc")
		} catch(Exception e) {
			// we should get an error
			e.printStackTrace(System.out)
		}
		testParser(symb("a").or(symb("b")).mult(0, -1).then(symb("c")), "abc")
	}

	def static testABCDExpression() {
		val parser = ABCDExpressionLanguage.parser as Parser<?>
		testParser(parser, "true")
		testParser(parser, "a")
		testParser(parser, "not a")
		testParser(parser, "true and false")
		testParser(parser, "true and a")
		testParser(parser, "a and a")
		testParser(parser, "a or b")
		testParser(parser, "not (a or b)")
		try {
			testParser(parser, "not (a or and b)")
		} catch(Exception e) {
			// we should get an error
			e.printStackTrace(System.out)
		}
		// DONE[wordBoundary]:
		//	- no word boundary after keywords?
		//		e.g., the following should be recognized as an identifier instead
		//		of as "true and a"
		//		=> solved this one by putting ident before true and false in
		//		primitive expressions
		testParser(parser, "trueanda")
		//		e.g., the following should be recognized as an identifier instead
		//		of as "not a"
		//		=> solved this one by using kw() instead of symb()
		testParser(parser, "nota")
		//		e.g., the following should fail
		//		=> same solution as above
		try {
			testParser(parser, "a andb")
		} catch(Exception e) {
			// we should get an error
			e.printStackTrace(System.out)
		}
		try {
			testParser(parser, "a orb")
		} catch(Exception e) {
			// we should get an error
			e.printStackTrace(System.out)
		}
	}

	def static testParser(Parser<?> parser, String program) {
		println('''Parsing: «program»''')
		val parsed = parser.parse(program)
		println('''	Result: «parsed»''')
		parsed
	}

	// non-ignored symbol
	def static Parser<Object> symb(String s) {
		new SymbolParser(s, false)
	}

	// ignored symbol
	def static Parser<Object> isymb(String s) {
		new SymbolParser(s, true)
	}

	def static Parser<Object> terminal(String regex) {
		new TerminalParser(regex)
	}

	// TODO:
	//	- improve over remembering longest failure
	//		- keywords can be read as identifiers therefore longest parse may be
	//			an otherwise incorrect one (if keywords cannot be identifiers)
	// DONE:
	//	- use this instead of Pair
	//	- remember longest parse to report error (instead of last error)
	@Data
	static class Result {
		val boolean success
		val ParserState state

		new(boolean success, List<Object> stack, String rest, ParserState longestFailure) {
			this(success, new ParserState(stack, rest, longestFailure))
		}

		new(boolean success, ParserState state) {
			this.success = success
			this.state = state
		}

		def getFailure() {
			!success
		}

		def getStack() {
			state.stack
		}

		def getRest() {
			state.rest
		}

		def getLongestFailure() {
			state.longestFailure
		}

		def stateWithStack(List<Object> st) {
			new ParserState(st, rest, longestFailure)
		}
	}

	@Data
	static class ParserState {
		val List<Object> stack
		val String rest
		val ParserState longestFailure

		def withRest(String rest) {
			new ParserState(stack, rest, longestFailure)
		}

		def withLongestFailureOf(ParserState other) {
			new ParserState(stack, rest, other.longestFailure)
		}

		// TODO: error message
		def fail() {
//			var rs = this
//			if(longestFailure == null) {
//				rs = new State(stack, rest, new Result(false, ))
//			} else {
//				
//			}
			val lf =
				if(longestFailure == null || rest.length < longestFailure.rest.length) {
					new ParserState(stack, rest, null)// use longestFailure instead of null to remember more failures
				} else {
					longestFailure
				}
			return new Result(false, new ParserState(stack, rest, lf))
//			}
//			return new Result(false, this)
		}
	}

	static abstract class Parser<T> {
		def Parser<T> or(Parser<T> o) {
			new OrParser(this, o)
		}

		def Parser<T> then(Parser<T> o) {
			new ThenParser(this, o)
		}

		def Parser<T> mult(int lower, int upper) {
			new MultParser(this, lower, upper, false, null)
		}

		def Parser<T> mult(int lower, int upper, Parser<T> separator) {
			new MultParser(this, lower, upper, false, separator)
		}

		def Parser<T> multSet(int lower, int upper) {
			new MultParser(this, lower, upper, true, null)
		}

		def Parser<T> multSet(int lower, int upper, Parser<T> separator) {
			new MultParser(this, lower, upper, true, separator)
		}

		// @return pair(ast, tail)
		def Result parseTrimmed(ParserState state)

		def Result parseInternal(ParserState state) {
			parseTrimmed(state.withRest(state.rest.ignore))
		}

		def ignore(String s) {
			//s.trim
			s.replaceFirst("^(\\s|--.*)*", "")
		}

		def T parse(String cs) {
			val parsed = parseInternal(new ParserState(nil, cs, null))
			if(parsed.failure) {
				throw new RuntimeException('''error: could not parse «parsed»''')
			}
			if(parsed.rest.ignore.length != 0) {
				throw new RuntimeException('''parsing incomplete: «parsed»''')
			}
			// DONE: check stack contains only 1 element!
			if(parsed.stack.length != 1) {
				println('''warning: more than one element left on stack: «parsed»''')
			}
			parsed.stack.head as T
		}

//		def named(String name) {
//			new NamingParser(name, this)
//		}

		// create Java object
		def create(Class<? extends T> c, Object...args) {
			new CreatingParser(c, this)
		}

		// create generic typed object
		def create(String typeName, String...argNames) {
			new NodeCreatingParser(this, typeName, argNames)
		}
	}

//	val static env = new HashMap<String, Object>

	@Data
	static class GenericNode {
		val String typeName
		val Collection<Pair<String, Object>> slots
	}

	@Data
	static class NodeCreatingParser<T> extends Parser<T> {
		val Parser<T> wrapped
		val String typeName
		val String[] argNames
		
		override parseTrimmed(ParserState state) {
			val ret = wrapped.parseInternal(state)
			if(ret.success) {
				var st = ret.stack
				var Collection<Pair<String, Object>> slots = new ArrayList
				for(argName : argNames.reverse) {
					slots.add(argName -> st.head)
					st = st.tail
				}
				var Object key = new GenericNode(typeName, slots)
				new Result(true, new Cons(key, st), ret.rest, ret.state.longestFailure)
			} else {
				ret
			}
		}
	}

	@Data
	static class CreatingParser<T> extends Parser<T> {
		val Class<? extends T> type
		val Parser<T> wrappedParser

		override parseTrimmed(ParserState state) {
			val ret = wrappedParser.parseInternal(state)
			if(ret.success) {
				val cons = type.constructors.get(0) as Constructor<T>
//				println(env)
				var st = ret.stack
				var args = newArrayOfSize(cons.parameterCount)
				for(var i = 0 ; i < args.length ; i++) {
					args.set(args.length - i - 1, st.head)
					st = st.tail
				}
				var Object key
				try {
					key = cons.newInstance(args)
				} catch(Exception e) {
					throw new RuntimeException('''could not create «cons.toString» with «args.toList»''', e)
				}
				new Result(true, new Cons(key, st), ret.rest, ret.state.longestFailure)
			} else {
				ret
			}
		}

		override toString() {
			'''(«wrappedParser»)[«type.simpleName»]'''
		}
	}

//	static class NamingParser extends Parser {
//		val String name
//		var Parser wrappedParser
//
//		new(String name, Parser wrappedParser) {
//			this.name = name
//			this.wrappedParser = wrappedParser
//		}
//
//		override parseTrimmed(String cs, List stack) {
//			val ret = wrappedParser.parseInternal(cs, stack)
//			if(ret != null) {
//				env.put(name, ret.key)
//				ret
//			} else {
//				null
//			}
//		}
//	}

	// notably used to support loops in grammar definition
	static class WrappingParser<T> extends Parser<T> {
		var Parser<T> wrappedParser

		override parseTrimmed(ParserState state) {
			wrappedParser.parseInternal(state)
		}

		def set(Parser<T> wrappedParser) {
			this.wrappedParser = wrappedParser
		}


		override toString() {
			"wrapping!"
		}
	}

	@Data
	static class OrParser<T> extends Parser<T> {
		val Parser<T> left
		val Parser<T> right
		
		override parseTrimmed(ParserState state) {
			var ret = left.parseInternal(state)
			if(ret.success) {
				return ret
			} else {
				ret = right.parseInternal(state.withLongestFailureOf(ret.state))
				return ret
			}
		}

		override toString() {
			'''(«left» | «right»)'''
		}
	}

	@Data
	static class ThenParser<T> extends Parser<T> {
		val Parser<T> left
		val Parser<T> right
		
		override parseTrimmed(ParserState state) {
			var retl = left.parseInternal(state)
			if(retl.success) {
				var retr = right.parseInternal(retl.state)
				retr
			} else {
				retl
			}
		}

		override toString() {
			'''(«left» «right»)'''
		}
	}

	@Data
	static class DroppingParser<T> extends Parser<T> {
		val Parser<T> wrapped
		
		override parseTrimmed(ParserState state) {
			val ret = wrapped.parseInternal(state)
			if(ret.success) {
				// forgetting what was pushed on stack by wrapped Parser
				new Result(true, state.stack, ret.rest, ret.state.longestFailure)
			} else {
				ret
			}
		}
		
	}

	@Data
	static class MultParser<T> extends Parser<T> {
		val Parser<T> left
		val int lower
		val int upper
		val boolean elemsAsSet
		val Parser<T> separator
		
		override parseTrimmed(ParserState state) {
			var next = state.rest
			// TODO[nonUniqueRepetitions]: replace LinkedHashSet by ArrayList... because sets are not ok here (we may have duplicates)
			var elems = new LinkedHashSet<Object>
			var st = if(elemsAsSet) {new Cons(elems, state.stack)} else {state.stack}
			var ret = new Result(true, st, next, state.longestFailure)

			// DONE[enforceMultiplicities]: enforce multiplicities
			var count = 0

			var goon = true
			do {
				var r = left.parseInternal(new ParserState(if(elemsAsSet) {nil} else {st}, next, ret.state.longestFailure))
				if(r.success) {
					count++
					if(elemsAsSet) {
						if(r.stack.length != 1) {
							throw new RuntimeException()
						}
						elems.add(r.stack.head)
					} else {
						st = r.stack
					}
					if(separator != null) {
						val tr = separator.parseInternal(r.stateWithStack(st))
						if(tr.success) {
							st = tr.stack
							r = tr
							// TODO[trailingSeparator]: make sure we have an additional element after a separator
						} else {
							goon = false
						}
					}
					next = r.rest
					ret = new Result(true, r.stateWithStack(st))
				} else {
					goon = false
				}
			} while(goon && (upper < 0 || count < upper))

			if(count < lower) {
				return state.fail
			}

			ret
		}

		override toString() {
			left +
			if(lower == 0) {
				if(upper == -1) {
					return "*"
				} else if(upper == 1) {
					return "?"
				}
			} else if(lower == 1) {
				if(upper == -1) {
					return "+"
				}
			}
			return '''{«lower», «upper»)'''
		}
	}

	@Data
	static class SymbolParser<T> extends Parser<T> {
		String symbol
		boolean ignored

		override parseTrimmed(ParserState state) {
			if(state.rest.startsWith(symbol)) {
				val st =
					if(!ignored) {
						new Cons(symbol, state.stack)
					} else {
						state.stack
					}
				new Result(true, st, state.rest.substring(symbol.length), state.longestFailure)
			} else {
				state.fail
			}
		}

		override toString() {
			'''"«symbol»"«if(ignored) {"!"} else {""}»'''
		}
	}

	static class TerminalParser<T> extends Parser<T> {
		val Pattern pattern

		new(String regex) {
			pattern = Pattern.compile('''^(?s)«regex»(.*)$''')
		}

		override parseTrimmed(ParserState state) {
			val matcher = pattern.matcher(state.rest)
			if(matcher.matches) {
				new Result(true, new Cons(matcher.group(1), state.stack), matcher.group(2), state.longestFailure)
			} else {
				state.fail
			}
		}

		override toString() {
			'''/«pattern.toString»/'''
		}
	}

//	static abstract class Node {
//	}
//
//	@Data
//	static class Terminal extends Node {
//		val String value
//	}


	static abstract class List<T> {
		def T getHead()
		def List<T> getTail()
		def int getLength()
		def List<T> push(T e) {
			new Cons(e, this)
		}
	}

	@Data
	static class Cons<T> extends List<T> {
		val T head
		val List<T> tail
		val int length

		new(T head) {
			this(head, nil)
		}

		new(T head, List<T> tail) {
			this.head = head
			this.tail = tail
			this.length = tail.length + 1
		}

		override toString() {
			val ret = new StringBuffer('[')
			var List<?> p = this
			var first = true
			while(p instanceof Cons<?>) {
				if(first) {
					first = false
				} else {
					ret.append(", ")
				}
				ret.append(p.head)
				p = p.tail
			}
			ret.append(']')
			ret.toString
		}

//		override hashCode() {
//			0	// DONE (disappeared), why removing this appeared to trigger stackoverflow on hashcode computation?
//				// actually it may still do, but we do not compute hashcode any more?
//		}
	}

	val static nil_ = new Nil as List<Object>
	def static <T> List<T> nil() {
		nil_ as List<?> as List<T>
	}

	static class Nil<T> extends List<T> {
		
		override getHead() {
			throw new UnsupportedOperationException()
		}
		
		override getTail() {
			throw new UnsupportedOperationException()
		}

		override toString() {
			"[]"
		}

		override getLength() {
			0
		}
	}

	@Data
	static class Node {
		val Object left
		val Object right
	}
}

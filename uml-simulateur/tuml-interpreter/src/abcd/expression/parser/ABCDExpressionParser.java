package abcd.expression.parser;

import static org.petitparser.parser.combinators.SettableParser.undefined;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.CharacterParser.pattern;
import static org.petitparser.parser.primitive.CharacterParser.whitespace;
import static org.petitparser.parser.primitive.StringParser.of;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.tools.CompositeParser;
import org.petitparser.tools.ExpressionBuilder;

public class ABCDExpressionParser extends CompositeParser {
	Map<String, Object> env;

	@Override
	protected void initialize() {
		other();
		abcd();
	}

	public <T> T eval(String input, Map<String, Object> env) {
		this.env = env;
		return super.parse(input).get();
	}

	void abcd() {
		SettableParser root = undefined();
		ExpressionBuilder builder = new ExpressionBuilder();
		builder.group()
				.primitive(of('(').trim().seq(root).seq(of(')').trim()).pick(1))
				.primitive(ref("numberLiteral").map((Function<String, Integer>) Integer::parseInt))
				.primitive(of("true").flatten().trim().map(Boolean::parseBoolean))
				.primitive(of("false").flatten().trim().map(Boolean::parseBoolean))
				.primitive(ref("identifier").flatten().trim().map(this::parseIdentifier));
		builder.group()
				.prefix(of('-').trim(), (List<Integer> values) -> -values.get(1))
				.prefix(of("not").trim(), (List<Boolean> values) -> !values.get(1));
		builder.group()
				.left(of('+').trim(), (List<Integer> values) -> values.get(0) + values.get(2))
				.left(of('-').trim(), (List<Integer> values) -> values.get(0) - values.get(2));
		builder.group()
				.left(of("<").trim(), (List<Integer> values) -> values.get(0) < values.get(2))
				.left(of("<=").trim(), (List<Integer> values) -> values.get(0) <= values.get(2))
				.left(of(">").trim(), (List<Integer> values) -> values.get(0) > values.get(2))
				.left(of(">=").trim(), (List<Integer> values) -> values.get(0) >= values.get(2));
		builder.group()
				.left(of("=").trim(), (List<Integer> values) -> values.get(0) == values.get(2))
				.left(of("<>").trim(), (List<Integer> values) -> values.get(0) != values.get(2));
		builder.group()
				.left(of("and").trim(), (List<Boolean> values) -> values.get(0) && values.get(2))
				.left(of("or").trim(), (List<Boolean> values) -> values.get(0) || values.get(2));
		root.set(builder.build());

		def("start", ref("expression"));
		def("expression", root.end());

		def("identifier", pattern("a-zA-Z_").seq(pattern("a-zA-Z0-9_").star()));
		def("numberLiteral", ref("numberToken").flatten().trim());
		def("numberToken", of('-').optional().seq(of('0').or(digit().plus())));
	}

	Object parseIdentifier(String ident) {
		return env.get(ident);
	}

	private void other() {
		// the original implementation uses a handwritten parser to
		// efficiently consume whitespace and comments
		def("whitespace", whitespace());

	}

	private Parser token(Object input) {
		Parser parser;
		if (input instanceof Parser) {
			parser = (Parser) input;
		} else if (input instanceof Character) {
			parser = of((Character) input);
		} else if (input instanceof String) {
			parser = of((String) input);
		} else {
			throw new IllegalStateException("Object not parsable: " + input);
		}
		return parser.token().trim(ref("whitespace"));
	}
}

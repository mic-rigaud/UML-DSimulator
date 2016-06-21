package tuml.interpreter.parsercombinator;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.MapExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;
import tuml.interpreter.parsercombinator.ParserCombinator;

@SuppressWarnings("all")
public class GrammarLanguage extends ParserCombinator {
  @Data
  public static abstract class GrammarNode {
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      GrammarLanguage.GrammarNode other = (GrammarLanguage.GrammarNode) obj;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      ToStringBuilder b = new ToStringBuilder(this);
      return b.toString();
    }
  }
  
  @Data
  public static class Grammar extends GrammarLanguage.GrammarNode {
    private final Set<GrammarLanguage.ProductionRule> productionRules;
    
    public ParserCombinator.WrappingParser<Object> eval() {
      ParserCombinator.WrappingParser<Object> _xblockexpression = null;
      {
        final Function1<GrammarLanguage.ProductionRule, String> _function = new Function1<GrammarLanguage.ProductionRule, String>() {
          @Override
          public String apply(final GrammarLanguage.ProductionRule it) {
            return it.name;
          }
        };
        Map<String, GrammarLanguage.ProductionRule> _map = IterableExtensions.<String, GrammarLanguage.ProductionRule>toMap(this.productionRules, _function);
        final Function1<GrammarLanguage.ProductionRule, ParserCombinator.WrappingParser<Object>> _function_1 = new Function1<GrammarLanguage.ProductionRule, ParserCombinator.WrappingParser<Object>>() {
          @Override
          public ParserCombinator.WrappingParser<Object> apply(final GrammarLanguage.ProductionRule it) {
            return new ParserCombinator.WrappingParser<Object>();
          }
        };
        Map<String, ParserCombinator.WrappingParser<Object>> _mapValues = MapExtensions.<String, GrammarLanguage.ProductionRule, ParserCombinator.WrappingParser<Object>>mapValues(_map, _function_1);
        final Map<String, ParserCombinator.WrappingParser<Object>> rules = ImmutableMap.<String, ParserCombinator.WrappingParser<Object>>copyOf(_mapValues);
        final Consumer<GrammarLanguage.ProductionRule> _function_2 = new Consumer<GrammarLanguage.ProductionRule>() {
          @Override
          public void accept(final GrammarLanguage.ProductionRule productionRule) {
            ParserCombinator.WrappingParser<Object> _get = rules.get(productionRule.name);
            ParserCombinator.Parser<Object> _eval = productionRule.body.eval(rules);
            _get.set(_eval);
          }
        };
        this.productionRules.forEach(_function_2);
        GrammarLanguage.ProductionRule _get = ((GrammarLanguage.ProductionRule[])Conversions.unwrapArray(this.productionRules, GrammarLanguage.ProductionRule.class))[0];
        _xblockexpression = rules.get(_get.name);
      }
      return _xblockexpression;
    }
    
    public Grammar(final Set<GrammarLanguage.ProductionRule> productionRules) {
      super();
      this.productionRules = productionRules;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((this.productionRules== null) ? 0 : this.productionRules.hashCode());
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      GrammarLanguage.Grammar other = (GrammarLanguage.Grammar) obj;
      if (this.productionRules == null) {
        if (other.productionRules != null)
          return false;
      } else if (!this.productionRules.equals(other.productionRules))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      String result = new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
      return result;
    }
    
    @Pure
    public Set<GrammarLanguage.ProductionRule> getProductionRules() {
      return this.productionRules;
    }
  }
  
  @Data
  public static class ProductionRule extends GrammarLanguage.GrammarNode {
    private final String name;
    
    private final GrammarLanguage.Expression body;
    
    public ProductionRule(final String name, final GrammarLanguage.Expression body) {
      super();
      this.name = name;
      this.body = body;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
      result = prime * result + ((this.body== null) ? 0 : this.body.hashCode());
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      GrammarLanguage.ProductionRule other = (GrammarLanguage.ProductionRule) obj;
      if (this.name == null) {
        if (other.name != null)
          return false;
      } else if (!this.name.equals(other.name))
        return false;
      if (this.body == null) {
        if (other.body != null)
          return false;
      } else if (!this.body.equals(other.body))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      String result = new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
      return result;
    }
    
    @Pure
    public String getName() {
      return this.name;
    }
    
    @Pure
    public GrammarLanguage.Expression getBody() {
      return this.body;
    }
  }
  
  @Data
  public static abstract class Expression extends GrammarLanguage.GrammarNode {
    public abstract ParserCombinator.Parser<Object> eval(final Map<String, ? extends ParserCombinator.Parser<Object>> rules);
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      GrammarLanguage.Expression other = (GrammarLanguage.Expression) obj;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      String result = new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
      return result;
    }
  }
  
  @Data
  public static class Symbol extends GrammarLanguage.Expression {
    private final String value;
    
    @Override
    public ParserCombinator.Parser<Object> eval(final Map<String, ? extends ParserCombinator.Parser<Object>> rules) {
      return new ParserCombinator.SymbolParser<Object>(this.value, false);
    }
    
    public Symbol(final String value) {
      super();
      this.value = value;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((this.value== null) ? 0 : this.value.hashCode());
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      GrammarLanguage.Symbol other = (GrammarLanguage.Symbol) obj;
      if (this.value == null) {
        if (other.value != null)
          return false;
      } else if (!this.value.equals(other.value))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      String result = new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
      return result;
    }
    
    @Pure
    public String getValue() {
      return this.value;
    }
  }
  
  @Data
  public static class Terminal extends GrammarLanguage.Expression {
    private final String value;
    
    @Override
    public ParserCombinator.Parser<Object> eval(final Map<String, ? extends ParserCombinator.Parser<Object>> rules) {
      return new ParserCombinator.TerminalParser<Object>(this.value);
    }
    
    public Terminal(final String value) {
      super();
      this.value = value;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((this.value== null) ? 0 : this.value.hashCode());
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      GrammarLanguage.Terminal other = (GrammarLanguage.Terminal) obj;
      if (this.value == null) {
        if (other.value != null)
          return false;
      } else if (!this.value.equals(other.value))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      String result = new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
      return result;
    }
    
    @Pure
    public String getValue() {
      return this.value;
    }
  }
  
  @Data
  public static class RuleCall extends GrammarLanguage.Expression {
    private final String ruleName;
    
    @Override
    public ParserCombinator.Parser<Object> eval(final Map<String, ? extends ParserCombinator.Parser<Object>> rules) {
      ParserCombinator.Parser<Object> _xblockexpression = null;
      {
        final ParserCombinator.Parser<Object> ret = rules.get(this.ruleName);
        boolean _equals = Objects.equal(ret, null);
        if (_equals) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("error: rule ");
          _builder.append(this.ruleName, "");
          _builder.append(" not found");
          throw new RuntimeException(_builder.toString());
        }
        _xblockexpression = ret;
      }
      return _xblockexpression;
    }
    
    public RuleCall(final String ruleName) {
      super();
      this.ruleName = ruleName;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((this.ruleName== null) ? 0 : this.ruleName.hashCode());
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      GrammarLanguage.RuleCall other = (GrammarLanguage.RuleCall) obj;
      if (this.ruleName == null) {
        if (other.ruleName != null)
          return false;
      } else if (!this.ruleName.equals(other.ruleName))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      String result = new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
      return result;
    }
    
    @Pure
    public String getRuleName() {
      return this.ruleName;
    }
  }
  
  @Data
  public static class Alternative extends GrammarLanguage.Expression {
    private final GrammarLanguage.Expression left;
    
    private final GrammarLanguage.Expression right;
    
    @Override
    public ParserCombinator.Parser<Object> eval(final Map<String, ? extends ParserCombinator.Parser<Object>> rules) {
      ParserCombinator.Parser<Object> _eval = this.left.eval(rules);
      ParserCombinator.Parser<Object> _eval_1 = this.right.eval(rules);
      return new ParserCombinator.OrParser<Object>(_eval, _eval_1);
    }
    
    public Alternative(final GrammarLanguage.Expression left, final GrammarLanguage.Expression right) {
      super();
      this.left = left;
      this.right = right;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((this.left== null) ? 0 : this.left.hashCode());
      result = prime * result + ((this.right== null) ? 0 : this.right.hashCode());
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      GrammarLanguage.Alternative other = (GrammarLanguage.Alternative) obj;
      if (this.left == null) {
        if (other.left != null)
          return false;
      } else if (!this.left.equals(other.left))
        return false;
      if (this.right == null) {
        if (other.right != null)
          return false;
      } else if (!this.right.equals(other.right))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      String result = new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
      return result;
    }
    
    @Pure
    public GrammarLanguage.Expression getLeft() {
      return this.left;
    }
    
    @Pure
    public GrammarLanguage.Expression getRight() {
      return this.right;
    }
  }
  
  @Data
  public static class Sequence extends GrammarLanguage.Expression {
    private final GrammarLanguage.Expression left;
    
    private final GrammarLanguage.Expression right;
    
    @Override
    public ParserCombinator.Parser<Object> eval(final Map<String, ? extends ParserCombinator.Parser<Object>> rules) {
      ParserCombinator.Parser<Object> _eval = this.left.eval(rules);
      ParserCombinator.Parser<Object> _eval_1 = this.right.eval(rules);
      return new ParserCombinator.ThenParser<Object>(_eval, _eval_1);
    }
    
    public Sequence(final GrammarLanguage.Expression left, final GrammarLanguage.Expression right) {
      super();
      this.left = left;
      this.right = right;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((this.left== null) ? 0 : this.left.hashCode());
      result = prime * result + ((this.right== null) ? 0 : this.right.hashCode());
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      GrammarLanguage.Sequence other = (GrammarLanguage.Sequence) obj;
      if (this.left == null) {
        if (other.left != null)
          return false;
      } else if (!this.left.equals(other.left))
        return false;
      if (this.right == null) {
        if (other.right != null)
          return false;
      } else if (!this.right.equals(other.right))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      String result = new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
      return result;
    }
    
    @Pure
    public GrammarLanguage.Expression getLeft() {
      return this.left;
    }
    
    @Pure
    public GrammarLanguage.Expression getRight() {
      return this.right;
    }
  }
  
  @Data
  public static class Repetition extends GrammarLanguage.Expression {
    private final GrammarLanguage.Expression expression;
    
    private final String op;
    
    private final Collection<String> asSet;
    
    private final Collection<GrammarLanguage.Expression> separator;
    
    @Override
    public ParserCombinator.Parser<Object> eval(final Map<String, ? extends ParserCombinator.Parser<Object>> rules) {
      ParserCombinator.MultParser<Object> _xblockexpression = null;
      {
        Pair<Integer, Integer> _switchResult = null;
        final String op = this.op;
        boolean _matched = false;
        if (!_matched) {
          if (Objects.equal(op, "?")) {
            _matched=true;
            _switchResult = Pair.<Integer, Integer>of(Integer.valueOf(0), Integer.valueOf(1));
          }
        }
        if (!_matched) {
          if (Objects.equal(op, "*")) {
            _matched=true;
            _switchResult = Pair.<Integer, Integer>of(Integer.valueOf(0), Integer.valueOf((-1)));
          }
        }
        if (!_matched) {
          if (Objects.equal(op, "+")) {
            _matched=true;
            _switchResult = Pair.<Integer, Integer>of(Integer.valueOf(1), Integer.valueOf((-1)));
          }
        }
        if (!_matched) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("error: unsupported repetition operator: ");
          _builder.append(this.op, "");
          throw new RuntimeException(_builder.toString());
        }
        final Pair<Integer, Integer> mult = _switchResult;
        ParserCombinator.Parser<Object> _xifexpression = null;
        boolean _isEmpty = this.separator.isEmpty();
        if (_isEmpty) {
          _xifexpression = null;
        } else {
          final Function1<GrammarLanguage.Expression, Boolean> _function = new Function1<GrammarLanguage.Expression, Boolean>() {
            @Override
            public Boolean apply(final GrammarLanguage.Expression it) {
              return Boolean.valueOf(true);
            }
          };
          GrammarLanguage.Expression _findFirst = IterableExtensions.<GrammarLanguage.Expression>findFirst(this.separator, _function);
          _xifexpression = _findFirst.eval(rules);
        }
        final ParserCombinator.Parser<Object> sep = _xifexpression;
        ParserCombinator.Parser<Object> _eval = this.expression.eval(rules);
        Integer _key = mult.getKey();
        Integer _value = mult.getValue();
        boolean _isEmpty_1 = this.asSet.isEmpty();
        boolean _not = (!_isEmpty_1);
        _xblockexpression = new ParserCombinator.MultParser<Object>(_eval, (_key).intValue(), (_value).intValue(), _not, sep);
      }
      return _xblockexpression;
    }
    
    public Repetition(final GrammarLanguage.Expression expression, final String op, final Collection<String> asSet, final Collection<GrammarLanguage.Expression> separator) {
      super();
      this.expression = expression;
      this.op = op;
      this.asSet = asSet;
      this.separator = separator;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((this.expression== null) ? 0 : this.expression.hashCode());
      result = prime * result + ((this.op== null) ? 0 : this.op.hashCode());
      result = prime * result + ((this.asSet== null) ? 0 : this.asSet.hashCode());
      result = prime * result + ((this.separator== null) ? 0 : this.separator.hashCode());
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      GrammarLanguage.Repetition other = (GrammarLanguage.Repetition) obj;
      if (this.expression == null) {
        if (other.expression != null)
          return false;
      } else if (!this.expression.equals(other.expression))
        return false;
      if (this.op == null) {
        if (other.op != null)
          return false;
      } else if (!this.op.equals(other.op))
        return false;
      if (this.asSet == null) {
        if (other.asSet != null)
          return false;
      } else if (!this.asSet.equals(other.asSet))
        return false;
      if (this.separator == null) {
        if (other.separator != null)
          return false;
      } else if (!this.separator.equals(other.separator))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      String result = new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
      return result;
    }
    
    @Pure
    public GrammarLanguage.Expression getExpression() {
      return this.expression;
    }
    
    @Pure
    public String getOp() {
      return this.op;
    }
    
    @Pure
    public Collection<String> getAsSet() {
      return this.asSet;
    }
    
    @Pure
    public Collection<GrammarLanguage.Expression> getSeparator() {
      return this.separator;
    }
  }
  
  @Data
  public static class Drop extends GrammarLanguage.Expression {
    private final GrammarLanguage.Expression expression;
    
    @Override
    public ParserCombinator.Parser<Object> eval(final Map<String, ? extends ParserCombinator.Parser<Object>> rules) {
      ParserCombinator.Parser<Object> _eval = this.expression.eval(rules);
      return new ParserCombinator.DroppingParser<Object>(_eval);
    }
    
    public Drop(final GrammarLanguage.Expression expression) {
      super();
      this.expression = expression;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((this.expression== null) ? 0 : this.expression.hashCode());
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      GrammarLanguage.Drop other = (GrammarLanguage.Drop) obj;
      if (this.expression == null) {
        if (other.expression != null)
          return false;
      } else if (!this.expression.equals(other.expression))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      String result = new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
      return result;
    }
    
    @Pure
    public GrammarLanguage.Expression getExpression() {
      return this.expression;
    }
  }
  
  @Data
  public static class Creation extends GrammarLanguage.Expression {
    private final GrammarLanguage.Expression expression;
    
    private final String nodeType;
    
    private final Set<String> argumentNames;
    
    @Override
    public ParserCombinator.Parser<Object> eval(final Map<String, ? extends ParserCombinator.Parser<Object>> rules) {
      ParserCombinator.Parser<Object> _eval = this.expression.eval(rules);
      return new ParserCombinator.NodeCreatingParser<Object>(_eval, this.nodeType, ((String[])Conversions.unwrapArray(this.argumentNames, String.class)));
    }
    
    public Creation(final GrammarLanguage.Expression expression, final String nodeType, final Set<String> argumentNames) {
      super();
      this.expression = expression;
      this.nodeType = nodeType;
      this.argumentNames = argumentNames;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((this.expression== null) ? 0 : this.expression.hashCode());
      result = prime * result + ((this.nodeType== null) ? 0 : this.nodeType.hashCode());
      result = prime * result + ((this.argumentNames== null) ? 0 : this.argumentNames.hashCode());
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      GrammarLanguage.Creation other = (GrammarLanguage.Creation) obj;
      if (this.expression == null) {
        if (other.expression != null)
          return false;
      } else if (!this.expression.equals(other.expression))
        return false;
      if (this.nodeType == null) {
        if (other.nodeType != null)
          return false;
      } else if (!this.nodeType.equals(other.nodeType))
        return false;
      if (this.argumentNames == null) {
        if (other.argumentNames != null)
          return false;
      } else if (!this.argumentNames.equals(other.argumentNames))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      String result = new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
      return result;
    }
    
    @Pure
    public GrammarLanguage.Expression getExpression() {
      return this.expression;
    }
    
    @Pure
    public String getNodeType() {
      return this.nodeType;
    }
    
    @Pure
    public Set<String> getArgumentNames() {
      return this.argumentNames;
    }
  }
  
  private static ParserCombinator.Parser<GrammarLanguage.Grammar> parser;
  
  public static ParserCombinator.Parser<GrammarLanguage.Grammar> getParser() {
    ParserCombinator.Parser<GrammarLanguage.Grammar> _xblockexpression = null;
    {
      boolean _equals = Objects.equal(GrammarLanguage.parser, null);
      if (_equals) {
        final ParserCombinator.Parser<Object> ident = ParserCombinator.terminal("([A-Za-z_]\\w*)");
        ParserCombinator.Parser<Object> _terminal = ParserCombinator.terminal("\"([^\"]*)\"");
        final ParserCombinator.CreatingParser<Object> term = _terminal.create(GrammarLanguage.Symbol.class);
        ParserCombinator.Parser<Object> _terminal_1 = ParserCombinator.terminal("/((?:[^/\\\\]|\\\\.)*)/");
        ParserCombinator.CreatingParser<Object> regex = _terminal_1.create(GrammarLanguage.Terminal.class);
        final ParserCombinator.WrappingParser<Object> exp = new ParserCombinator.WrappingParser<Object>();
        ParserCombinator.CreatingParser<Object> _create = ident.create(GrammarLanguage.RuleCall.class);
        ParserCombinator.Parser<Object> _or = term.or(_create);
        ParserCombinator.Parser<Object> _or_1 = _or.or(regex);
        ParserCombinator.Parser<Object> _isymb = ParserCombinator.isymb("(");
        ParserCombinator.Parser<Object> _then = _isymb.then(exp);
        ParserCombinator.Parser<Object> _isymb_1 = ParserCombinator.isymb(")");
        ParserCombinator.Parser<Object> _then_1 = _then.then(_isymb_1);
        final ParserCombinator.Parser<Object> primitiveExp = _or_1.or(_then_1);
        ParserCombinator.Parser<Object> _symb = ParserCombinator.symb("*");
        ParserCombinator.Parser<Object> _symb_1 = ParserCombinator.symb("+");
        ParserCombinator.Parser<Object> _symb_2 = ParserCombinator.symb("?");
        ParserCombinator.Parser<Object> _or_2 = _symb_1.or(_symb_2);
        ParserCombinator.Parser<Object> _or_3 = _symb.or(_or_2);
        ParserCombinator.Parser<Object> _symb_3 = ParserCombinator.symb("#");
        ParserCombinator.Parser<Object> _multSet = _symb_3.multSet(0, 1);
        ParserCombinator.Parser<Object> _then_2 = _or_3.then(_multSet);
        ParserCombinator.Parser<Object> _isymb_2 = ParserCombinator.isymb(",");
        ParserCombinator.Parser<Object> _then_3 = _isymb_2.then(primitiveExp);
        ParserCombinator.Parser<Object> _multSet_1 = _then_3.multSet(0, 1);
        ParserCombinator.Parser<Object> _then_4 = _then_2.then(_multSet_1);
        ParserCombinator.Parser<Object> _then_5 = primitiveExp.then(_then_4);
        ParserCombinator.CreatingParser<Object> _create_1 = _then_5.create(GrammarLanguage.Repetition.class);
        ParserCombinator.Parser<Object> _isymb_3 = ParserCombinator.isymb("!");
        ParserCombinator.Parser<Object> _then_6 = primitiveExp.then(_isymb_3);
        ParserCombinator.CreatingParser<Object> _create_2 = _then_6.create(GrammarLanguage.Drop.class);
        ParserCombinator.Parser<Object> _or_4 = _create_1.or(_create_2);
        final ParserCombinator.Parser<Object> priority0 = _or_4.or(primitiveExp);
        ParserCombinator.CreatingParser<Object> _create_3 = priority0.create(GrammarLanguage.Sequence.class);
        ParserCombinator.Parser<Object> _mult = _create_3.mult(0, (-1));
        final ParserCombinator.Parser<Object> priority1 = priority0.then(_mult);
        ParserCombinator.Parser<Object> _isymb_4 = ParserCombinator.isymb("{");
        ParserCombinator.Parser<Object> _then_7 = _isymb_4.then(ident);
        ParserCombinator.Parser<Object> _isymb_5 = ParserCombinator.isymb("(");
        ParserCombinator.Parser<Object> _then_8 = _then_7.then(_isymb_5);
        ParserCombinator.Parser<Object> _isymb_6 = ParserCombinator.isymb(",");
        ParserCombinator.Parser<Object> _multSet_2 = ident.multSet(0, (-1), _isymb_6);
        ParserCombinator.Parser<Object> _then_9 = _then_8.then(_multSet_2);
        ParserCombinator.Parser<Object> _isymb_7 = ParserCombinator.isymb(")");
        ParserCombinator.Parser<Object> _then_10 = _then_9.then(_isymb_7);
        ParserCombinator.Parser<Object> _isymb_8 = ParserCombinator.isymb("}");
        ParserCombinator.Parser<Object> _then_11 = _then_10.then(_isymb_8);
        ParserCombinator.CreatingParser<Object> _create_4 = _then_11.create(GrammarLanguage.Creation.class);
        ParserCombinator.Parser<Object> _then_12 = priority1.then(_create_4);
        final ParserCombinator.Parser<Object> priority2 = _then_12.or(priority1);
        ParserCombinator.Parser<Object> _isymb_9 = ParserCombinator.isymb("|");
        ParserCombinator.Parser<Object> _then_13 = _isymb_9.then(priority2);
        ParserCombinator.CreatingParser<Object> _create_5 = _then_13.create(GrammarLanguage.Alternative.class);
        ParserCombinator.Parser<Object> _mult_1 = _create_5.mult(0, (-1));
        ParserCombinator.Parser<Object> priority3 = priority2.then(_mult_1);
        exp.set(priority3);
        ParserCombinator.Parser<Object> _isymb_10 = ParserCombinator.isymb(":=");
        ParserCombinator.Parser<Object> _then_14 = ident.then(_isymb_10);
        ParserCombinator.Parser<Object> _then_15 = _then_14.then(exp);
        ParserCombinator.Parser<Object> _isymb_11 = ParserCombinator.isymb(";");
        ParserCombinator.Parser<Object> _then_16 = _then_15.then(_isymb_11);
        final ParserCombinator.CreatingParser<Object> productionRule = _then_16.create(GrammarLanguage.ProductionRule.class);
        ParserCombinator.Parser<Object> _multSet_3 = productionRule.multSet(1, (-1));
        ParserCombinator.CreatingParser<Object> _create_6 = _multSet_3.create(GrammarLanguage.Grammar.class);
        GrammarLanguage.parser = ((ParserCombinator.Parser<GrammarLanguage.Grammar>) ((ParserCombinator.Parser) _create_6));
      }
      _xblockexpression = GrammarLanguage.parser;
    }
    return _xblockexpression;
  }
}

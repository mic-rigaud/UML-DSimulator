package tuml.interpreter.parsercombinator;

import com.google.common.base.Objects;
import java.util.function.Function;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;
import tuml.interpreter.parsercombinator.ParserCombinator;

@SuppressWarnings("all")
public class ABCDExpressionLanguage extends ParserCombinator {
  public static abstract class Exp {
    public abstract Object eval(final Function<String, Object> env);
  }
  
  public static class True extends ABCDExpressionLanguage.Exp {
    @Override
    public String toString() {
      return "true";
    }
    
    @Override
    public Object eval(final Function<String, Object> env) {
      return Boolean.valueOf(true);
    }
  }
  
  public static class False extends ABCDExpressionLanguage.Exp {
    @Override
    public String toString() {
      return "false";
    }
    
    @Override
    public Object eval(final Function<String, Object> env) {
      return Boolean.valueOf(false);
    }
  }
  
  @Data
  public static class Var extends ABCDExpressionLanguage.Exp {
    private final String name;
    
    @Override
    public String toString() {
      return this.name;
    }
    
    @Override
    public Object eval(final Function<String, Object> env) {
      return env.apply(this.name);
    }
    
    public Var(final String name) {
      super();
      this.name = name;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
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
      ABCDExpressionLanguage.Var other = (ABCDExpressionLanguage.Var) obj;
      if (this.name == null) {
        if (other.name != null)
          return false;
      } else if (!this.name.equals(other.name))
        return false;
      return true;
    }
    
    @Pure
    public String getName() {
      return this.name;
    }
  }
  
  @Data
  public static class Int extends ABCDExpressionLanguage.Exp {
    private final int value;
    
    @Override
    public String toString() {
      return ("" + Integer.valueOf(this.value));
    }
    
    public Int(final String value) {
      int _parseInt = Integer.parseInt(value);
      this.value = _parseInt;
    }
    
    @Override
    public Object eval(final Function<String, Object> env) {
      return Integer.valueOf(this.value);
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + this.value;
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
      ABCDExpressionLanguage.Int other = (ABCDExpressionLanguage.Int) obj;
      if (other.value != this.value)
        return false;
      return true;
    }
    
    @Pure
    public int getValue() {
      return this.value;
    }
  }
  
  @Data
  public static class UnaryExp extends ABCDExpressionLanguage.Exp {
    private final String op;
    
    private final ABCDExpressionLanguage.Exp operand;
    
    @Override
    public Object eval(final Function<String, Object> env) {
      Object _switchResult = null;
      final String op = this.op;
      boolean _matched = false;
      if (!_matched) {
        if (Objects.equal(op, "not")) {
          _matched=true;
          Object _eval = this.operand.eval(env);
          _switchResult = Boolean.valueOf((!(((Boolean) _eval)).booleanValue()));
        }
      }
      if (!_matched) {
        if (Objects.equal(op, "-")) {
          _matched=true;
          Object _eval_1 = this.operand.eval(env);
          _switchResult = Integer.valueOf((-(((Integer) _eval_1)).intValue()));
        }
      }
      if (!_matched) {
        if (Objects.equal(op, "+")) {
          _matched=true;
          Object _eval_2 = this.operand.eval(env);
          _switchResult = ((Integer) _eval_2);
        }
      }
      if (!_matched) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("Unsupported unary operator: ");
        _builder.append(this.op, "");
        throw new RuntimeException(_builder.toString());
      }
      return _switchResult;
    }
    
    public UnaryExp(final String op, final ABCDExpressionLanguage.Exp operand) {
      super();
      this.op = op;
      this.operand = operand;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.op== null) ? 0 : this.op.hashCode());
      result = prime * result + ((this.operand== null) ? 0 : this.operand.hashCode());
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
      ABCDExpressionLanguage.UnaryExp other = (ABCDExpressionLanguage.UnaryExp) obj;
      if (this.op == null) {
        if (other.op != null)
          return false;
      } else if (!this.op.equals(other.op))
        return false;
      if (this.operand == null) {
        if (other.operand != null)
          return false;
      } else if (!this.operand.equals(other.operand))
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
    public String getOp() {
      return this.op;
    }
    
    @Pure
    public ABCDExpressionLanguage.Exp getOperand() {
      return this.operand;
    }
  }
  
  @Data
  public static class BinaryExp extends ABCDExpressionLanguage.Exp {
    private final ABCDExpressionLanguage.Exp left;
    
    private final String op;
    
    private final ABCDExpressionLanguage.Exp right;
    
    @Override
    public Object eval(final Function<String, Object> env) {
      Object _switchResult = null;
      final String op = this.op;
      boolean _matched = false;
      if (!_matched) {
        if (Objects.equal(op, "or")) {
          _matched=true;
          boolean _or = false;
          Object _eval = this.left.eval(env);
          if ((((Boolean) _eval)).booleanValue()) {
            _or = true;
          } else {
            Object _eval_1 = this.right.eval(env);
            _or = (((Boolean) _eval_1)).booleanValue();
          }
          _switchResult = Boolean.valueOf(_or);
        }
      }
      if (!_matched) {
        if (Objects.equal(op, "and")) {
          _matched=true;
          boolean _and = false;
          Object _eval_2 = this.left.eval(env);
          if (!(((Boolean) _eval_2)).booleanValue()) {
            _and = false;
          } else {
            Object _eval_3 = this.right.eval(env);
            _and = (((Boolean) _eval_3)).booleanValue();
          }
          _switchResult = Boolean.valueOf(_and);
        }
      }
      if (!_matched) {
        if (Objects.equal(op, "==")) {
          _matched=true;
          Object _eval_4 = this.left.eval(env);
          Object _eval_5 = this.right.eval(env);
          _switchResult = Boolean.valueOf(Objects.equal(_eval_4, _eval_5));
        }
      }
      if (!_matched) {
        if (Objects.equal(op, "!=")) {
          _matched=true;
          Object _eval_6 = this.left.eval(env);
          Object _eval_7 = this.right.eval(env);
          _switchResult = Boolean.valueOf((!Objects.equal(_eval_6, _eval_7)));
        }
      }
      if (!_matched) {
        if (Objects.equal(op, "-")) {
          _matched=true;
          Object _eval_8 = this.left.eval(env);
          Object _eval_9 = this.right.eval(env);
          _switchResult = Integer.valueOf(((((Integer) _eval_8)).intValue() - (((Integer) _eval_9)).intValue()));
        }
      }
      if (!_matched) {
        if (Objects.equal(op, "+")) {
          _matched=true;
          Object _eval_10 = this.left.eval(env);
          Object _eval_11 = this.right.eval(env);
          _switchResult = Integer.valueOf(((((Integer) _eval_10)).intValue() + (((Integer) _eval_11)).intValue()));
        }
      }
      if (!_matched) {
        if (Objects.equal(op, "*")) {
          _matched=true;
          Object _eval_12 = this.left.eval(env);
          Object _eval_13 = this.right.eval(env);
          _switchResult = Integer.valueOf(((((Integer) _eval_12)).intValue() * (((Integer) _eval_13)).intValue()));
        }
      }
      if (!_matched) {
        if (Objects.equal(op, "/")) {
          _matched=true;
          Object _eval_14 = this.left.eval(env);
          Object _eval_15 = this.right.eval(env);
          _switchResult = Integer.valueOf(((((Integer) _eval_14)).intValue() / (((Integer) _eval_15)).intValue()));
        }
      }
      if (!_matched) {
        if (Objects.equal(op, "%")) {
          _matched=true;
          Object _eval_16 = this.left.eval(env);
          Object _eval_17 = this.right.eval(env);
          _switchResult = Integer.valueOf(((((Integer) _eval_16)).intValue() % (((Integer) _eval_17)).intValue()));
        }
      }
      if (!_matched) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("Unsupported binary operator: ");
        _builder.append(this.op, "");
        throw new RuntimeException(_builder.toString());
      }
      return _switchResult;
    }
    
    public BinaryExp(final ABCDExpressionLanguage.Exp left, final String op, final ABCDExpressionLanguage.Exp right) {
      super();
      this.left = left;
      this.op = op;
      this.right = right;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.left== null) ? 0 : this.left.hashCode());
      result = prime * result + ((this.op== null) ? 0 : this.op.hashCode());
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
      ABCDExpressionLanguage.BinaryExp other = (ABCDExpressionLanguage.BinaryExp) obj;
      if (this.left == null) {
        if (other.left != null)
          return false;
      } else if (!this.left.equals(other.left))
        return false;
      if (this.op == null) {
        if (other.op != null)
          return false;
      } else if (!this.op.equals(other.op))
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
    public ABCDExpressionLanguage.Exp getLeft() {
      return this.left;
    }
    
    @Pure
    public String getOp() {
      return this.op;
    }
    
    @Pure
    public ABCDExpressionLanguage.Exp getRight() {
      return this.right;
    }
  }
  
  public static ParserCombinator.Parser<Object> kw(final String s) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(");
    _builder.append(s, "");
    _builder.append(")\\b");
    return ParserCombinator.terminal(_builder.toString());
  }
  
  private static ParserCombinator.Parser<ABCDExpressionLanguage.Exp> expressionParser;
  
  public static Object getParser() {
    boolean _equals = Objects.equal(ABCDExpressionLanguage.expressionParser, null);
    if (_equals) {
      final ParserCombinator.Parser<Object> ident = ParserCombinator.terminal("([A-Za-z_]\\w*)");
      final ParserCombinator.Parser<Object> integer = ParserCombinator.terminal("([0-9]+)");
      final ParserCombinator.WrappingParser<Object> expression = new ParserCombinator.WrappingParser<Object>();
      ParserCombinator.CreatingParser<Object> _create = ident.create(ABCDExpressionLanguage.Var.class);
      ParserCombinator.Parser<Object> _isymb = ParserCombinator.isymb("false");
      ParserCombinator.CreatingParser<Object> _create_1 = _isymb.create(ABCDExpressionLanguage.False.class);
      ParserCombinator.Parser<Object> _or = _create.or(_create_1);
      ParserCombinator.Parser<Object> _isymb_1 = ParserCombinator.isymb("true");
      ParserCombinator.CreatingParser<Object> _create_2 = _isymb_1.create(ABCDExpressionLanguage.True.class);
      ParserCombinator.Parser<Object> _or_1 = _or.or(_create_2);
      ParserCombinator.CreatingParser<Object> _create_3 = integer.create(ABCDExpressionLanguage.Int.class);
      ParserCombinator.Parser<Object> _or_2 = _or_1.or(_create_3);
      ParserCombinator.Parser<Object> _isymb_2 = ParserCombinator.isymb("(");
      ParserCombinator.Parser<Object> _then = _isymb_2.then(expression);
      ParserCombinator.Parser<Object> _isymb_3 = ParserCombinator.isymb(")");
      ParserCombinator.Parser<Object> _then_1 = _then.then(_isymb_3);
      final ParserCombinator.Parser<Object> primitiveExpression = _or_2.or(_then_1);
      ParserCombinator.Parser<Object> _kw = ABCDExpressionLanguage.kw("not");
      ParserCombinator.Parser<Object> _symb = ParserCombinator.symb("-");
      ParserCombinator.Parser<Object> _or_3 = _kw.or(_symb);
      ParserCombinator.Parser<Object> _symb_1 = ParserCombinator.symb("+");
      ParserCombinator.Parser<Object> _or_4 = _or_3.or(_symb_1);
      ParserCombinator.CreatingParser<Object> _create_4 = primitiveExpression.create(ABCDExpressionLanguage.UnaryExp.class);
      ParserCombinator.Parser<Object> _then_2 = _or_4.then(_create_4);
      final ParserCombinator.Parser<Object> priority0 = _then_2.or(primitiveExpression);
      ParserCombinator.Parser<Object> _symb_2 = ParserCombinator.symb("*");
      ParserCombinator.Parser<Object> _symb_3 = ParserCombinator.symb("/");
      ParserCombinator.Parser<Object> _or_5 = _symb_2.or(_symb_3);
      ParserCombinator.Parser<Object> _symb_4 = ParserCombinator.symb("%");
      ParserCombinator.Parser<Object> _or_6 = _or_5.or(_symb_4);
      ParserCombinator.Parser<Object> _then_3 = _or_6.then(priority0);
      ParserCombinator.CreatingParser<Object> _create_5 = _then_3.create(ABCDExpressionLanguage.BinaryExp.class);
      ParserCombinator.Parser<Object> _mult = _create_5.mult(0, (-1));
      final ParserCombinator.Parser<Object> priority1 = priority0.then(_mult);
      ParserCombinator.Parser<Object> _symb_5 = ParserCombinator.symb("+");
      ParserCombinator.Parser<Object> _symb_6 = ParserCombinator.symb("-");
      ParserCombinator.Parser<Object> _or_7 = _symb_5.or(_symb_6);
      ParserCombinator.Parser<Object> _then_4 = _or_7.then(priority1);
      ParserCombinator.CreatingParser<Object> _create_6 = _then_4.create(ABCDExpressionLanguage.BinaryExp.class);
      ParserCombinator.Parser<Object> _mult_1 = _create_6.mult(0, (-1));
      final ParserCombinator.Parser<Object> priority2 = priority1.then(_mult_1);
      ParserCombinator.Parser<Object> _symb_7 = ParserCombinator.symb("<");
      ParserCombinator.Parser<Object> _symb_8 = ParserCombinator.symb("<=");
      ParserCombinator.Parser<Object> _or_8 = _symb_7.or(_symb_8);
      ParserCombinator.Parser<Object> _symb_9 = ParserCombinator.symb(">");
      ParserCombinator.Parser<Object> _or_9 = _or_8.or(_symb_9);
      ParserCombinator.Parser<Object> _symb_10 = ParserCombinator.symb(">=");
      ParserCombinator.Parser<Object> _or_10 = _or_9.or(_symb_10);
      ParserCombinator.Parser<Object> _then_5 = _or_10.then(priority2);
      ParserCombinator.CreatingParser<Object> _create_7 = _then_5.create(ABCDExpressionLanguage.BinaryExp.class);
      ParserCombinator.Parser<Object> _mult_2 = _create_7.mult(0, (-1));
      final ParserCombinator.Parser<Object> priority3 = priority2.then(_mult_2);
      ParserCombinator.Parser<Object> _symb_11 = ParserCombinator.symb("==");
      ParserCombinator.Parser<Object> _symb_12 = ParserCombinator.symb("!=");
      ParserCombinator.Parser<Object> _or_11 = _symb_11.or(_symb_12);
      ParserCombinator.Parser<Object> _then_6 = _or_11.then(priority3);
      ParserCombinator.CreatingParser<Object> _create_8 = _then_6.create(ABCDExpressionLanguage.BinaryExp.class);
      ParserCombinator.Parser<Object> _mult_3 = _create_8.mult(0, (-1));
      final ParserCombinator.Parser<Object> priority4 = priority3.then(_mult_3);
      ParserCombinator.Parser<Object> _kw_1 = ABCDExpressionLanguage.kw("or");
      ParserCombinator.Parser<Object> _kw_2 = ABCDExpressionLanguage.kw("and");
      ParserCombinator.Parser<Object> _or_12 = _kw_1.or(_kw_2);
      ParserCombinator.Parser<Object> _kw_3 = ABCDExpressionLanguage.kw("nor");
      ParserCombinator.Parser<Object> _or_13 = _or_12.or(_kw_3);
      ParserCombinator.Parser<Object> _kw_4 = ABCDExpressionLanguage.kw("nand");
      ParserCombinator.Parser<Object> _or_14 = _or_13.or(_kw_4);
      ParserCombinator.Parser<Object> _kw_5 = ABCDExpressionLanguage.kw("xor");
      ParserCombinator.Parser<Object> _or_15 = _or_14.or(_kw_5);
      ParserCombinator.Parser<Object> _then_7 = _or_15.then(priority4);
      ParserCombinator.CreatingParser<Object> _create_9 = _then_7.create(ABCDExpressionLanguage.BinaryExp.class);
      ParserCombinator.Parser<Object> _mult_4 = _create_9.mult(0, (-1));
      final ParserCombinator.Parser<Object> priority5 = priority4.then(_mult_4);
      expression.set(priority5);
      ABCDExpressionLanguage.expressionParser = ((ParserCombinator.Parser<ABCDExpressionLanguage.Exp>) ((ParserCombinator.Parser) expression));
    }
    return ABCDExpressionLanguage.expressionParser;
  }
}

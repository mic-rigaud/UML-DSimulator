package tuml.interpreter.parsercombinator;

import com.google.common.base.Objects;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;
import tuml.interpreter.parsercombinator.ABCDExpressionLanguage;
import tuml.interpreter.parsercombinator.GrammarLanguage;

@SuppressWarnings("all")
public class ParserCombinator {
  @Data
  public static class Result {
    private final boolean success;
    
    private final ParserCombinator.ParserState state;
    
    public Result(final boolean success, final ParserCombinator.List<Object> stack, final String rest, final ParserCombinator.ParserState longestFailure) {
      this(success, new ParserCombinator.ParserState(stack, rest, longestFailure));
    }
    
    public Result(final boolean success, final ParserCombinator.ParserState state) {
      this.success = success;
      this.state = state;
    }
    
    public boolean getFailure() {
      return (!this.success);
    }
    
    public ParserCombinator.List<Object> getStack() {
      return this.state.stack;
    }
    
    public String getRest() {
      return this.state.rest;
    }
    
    public ParserCombinator.ParserState getLongestFailure() {
      return this.state.longestFailure;
    }
    
    public ParserCombinator.ParserState stateWithStack(final ParserCombinator.List<Object> st) {
      String _rest = this.getRest();
      ParserCombinator.ParserState _longestFailure = this.getLongestFailure();
      return new ParserCombinator.ParserState(st, _rest, _longestFailure);
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (this.success ? 1231 : 1237);
      result = prime * result + ((this.state== null) ? 0 : this.state.hashCode());
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
      ParserCombinator.Result other = (ParserCombinator.Result) obj;
      if (other.success != this.success)
        return false;
      if (this.state == null) {
        if (other.state != null)
          return false;
      } else if (!this.state.equals(other.state))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      ToStringBuilder b = new ToStringBuilder(this);
      b.add("success", this.success);
      b.add("state", this.state);
      return b.toString();
    }
    
    @Pure
    public boolean isSuccess() {
      return this.success;
    }
    
    @Pure
    public ParserCombinator.ParserState getState() {
      return this.state;
    }
  }
  
  @Data
  public static class ParserState {
    private final ParserCombinator.List<Object> stack;
    
    private final String rest;
    
    private final ParserCombinator.ParserState longestFailure;
    
    public ParserCombinator.ParserState withRest(final String rest) {
      return new ParserCombinator.ParserState(this.stack, rest, this.longestFailure);
    }
    
    public ParserCombinator.ParserState withLongestFailureOf(final ParserCombinator.ParserState other) {
      return new ParserCombinator.ParserState(this.stack, this.rest, other.longestFailure);
    }
    
    public ParserCombinator.Result fail() {
      ParserCombinator.ParserState _xifexpression = null;
      boolean _or = false;
      boolean _equals = Objects.equal(this.longestFailure, null);
      if (_equals) {
        _or = true;
      } else {
        int _length = this.rest.length();
        int _length_1 = this.longestFailure.rest.length();
        boolean _lessThan = (_length < _length_1);
        _or = _lessThan;
      }
      if (_or) {
        _xifexpression = new ParserCombinator.ParserState(this.stack, this.rest, null);
      } else {
        _xifexpression = this.longestFailure;
      }
      final ParserCombinator.ParserState lf = _xifexpression;
      ParserCombinator.ParserState _parserState = new ParserCombinator.ParserState(this.stack, this.rest, lf);
      return new ParserCombinator.Result(false, _parserState);
    }
    
    public ParserState(final ParserCombinator.List<Object> stack, final String rest, final ParserCombinator.ParserState longestFailure) {
      super();
      this.stack = stack;
      this.rest = rest;
      this.longestFailure = longestFailure;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.stack== null) ? 0 : this.stack.hashCode());
      result = prime * result + ((this.rest== null) ? 0 : this.rest.hashCode());
      result = prime * result + ((this.longestFailure== null) ? 0 : this.longestFailure.hashCode());
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
      ParserCombinator.ParserState other = (ParserCombinator.ParserState) obj;
      if (this.stack == null) {
        if (other.stack != null)
          return false;
      } else if (!this.stack.equals(other.stack))
        return false;
      if (this.rest == null) {
        if (other.rest != null)
          return false;
      } else if (!this.rest.equals(other.rest))
        return false;
      if (this.longestFailure == null) {
        if (other.longestFailure != null)
          return false;
      } else if (!this.longestFailure.equals(other.longestFailure))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      ToStringBuilder b = new ToStringBuilder(this);
      b.add("stack", this.stack);
      b.add("rest", this.rest);
      b.add("longestFailure", this.longestFailure);
      return b.toString();
    }
    
    @Pure
    public ParserCombinator.List<Object> getStack() {
      return this.stack;
    }
    
    @Pure
    public String getRest() {
      return this.rest;
    }
    
    @Pure
    public ParserCombinator.ParserState getLongestFailure() {
      return this.longestFailure;
    }
  }
  
  public static abstract class Parser<T extends Object> {
    public ParserCombinator.Parser<T> or(final ParserCombinator.Parser<T> o) {
      return new ParserCombinator.OrParser<T>(this, o);
    }
    
    public ParserCombinator.Parser<T> then(final ParserCombinator.Parser<T> o) {
      return new ParserCombinator.ThenParser<T>(this, o);
    }
    
    public ParserCombinator.Parser<T> mult(final int lower, final int upper) {
      return new ParserCombinator.MultParser<T>(this, lower, upper, false, null);
    }
    
    public ParserCombinator.Parser<T> mult(final int lower, final int upper, final ParserCombinator.Parser<T> separator) {
      return new ParserCombinator.MultParser<T>(this, lower, upper, false, separator);
    }
    
    public ParserCombinator.Parser<T> multSet(final int lower, final int upper) {
      return new ParserCombinator.MultParser<T>(this, lower, upper, true, null);
    }
    
    public ParserCombinator.Parser<T> multSet(final int lower, final int upper, final ParserCombinator.Parser<T> separator) {
      return new ParserCombinator.MultParser<T>(this, lower, upper, true, separator);
    }
    
    public abstract ParserCombinator.Result parseTrimmed(final ParserCombinator.ParserState state);
    
    public ParserCombinator.Result parseInternal(final ParserCombinator.ParserState state) {
      String _ignore = this.ignore(state.rest);
      ParserCombinator.ParserState _withRest = state.withRest(_ignore);
      return this.parseTrimmed(_withRest);
    }
    
    public String ignore(final String s) {
      return s.replaceFirst("^(\\s|--.*)*", "");
    }
    
    public T parse(final String cs) {
      T _xblockexpression = null;
      {
        ParserCombinator.List<Object> _nil = ParserCombinator.<Object>nil();
        ParserCombinator.ParserState _parserState = new ParserCombinator.ParserState(_nil, cs, null);
        final ParserCombinator.Result parsed = this.parseInternal(_parserState);
        boolean _failure = parsed.getFailure();
        if (_failure) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("error: could not parse ");
          _builder.append(parsed, "");
          throw new RuntimeException(_builder.toString());
        }
        String _rest = parsed.getRest();
        String _ignore = this.ignore(_rest);
        int _length = _ignore.length();
        boolean _notEquals = (_length != 0);
        if (_notEquals) {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("parsing incomplete: ");
          _builder_1.append(parsed, "");
          throw new RuntimeException(_builder_1.toString());
        }
        ParserCombinator.List<Object> _stack = parsed.getStack();
        int _length_1 = _stack.getLength();
        boolean _notEquals_1 = (_length_1 != 1);
        if (_notEquals_1) {
          StringConcatenation _builder_2 = new StringConcatenation();
          _builder_2.append("warning: more than one element left on stack: ");
          _builder_2.append(parsed, "");
          InputOutput.<String>println(_builder_2.toString());
        }
        ParserCombinator.List<Object> _stack_1 = parsed.getStack();
        Object _head = _stack_1.getHead();
        _xblockexpression = ((T) _head);
      }
      return _xblockexpression;
    }
    
    public ParserCombinator.CreatingParser<T> create(final Class<? extends T> c, final Object... args) {
      return new ParserCombinator.CreatingParser<T>(c, this);
    }
    
    public ParserCombinator.NodeCreatingParser<T> create(final String typeName, final String... argNames) {
      return new ParserCombinator.NodeCreatingParser<T>(this, typeName, argNames);
    }
  }
  
  @Data
  public static class GenericNode {
    private final String typeName;
    
    private final Collection<Pair<String, Object>> slots;
    
    public GenericNode(final String typeName, final Collection<Pair<String, Object>> slots) {
      super();
      this.typeName = typeName;
      this.slots = slots;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.typeName== null) ? 0 : this.typeName.hashCode());
      result = prime * result + ((this.slots== null) ? 0 : this.slots.hashCode());
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
      ParserCombinator.GenericNode other = (ParserCombinator.GenericNode) obj;
      if (this.typeName == null) {
        if (other.typeName != null)
          return false;
      } else if (!this.typeName.equals(other.typeName))
        return false;
      if (this.slots == null) {
        if (other.slots != null)
          return false;
      } else if (!this.slots.equals(other.slots))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      ToStringBuilder b = new ToStringBuilder(this);
      b.add("typeName", this.typeName);
      b.add("slots", this.slots);
      return b.toString();
    }
    
    @Pure
    public String getTypeName() {
      return this.typeName;
    }
    
    @Pure
    public Collection<Pair<String, Object>> getSlots() {
      return this.slots;
    }
  }
  
  @Data
  public static class NodeCreatingParser<T extends Object> extends ParserCombinator.Parser<T> {
    private final ParserCombinator.Parser<T> wrapped;
    
    private final String typeName;
    
    private final String[] argNames;
    
    @Override
    public ParserCombinator.Result parseTrimmed(final ParserCombinator.ParserState state) {
      ParserCombinator.Result _xblockexpression = null;
      {
        final ParserCombinator.Result ret = this.wrapped.parseInternal(state);
        ParserCombinator.Result _xifexpression = null;
        if (ret.success) {
          ParserCombinator.Result _xblockexpression_1 = null;
          {
            ParserCombinator.List<Object> st = ret.getStack();
            Collection<Pair<String, Object>> slots = new ArrayList<Pair<String, Object>>();
            java.util.List<String> _reverse = ListExtensions.<String>reverse(((java.util.List<String>)Conversions.doWrapArray(this.argNames)));
            for (final String argName : _reverse) {
              {
                Object _head = st.getHead();
                Pair<String, Object> _mappedTo = Pair.<String, Object>of(argName, _head);
                slots.add(_mappedTo);
                ParserCombinator.List<Object> _tail = st.getTail();
                st = _tail;
              }
            }
            Object key = new ParserCombinator.GenericNode(this.typeName, slots);
            ParserCombinator.Cons<Object> _cons = new ParserCombinator.Cons<Object>(key, st);
            String _rest = ret.getRest();
            _xblockexpression_1 = new ParserCombinator.Result(true, _cons, _rest, ret.state.longestFailure);
          }
          _xifexpression = _xblockexpression_1;
        } else {
          _xifexpression = ret;
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    }
    
    public NodeCreatingParser(final ParserCombinator.Parser<T> wrapped, final String typeName, final String[] argNames) {
      super();
      this.wrapped = wrapped;
      this.typeName = typeName;
      this.argNames = argNames;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.wrapped== null) ? 0 : this.wrapped.hashCode());
      result = prime * result + ((this.typeName== null) ? 0 : this.typeName.hashCode());
      result = prime * result + ((this.argNames== null) ? 0 : Arrays.deepHashCode(this.argNames));
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
      ParserCombinator.NodeCreatingParser<?> other = (ParserCombinator.NodeCreatingParser<?>) obj;
      if (this.wrapped == null) {
        if (other.wrapped != null)
          return false;
      } else if (!this.wrapped.equals(other.wrapped))
        return false;
      if (this.typeName == null) {
        if (other.typeName != null)
          return false;
      } else if (!this.typeName.equals(other.typeName))
        return false;
      if (this.argNames == null) {
        if (other.argNames != null)
          return false;
      } else if (!Arrays.deepEquals(this.argNames, other.argNames))
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
    public ParserCombinator.Parser<T> getWrapped() {
      return this.wrapped;
    }
    
    @Pure
    public String getTypeName() {
      return this.typeName;
    }
    
    @Pure
    public String[] getArgNames() {
      return this.argNames;
    }
  }
  
  @Data
  public static class CreatingParser<T extends Object> extends ParserCombinator.Parser<T> {
    private final Class<? extends T> type;
    
    private final ParserCombinator.Parser<T> wrappedParser;
    
    @Override
    public ParserCombinator.Result parseTrimmed(final ParserCombinator.ParserState state) {
      ParserCombinator.Result _xblockexpression = null;
      {
        final ParserCombinator.Result ret = this.wrappedParser.parseInternal(state);
        ParserCombinator.Result _xifexpression = null;
        if (ret.success) {
          ParserCombinator.Result _xblockexpression_1 = null;
          {
            Constructor<?>[] _constructors = this.type.getConstructors();
            Constructor<?> _get = _constructors[0];
            final Constructor<T> cons = ((Constructor<T>) _get);
            ParserCombinator.List<Object> st = ret.getStack();
            int _parameterCount = cons.getParameterCount();
            Object[] args = new Object[_parameterCount];
            for (int i = 0; (i < args.length); i++) {
              {
                int _length = args.length;
                int _minus = (_length - i);
                int _minus_1 = (_minus - 1);
                Object _head = st.getHead();
                args[_minus_1] = _head;
                ParserCombinator.List<Object> _tail = st.getTail();
                st = _tail;
              }
            }
            Object key = null;
            try {
              T _newInstance = cons.newInstance(args);
              key = _newInstance;
            } catch (final Throwable _t) {
              if (_t instanceof Exception) {
                final Exception e = (Exception)_t;
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("could not create ");
                String _string = cons.toString();
                _builder.append(_string, "");
                _builder.append(" with ");
                final Object[] _converted_args = (Object[])args;
                java.util.List<Object> _list = IterableExtensions.<Object>toList(((Iterable<Object>)Conversions.doWrapArray(_converted_args)));
                _builder.append(_list, "");
                throw new RuntimeException(_builder.toString(), e);
              } else {
                throw Exceptions.sneakyThrow(_t);
              }
            }
            ParserCombinator.Cons<Object> _cons = new ParserCombinator.Cons<Object>(key, st);
            String _rest = ret.getRest();
            _xblockexpression_1 = new ParserCombinator.Result(true, _cons, _rest, ret.state.longestFailure);
          }
          _xifexpression = _xblockexpression_1;
        } else {
          _xifexpression = ret;
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    }
    
    @Override
    public String toString() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("(");
      _builder.append(this.wrappedParser, "");
      _builder.append(")[");
      String _simpleName = this.type.getSimpleName();
      _builder.append(_simpleName, "");
      _builder.append("]");
      return _builder.toString();
    }
    
    public CreatingParser(final Class<? extends T> type, final ParserCombinator.Parser<T> wrappedParser) {
      super();
      this.type = type;
      this.wrappedParser = wrappedParser;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.type== null) ? 0 : this.type.hashCode());
      result = prime * result + ((this.wrappedParser== null) ? 0 : this.wrappedParser.hashCode());
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
      ParserCombinator.CreatingParser<?> other = (ParserCombinator.CreatingParser<?>) obj;
      if (this.type == null) {
        if (other.type != null)
          return false;
      } else if (!this.type.equals(other.type))
        return false;
      if (this.wrappedParser == null) {
        if (other.wrappedParser != null)
          return false;
      } else if (!this.wrappedParser.equals(other.wrappedParser))
        return false;
      return true;
    }
    
    @Pure
    public Class<? extends T> getType() {
      return this.type;
    }
    
    @Pure
    public ParserCombinator.Parser<T> getWrappedParser() {
      return this.wrappedParser;
    }
  }
  
  public static class WrappingParser<T extends Object> extends ParserCombinator.Parser<T> {
    private ParserCombinator.Parser<T> wrappedParser;
    
    @Override
    public ParserCombinator.Result parseTrimmed(final ParserCombinator.ParserState state) {
      return this.wrappedParser.parseInternal(state);
    }
    
    public ParserCombinator.Parser<T> set(final ParserCombinator.Parser<T> wrappedParser) {
      return this.wrappedParser = wrappedParser;
    }
    
    @Override
    public String toString() {
      return "wrapping!";
    }
  }
  
  @Data
  public static class OrParser<T extends Object> extends ParserCombinator.Parser<T> {
    private final ParserCombinator.Parser<T> left;
    
    private final ParserCombinator.Parser<T> right;
    
    @Override
    public ParserCombinator.Result parseTrimmed(final ParserCombinator.ParserState state) {
      ParserCombinator.Result ret = this.left.parseInternal(state);
      if (ret.success) {
        return ret;
      } else {
        ParserCombinator.ParserState _withLongestFailureOf = state.withLongestFailureOf(ret.state);
        ParserCombinator.Result _parseInternal = this.right.parseInternal(_withLongestFailureOf);
        ret = _parseInternal;
        return ret;
      }
    }
    
    @Override
    public String toString() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("(");
      _builder.append(this.left, "");
      _builder.append(" | ");
      _builder.append(this.right, "");
      _builder.append(")");
      return _builder.toString();
    }
    
    public OrParser(final ParserCombinator.Parser<T> left, final ParserCombinator.Parser<T> right) {
      super();
      this.left = left;
      this.right = right;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
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
      ParserCombinator.OrParser<?> other = (ParserCombinator.OrParser<?>) obj;
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
    
    @Pure
    public ParserCombinator.Parser<T> getLeft() {
      return this.left;
    }
    
    @Pure
    public ParserCombinator.Parser<T> getRight() {
      return this.right;
    }
  }
  
  @Data
  public static class ThenParser<T extends Object> extends ParserCombinator.Parser<T> {
    private final ParserCombinator.Parser<T> left;
    
    private final ParserCombinator.Parser<T> right;
    
    @Override
    public ParserCombinator.Result parseTrimmed(final ParserCombinator.ParserState state) {
      ParserCombinator.Result _xblockexpression = null;
      {
        ParserCombinator.Result retl = this.left.parseInternal(state);
        ParserCombinator.Result _xifexpression = null;
        if (retl.success) {
          ParserCombinator.Result _xblockexpression_1 = null;
          {
            ParserCombinator.Result retr = this.right.parseInternal(retl.state);
            _xblockexpression_1 = retr;
          }
          _xifexpression = _xblockexpression_1;
        } else {
          _xifexpression = retl;
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    }
    
    @Override
    public String toString() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("(");
      _builder.append(this.left, "");
      _builder.append(" ");
      _builder.append(this.right, "");
      _builder.append(")");
      return _builder.toString();
    }
    
    public ThenParser(final ParserCombinator.Parser<T> left, final ParserCombinator.Parser<T> right) {
      super();
      this.left = left;
      this.right = right;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
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
      ParserCombinator.ThenParser<?> other = (ParserCombinator.ThenParser<?>) obj;
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
    
    @Pure
    public ParserCombinator.Parser<T> getLeft() {
      return this.left;
    }
    
    @Pure
    public ParserCombinator.Parser<T> getRight() {
      return this.right;
    }
  }
  
  @Data
  public static class DroppingParser<T extends Object> extends ParserCombinator.Parser<T> {
    private final ParserCombinator.Parser<T> wrapped;
    
    @Override
    public ParserCombinator.Result parseTrimmed(final ParserCombinator.ParserState state) {
      ParserCombinator.Result _xblockexpression = null;
      {
        final ParserCombinator.Result ret = this.wrapped.parseInternal(state);
        ParserCombinator.Result _xifexpression = null;
        if (ret.success) {
          String _rest = ret.getRest();
          _xifexpression = new ParserCombinator.Result(true, state.stack, _rest, ret.state.longestFailure);
        } else {
          _xifexpression = ret;
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    }
    
    public DroppingParser(final ParserCombinator.Parser<T> wrapped) {
      super();
      this.wrapped = wrapped;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.wrapped== null) ? 0 : this.wrapped.hashCode());
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
      ParserCombinator.DroppingParser<?> other = (ParserCombinator.DroppingParser<?>) obj;
      if (this.wrapped == null) {
        if (other.wrapped != null)
          return false;
      } else if (!this.wrapped.equals(other.wrapped))
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
    public ParserCombinator.Parser<T> getWrapped() {
      return this.wrapped;
    }
  }
  
  @Data
  public static class MultParser<T extends Object> extends ParserCombinator.Parser<T> {
    private final ParserCombinator.Parser<T> left;
    
    private final int lower;
    
    private final int upper;
    
    private final boolean elemsAsSet;
    
    private final ParserCombinator.Parser<T> separator;
    
    @Override
    public ParserCombinator.Result parseTrimmed(final ParserCombinator.ParserState state) {
      ParserCombinator.Result _xblockexpression = null;
      {
        String next = state.rest;
        LinkedHashSet<Object> elems = new LinkedHashSet<Object>();
        ParserCombinator.List<Object> _xifexpression = null;
        if (this.elemsAsSet) {
          _xifexpression = new ParserCombinator.Cons<Object>(elems, state.stack);
        } else {
          _xifexpression = state.stack;
        }
        ParserCombinator.List<Object> st = _xifexpression;
        ParserCombinator.Result ret = new ParserCombinator.Result(true, st, next, state.longestFailure);
        int count = 0;
        boolean goon = true;
        do {
          {
            ParserCombinator.List<Object> _xifexpression_1 = null;
            if (this.elemsAsSet) {
              _xifexpression_1 = ParserCombinator.<Object>nil();
            } else {
              _xifexpression_1 = st;
            }
            ParserCombinator.ParserState _parserState = new ParserCombinator.ParserState(_xifexpression_1, next, ret.state.longestFailure);
            ParserCombinator.Result r = this.left.parseInternal(_parserState);
            if (r.success) {
              count++;
              if (this.elemsAsSet) {
                ParserCombinator.List<Object> _stack = r.getStack();
                int _length = _stack.getLength();
                boolean _notEquals = (_length != 1);
                if (_notEquals) {
                  throw new RuntimeException();
                }
                ParserCombinator.List<Object> _stack_1 = r.getStack();
                Object _head = _stack_1.getHead();
                elems.add(_head);
              } else {
                ParserCombinator.List<Object> _stack_2 = r.getStack();
                st = _stack_2;
              }
              boolean _notEquals_1 = (!Objects.equal(this.separator, null));
              if (_notEquals_1) {
                ParserCombinator.ParserState _stateWithStack = r.stateWithStack(st);
                final ParserCombinator.Result tr = this.separator.parseInternal(_stateWithStack);
                if (tr.success) {
                  ParserCombinator.List<Object> _stack_3 = tr.getStack();
                  st = _stack_3;
                  r = tr;
                } else {
                  goon = false;
                }
              }
              String _rest = r.getRest();
              next = _rest;
              ParserCombinator.ParserState _stateWithStack_1 = r.stateWithStack(st);
              ParserCombinator.Result _result = new ParserCombinator.Result(true, _stateWithStack_1);
              ret = _result;
            } else {
              goon = false;
            }
          }
        } while((goon && ((this.upper < 0) || (count < this.upper))));
        if ((count < this.lower)) {
          return state.fail();
        }
        _xblockexpression = ret;
      }
      return _xblockexpression;
    }
    
    @Override
    public String toString() {
      String _xifexpression = null;
      if ((this.lower == 0)) {
        if ((this.upper == (-1))) {
          return "*";
        } else {
          if ((this.upper == 1)) {
            return "?";
          }
        }
      } else {
        if ((this.lower == 1)) {
          if ((this.upper == (-1))) {
            return "+";
          }
        }
      }
      /* (this.left + _xifexpression); */
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("{");
      _builder.append(this.lower, "");
      _builder.append(", ");
      _builder.append(this.upper, "");
      _builder.append(")");
      return _builder.toString();
    }
    
    public MultParser(final ParserCombinator.Parser<T> left, final int lower, final int upper, final boolean elemsAsSet, final ParserCombinator.Parser<T> separator) {
      super();
      this.left = left;
      this.lower = lower;
      this.upper = upper;
      this.elemsAsSet = elemsAsSet;
      this.separator = separator;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.left== null) ? 0 : this.left.hashCode());
      result = prime * result + this.lower;
      result = prime * result + this.upper;
      result = prime * result + (this.elemsAsSet ? 1231 : 1237);
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
      ParserCombinator.MultParser<?> other = (ParserCombinator.MultParser<?>) obj;
      if (this.left == null) {
        if (other.left != null)
          return false;
      } else if (!this.left.equals(other.left))
        return false;
      if (other.lower != this.lower)
        return false;
      if (other.upper != this.upper)
        return false;
      if (other.elemsAsSet != this.elemsAsSet)
        return false;
      if (this.separator == null) {
        if (other.separator != null)
          return false;
      } else if (!this.separator.equals(other.separator))
        return false;
      return true;
    }
    
    @Pure
    public ParserCombinator.Parser<T> getLeft() {
      return this.left;
    }
    
    @Pure
    public int getLower() {
      return this.lower;
    }
    
    @Pure
    public int getUpper() {
      return this.upper;
    }
    
    @Pure
    public boolean isElemsAsSet() {
      return this.elemsAsSet;
    }
    
    @Pure
    public ParserCombinator.Parser<T> getSeparator() {
      return this.separator;
    }
  }
  
  @Data
  public static class SymbolParser<T extends Object> extends ParserCombinator.Parser<T> {
    private final String symbol;
    
    private final boolean ignored;
    
    @Override
    public ParserCombinator.Result parseTrimmed(final ParserCombinator.ParserState state) {
      ParserCombinator.Result _xifexpression = null;
      boolean _startsWith = state.rest.startsWith(this.symbol);
      if (_startsWith) {
        ParserCombinator.Result _xblockexpression = null;
        {
          ParserCombinator.List<Object> _xifexpression_1 = null;
          if ((!this.ignored)) {
            _xifexpression_1 = new ParserCombinator.Cons<Object>(this.symbol, state.stack);
          } else {
            _xifexpression_1 = state.stack;
          }
          final ParserCombinator.List<Object> st = _xifexpression_1;
          int _length = this.symbol.length();
          String _substring = state.rest.substring(_length);
          _xblockexpression = new ParserCombinator.Result(true, st, _substring, state.longestFailure);
        }
        _xifexpression = _xblockexpression;
      } else {
        _xifexpression = state.fail();
      }
      return _xifexpression;
    }
    
    @Override
    public String toString() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("\"");
      _builder.append(this.symbol, "");
      _builder.append("\"");
      String _xifexpression = null;
      if (this.ignored) {
        _xifexpression = "!";
      } else {
        _xifexpression = "";
      }
      _builder.append(_xifexpression, "");
      return _builder.toString();
    }
    
    public SymbolParser(final String symbol, final boolean ignored) {
      super();
      this.symbol = symbol;
      this.ignored = ignored;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.symbol== null) ? 0 : this.symbol.hashCode());
      result = prime * result + (this.ignored ? 1231 : 1237);
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
      ParserCombinator.SymbolParser<?> other = (ParserCombinator.SymbolParser<?>) obj;
      if (this.symbol == null) {
        if (other.symbol != null)
          return false;
      } else if (!this.symbol.equals(other.symbol))
        return false;
      if (other.ignored != this.ignored)
        return false;
      return true;
    }
    
    @Pure
    public String getSymbol() {
      return this.symbol;
    }
    
    @Pure
    public boolean isIgnored() {
      return this.ignored;
    }
  }
  
  public static class TerminalParser<T extends Object> extends ParserCombinator.Parser<T> {
    private final Pattern pattern;
    
    public TerminalParser(final String regex) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("^(?s)");
      _builder.append(regex, "");
      _builder.append("(.*)$");
      Pattern _compile = Pattern.compile(_builder.toString());
      this.pattern = _compile;
    }
    
    @Override
    public ParserCombinator.Result parseTrimmed(final ParserCombinator.ParserState state) {
      ParserCombinator.Result _xblockexpression = null;
      {
        final Matcher matcher = this.pattern.matcher(state.rest);
        ParserCombinator.Result _xifexpression = null;
        boolean _matches = matcher.matches();
        if (_matches) {
          String _group = matcher.group(1);
          ParserCombinator.Cons<Object> _cons = new ParserCombinator.Cons<Object>(_group, state.stack);
          String _group_1 = matcher.group(2);
          _xifexpression = new ParserCombinator.Result(true, _cons, _group_1, state.longestFailure);
        } else {
          _xifexpression = state.fail();
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    }
    
    @Override
    public String toString() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("/");
      String _string = this.pattern.toString();
      _builder.append(_string, "");
      _builder.append("/");
      return _builder.toString();
    }
  }
  
  public static abstract class List<T extends Object> {
    public abstract T getHead();
    
    public abstract ParserCombinator.List<T> getTail();
    
    public abstract int getLength();
    
    public ParserCombinator.List<T> push(final T e) {
      return new ParserCombinator.Cons<T>(e, this);
    }
  }
  
  @Data
  public static class Cons<T extends Object> extends ParserCombinator.List<T> {
    private final T head;
    
    private final ParserCombinator.List<T> tail;
    
    private final int length;
    
    public Cons(final T head) {
      this(head, ParserCombinator.<T>nil());
    }
    
    public Cons(final T head, final ParserCombinator.List<T> tail) {
      this.head = head;
      this.tail = tail;
      int _length = tail.getLength();
      int _plus = (_length + 1);
      this.length = _plus;
    }
    
    @Override
    public String toString() {
      String _xblockexpression = null;
      {
        final StringBuffer ret = new StringBuffer("[");
        ParserCombinator.List<?> p = this;
        boolean first = true;
        while ((p instanceof ParserCombinator.Cons<?>)) {
          {
            if (first) {
              first = false;
            } else {
              ret.append(", ");
            }
            ret.append(((ParserCombinator.Cons<?>)p).head);
            p = ((ParserCombinator.Cons<?>)p).tail;
          }
        }
        ret.append("]");
        _xblockexpression = ret.toString();
      }
      return _xblockexpression;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.head== null) ? 0 : this.head.hashCode());
      result = prime * result + ((this.tail== null) ? 0 : this.tail.hashCode());
      result = prime * result + this.length;
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
      ParserCombinator.Cons<?> other = (ParserCombinator.Cons<?>) obj;
      if (this.head == null) {
        if (other.head != null)
          return false;
      } else if (!this.head.equals(other.head))
        return false;
      if (this.tail == null) {
        if (other.tail != null)
          return false;
      } else if (!this.tail.equals(other.tail))
        return false;
      if (other.length != this.length)
        return false;
      return true;
    }
    
    @Pure
    public T getHead() {
      return this.head;
    }
    
    @Pure
    public ParserCombinator.List<T> getTail() {
      return this.tail;
    }
    
    @Pure
    public int getLength() {
      return this.length;
    }
  }
  
  public static class Nil<T extends Object> extends ParserCombinator.List<T> {
    @Override
    public T getHead() {
      throw new UnsupportedOperationException();
    }
    
    @Override
    public ParserCombinator.List<T> getTail() {
      throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
      return "[]";
    }
    
    @Override
    public int getLength() {
      return 0;
    }
  }
  
  @Data
  public static class Node {
    private final Object left;
    
    private final Object right;
    
    public Node(final Object left, final Object right) {
      super();
      this.left = left;
      this.right = right;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
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
      ParserCombinator.Node other = (ParserCombinator.Node) obj;
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
      ToStringBuilder b = new ToStringBuilder(this);
      b.add("left", this.left);
      b.add("right", this.right);
      return b.toString();
    }
    
    @Pure
    public Object getLeft() {
      return this.left;
    }
    
    @Pure
    public Object getRight() {
      return this.right;
    }
  }
  
  public static void main(final String[] args) {
    ParserCombinator.simpleTests();
    ParserCombinator.testGrammars();
    ParserCombinator.testABCDExpression();
  }
  
  public static void testGrammar(final String grammar, final String... programs) {
    final ParserCombinator.Parser<GrammarLanguage.Grammar> parser = GrammarLanguage.getParser();
    Object _testParser = ParserCombinator.testParser(parser, grammar);
    final GrammarLanguage.Grammar parsed = ((GrammarLanguage.Grammar) _testParser);
    final ParserCombinator.WrappingParser<Object> parsedParser = parsed.eval();
  }
  
  public static void testGrammars() {
    ParserCombinator.testSimpleGrammars();
    ParserCombinator.testKM3Grammar();
    ParserCombinator.testMetaGrammar();
  }
  
  public static void testSimpleGrammars() {
    ParserCombinator.testGrammar("a := \"A\";", "A");
    ParserCombinator.testGrammar("a := \"A\" | \"B\";", "A", "B");
    ParserCombinator.testGrammar("a := \"A\" \"B\";", "AB", "A B");
    ParserCombinator.testGrammar("a := \"A\" \"B\" | c; c := \"C\";", "AB", "C");
    ParserCombinator.testGrammar("a := \"a\"*#,(\",\"!);", "a,a,a,a,");
    try {
      ParserCombinator.testGrammar("a := \"a\"?#;", "aa");
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception e = (Exception)_t;
        e.printStackTrace(System.out);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    try {
      ParserCombinator.testGrammar("a := \"a\"+#;", "");
    } catch (final Throwable _t_1) {
      if (_t_1 instanceof Exception) {
        final Exception e_1 = (Exception)_t_1;
        e_1.printStackTrace(System.out);
      } else {
        throw Exceptions.sneakyThrow(_t_1);
      }
    }
  }
  
  private final static String km3Grammar = "\r\n\t\tmetamodel := package*# {Metamodel(packages)};\r\n\t\tpackage := \"package\"! ident \"{\"! classifier*# \"}\"! {Package(name,classifiers)};\r\n\t\tclassifier := class | dataType;\r\n\t\tclass := \"class\"! ident (\"extends\"! ident)?# \"{\"! structuralFeature*# \"}\"! {Class(name,supertype,structuralFeatures)};\r\n\t\tstructuralFeature := attribute | reference;\r\n\t\tattribute := \"attribute\"! ident multiplicity \":\"! ident \";\"! {Attribute(name,multiplicity,typeName)};\r\n\t\treference := \"reference\"! ident multiplicity \"container\"?# \":\"! ident (\"oppositeOf\"! ident)?# \";\"! {Reference(name,multiplicity,isContainer,typeName,opposite)};\r\n\t\tmultiplicity := (\"[\"! (\"*\"! {AnyNumber()} | int \"-\"! int {Bounded(lower, upper)}) \"]\"!)?# \"ordered\"?# \"unique\"?# {Multiplicity(number,isOrdered,isUnique)};\r\n\t\tdataType := \"dataType\"! ident \";\"! {DataType(name)};\r\n\t\tint := /([0-9]+)/;\r\n\t\tident := /([A-Za-z_]\\w*)/;\r\n\t";
  
  public static void testKM3Grammar() {
    ParserCombinator.testGrammar(ParserCombinator.km3Grammar, 
      "\r\n\t\t\t\tpackage KM3 {\t-- test\r\n\t\t\t\t\tclass ModelElement {\r\n\t\t\t\t\t\tattribute name : String;\r\n\t\t\t\t\t}\r\n\r\n\t\t\t\t\tclass Package extends ModelElement {\r\n\t\t\t\t\t\treference classifiers[*] ordered container : Classifier;\r\n\t\t\t\t\t}\r\n\r\n\t\t\t\t\tclass Classifier extends ModelElement {}\r\n\r\n\t\t\t\t\tclass Class extends Classifier {\r\n\t\t\t\t\t\treference supertype[0-1] : Class;\r\n\t\t\t\t\t\treference structuralFeatures[*] ordered container : StructuralFeature oppositeOf owner;\r\n\t\t\t\t\t}\r\n\t\r\n\t\t\t\t\tclass StructuralFeature extends ModelElement {\r\n\t\t\t\t\t\treference owner : Class oppositeOf structuralFeatures;\r\n\r\n\t\t\t\t\t\tattribute lower : Integer;\r\n\t\t\t\t\t\tattribute upper : Integer;\r\n\t\t\t\t\t\tattribute isOrdered : Boolean;\r\n\t\t\t\t\t}\r\n\r\n\t\t\t\t\tclass Attribute extends StructuralFeature {}\r\n\r\n\t\t\t\t\tclass Reference extends StructuralFeature {\r\n\t\t\t\t\t\tattribute isContainer : Boolean;\r\n\t\t\t\t\t\treference opposite : Reference;\r\n\t\t\t\t\t}\r\n\r\n\t\t\t\t\tclass DataType extends Classifier {}\r\n\t\t\t\t}\r\n\r\n\t\t\t\tpackage PrimitiveTypes {\r\n\t\t\t\t\tdataType Boolean;\r\n\t\t\t\t\tdataType Integer;\r\n\t\t\t\t\tdataType String;\r\n\t\t\t\t}\r\n\t\t\t", 
      "\r\n\t\t\t\tpackage A {\t-- test\r\n\t\t\t\t\tclass B {\r\n\t\t\t\t\t\tattribute name orderedunique : String;\r\n\t\t\t\t\t}\r\n\t\t\t\t}\r\n\t\t\t");
  }
  
  public static void testMetaGrammar() {
    final String metaGrammar = "\r\n\t\t\tgrammar := productionRule*;\r\n\t\t\tproductionRule := ident \":=\"! expression \";\"! {ProductionRule(name,body)};\r\n\t\t\texpression := priority3;\r\n\t\t\tpriority3 := priority2 (\"|\"! priority2 {Alternative(left, right)})*;\r\n\t\t\tpriority2 := priority1 (\"{\"! ident \"(\"! ident*#,(\",\"!) \")\"! \"}\"! {Creation(wrapped,typeName,argNames)})?;\r\n\t\t\tpriority1 := priority0 (priority0 {Sequence(left,right)})*;\r\n\t\t\tpriority0 := primitiveExp (\"*\" | \"+\" | \"?\") \"#\"?# (\",\"! primitiveExp)?# {Repetition(wrapped,op,asSet,separator)} | primitiveExp \"!\"! {Drop(wrapped)} | primitiveExp;\r\n\t\t\tprimitiveExp := ident | terminal | regex | \"(\"! expression \")\"!;\r\n\t\t\tident := /([A-Za-z_]\\w*)/;\r\n\t\t\tterminal := /\"([^\"]*)\"/;\r\n\t\t\tregex := /\\/((?:[^\\/\\\\]|\\\\.)*)\\//;\r\n\t\t";
    ParserCombinator.testGrammar(metaGrammar, "a := A;", ParserCombinator.km3Grammar, metaGrammar);
  }
  
  public static Object simpleTests() {
    Object _xblockexpression = null;
    {
      try {
        ParserCombinator.Parser<Object> _symb = ParserCombinator.symb("a");
        ParserCombinator.testParser(_symb, "ab");
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception e = (Exception)_t;
          e.printStackTrace(System.out);
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      ParserCombinator.Parser<Object> _symb_1 = ParserCombinator.symb("a");
      ParserCombinator.Parser<Object> _symb_2 = ParserCombinator.symb("b");
      ParserCombinator.Parser<Object> _then = _symb_1.then(_symb_2);
      ParserCombinator.testParser(_then, "ab");
      ParserCombinator.Parser<Object> _symb_3 = ParserCombinator.symb("a");
      ParserCombinator.Parser<Object> _symb_4 = ParserCombinator.symb("b");
      ParserCombinator.Parser<Object> _or = _symb_3.or(_symb_4);
      ParserCombinator.testParser(_or, "a");
      ParserCombinator.Parser<Object> _symb_5 = ParserCombinator.symb("a");
      ParserCombinator.Parser<Object> _symb_6 = ParserCombinator.symb("b");
      ParserCombinator.Parser<Object> _or_1 = _symb_5.or(_symb_6);
      ParserCombinator.testParser(_or_1, "b");
      try {
        ParserCombinator.Parser<Object> _symb_7 = ParserCombinator.symb("a");
        ParserCombinator.Parser<Object> _symb_8 = ParserCombinator.symb("b");
        ParserCombinator.Parser<Object> _or_2 = _symb_7.or(_symb_8);
        ParserCombinator.testParser(_or_2, "c");
      } catch (final Throwable _t_1) {
        if (_t_1 instanceof Exception) {
          final Exception e_1 = (Exception)_t_1;
          e_1.printStackTrace(System.out);
        } else {
          throw Exceptions.sneakyThrow(_t_1);
        }
      }
      try {
        ParserCombinator.Parser<Object> _symb_9 = ParserCombinator.symb("a");
        ParserCombinator.Parser<Object> _symb_10 = ParserCombinator.symb("b");
        ParserCombinator.Parser<Object> _or_3 = _symb_9.or(_symb_10);
        ParserCombinator.Parser<Object> _mult = _or_3.mult(0, (-1));
        ParserCombinator.testParser(_mult, "abc");
      } catch (final Throwable _t_2) {
        if (_t_2 instanceof Exception) {
          final Exception e_2 = (Exception)_t_2;
          e_2.printStackTrace(System.out);
        } else {
          throw Exceptions.sneakyThrow(_t_2);
        }
      }
      ParserCombinator.Parser<Object> _symb_11 = ParserCombinator.symb("a");
      ParserCombinator.Parser<Object> _symb_12 = ParserCombinator.symb("b");
      ParserCombinator.Parser<Object> _or_4 = _symb_11.or(_symb_12);
      ParserCombinator.Parser<Object> _mult_1 = _or_4.mult(0, (-1));
      ParserCombinator.Parser<Object> _symb_13 = ParserCombinator.symb("c");
      ParserCombinator.Parser<Object> _then_1 = _mult_1.then(_symb_13);
      _xblockexpression = ParserCombinator.testParser(_then_1, "abc");
    }
    return _xblockexpression;
  }
  
  public static Object testABCDExpression() {
    Object _xblockexpression = null;
    {
      Object _parser = ABCDExpressionLanguage.getParser();
      final ParserCombinator.Parser<?> parser = ((ParserCombinator.Parser<?>) _parser);
      ParserCombinator.testParser(parser, "true");
      ParserCombinator.testParser(parser, "a");
      ParserCombinator.testParser(parser, "not a");
      ParserCombinator.testParser(parser, "true and false");
      ParserCombinator.testParser(parser, "true and a");
      ParserCombinator.testParser(parser, "a and a");
      ParserCombinator.testParser(parser, "a or b");
      ParserCombinator.testParser(parser, "not (a or b)");
      try {
        ParserCombinator.testParser(parser, "not (a or and b)");
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception e = (Exception)_t;
          e.printStackTrace(System.out);
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      ParserCombinator.testParser(parser, "trueanda");
      ParserCombinator.testParser(parser, "nota");
      try {
        ParserCombinator.testParser(parser, "a andb");
      } catch (final Throwable _t_1) {
        if (_t_1 instanceof Exception) {
          final Exception e_1 = (Exception)_t_1;
          e_1.printStackTrace(System.out);
        } else {
          throw Exceptions.sneakyThrow(_t_1);
        }
      }
      Object _xtrycatchfinallyexpression = null;
      try {
        _xtrycatchfinallyexpression = ParserCombinator.testParser(parser, "a orb");
      } catch (final Throwable _t_2) {
        if (_t_2 instanceof Exception) {
          final Exception e_2 = (Exception)_t_2;
          e_2.printStackTrace(System.out);
        } else {
          throw Exceptions.sneakyThrow(_t_2);
        }
      }
      _xblockexpression = _xtrycatchfinallyexpression;
    }
    return _xblockexpression;
  }
  
  public static Object testParser(final ParserCombinator.Parser<?> parser, final String program) {
    Object _xblockexpression = null;
    {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Parsing: ");
      _builder.append(program, "");
      InputOutput.<String>println(_builder.toString());
      final Object parsed = parser.parse(program);
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("\t");
      _builder_1.append("Result: ");
      _builder_1.append(parsed, "\t");
      InputOutput.<String>println(_builder_1.toString());
      _xblockexpression = parsed;
    }
    return _xblockexpression;
  }
  
  public static ParserCombinator.Parser<Object> symb(final String s) {
    return new ParserCombinator.SymbolParser<Object>(s, false);
  }
  
  public static ParserCombinator.Parser<Object> isymb(final String s) {
    return new ParserCombinator.SymbolParser<Object>(s, true);
  }
  
  public static ParserCombinator.Parser<Object> terminal(final String regex) {
    return new ParserCombinator.TerminalParser<Object>(regex);
  }
  
  private final static ParserCombinator.List<Object> nil_ = ((ParserCombinator.List<Object>) new ParserCombinator.Nil<Object>());
  
  public static <T extends Object> ParserCombinator.List<T> nil() {
    return ((ParserCombinator.List<T>) ((ParserCombinator.List<?>) ParserCombinator.nil_));
  }
}

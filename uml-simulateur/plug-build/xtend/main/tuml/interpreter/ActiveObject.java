package tuml.interpreter;

import abcd.expression.parser.ABCDExpressionParser;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.OpaqueBehavior;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.Signal;
import org.eclipse.uml2.uml.SignalEvent;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.ValueSpecification;
import org.eclipse.uml2.uml.Vertex;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.MapExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Pure;
import tuml.interpreter.Environment;
import tuml.interpreter.IAbstractVisitor;

@SuppressWarnings("all")
public class ActiveObject {
  private final Environment env;
  
  private final org.eclipse.uml2.uml.Class type;
  
  private final String name;
  
  private final List<Vertex> states;
  
  private final Map<Vertex, Integer> state2int;
  
  private final Map<Integer, Vertex> int2state;
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private Queue<Signal> eventPool = new ArrayDeque<Signal>();
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private State currentState;
  
  public static int maxEventPoolSize = 10;
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private final HashMap<String, ActiveObject> peers = new HashMap<String, ActiveObject>();
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private final HashMap<String, Object> localVars = new HashMap<String, Object>();
  
  private final HashSet<Signal> vocabulary = new HashSet<Signal>();
  
  public ActiveObject(final Environment env, final org.eclipse.uml2.uml.Class type, final String name) {
    this.env = env;
    this.type = type;
    this.name = name;
    Behavior _classifierBehavior = type.getClassifierBehavior();
    EList<Region> _regions = ((StateMachine) _classifierBehavior).getRegions();
    final Region region = _regions.get(0);
    EList<Vertex> _subvertices = region.getSubvertices();
    this.states = _subvertices;
    HashMap<Integer, Vertex> _hashMap = new HashMap<Integer, Vertex>();
    this.int2state = _hashMap;
    Iterable<Pair<Integer, Vertex>> _indexed = IterableExtensions.<Vertex>indexed(this.states);
    HashMap<Vertex, Integer> _newHashMap = CollectionLiterals.<Vertex, Integer>newHashMap();
    final Function2<HashMap<Vertex, Integer>, Pair<Integer, Vertex>, HashMap<Vertex, Integer>> _function = new Function2<HashMap<Vertex, Integer>, Pair<Integer, Vertex>, HashMap<Vertex, Integer>>() {
      @Override
      public HashMap<Vertex, Integer> apply(final HashMap<Vertex, Integer> map, final Pair<Integer, Vertex> pair) {
        HashMap<Vertex, Integer> _xblockexpression = null;
        {
          Vertex _value = pair.getValue();
          Integer _key = pair.getKey();
          map.put(_value, _key);
          Integer _key_1 = pair.getKey();
          Vertex _value_1 = pair.getValue();
          ActiveObject.this.int2state.put(_key_1, _value_1);
          _xblockexpression = map;
        }
        return _xblockexpression;
      }
    };
    HashMap<Vertex, Integer> _fold = IterableExtensions.<Pair<Integer, Vertex>, HashMap<Vertex, Integer>>fold(_indexed, _newHashMap, _function);
    this.state2int = _fold;
    EList<Vertex> _subvertices_1 = region.getSubvertices();
    final Function1<Vertex, Boolean> _function_1 = new Function1<Vertex, Boolean>() {
      @Override
      public Boolean apply(final Vertex v) {
        boolean _xifexpression = false;
        if ((v instanceof Pseudostate)) {
          PseudostateKind _kind = ((Pseudostate)v).getKind();
          int _value = _kind.getValue();
          _xifexpression = (_value == PseudostateKind.INITIAL);
        } else {
          _xifexpression = false;
        }
        return Boolean.valueOf(_xifexpression);
      }
    };
    Vertex _findFirst = IterableExtensions.<Vertex>findFirst(_subvertices_1, _function_1);
    final EList<Transition> initialTransitions = _findFirst.getOutgoings();
    int _size = initialTransitions.size();
    boolean _notEquals = (_size != 1);
    if (_notEquals) {
      throw new RuntimeException();
    }
    Transition _get = initialTransitions.get(0);
    Vertex target = _get.getTarget();
    this.currentState = ((State) target);
    if (env.outputPlantUMLStates) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("hnote over ");
      _builder.append(name, "");
      _builder.append(" : ");
      String _name = this.currentState.getName();
      _builder.append(_name, "");
      InputOutput.<String>println(_builder.toString());
    }
    EList<Property> _attributes = type.getAttributes();
    final Function1<Property, Boolean> _function_2 = new Function1<Property, Boolean>() {
      @Override
      public Boolean apply(final Property it) {
        VisibilityKind _visibility = it.getVisibility();
        int _value = _visibility.getValue();
        int _intValue = Integer.valueOf(VisibilityKind.PRIVATE).intValue();
        return Boolean.valueOf((_value == _intValue));
      }
    };
    Iterable<Property> _filter = IterableExtensions.<Property>filter(_attributes, _function_2);
    final Consumer<Property> _function_3 = new Consumer<Property>() {
      @Override
      public void accept(final Property at) {
        String _name = at.getName();
        ActiveObject.this.localVars.put(_name, Boolean.valueOf(false));
      }
    };
    _filter.forEach(_function_3);
    Behavior _classifierBehavior_1 = type.getClassifierBehavior();
    EList<Region> _regions_1 = ((StateMachine) _classifierBehavior_1).getRegions();
    Region _get_1 = _regions_1.get(0);
    EList<Transition> _transitions = _get_1.getTransitions();
    final Function2<HashSet<Signal>, Transition, HashSet<Signal>> _function_4 = new Function2<HashSet<Signal>, Transition, HashSet<Signal>>() {
      @Override
      public HashSet<Signal> apply(final HashSet<Signal> vocab, final Transition t) {
        HashSet<Signal> _xblockexpression = null;
        {
          EList<Trigger> _triggers = t.getTriggers();
          final Function1<Trigger, Event> _function = new Function1<Trigger, Event>() {
            @Override
            public Event apply(final Trigger it) {
              return it.getEvent();
            }
          };
          List<Event> _map = ListExtensions.<Trigger, Event>map(_triggers, _function);
          Iterable<SignalEvent> _filter = Iterables.<SignalEvent>filter(_map, SignalEvent.class);
          final Function1<SignalEvent, Signal> _function_1 = new Function1<SignalEvent, Signal>() {
            @Override
            public Signal apply(final SignalEvent it) {
              return it.getSignal();
            }
          };
          Iterable<Signal> _map_1 = IterableExtensions.<SignalEvent, Signal>map(_filter, _function_1);
          final Set<Signal> signalz = IterableExtensions.<Signal>toSet(_map_1);
          vocab.addAll(signalz);
          _xblockexpression = vocab;
        }
        return _xblockexpression;
      }
    };
    IterableExtensions.<Transition, HashSet<Signal>>fold(_transitions, this.vocabulary, _function_4);
  }
  
  public ActiveObject addPeer(final String name, final ActiveObject peer) {
    ActiveObject _xblockexpression = null;
    {
      EList<Property> _allAttributes = this.type.allAttributes();
      final Function1<Property, Boolean> _function = new Function1<Property, Boolean>() {
        @Override
        public Boolean apply(final Property a) {
          String _name = a.getName();
          return Boolean.valueOf(Objects.equal(_name, name));
        }
      };
      boolean _exists = IterableExtensions.<Property>exists(_allAttributes, _function);
      boolean _not = (!_exists);
      if (_not) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("Class ");
        String _name = this.type.getName();
        _builder.append(_name, "");
        _builder.append(" does not have an attribute named ");
        _builder.append(name, "");
        throw new RuntimeException(_builder.toString());
      }
      _xblockexpression = this.peers.put(name, peer);
    }
    return _xblockexpression;
  }
  
  public String getName() {
    return this.name;
  }
  
  public boolean queue(final Signal s) {
    return this.eventPool.add(s);
  }
  
  private final UMLFactory umlFactory = UMLFactory.eINSTANCE;
  
  public Transition createIgnoringTransition(final Set<Signal> ignoredTriggers) {
    Transition _xblockexpression = null;
    {
      final Transition ignoringTransition = this.umlFactory.createTransition();
      final Function1<Signal, Trigger> _function = new Function1<Signal, Trigger>() {
        @Override
        public Trigger apply(final Signal signal) {
          Trigger _xblockexpression = null;
          {
            final SignalEvent sE = ActiveObject.this.umlFactory.createSignalEvent();
            sE.setSignal(signal);
            final Trigger trig = ActiveObject.this.umlFactory.createTrigger();
            trig.setEvent(sE);
            _xblockexpression = trig;
          }
          return _xblockexpression;
        }
      };
      final Iterable<Trigger> triggers = IterableExtensions.<Signal, Trigger>map(ignoredTriggers, _function);
      EList<Trigger> _triggers = ignoringTransition.getTriggers();
      Iterables.<Trigger>addAll(_triggers, triggers);
      ignoringTransition.setSource(this.currentState);
      ignoringTransition.setTarget(this.currentState);
      _xblockexpression = ignoringTransition;
    }
    return _xblockexpression;
  }
  
  public Set<Pair<ActiveObject, Transition>> fireableTransitions() {
    Set<Pair<ActiveObject, Transition>> _xblockexpression = null;
    {
      Object _clone = this.vocabulary.clone();
      final Set<Signal> ignoredSignals = ((Set<Signal>) _clone);
      EList<Transition> _outgoings = this.currentState.getOutgoings();
      final Function1<Transition, Boolean> _function = new Function1<Transition, Boolean>() {
        @Override
        public Boolean apply(final Transition t) {
          EList<Trigger> _triggers = t.getTriggers();
          boolean _isEmpty = _triggers.isEmpty();
          if (_isEmpty) {
            String _guard = ActiveObject.this.guard(t);
            return Boolean.valueOf(ActiveObject.this.eval(_guard));
          }
          EList<Trigger> _triggers_1 = t.getTriggers();
          final Function1<Trigger, Event> _function = new Function1<Trigger, Event>() {
            @Override
            public Event apply(final Trigger it) {
              return it.getEvent();
            }
          };
          List<Event> _map = ListExtensions.<Trigger, Event>map(_triggers_1, _function);
          Iterable<SignalEvent> _filter = Iterables.<SignalEvent>filter(_map, SignalEvent.class);
          final Function1<SignalEvent, Signal> _function_1 = new Function1<SignalEvent, Signal>() {
            @Override
            public Signal apply(final SignalEvent it) {
              return it.getSignal();
            }
          };
          Iterable<Signal> _map_1 = IterableExtensions.<SignalEvent, Signal>map(_filter, _function_1);
          final Set<Signal> signals = IterableExtensions.<Signal>toSet(_map_1);
          ignoredSignals.removeAll(signals);
          String _guard_1 = ActiveObject.this.guard(t);
          boolean _eval = ActiveObject.this.eval(_guard_1);
          boolean _not = (!_eval);
          if (_not) {
            return Boolean.valueOf(false);
          }
          boolean _isEmpty_1 = signals.isEmpty();
          if (_isEmpty_1) {
            return Boolean.valueOf(true);
          }
          boolean _isEmpty_2 = ActiveObject.this.eventPool.isEmpty();
          if (_isEmpty_2) {
            return Boolean.valueOf(false);
          }
          final Signal availableSignal = ActiveObject.this.eventPool.peek();
          boolean _and = false;
          boolean _isEmpty_3 = ActiveObject.this.eventPool.isEmpty();
          boolean _not_1 = (!_isEmpty_3);
          if (!_not_1) {
            _and = false;
          } else {
            boolean _contains = signals.contains(availableSignal);
            _and = _contains;
          }
          return Boolean.valueOf(_and);
        }
      };
      Iterable<Transition> _filter = IterableExtensions.<Transition>filter(_outgoings, _function);
      final Function1<Transition, Pair<ActiveObject, Transition>> _function_1 = new Function1<Transition, Pair<ActiveObject, Transition>>() {
        @Override
        public Pair<ActiveObject, Transition> apply(final Transition it) {
          return Pair.<ActiveObject, Transition>of(ActiveObject.this, it);
        }
      };
      Iterable<Pair<ActiveObject, Transition>> _map = IterableExtensions.<Transition, Pair<ActiveObject, Transition>>map(_filter, _function_1);
      final Set<Pair<ActiveObject, Transition>> res = IterableExtensions.<Pair<ActiveObject, Transition>>toSet(_map);
      boolean _and = false;
      boolean _isEmpty = this.eventPool.isEmpty();
      boolean _not = (!_isEmpty);
      if (!_not) {
        _and = false;
      } else {
        boolean _isEmpty_1 = ignoredSignals.isEmpty();
        boolean _not_1 = (!_isEmpty_1);
        _and = _not_1;
      }
      if (_and) {
        Transition _createIgnoringTransition = this.createIgnoringTransition(ignoredSignals);
        Pair<ActiveObject, Transition> _mappedTo = Pair.<ActiveObject, Transition>of(this, _createIgnoringTransition);
        res.add(_mappedTo);
      }
      _xblockexpression = res;
    }
    return _xblockexpression;
  }
  
  private final Pattern sendPattern = Pattern.compile("^send (\\S+) to (\\S+)$");
  
  private final String identRegex = "([A-Za-z_]\\w*)";
  
  private final Pattern identPattern = new Function0<Pattern>() {
    public Pattern apply() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("^");
      _builder.append(ActiveObject.this.identRegex, "");
      _builder.append("$");
      Pattern _compile = Pattern.compile(_builder.toString());
      return _compile;
    }
  }.apply();
  
  private final Pattern assignPattern = new Function0<Pattern>() {
    public Pattern apply() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("^");
      _builder.append(ActiveObject.this.identRegex, "");
      _builder.append(" := (.*)$");
      Pattern _compile = Pattern.compile(_builder.toString());
      return _compile;
    }
  }.apply();
  
  public void executeEffect(final Transition t) {
    Behavior _effect = t.getEffect();
    boolean _notEquals = (!Objects.equal(_effect, null));
    if (_notEquals) {
      Behavior _effect_1 = t.getEffect();
      EList<String> _bodies = ((OpaqueBehavior) _effect_1).getBodies();
      final String body = _bodies.get(0);
      String[] _split = body.split(";");
      final Function1<String, Matcher> _function = new Function1<String, Matcher>() {
        @Override
        public Matcher apply(final String stmt) {
          String _trim = stmt.trim();
          return ActiveObject.this.sendPattern.matcher(_trim);
        }
      };
      List<Matcher> _map = ListExtensions.<String, Matcher>map(((List<String>)Conversions.doWrapArray(_split)), _function);
      final Function1<Matcher, Boolean> _function_1 = new Function1<Matcher, Boolean>() {
        @Override
        public Boolean apply(final Matcher it) {
          return Boolean.valueOf(it.matches());
        }
      };
      Iterable<Matcher> _filter = IterableExtensions.<Matcher>filter(_map, _function_1);
      final Function1<Matcher, Pair<String, String>> _function_2 = new Function1<Matcher, Pair<String, String>>() {
        @Override
        public Pair<String, String> apply(final Matcher matcher) {
          String _group = matcher.group(1);
          String _group_1 = matcher.group(2);
          return Pair.<String, String>of(_group, _group_1);
        }
      };
      final Iterable<Pair<String, String>> sends = IterableExtensions.<Matcher, Pair<String, String>>map(_filter, _function_2);
      if (this.env.outputPlantUML) {
        final Consumer<Pair<String, String>> _function_3 = new Consumer<Pair<String, String>>() {
          @Override
          public void accept(final Pair<String, String> it) {
            StringConcatenation _builder = new StringConcatenation();
            _builder.append(ActiveObject.this.name, "");
            _builder.append(" -> ");
            String _value = it.getValue();
            _builder.append(_value, "");
            _builder.append(" : ");
            String _key = it.getKey();
            _builder.append(_key, "");
            InputOutput.<String>println(_builder.toString());
          }
        };
        sends.forEach(_function_3);
      }
      final Consumer<Pair<String, String>> _function_4 = new Consumer<Pair<String, String>>() {
        @Override
        public void accept(final Pair<String, String> toSend) {
          String _value = toSend.getValue();
          ActiveObject _get = ActiveObject.this.peers.get(_value);
          final Queue<Signal> eventPool = _get.eventPool;
          boolean _and = false;
          if (!(ActiveObject.maxEventPoolSize > 0)) {
            _and = false;
          } else {
            int _size = eventPool.size();
            boolean _greaterThan = (_size > ActiveObject.maxEventPoolSize);
            _and = _greaterThan;
          }
          if (_and) {
            throw new RuntimeException("EventPool size exceeded");
          }
          String _value_1 = toSend.getValue();
          ActiveObject _get_1 = ActiveObject.this.peers.get(_value_1);
          String _key = toSend.getKey();
          Signal _signal = ActiveObject.this.env.getSignal(_key);
          _get_1.queue(_signal);
        }
      };
      sends.forEach(_function_4);
      String[] _split_1 = body.split(";");
      final Function1<String, Matcher> _function_5 = new Function1<String, Matcher>() {
        @Override
        public Matcher apply(final String stmt) {
          String _trim = stmt.trim();
          return ActiveObject.this.assignPattern.matcher(_trim);
        }
      };
      List<Matcher> _map_1 = ListExtensions.<String, Matcher>map(((List<String>)Conversions.doWrapArray(_split_1)), _function_5);
      final Function1<Matcher, Boolean> _function_6 = new Function1<Matcher, Boolean>() {
        @Override
        public Boolean apply(final Matcher it) {
          return Boolean.valueOf(it.matches());
        }
      };
      Iterable<Matcher> _filter_1 = IterableExtensions.<Matcher>filter(_map_1, _function_6);
      final Consumer<Matcher> _function_7 = new Consumer<Matcher>() {
        @Override
        public void accept(final Matcher matcher) {
          String _group = matcher.group(1);
          String _group_1 = matcher.group(2);
          boolean _eval = ActiveObject.this.eval(_group_1);
          ActiveObject.this.localVars.put(_group, Boolean.valueOf(_eval));
        }
      };
      _filter_1.forEach(_function_7);
    }
  }
  
  public String guard(final Transition t) {
    String _xblockexpression = null;
    {
      Constraint _guard = t.getGuard();
      boolean _equals = Objects.equal(_guard, null);
      if (_equals) {
        return "true";
      }
      Constraint _guard_1 = t.getGuard();
      ValueSpecification _specification = _guard_1.getSpecification();
      EList<String> _bodies = ((OpaqueExpression) _specification).getBodies();
      _xblockexpression = _bodies.get(0);
    }
    return _xblockexpression;
  }
  
  public String condition(final Transition t) {
    String _xblockexpression = null;
    {
      EList<Constraint> _ownedRules = t.getOwnedRules();
      int _size = _ownedRules.size();
      boolean _notEquals = (_size != 1);
      if (_notEquals) {
        throw new RuntimeException();
      }
      EList<Constraint> _ownedRules_1 = t.getOwnedRules();
      Constraint _get = _ownedRules_1.get(0);
      final ValueSpecification spec = _get.getSpecification();
      String _switchResult = null;
      boolean _matched = false;
      if (!_matched) {
        if (spec instanceof OpaqueExpression) {
          _matched=true;
          EList<String> _bodies = ((OpaqueExpression)spec).getBodies();
          _switchResult = _bodies.get(0);
        }
      }
      if (!_matched) {
        throw new RuntimeException();
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
  
  private final ABCDExpressionParser abcd = new ABCDExpressionParser();
  
  public boolean eval(final String condition) {
    Object _eval = this.abcd.<Object>eval(condition, this.localVars);
    final Boolean evaled = ((Boolean) _eval);
    return (((Boolean) evaled)).booleanValue();
  }
  
  public String fire(final Transition t) {
    String _xblockexpression = null;
    {
      boolean _and = false;
      boolean _isEmpty = this.eventPool.isEmpty();
      boolean _not = (!_isEmpty);
      if (!_not) {
        _and = false;
      } else {
        EList<Trigger> _triggers = t.getTriggers();
        boolean _isEmpty_1 = _triggers.isEmpty();
        boolean _not_1 = (!_isEmpty_1);
        _and = _not_1;
      }
      if (_and) {
        final Signal s = this.eventPool.remove();
      }
      this.executeEffect(t);
      Vertex target = t.getTarget();
      while ((target instanceof Pseudostate)) {
        PseudostateKind _kind = ((Pseudostate)target).getKind();
        int _value = _kind.getValue();
        switch (_value) {
          case PseudostateKind.CHOICE:
            Transition tr = ((Transition) null);
            EList<Transition> _outgoings = ((Pseudostate)target).getOutgoings();
            final Function1<Transition, Boolean> _function = new Function1<Transition, Boolean>() {
              @Override
              public Boolean apply(final Transition it) {
                String _condition = ActiveObject.this.condition(it);
                return Boolean.valueOf((!Objects.equal(_condition, "else")));
              }
            };
            Iterable<Transition> _filter = IterableExtensions.<Transition>filter(_outgoings, _function);
            for (final Transition e : _filter) {
              boolean _and_1 = false;
              boolean _equals = Objects.equal(tr, null);
              if (!_equals) {
                _and_1 = false;
              } else {
                String _condition = this.condition(e);
                boolean _eval = this.eval(_condition);
                _and_1 = _eval;
              }
              if (_and_1) {
                tr = e;
              }
            }
            boolean _equals_1 = Objects.equal(tr, null);
            if (_equals_1) {
              EList<Transition> _outgoings_1 = ((Pseudostate)target).getOutgoings();
              final Function1<Transition, Boolean> _function_1 = new Function1<Transition, Boolean>() {
                @Override
                public Boolean apply(final Transition it) {
                  String _condition = ActiveObject.this.condition(it);
                  return Boolean.valueOf(Objects.equal(_condition, "else"));
                }
              };
              Transition _findFirst = IterableExtensions.<Transition>findFirst(_outgoings_1, _function_1);
              tr = _findFirst;
            }
            this.executeEffect(tr);
            Vertex _target = tr.getTarget();
            target = _target;
            break;
          default:
            StringConcatenation _builder = new StringConcatenation();
            _builder.append("Unsupported PseudostateKind: ");
            _builder.append(target, "");
            throw new RuntimeException(_builder.toString());
        }
      }
      this.currentState = ((State) target);
      String _xifexpression = null;
      if (this.env.outputPlantUMLStates) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("hnote over ");
        _builder.append(this.name, "");
        _builder.append(" : ");
        String _name = this.currentState.getName();
        _builder.append(_name, "");
        _xifexpression = InputOutput.<String>println(_builder.toString());
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public CharSequence toShortString() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(this.name, "");
    _builder.append(":");
    String _name = this.type.getName();
    _builder.append(_name, "");
    return _builder;
  }
  
  @Override
  public String toString() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(this.name, "");
    _builder.append(":");
    String _name = this.type.getName();
    _builder.append(_name, "");
    _builder.append("(currentState = ");
    String _name_1 = this.currentState.getName();
    _builder.append(_name_1, "");
    _builder.append(", eventPool = ");
    final Function1<Signal, String> _function = new Function1<Signal, String>() {
      @Override
      public String apply(final Signal it) {
        return it.getName();
      }
    };
    Iterable<String> _map = IterableExtensions.<Signal, String>map(this.eventPool, _function);
    _builder.append(_map, "");
    _builder.append(", peers = ");
    final Function1<ActiveObject, String> _function_1 = new Function1<ActiveObject, String>() {
      @Override
      public String apply(final ActiveObject it) {
        return it.name;
      }
    };
    Map<String, String> _mapValues = MapExtensions.<String, ActiveObject, String>mapValues(this.peers, _function_1);
    _builder.append(_mapValues, "");
    _builder.append(")");
    return _builder.toString();
  }
  
  public int getIntBufferSize() {
    int _size = this.eventPool.size();
    int _plus = ((1 + 1) + _size);
    int _size_1 = this.localVars.size();
    return (_plus + _size_1);
  }
  
  public int getBufferSize() {
    int _intBufferSize = this.getIntBufferSize();
    return (_intBufferSize * 4);
  }
  
  public void encode(final ByteBuffer buffer) {
    if ((this.currentState instanceof Pseudostate)) {
      throw new RuntimeException();
    }
    Integer _get = this.state2int.get(this.currentState);
    buffer.putInt((_get).intValue());
    int _size = this.eventPool.size();
    buffer.putInt(_size);
    final Consumer<Signal> _function = new Consumer<Signal>() {
      @Override
      public void accept(final Signal t) {
        Map<Signal, Integer> _signal2id = ActiveObject.this.env.signal2id();
        Integer _get = _signal2id.get(t);
        buffer.putInt((_get).intValue());
      }
    };
    this.eventPool.forEach(_function);
    final BiConsumer<String, Object> _function_1 = new BiConsumer<String, Object>() {
      @Override
      public void accept(final String k, final Object v) {
        if ((v instanceof Boolean)) {
          boolean _booleanValue = ((Boolean) v).booleanValue();
          if (_booleanValue) {
            buffer.put(((byte) 1));
          } else {
            buffer.put(((byte) 0));
          }
        } else {
          if ((v instanceof Integer)) {
            final int iv = ((Integer) v).intValue();
            buffer.putInt(iv);
          } else {
            throw new RuntimeException();
          }
        }
      }
    };
    this.localVars.forEach(_function_1);
  }
  
  public void decode(final ByteBuffer buffer) {
    final int stateID = buffer.getInt();
    Vertex _get = this.int2state.get(Integer.valueOf(stateID));
    if ((_get instanceof Pseudostate)) {
      throw new RuntimeException();
    } else {
      Vertex _get_1 = this.int2state.get(Integer.valueOf(stateID));
      this.currentState = ((State) _get_1);
    }
    final int epSize = buffer.getInt();
    ArrayDeque<Signal> _arrayDeque = new ArrayDeque<Signal>(epSize);
    this.eventPool = _arrayDeque;
    for (int i = 0; (i < epSize); i++) {
      List<Signal> _signals = this.env.signals();
      int _int = buffer.getInt();
      Signal _get_2 = _signals.get(_int);
      this.eventPool.add(_get_2);
    }
    final BiConsumer<String, Object> _function = new BiConsumer<String, Object>() {
      @Override
      public void accept(final String k, final Object v) {
        if ((v instanceof Boolean)) {
          final byte bv = buffer.get();
          if ((bv == 1)) {
            ActiveObject.this.localVars.put(k, Boolean.valueOf(true));
          } else {
            ActiveObject.this.localVars.put(k, Boolean.valueOf(false));
          }
        } else {
          if ((v instanceof Integer)) {
            final int iv = buffer.getInt();
            ActiveObject.this.localVars.put(k, Integer.valueOf(iv));
          } else {
            throw new RuntimeException();
          }
        }
      }
    };
    this.localVars.forEach(_function);
  }
  
  public <T extends Object> T accept(final IAbstractVisitor<T> visitor) {
    return visitor.visitActiveObject(this);
  }
  
  @Pure
  public Queue<Signal> getEventPool() {
    return this.eventPool;
  }
  
  @Pure
  public State getCurrentState() {
    return this.currentState;
  }
  
  @Pure
  public HashMap<String, ActiveObject> getPeers() {
    return this.peers;
  }
  
  @Pure
  public HashMap<String, Object> getLocalVars() {
    return this.localVars;
  }
}

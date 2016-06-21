package tuml.interpreter;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.ConnectableElement;
import org.eclipse.uml2.uml.Connector;
import org.eclipse.uml2.uml.ConnectorEnd;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Type;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Pure;
import tuml.interpreter.ActiveObject;
import tuml.interpreter.Environment;
import tuml.interpreter.IAbstractVisitor;

@SuppressWarnings("all")
public class Composite {
  private final Environment env;
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private final org.eclipse.uml2.uml.Class c;
  
  private final HashMap<String, ActiveObject> parts = new HashMap<String, ActiveObject>();
  
  public Composite(final Environment env, final org.eclipse.uml2.uml.Class c) {
    this.env = env;
    this.c = c;
    EList<Property> _ownedAttributes = c.getOwnedAttributes();
    final Function1<Property, Boolean> _function = new Function1<Property, Boolean>() {
      @Override
      public Boolean apply(final Property it) {
        return Boolean.valueOf(Composite.isProcessInstance(it));
      }
    };
    Iterable<Property> _filter = IterableExtensions.<Property>filter(_ownedAttributes, _function);
    final Consumer<Property> _function_1 = new Consumer<Property>() {
      @Override
      public void accept(final Property part) {
        String _name = part.getName();
        Type _type = part.getType();
        String _name_1 = part.getName();
        ActiveObject _activeObject = new ActiveObject(env, ((org.eclipse.uml2.uml.Class) _type), _name_1);
        Composite.this.parts.put(_name, _activeObject);
      }
    };
    _filter.forEach(_function_1);
    EList<Connector> _ownedConnectors = c.getOwnedConnectors();
    final Consumer<Connector> _function_2 = new Consumer<Connector>() {
      @Override
      public void accept(final Connector con) {
        EList<ConnectorEnd> _ends = con.getEnds();
        final ConnectorEnd end1 = _ends.get(0);
        ConnectableElement _role = end1.getRole();
        String _name = _role.getName();
        final ActiveObject ao1 = Composite.this.parts.get(_name);
        EList<ConnectorEnd> _ends_1 = con.getEnds();
        final ConnectorEnd end2 = _ends_1.get(1);
        ConnectableElement _role_1 = end2.getRole();
        String _name_1 = _role_1.getName();
        final ActiveObject ao2 = Composite.this.parts.get(_name_1);
        Property _definingEnd = end2.getDefiningEnd();
        org.eclipse.uml2.uml.Class _class_ = _definingEnd.getClass_();
        boolean _notEquals = (!Objects.equal(_class_, null));
        if (_notEquals) {
          Property _definingEnd_1 = end2.getDefiningEnd();
          String _name_2 = _definingEnd_1.getName();
          ao1.addPeer(_name_2, ao2);
        }
        Property _definingEnd_2 = end1.getDefiningEnd();
        org.eclipse.uml2.uml.Class _class__1 = _definingEnd_2.getClass_();
        boolean _notEquals_1 = (!Objects.equal(_class__1, null));
        if (_notEquals_1) {
          Property _definingEnd_3 = end1.getDefiningEnd();
          String _name_3 = _definingEnd_3.getName();
          ao2.addPeer(_name_3, ao1);
        }
      }
    };
    _ownedConnectors.forEach(_function_2);
  }
  
  public Environment getEnvironment() {
    return this.env;
  }
  
  public List<Pair<ActiveObject, Transition>> fireableTransitions() {
    Collection<ActiveObject> _values = this.parts.values();
    final Function1<ActiveObject, Set<Pair<ActiveObject, Transition>>> _function = new Function1<ActiveObject, Set<Pair<ActiveObject, Transition>>>() {
      @Override
      public Set<Pair<ActiveObject, Transition>> apply(final ActiveObject it) {
        return it.fireableTransitions();
      }
    };
    Iterable<Set<Pair<ActiveObject, Transition>>> _map = IterableExtensions.<ActiveObject, Set<Pair<ActiveObject, Transition>>>map(_values, _function);
    Iterable<Pair<ActiveObject, Transition>> _flatten = Iterables.<Pair<ActiveObject, Transition>>concat(_map);
    return IterableExtensions.<Pair<ActiveObject, Transition>>toList(_flatten);
  }
  
  public ActiveObject getPart(final String partName) {
    return this.parts.get(partName);
  }
  
  public Collection<ActiveObject> getParts() {
    return this.parts.values();
  }
  
  @Override
  public String toString() {
    Collection<ActiveObject> _values = this.parts.values();
    final Function1<ActiveObject, String> _function = new Function1<ActiveObject, String>() {
      @Override
      public String apply(final ActiveObject it) {
        return it.toString();
      }
    };
    Iterable<String> _map = IterableExtensions.<ActiveObject, String>map(_values, _function);
    final Function2<String, String, String> _function_1 = new Function2<String, String, String>() {
      @Override
      public String apply(final String acc, final String e) {
        return ((acc + e) + "\n");
      }
    };
    return IterableExtensions.<String, String>fold(_map, "", _function_1);
  }
  
  public static boolean isProcess(final Type c) {
    boolean _xifexpression = false;
    if ((c instanceof org.eclipse.uml2.uml.Class)) {
      _xifexpression = ((org.eclipse.uml2.uml.Class)c).isActive();
    } else {
      _xifexpression = false;
    }
    return _xifexpression;
  }
  
  public static boolean isComponent(final Element e) {
    boolean _xifexpression = false;
    if ((e instanceof org.eclipse.uml2.uml.Class)) {
      EList<Connector> _ownedConnectors = ((org.eclipse.uml2.uml.Class)e).getOwnedConnectors();
      boolean _isEmpty = _ownedConnectors.isEmpty();
      _xifexpression = (!_isEmpty);
    } else {
      _xifexpression = false;
    }
    return _xifexpression;
  }
  
  public static boolean isProcessInstance(final Property p) {
    boolean _and = false;
    boolean _and_1 = false;
    Type _type = p.getType();
    boolean _notEquals = (!Objects.equal(_type, null));
    if (!_notEquals) {
      _and_1 = false;
    } else {
      Type _type_1 = p.getType();
      boolean _isProcess = Composite.isProcess(_type_1);
      _and_1 = _isProcess;
    }
    if (!_and_1) {
      _and = false;
    } else {
      Element _owner = p.getOwner();
      boolean _isComponent = Composite.isComponent(_owner);
      _and = _isComponent;
    }
    return _and;
  }
  
  public Integer getBufferSize() {
    Collection<ActiveObject> _values = this.parts.values();
    final Function2<Integer, ActiveObject, Integer> _function = new Function2<Integer, ActiveObject, Integer>() {
      @Override
      public Integer apply(final Integer size, final ActiveObject ao) {
        int _bufferSize = ao.getBufferSize();
        return Integer.valueOf(((size).intValue() + _bufferSize));
      }
    };
    return IterableExtensions.<ActiveObject, Integer>fold(_values, Integer.valueOf(0), _function);
  }
  
  public Buffer encode(final ByteBuffer buffer) {
    Buffer _xblockexpression = null;
    {
      Collection<ActiveObject> _values = this.parts.values();
      final Consumer<ActiveObject> _function = new Consumer<ActiveObject>() {
        @Override
        public void accept(final ActiveObject ao) {
          ao.encode(buffer);
        }
      };
      _values.forEach(_function);
      _xblockexpression = buffer.flip();
    }
    return _xblockexpression;
  }
  
  public Buffer decode(final ByteBuffer buffer) {
    Buffer _xblockexpression = null;
    {
      Collection<ActiveObject> _values = this.parts.values();
      final Consumer<ActiveObject> _function = new Consumer<ActiveObject>() {
        @Override
        public void accept(final ActiveObject ao) {
          ao.decode(buffer);
        }
      };
      _values.forEach(_function);
      _xblockexpression = buffer.flip();
    }
    return _xblockexpression;
  }
  
  public ByteBuffer getCurrentState() {
    Integer _bufferSize = this.getBufferSize();
    ByteBuffer buffer = ByteBuffer.allocate((_bufferSize).intValue());
    this.encode(buffer);
    return buffer;
  }
  
  public <T extends Object> T accept(final IAbstractVisitor<T> visitor) {
    return visitor.visitComposite(this);
  }
  
  @Pure
  public org.eclipse.uml2.uml.Class getC() {
    return this.c;
  }
}

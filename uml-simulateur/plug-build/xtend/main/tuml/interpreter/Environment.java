package tuml.interpreter;

import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Signal;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Data
@SuppressWarnings("all")
public class Environment {
  public final boolean outputPlantUML = false;
  
  public final boolean outputPlantUMLStates = (this.outputPlantUML && false);
  
  private final Resource r;
  
  private final List<Signal> signalsCache = new ArrayList<Signal>();
  
  private final Map<Signal, Integer> signal2idCache = new HashMap<Signal, Integer>();
  
  public List<Signal> signals() {
    List<Signal> _xblockexpression = null;
    {
      int _size = this.signalsCache.size();
      boolean _equals = (_size == 0);
      if (_equals) {
        TreeIterator<EObject> _allContents = this.r.getAllContents();
        Iterator<Signal> _filter = Iterators.<Signal>filter(_allContents, Signal.class);
        List<Signal> _list = IteratorExtensions.<Signal>toList(_filter);
        this.signalsCache.addAll(_list);
      }
      _xblockexpression = this.signalsCache;
    }
    return _xblockexpression;
  }
  
  public Signal getSignal(final String name) {
    List<Signal> _signals = this.signals();
    final Function1<Signal, Boolean> _function = new Function1<Signal, Boolean>() {
      @Override
      public Boolean apply(final Signal s) {
        String _name = s.getName();
        return Boolean.valueOf(Objects.equal(_name, name));
      }
    };
    return IterableExtensions.<Signal>findFirst(_signals, _function);
  }
  
  public Map<Signal, Integer> signal2id() {
    Map<Signal, Integer> _xblockexpression = null;
    {
      int _size = this.signal2idCache.size();
      boolean _equals = (_size == 0);
      if (_equals) {
        List<Signal> _signals = this.signals();
        Iterable<Pair<Integer, Signal>> _indexed = IterableExtensions.<Signal>indexed(_signals);
        HashMap<Signal, Integer> _newHashMap = CollectionLiterals.<Signal, Integer>newHashMap();
        final Function2<HashMap<Signal, Integer>, Pair<Integer, Signal>, HashMap<Signal, Integer>> _function = new Function2<HashMap<Signal, Integer>, Pair<Integer, Signal>, HashMap<Signal, Integer>>() {
          @Override
          public HashMap<Signal, Integer> apply(final HashMap<Signal, Integer> map, final Pair<Integer, Signal> pair) {
            HashMap<Signal, Integer> _xblockexpression = null;
            {
              Signal _value = pair.getValue();
              Integer _key = pair.getKey();
              map.put(_value, _key);
              _xblockexpression = map;
            }
            return _xblockexpression;
          }
        };
        HashMap<Signal, Integer> _fold = IterableExtensions.<Pair<Integer, Signal>, HashMap<Signal, Integer>>fold(_indexed, _newHashMap, _function);
        this.signal2idCache.putAll(_fold);
      }
      _xblockexpression = this.signal2idCache;
    }
    return _xblockexpression;
  }
  
  public Environment(final Resource r) {
    super();
    this.r = r;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.outputPlantUML ? 1231 : 1237);
    result = prime * result + (this.outputPlantUMLStates ? 1231 : 1237);
    result = prime * result + ((this.r== null) ? 0 : this.r.hashCode());
    result = prime * result + ((this.signalsCache== null) ? 0 : this.signalsCache.hashCode());
    result = prime * result + ((this.signal2idCache== null) ? 0 : this.signal2idCache.hashCode());
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
    Environment other = (Environment) obj;
    if (other.outputPlantUML != this.outputPlantUML)
      return false;
    if (other.outputPlantUMLStates != this.outputPlantUMLStates)
      return false;
    if (this.r == null) {
      if (other.r != null)
        return false;
    } else if (!this.r.equals(other.r))
      return false;
    if (this.signalsCache == null) {
      if (other.signalsCache != null)
        return false;
    } else if (!this.signalsCache.equals(other.signalsCache))
      return false;
    if (this.signal2idCache == null) {
      if (other.signal2idCache != null)
        return false;
    } else if (!this.signal2idCache.equals(other.signal2idCache))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("outputPlantUML", this.outputPlantUML);
    b.add("outputPlantUMLStates", this.outputPlantUMLStates);
    b.add("r", this.r);
    b.add("signalsCache", this.signalsCache);
    b.add("signal2idCache", this.signal2idCache);
    return b.toString();
  }
  
  @Pure
  public boolean getOutputPlantUML() {
    return this.outputPlantUML;
  }
  
  @Pure
  public boolean getOutputPlantUMLStates() {
    return this.outputPlantUMLStates;
  }
  
  @Pure
  public Resource getR() {
    return this.r;
  }
  
  @Pure
  public List<Signal> getSignalsCache() {
    return this.signalsCache;
  }
  
  @Pure
  public Map<Signal, Integer> getSignal2idCache() {
    return this.signal2idCache;
  }
}

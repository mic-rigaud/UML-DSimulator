package tuml.interpreter;

import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.impl.URIMappingRegistryImpl;
import org.eclipse.uml2.uml.Signal;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Vertex;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import tuml.interpreter.ActiveObject;
import tuml.interpreter.Composite;
import tuml.interpreter.Environment;

@SuppressWarnings("all")
public class Main {
  private static void init() {
    EPackage.Registry.INSTANCE.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
    EPackage.Registry.INSTANCE.put("http://www.eclipse.org/uml2/4.0.0/UML", UMLPackage.eINSTANCE);
    Map<String, Object> _extensionToFactoryMap = ResourceFactoryRegistryImpl.INSTANCE.getExtensionToFactoryMap();
    _extensionToFactoryMap.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
    final HashMap<String, String> pluginToURL = new HashMap<String, String>();
    Main.addPlugin(pluginToURL, UMLResourcesUtil.class);
    String _get = pluginToURL.get("org.eclipse.uml2.uml.resources");
    String _string = _get.toString();
    String _plus = (_string + "/");
    final URI uri = URI.createURI(_plus);
    URI _createURI = URI.createURI(UMLResource.LIBRARIES_PATHMAP);
    URI _appendSegment = uri.appendSegment("libraries");
    URI _appendSegment_1 = _appendSegment.appendSegment("");
    URIMappingRegistryImpl.INSTANCE.put(_createURI, _appendSegment_1);
    URI _createURI_1 = URI.createURI(UMLResource.METAMODELS_PATHMAP);
    URI _appendSegment_2 = uri.appendSegment("metamodels");
    URI _appendSegment_3 = _appendSegment_2.appendSegment("");
    URIMappingRegistryImpl.INSTANCE.put(_createURI_1, _appendSegment_3);
    URI _createURI_2 = URI.createURI(UMLResource.PROFILES_PATHMAP);
    URI _appendSegment_4 = uri.appendSegment("profiles");
    URI _appendSegment_5 = _appendSegment_4.appendSegment("");
    URIMappingRegistryImpl.INSTANCE.put(_createURI_2, _appendSegment_5);
  }
  
  public static void main(final String[] args) {
    Main.init();
    final ResourceSetImpl rs = new ResourceSetImpl();
    final URLStreamHandlerFactory _function = new URLStreamHandlerFactory() {
      @Override
      public URLStreamHandler createURLStreamHandler(final String it) {
        URLStreamHandler _xifexpression = null;
        boolean _or = false;
        boolean _equals = Objects.equal(it, "http");
        if (_equals) {
          _or = true;
        } else {
          boolean _equals_1 = Objects.equal(it, "https");
          _or = _equals_1;
        }
        if (_or) {
          final URLStreamHandler _function = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(final URL it) throws IOException {
              Object _xblockexpression = null;
              {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("warning: prevented connection to ");
                _builder.append(it, "");
                InputOutput.<String>println(_builder.toString());
                _xblockexpression = null;
              }
              return ((URLConnection)_xblockexpression);
            }
          };
          _xifexpression = _function;
        }
        return _xifexpression;
      }
    };
    URL.setURLStreamHandlerFactory(_function);
    URI _createFileURI = URI.createFileURI("samples/PingPong0.tuml.uml");
    final Resource r = rs.getResource(_createFileURI, true);
    final Environment env = new Environment(r);
    TreeIterator<EObject> _allContents = r.getAllContents();
    Iterator<org.eclipse.uml2.uml.Class> _filter = Iterators.<org.eclipse.uml2.uml.Class>filter(_allContents, org.eclipse.uml2.uml.Class.class);
    final Function1<org.eclipse.uml2.uml.Class, Boolean> _function_1 = new Function1<org.eclipse.uml2.uml.Class, Boolean>() {
      @Override
      public Boolean apply(final org.eclipse.uml2.uml.Class it) {
        String _name = it.getName();
        return Boolean.valueOf(Objects.equal(_name, "SUS"));
      }
    };
    final org.eclipse.uml2.uml.Class susClass = IteratorExtensions.<org.eclipse.uml2.uml.Class>findFirst(_filter, _function_1);
    final Composite sus = new Composite(env, susClass);
    Main.testSUS(env, sus);
  }
  
  public static Composite testSUS(final Environment env, final Composite sus) {
    Composite _xblockexpression = null;
    {
      final ActiveObject dispatcher = sus.getPart("dispatcher");
      Signal _signal = env.getSignal("ini");
      dispatcher.queue(_signal);
      Signal _signal_1 = env.getSignal("Z1Occupe");
      dispatcher.queue(_signal_1);
      Signal _signal_2 = env.getSignal("pedaleEnclenchee");
      dispatcher.queue(_signal_2);
      Signal _signal_3 = env.getSignal("Z1Libre");
      dispatcher.queue(_signal_3);
      Signal _signal_4 = env.getSignal("Z3Occupe");
      dispatcher.queue(_signal_4);
      Signal _signal_5 = env.getSignal("Z5Occupe");
      dispatcher.queue(_signal_5);
      Signal _signal_6 = env.getSignal("Z3Libre");
      dispatcher.queue(_signal_6);
      Signal _signal_7 = env.getSignal("Z5Libre");
      dispatcher.queue(_signal_7);
      Signal _signal_8 = env.getSignal("Z5_1Occupe");
      dispatcher.queue(_signal_8);
      Signal _signal_9 = env.getSignal("Z5_1Libre");
      dispatcher.queue(_signal_9);
      final boolean debug = true;
      Integer _bufferSize = sus.getBufferSize();
      ByteBuffer buffer = ByteBuffer.allocate((_bufferSize).intValue());
      sus.encode(buffer);
      if (debug) {
        InputOutput.<Composite>println(sus);
        InputOutput.<String>println("************************************************");
        byte[] _array = buffer.array();
        String _string = Arrays.toString(_array);
        InputOutput.<String>println(_string);
      }
      List<Pair<ActiveObject, Transition>> fireableTransitions = sus.fireableTransitions();
      if (debug) {
        final Function1<Pair<ActiveObject, Transition>, CharSequence> _function = new Function1<Pair<ActiveObject, Transition>, CharSequence>() {
          @Override
          public CharSequence apply(final Pair<ActiveObject, Transition> it) {
            return Main.prettyName(it);
          }
        };
        List<CharSequence> _map = ListExtensions.<Pair<ActiveObject, Transition>, CharSequence>map(fireableTransitions, _function);
        InputOutput.<List<CharSequence>>println(_map);
      }
      while ((!fireableTransitions.isEmpty())) {
        {
          final Pair<ActiveObject, Transition> trans = fireableTransitions.get(0);
          ActiveObject _key = trans.getKey();
          Transition _value = trans.getValue();
          _key.fire(_value);
          Integer _bufferSize_1 = sus.getBufferSize();
          ByteBuffer _allocate = ByteBuffer.allocate((_bufferSize_1).intValue());
          buffer = _allocate;
          sus.encode(buffer);
          if (debug) {
            byte[] _array_1 = buffer.array();
            String _string_1 = Arrays.toString(_array_1);
            InputOutput.<String>println(_string_1);
            ActiveObject _key_1 = trans.getKey();
            InputOutput.<ActiveObject>println(_key_1);
          }
          List<Pair<ActiveObject, Transition>> _fireableTransitions = sus.fireableTransitions();
          fireableTransitions = _fireableTransitions;
          if (debug) {
            final Function1<Pair<ActiveObject, Transition>, CharSequence> _function_1 = new Function1<Pair<ActiveObject, Transition>, CharSequence>() {
              @Override
              public CharSequence apply(final Pair<ActiveObject, Transition> it) {
                return Main.prettyName(it);
              }
            };
            List<CharSequence> _map_1 = ListExtensions.<Pair<ActiveObject, Transition>, CharSequence>map(fireableTransitions, _function_1);
            InputOutput.<List<CharSequence>>println(_map_1);
          }
        }
      }
      Composite _xifexpression = null;
      if (debug) {
        Composite _xblockexpression_1 = null;
        {
          InputOutput.<String>println("************************************************");
          _xblockexpression_1 = InputOutput.<Composite>println(sus);
        }
        _xifexpression = _xblockexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static Composite testControleur(final Environment env, final Composite sus, final ActiveObject ao) {
    Composite _xblockexpression = null;
    {
      Signal _signal = env.getSignal("sysInit");
      ao.queue(_signal);
      Signal _signal_1 = env.getSignal("annonceApproche");
      ao.queue(_signal_1);
      Signal _signal_2 = env.getSignal("annoncePassage");
      ao.queue(_signal_2);
      Signal _signal_3 = env.getSignal("liberation");
      ao.queue(_signal_3);
      Signal _signal_4 = env.getSignal("annonceApproche");
      ao.queue(_signal_4);
      Signal _signal_5 = env.getSignal("annoncePassage");
      ao.queue(_signal_5);
      Signal _signal_6 = env.getSignal("liberation");
      ao.queue(_signal_6);
      InputOutput.<Composite>println(sus);
      InputOutput.<String>println("************************************************");
      Set<Pair<ActiveObject, Transition>> fireableTransitions = ao.fireableTransitions();
      final Function1<Pair<ActiveObject, Transition>, CharSequence> _function = new Function1<Pair<ActiveObject, Transition>, CharSequence>() {
        @Override
        public CharSequence apply(final Pair<ActiveObject, Transition> it) {
          return Main.prettyName(it);
        }
      };
      Iterable<CharSequence> _map = IterableExtensions.<Pair<ActiveObject, Transition>, CharSequence>map(fireableTransitions, _function);
      InputOutput.<Iterable<CharSequence>>println(_map);
      while ((!fireableTransitions.isEmpty())) {
        {
          final Set<Pair<ActiveObject, Transition>> _converted_fireableTransitions = (Set<Pair<ActiveObject, Transition>>)fireableTransitions;
          Pair<ActiveObject, Transition> _get = ((Pair<ActiveObject, Transition>[])Conversions.unwrapArray(_converted_fireableTransitions, Pair.class))[0];
          Transition _value = _get.getValue();
          ao.fire(_value);
          InputOutput.<ActiveObject>println(ao);
          Set<Pair<ActiveObject, Transition>> _fireableTransitions = ao.fireableTransitions();
          fireableTransitions = _fireableTransitions;
          final Function1<Pair<ActiveObject, Transition>, CharSequence> _function_1 = new Function1<Pair<ActiveObject, Transition>, CharSequence>() {
            @Override
            public CharSequence apply(final Pair<ActiveObject, Transition> it) {
              return Main.prettyName(it);
            }
          };
          Iterable<CharSequence> _map_1 = IterableExtensions.<Pair<ActiveObject, Transition>, CharSequence>map(fireableTransitions, _function_1);
          InputOutput.<Iterable<CharSequence>>println(_map_1);
        }
      }
      InputOutput.<String>println("************************************************");
      _xblockexpression = InputOutput.<Composite>println(sus);
    }
    return _xblockexpression;
  }
  
  public static CharSequence prettyName(final Pair<ActiveObject, Transition> t) {
    StringConcatenation _builder = new StringConcatenation();
    ActiveObject _key = t.getKey();
    String _name = _key.getName();
    _builder.append(_name, "");
    _builder.append(":");
    Transition _value = t.getValue();
    Vertex _source = _value.getSource();
    String _name_1 = _source.getName();
    _builder.append(_name_1, "");
    _builder.append("->");
    Transition _value_1 = t.getValue();
    Vertex _target = _value_1.getTarget();
    String _name_2 = _target.getName();
    _builder.append(_name_2, "");
    return _builder;
  }
  
  private static void addPlugin(final Map<String, String> pluginToURL, final Class c) {
    String _simpleName = c.getSimpleName();
    String _plus = (_simpleName + ".class");
    URL _resource = c.getResource(_plus);
    String url = _resource.toString();
    String _replaceAll = url.replaceAll("![^!]*$", "!");
    url = _replaceAll;
    String plugin = url.replaceAll("^.*/plugins/([^_-]*)[_-].*$", "$1");
    String _replaceAll_1 = plugin.replaceAll("^.*/libs/([^_-]*)[_-].*$", "$1");
    plugin = _replaceAll_1;
    String _replaceAll_2 = plugin.replaceAll("^.*/([^_-]*)[_-].*!$", "$1");
    plugin = _replaceAll_2;
    pluginToURL.put(plugin, url);
  }
}

package tuml.interpreter;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.impl.URIMappingRegistryImpl;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import static java.net.URL.*;


class Main {

    def private static void init() {
	EPackage.Registry.INSTANCE.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
	EPackage.Registry.INSTANCE.put("http://www.eclipse.org/uml2/4.0.0/UML", UMLPackage.eINSTANCE);
		
	ResourceFactoryRegistryImpl.INSTANCE.extensionToFactoryMap.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

	val pluginToURL = new HashMap<String, String>;
	addPlugin(pluginToURL, UMLResourcesUtil);
	val uri = URI.createURI(pluginToURL.get("org.eclipse.uml2.uml.resources").toString() + '/');
	//		System.out.println(uri);
	URIMappingRegistryImpl.INSTANCE.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uri.appendSegment("libraries").appendSegment(""));
	URIMappingRegistryImpl.INSTANCE.put(URI.createURI(UMLResource.METAMODELS_PATHMAP), uri.appendSegment("metamodels").appendSegment(""));
	URIMappingRegistryImpl.INSTANCE.put(URI.createURI(UMLResource.PROFILES_PATHMAP), uri.appendSegment("profiles").appendSegment(""));
	
	
    }

    def static void main(String[] args) {
	init;

	val rs = new ResourceSetImpl();
	

	// Preventing HTTP connections to retrieve metamodels
	// - not working:
	//		rs.loadOptions.put(XMLResource.OPTION_USE_PACKAGE_NS_URI_AS_LOCATION, Boolean.FALSE)
	// - *very* broad, but should be OK in an application that does not otherwise
	// use http and https protocols.
	URL.URLStreamHandlerFactory = [
				       if(it == "http" || it == "https") {
					   [
					    println('''warning: prevented connection to «it»''')
					    null
					    ]
				       }
				       ];
	    
	val r = rs.getResource(URI.createFileURI("samples/PingPong0.tuml.uml"), true);

	val env = new Environment(r);
	val susClass = r.allContents.filter(Class).findFirst[it.name == "SUS"];
	    
	val sus = new Composite(env, susClass);
	


	//		testControleur(env, sus, sus.getPart("controleur"))
	testSUS(env, sus);
	
    }

    def static testSUS(Environment env, Composite sus) {
	val dispatcher = sus.getPart("dispatcher");
	    

	dispatcher.queue(env.getSignal("ini"));
	dispatcher.queue(env.getSignal("Z1Occupe"));
	dispatcher.queue(env.getSignal("pedaleEnclenchee"));
	dispatcher.queue(env.getSignal("Z1Libre"));
	dispatcher.queue(env.getSignal("Z3Occupe"));
	dispatcher.queue(env.getSignal("Z5Occupe"));
	dispatcher.queue(env.getSignal("Z3Libre"));
	dispatcher.queue(env.getSignal("Z5Libre"));
	dispatcher.queue(env.getSignal("Z5_1Occupe"));
	dispatcher.queue(env.getSignal("Z5_1Libre"));
	    
	val debug = true;
	    
	    
	var buffer = ByteBuffer.allocate(sus.bufferSize);
	sus.encode(buffer);
	if(debug) {
	    println(sus);
	    println("************************************************");
	    println(Arrays.toString(buffer.array));
	}

	var fireableTransitions = sus.fireableTransitions
	    if(debug) {
		println(fireableTransitions.map[prettyName])
	    }
	while(!fireableTransitions.empty) {
	    val trans = fireableTransitions.get(0)
		trans.key.fire(trans.value)
			
		buffer = ByteBuffer.allocate(sus.bufferSize);
	    sus.encode(buffer);
	    if(debug) {
		println(Arrays.toString(buffer.array));
			
		println(trans.key)
		    }
	    fireableTransitions = sus.fireableTransitions
		if(debug) {
		    println(fireableTransitions.map[it.prettyName])
		}
	}

	if(debug) {
	    println("************************************************")
		println(sus)
		}
    }

    def static testControleur(Environment env, Composite sus, ActiveObject ao) {
	ao.queue(env.getSignal("sysInit"));
	ao.queue(env.getSignal("annonceApproche"));
	ao.queue(env.getSignal("annoncePassage"));
	ao.queue(env.getSignal("liberation"));
	ao.queue(env.getSignal("annonceApproche"));
	ao.queue(env.getSignal("annoncePassage"));
	ao.queue(env.getSignal("liberation"));

	println(sus);
	println("************************************************");

	var fireableTransitions = ao.fireableTransitions
	    println(fireableTransitions.map[it.prettyName]);
	while(!fireableTransitions.empty) {
	    ao.fire(fireableTransitions.get(0).value);
	    println(ao);
	    fireableTransitions = ao.fireableTransitions
		println(fireableTransitions.map[it.prettyName]);
	}

	println("************************************************");
	println(sus);
    }

    def static prettyName(Pair<ActiveObject, Transition> t) {
	//(t.owner.owner.owner as Class).name +
	'''«t.key.name»:«t.value.source.name»->«t.value.target.name»'''
	    }

    def private static void addPlugin(Map<String, String> pluginToURL, java.lang.Class c) {
	var url = c.getResource(c.getSimpleName() + ".class").toString();
	url = url.replaceAll("![^!]*$", "!");
	var plugin = url.replaceAll("^.*/plugins/([^_-]*)[_-].*$", "$1");	// launched form Eclipse
	plugin = plugin.replaceAll("^.*/libs/([^_-]*)[_-].*$", "$1");			// launched from command line
	plugin = plugin.replaceAll("^.*/([^_-]*)[_-].*!$", "$1");				// dependencies from gradle
	pluginToURL.put(plugin, url);
    }
}

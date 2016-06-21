import java.io.*;
import java.lang.Runtime;
import java.lang.Process;
import java.lang.Thread;





public class pere
{
    public static void main(String arg[])
    {

	//String classpath = "/home/michael/Documents/plug-simulation-ui-0.0.1/lib/plug-simulation-ui-0.0.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/plug-core-0.0.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/plug-runtime-ltuml-0.0.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/plug-utils-0.0.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.emf.common-2.11.0-v20150123-0347.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.emf.ecore-2.11.0-v20150123-0347.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.emf.ecore.xmi-2.11.0-v20150123-0347.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.emf.mapping.ecore2xml-2.8.0-v20150123-0452.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.uml2.common-2.0.1.v20150202-0947.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.uml2.types-2.0.0.v20150202-0947.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.uml2.uml-5.0.2.v20150202-0947.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.uml2.uml.profile.standard-1.0.0.v20150202-0947.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.uml2.uml.resources-5.0.2.v20150202-0947.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.xtend.standalone-2.4.3.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/tuml-interpreter-0.0.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/guava-15.0-rc1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/guice-3.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/log4j-1.2.15.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/java-petitparser-2.0.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/javax.inject-1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/aopalliance-1.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/cglib-2.2.1-v20090111.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/petitparser-core-2.0.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/petitparser-json-2.0.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/petitparser-smalltalk-2.0.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/petitparser-xml-2.0.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/asm-3.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.xtend.lib-2.9.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.xtend.lib.macro-2.9.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.xtext.xbase.lib-2.9.0.jar";

	
	//String cmd = "java -classpath " + classpath + "  plug.simulation.ui.PlugSimulatorUI";
	
	String cmd = "java fils";
	
	try{
	    
	    Runtime runtime = Runtime.getRuntime();
	    Process process = Runtime.getRuntime().exec(cmd,null);
	    
	    
	    
	    // Consommation de la sortie standard de l'application externe dans un Thread separe
	    new Thread() {
		public void run() {
		    try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			try {
			    while((line = reader.readLine()) != null) {
				System.out.println("message of my son: "+line);
			    
			    }
			} finally {
			    reader.close();
			}
		    } catch(IOException ioe) {
			ioe.printStackTrace();
		    }
		}
	    }.start();
	    
	    PrintWriter stdin = new PrintWriter(process.getOutputStream());
	    stdin.println("hello son");
	    stdin.close();
	    
	    //waitManySec(20);
	    // OutputStream toto = process.getOutputStream();
	    // PrintStream  prtStrm = new PrintStream(toto);
	    // // close the output stream
	    // prtStrm.println("Closing the output stream...");
	


	} catch (IOException e) {
	    System.out.println("error");
	}
	
	
    }
    
    public static void waitManySec(long s) {
	try {
	    Thread.currentThread().sleep(s * 1000);
	}
	catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }
    
    
}

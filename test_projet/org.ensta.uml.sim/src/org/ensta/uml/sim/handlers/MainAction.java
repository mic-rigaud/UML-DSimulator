package org.ensta.uml.sim.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
//import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.ensta.uml.sim.PluginUI;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class MainAction extends AbstractHandler {

    PluginUI plug;

    /**
     * The constructor.
     */
    public MainAction() {

    }

    /**
     * the command has been executed, so extract extract the needed information
     * from the application context.
     */

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

        // SimulationModel model = new SimulationModel();
        plug = new PluginUI();
        // model.ajouterObservateur(plug);

        Session mysession = SessionManager.INSTANCE.getSessions().toArray(new Session[0])[0];
        String phrase = "-----" + mysession.toString();
        MessageDialog.openInformation(window.getShell(), "Uml_sim", phrase);

        // List<EObject> liste =
        // DashboardServices.INSTANCE.getUmlModelsWithDashboard();
        // StateMachine stateMachine = (StateMachine)
        // UMLPackage.Literals.STATE_MACHINE;

        // Collection<Session> session = SessionManager.INSTANCE.getSessions();
        // Session mysession = session.toArray(new Session[0])[0];
        // DDiagram dia;
        // StateMachine s = null; EClass e =
        // UMLFactory.eINSTANCE.getUMLPackage().getStateMachine();
        // Set<Resource> res = mysession.getAllSessionResources();
        // DDiagram dia = liste.get(0);

        // Collection<DRepresentation> representations =
        // // DialectManager.INSTANCE.getAllRepresentations(session)
        // String phrase = "";
        // final Session session =
        // SessionManager.INSTANCE.getSessions().toArray(new Session[0])[0];
        // if (session != null) {
        // final Collection<DRepresentation> representations =
        // DialectManager.INSTANCE.getAllRepresentations(session);
        // for (final DRepresentation representation : representations) {
        // if (representation instanceof DSemanticDiagram) {
        // DSemanticDiagram diagram = (DSemanticDiagram) representation;
        // phrase += "\n------------- " + diagram.getName() +
        // "----------------";
        // if (diagram.getName().endsWith("State Machine Diagram")) {
        // for (int i = 0; i < diagram.getDiagramElements().size(); i++) {
        // phrase += "\n";
        // phrase += diagram.getDiagramElements().get(i).toString();
        // if
        // (diagram.getDiagramElements().get(i).toString().startsWith("NodeList"))
        // {
        // DDiagramElement node = diagram.getDiagramElements().get(i);
        //
        // }
        //
        // }
        // }
        // if
        // (DashboardServices.DASHBOARD_DIAGRAM_DESCRIPTION_ID.equals(diagram.getDescription().getName()))
        // {
        // DialectUIManager.INSTANCE.openEditor(session,
        // representation, new NullProgressMonitor());
        // }
        // MessageDialog.openInformation(window.getShell(),
        // "Uml_sim", phrase);

        // }
        // }

        // }

        // +
        // stateMachine.toString();

        // String phrase = "hello";
        // MessageDialog.openInformation(window.getShell(), "Uml_sim", phrase);

        // if (stateMachine == null) {
        // MessageDialog.openInformation(window.getShell(), "Uml_sim", phrase);
        // } else {
        // MessageDialog.openInformation(window.getShell(), "Uml_sim", phrase);
        // }
        //

        /********************************************************************
         * Lancement de l aplli de simulation
         */
        /*
         * String classpath =
         * "/home/michael/Documents/plug-simulation-ui-0.0.1/lib/plug-simulation-ui-0.0.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/plug-core-0.0.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/plug-runtime-ltuml-0.0.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/plug-utils-0.0.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.emf.common-2.11.0-v20150123-0347.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.emf.ecore-2.11.0-v20150123-0347.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.emf.ecore.xmi-2.11.0-v20150123-0347.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.emf.mapping.ecore2xml-2.8.0-v20150123-0452.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.uml2.common-2.0.1.v20150202-0947.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.uml2.types-2.0.0.v20150202-0947.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.uml2.uml-5.0.2.v20150202-0947.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.uml2.uml.profile.standard-1.0.0.v20150202-0947.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.uml2.uml.resources-5.0.2.v20150202-0947.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.xtend.standalone-2.4.3.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/tuml-interpreter-0.0.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/guava-15.0-rc1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/guice-3.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/log4j-1.2.15.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/java-petitparser-2.0.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/javax.inject-1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/aopalliance-1.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/cglib-2.2.1-v20090111.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/petitparser-core-2.0.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/petitparser-json-2.0.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/petitparser-smalltalk-2.0.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/petitparser-xml-2.0.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/asm-3.1.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.xtend.lib-2.9.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.xtend.lib.macro-2.9.0.jar:/home/michael/Documents/plug-simulation-ui-0.0.1/lib/org.eclipse.xtext.xbase.lib-2.9.0.jar";
         * String cmd = "java -classpath " + classpath +
         * " plug.simulation.ui.PlugSimulatorUI"; // String cmd = "ls"; try {
         * Runtime runtime = Runtime.getRuntime(); final Process process =
         * runtime.exec(cmd, null); // Consommation de la sortie standard de
         * l'application externe dans // un Thread separe new Thread() {
         * @Override public void run() { try { BufferedReader reader = new
         * BufferedReader(new InputStreamReader(process.getInputStream()));
         * String line = ""; try { while ((line = reader.readLine()) != null) {
         * System.out.println( "message du fils: " + line); } } finally {
         * reader.close(); } } catch (IOException ioe) { ioe.printStackTrace();
         * } } }.start(); } catch (IOException e) { System.out.println("error");
         * }
         */
        return null;
    }

    /*
     * protected RepresentationDescription getDiagramDescription(Session
     * session, StateMachine stateMachine) { for (final
     * RepresentationDescription representation :
     * DialectManager.INSTANCE.getAvailableRepresentationDescriptions(session.
     * getSelectedViewpoints(false), stateMachine)) { if (
     * "State Machine Diagram" .equals(representation.getName()) //$NON-NLS-1$
     * && representation instanceof DiagramDescriptionSpec) { return
     * representation; } } return null; }
     */
}

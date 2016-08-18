package org.ensta.uml.sim.views;

import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.ensta.uml.sim.simulateur.CommunicationS;
import org.ensta.uml.sim.views.communication.CommunicationP;
import org.ensta.uml.sim.views.design.DesignModifier;
import org.ensta.uml.sim.views.features.ActionDoubleClick;
import org.ensta.uml.sim.views.features.buttons.ActionPlay;
import org.ensta.uml.sim.views.features.buttons.ActionRestart;
import org.ensta.uml.sim.views.features.buttons.ActionRestartCommunication;
import org.ensta.uml.sim.views.features.buttons.ActionStop;
import org.ensta.uml.sim.views.features.buttons.ActionStopCommunication;
import org.ensta.uml.sim.views.features.buttons.ActionWaitCommunication;
import org.ensta.uml.sim.views.features.menu.ActionMenuProject;
import org.ensta.uml.sim.views.features.view.ProjectView;
import org.ensta.uml.sim.views.features.view.TransitionsView;
import org.ensta.uml.sim.views.model.StateModel;
import org.ensta.uml.sim.views.tools.MySemaphore;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class MainView extends ViewPart implements Observateur {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.ensta.uml.sim.views.SimulatorView";

    /**
     * The view with the transition
     */
    private static TransitionsView viewer;

    /**
     * The view with the project details
     */
    private static ProjectView tree;

    /**
     * button play
     */
    private static ActionPlay play;

    /**
     * button stop
     */
    private static ActionStop stop;

    /**
     * button restart
     */
    private static ActionRestart restart;

    /**
     * button to change the current tab
     */
    // private static ActionTab tab;

    /**
     * button to stop the simulator
     */
    private static ActionStopCommunication stopCommunication;

    /**
     * button to wait a new communication with a simulator
     */
    private static ActionWaitCommunication waitCommunication;

    /**
     * button to restart the communication with the default simulator
     */
    private static ActionRestartCommunication restartComminucation;

    /**
     * menu to change the project
     */
    private static ActionMenuProject choixVue;

    /**
     * clickable action
     */
    private static ActionDoubleClick doubleClickAction;

    /**
     * communication layer
     */
    private static CommunicationP communicationP;

    /**
     * the default simulator
     */
    private static CommunicationS communicationS;

    /**
     * next transitions
     */
    private static String[] transitions;

    /**
     * semaphore to synchronize to refresh of the view with a modification
     */
    private static MySemaphore sem = new MySemaphore(0, 1);

    /**
     * the oject to change the view of the model
     */
    private static DesignModifier design;

    /**
     * The constructor.
     */
    public MainView() {
        StateModel.initialize();
        Session session = SessionManager.INSTANCE.getSessions().toArray(new Session[0])[0];
        Resource res = session.getSessionResource();
        URI path = res.getURI();
        StateModel.setCurrentProjectName(path.segment(1));
        StateModel.setCurrentProjectPath(ResourcesPlugin.getWorkspace().getRoot().getFile(new org.eclipse.core.runtime.Path(res.getURI().toPlatformString(true))).getLocation().toPortableString()
                .replace(path.lastSegment(), "model.uml"));
        newCommunicationP();
        newCommunicationS();
        design = new DesignModifier(session);
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    @Override
    public void createPartControl(Composite parent) {
        FillLayout grid = (FillLayout) parent.getLayout();
        grid.type = SWT.VERTICAL;
        parent.setLayout(grid);
        viewer = new TransitionsView(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        getSite().setSelectionProvider(viewer);
        tree = new ProjectView(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        tree.addClickAction(design);
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
        refreshPartControl("Initialize");
    }

    /**
     * refresh the view of all elements
     * <p>
     * wait for a modification during 5s.
     */
    public void refreshPartControl() {
        try {
            if (sem.tryAcquire(5, TimeUnit.SECONDS)) {
                if (communicationP.isError()) {
                    transitions = new String[] { "Error" };
                    showMessage(communicationP.getErrorMessage());
                    viewer.changeViewLabel("icons/error.gif");
                }
                viewer.setInput(transitions);
                tree.refreshTreeView();
                design.refreshColor();
                viewer.changeViewLabel("icons/transition.gif");
            } else {
                showMessage("error: temps d attente d'un message ecoule");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * refresh the view of all elements. Precise a transition
     * <p>
     * wait for a modification during 5s.
     */
    public void refreshPartControl(String liste) {
        try {
            if (sem.tryAcquire(5, TimeUnit.SECONDS)) {
                sem.release();
                viewer.changeViewLabel("icons/init.gif");
                transitions = new String[] { liste };
                refreshPartControl();
            } else {
                showMessage("error: temps d attente d'un message ecoule");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                MainView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(choixVue);
        manager.add(new Separator());
        manager.add(play);
        manager.add(stop);
        manager.add(restart);
        manager.add(new Separator());
        manager.add(stopCommunication);
        manager.add(waitCommunication);
        manager.add(restartComminucation);
        // manager.add(tab);

    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(choixVue);
        manager.add(play);
        manager.add(stop);
        manager.add(restart);
        // manager.add(tab);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(choixVue);
        manager.add(play);
        manager.add(stop);
        manager.add(restart);
        // manager.add(tab);
    }

    private void makeActions() {
        restart = new ActionRestart(this);
        play = new ActionPlay(this);
        stop = new ActionStop(this);
        // tab = new ActionTab(this);
        stopCommunication = new ActionStopCommunication(this);
        waitCommunication = new ActionWaitCommunication(this);
        restartComminucation = new ActionRestartCommunication(this);
        choixVue = new ActionMenuProject(this);
        doubleClickAction = new ActionDoubleClick(this, viewer);
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    public void showMessage(String message) {
        MessageDialog.openInformation(viewer.getControl().getShell(), "Simulation", message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class arg0) {
        return null;
    }

    @Override
    public void actualiser(Observable o) {
        if (o instanceof CommunicationP) {
            CommunicationP comm = (CommunicationP) o;
            transitions = comm.getTransitions();
            StateModel.refreshElements(comm.getCurrentClass(), comm.getCurrentState());
        }
        sem.release();
    }

    /**
     * 
     * @return the communication layer
     */
    public CommunicationP getCommunicationP() {
        return communicationP;
    }

    /**
     * 
     * @return the object to modify the uml view
     */
    public DesignModifier getDesign() {
        return design;
    }

    /**
     * 
     * @param designModificateur
     */
    public void setDesign(DesignModifier designModificateur) {
        design = designModificateur;
    }

    /**
     * this methode is call when we want to add some menu
     * 
     * @return the view of transition
     */
    public TransitionsView getViewer() {
        return viewer;
    }

    /**
     * create a new communication layer
     */
    public void newCommunicationP() {
        communicationP = new CommunicationP(9000);
        communicationP.start();
        communicationP.ajouterObservateur(this);
    }

    /**
     * start a new simulator and wait is connection to the communication layer
     */
    public void newCommunicationS() {
        communicationS = new CommunicationS(9000);
        communicationS.start();
        if (!communicationP.waitConnection(10))
            showMessage("error: temps d'attente d'une communication depassé");
    }

}

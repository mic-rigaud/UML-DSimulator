package org.ensta.uml.sim.views;

import java.util.concurrent.Semaphore;

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
import org.ensta.uml.sim.views.design.DesignModificateur;
import org.ensta.uml.sim.views.features.ActionDoubleClick;
import org.ensta.uml.sim.views.features.buttons.ActionPlay;
import org.ensta.uml.sim.views.features.buttons.ActionRestart;
import org.ensta.uml.sim.views.features.buttons.ActionRestartCommunication;
import org.ensta.uml.sim.views.features.buttons.ActionStop;
import org.ensta.uml.sim.views.features.buttons.ActionStopCommunication;
import org.ensta.uml.sim.views.features.buttons.ActionWaitCommunication;
import org.ensta.uml.sim.views.features.menu.ActionMenuProject;
import org.ensta.uml.sim.views.features.view.ViewProject;
import org.ensta.uml.sim.views.features.view.ViewTransitions;
import org.ensta.uml.sim.views.model.StateModel;

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

public class SimulatorView extends ViewPart implements Observateur {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.ensta.uml.sim.views.SimulatorView";

    private static ViewTransitions viewer;

    private static ViewProject tree;

    private static ActionPlay play;

    private static ActionStop stop;

    private static ActionRestart restart;

    // private static ActionTab tab;

    private static ActionStopCommunication stopCommunication;

    private static ActionWaitCommunication waitCommunication;

    private static ActionRestartCommunication restartComminucation;

    private static ActionMenuProject choixVue;

    private static ActionDoubleClick doubleClickAction;

    private static CommunicationP communicationP;

    private static CommunicationS communicationS;

    private static String[] transitions;

    private static Semaphore sem = new Semaphore(0);

    private static DesignModificateur design;

    /**
     * The constructor.
     */
    public SimulatorView() {
        StateModel.initialize();
        Session session = SessionManager.INSTANCE.getSessions().toArray(new Session[0])[0];
        Resource res = session.getSessionResource();
        URI path = res.getURI();
        StateModel.setCurrentProjectName(path.segment(1));
        StateModel.setCurrentProjectPath(ResourcesPlugin.getWorkspace().getRoot().getFile(new org.eclipse.core.runtime.Path(res.getURI().toPlatformString(true))).getLocation().toPortableString()
                .replace(path.lastSegment(), "model.uml"));
        newCommunicationP();
        newCommunicationS();
        design = new DesignModificateur(session);
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
        viewer = new ViewTransitions(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        getSite().setSelectionProvider(viewer);
        tree = new ViewProject(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        tree.addClickAction(design);
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
        refreshPartControl("Initialize");
    }

    public void refreshPartControl() {
        try {
            sem.acquire();
            if (communicationP.isError()) {
                transitions = new String[] { "Error" };
                showMessage(communicationP.getErrorMessage());
                viewer.changeViewLabel("icons/error.gif");
            }
            viewer.setInput(transitions);
            tree.refreshTreeView();
            design.refreshColor();
            viewer.changeViewLabel("icons/transition.gif");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void refreshPartControl(String liste) {
        try {
            sem.acquire();
            sem.release();
            viewer.changeViewLabel("icons/init.gif");
            transitions = new String[] { liste };
            refreshPartControl();
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
                SimulatorView.this.fillContextMenu(manager);
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

    public CommunicationP getCommunicationP() {
        return communicationP;
    }

    public DesignModificateur getDesign() {
        return design;
    }

    public void setDesign(DesignModificateur designModificateur) {
        design = designModificateur;
    }

    public ViewTransitions getViewer() {
        return viewer;
    }

    public void newCommunicationP() {
        communicationP = new CommunicationP(9000);
        communicationP.start();
        communicationP.ajouterObservateur(this);
    }

    public void newCommunicationS() {
        communicationS = new CommunicationS(9000);
        communicationS.start();
        communicationP.waitConnection(10);
    }

}

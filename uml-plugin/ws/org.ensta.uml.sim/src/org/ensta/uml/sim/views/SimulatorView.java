package org.ensta.uml.sim.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.part.ViewPart;
import org.ensta.uml.sim.simulateur.CommunicationSortantSimulateur;
import org.osgi.framework.Bundle;

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

    private TableViewer viewer;

    private Tree tree;

    private Action play;

    private Action stop;

    private Action restart;

    private Action choixVue;

    private Action doubleClickAction;

    private CommunicationSimulateur comm;

    private CommunicationSortantSimulateur comm2;

    private String[] transitions;

    private String currentProject;

    private Semaphore sem = new Semaphore(0);

    private DesignModificateur design;

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
        @Override
        public String getColumnText(Object obj, int index) {
            return getText(obj);
        }

        @Override
        public Image getColumnImage(Object obj, int index) {
            return getImage(obj);
        }

        @Override
        public Image getImage(Object obj) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
    }

    /**
     * The constructor.
     */
    public SimulatorView() {
        Session session = SessionManager.INSTANCE.getSessions().toArray(new Session[0])[0];
        Resource res = session.getSessionResource();
        URI path = res.getURI();
        currentProject = path.segment(1);
        String nouveauPath = ResourcesPlugin.getWorkspace().getRoot().getFile(new org.eclipse.core.runtime.Path(res.getURI().toPlatformString(true))).getLocation().toPortableString()
                .replace(path.lastSegment(), "model.uml");
        comm = new CommunicationSimulateur(9000);
        comm.start();
        comm.ajouterObservateur(this);
        comm2 = new CommunicationSortantSimulateur(nouveauPath);
        comm2.start();
        design = new DesignModificateur();
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
        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setInput(new String[] { "Initialize" });
        viewer.setLabelProvider(new ViewLabelProvider());
        tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        tree.setHeaderVisible(true);

        TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
        column1.setText("Overview of the project");
        column1.setWidth(200);
        TreeItem item = new TreeItem(tree, SWT.NONE);
        item.setText(SessionManager.INSTANCE.getSessions().toArray(new Session[0])[0].getSessionResource().getURI().segment(1));

        // Create the help context id for the viewer's control
        PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "org.ensta.uml.sim.viewer");
        getSite().setSelectionProvider(viewer);
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
        selectedActionInstance();
    }

    /*
     * Code redondant... mais je vois pas comment le modifier. Attention: il
     * faut appeler transition apres le sem.acquire sinon prob...
     */
    public void actualiserPartControl() {
        try {
            sem.acquire();
            viewer.setInput(transitions);
            refreshTreeView();
            design.refreshColor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void actualiserPartControl(String liste) {
        try {
            sem.acquire();
            viewer.setInput(liste.split("#"));
            refreshTreeView();
            design.refreshColor();
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
        manager.add(new Separator());
        manager.add(restart);

    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(choixVue);
        manager.add(play);
        manager.add(stop);
        manager.add(restart);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(choixVue);
        manager.add(play);
        manager.add(stop);
        manager.add(restart);
    }

    private void makeActions() {
        restart = new Action() {
            @Override
            public void run() {
                comm.putJson("restart");
                comm.sendMessage();
                actualiserPartControl("Initialize");
                showMessage("Restart");
            }
        };
        restart.setText("Restart");
        restart.setToolTipText("restart tooltip");
        restart.setImageDescriptor(this.getImageDescriptor("icons/replay.gif"));

        play = new Action() {
            @Override
            public void run() {
                showMessage("Play the simulateur");
            }
        };
        play.setText("Play");
        play.setToolTipText("Play tooltip");
        play.setImageDescriptor(this.getImageDescriptor("icons/play.gif"));
        stop = new Action() {
            @Override
            public void run() {
                design.printInfo();
                showMessage("Stop Simulation");
            }
        };
        stop.setText("Stop");
        stop.setToolTipText("Stop tooltip");
        stop.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_STOP));

        choixVue = new Action("My action", Action.AS_DROP_DOWN_MENU) {
            @Override
            public void run() {
                MyMenuCreator creator = new MyMenuCreator();
                creator.setActions(createListActionsChoiceModel());
                this.setMenuCreator(creator);
                IMenuCreator imenu = this.getMenuCreator();
                Menu menu = imenu.getMenu(viewer.getControl());
                menu.setVisible(true);
            }
        };
        choixVue.setText("Choix model");
        choixVue.setToolTipText("Choix model");
        choixVue.setChecked(true);
        choixVue.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_HOME_NAV));

        doubleClickAction = new Action() {
            @Override
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                if (obj == null) {
                    System.out.println("erreur: doubleClickAction: Null");
                    return;
                }
                if (obj.toString().equalsIgnoreCase("initialize")) {
                    comm.putJson("initialize");
                } else {
                    comm.putJson("state", obj.toString());
                }
                comm.sendMessage();
                actualiserPartControl();
            }
        };
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    private void selectedActionInstance() {
        tree.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                String string = event.detail == SWT.CHECK ? "Checked" : "Selected";
                System.out.println(((TreeItem) event.item).getText());
            }
        });
    }

    private void showMessage(String message) {
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

    private List<Action> createListActionsChoiceModel() {
        List<Action> actions = new ArrayList<Action>();
        Session[] sessions = SessionManager.INSTANCE.getSessions().toArray(new Session[0]);
        for (int i = 0; i < sessions.length; i++) {
            final Session session = sessions[i];
            final Resource res = session.getSessionResource();
            final URI path = res.getURI();

            actions.add(new Action(path.segment(1)) {
                @Override
                public void run() {
                    design.initialiser(session);
                    String nouveauPath = ResourcesPlugin.getWorkspace().getRoot().getFile(new org.eclipse.core.runtime.Path(res.getURI().toPlatformString(true))).getLocation().toPortableString()
                            .replace(path.lastSegment(), "model.uml");
                    comm.putJson("reload", nouveauPath);
                    comm.sendMessage();
                    actualiserPartControl("Initialize");
                    currentProject = path.segment(1);
                    refreshTreeView();
                    showMessage("Vous avez bien basculer sur la session: " + path.segment(1));
                }
            });
        }
        return actions;
    }

    protected void refreshTreeView() {
        tree.removeAll();
        TreeItem item = new TreeItem(tree, SWT.NONE);
        item.setText(currentProject);
        item.setExpanded(true);
        HashMap<String, String> map = design.getActiveState();
        for (String etat : map.keySet()) {
            System.out.println(etat);
            if (etat.contains("/")) {
                System.out.println("une instance...");

            } else {
                TreeItem subitem = new TreeItem(item, SWT.NONE);
                subitem.setText("Class : " + etat);
            }
        }
    }

    @Override
    public void actualiser(Observable o) {
        if (o instanceof CommunicationSimulateur) {
            CommunicationSimulateur comm = (CommunicationSimulateur) o;
            transitions = comm.getTransitions();
            design.refreshElements(comm.getCurrentClass(), comm.getStates());
        }
        sem.release();
    }

    public ImageDescriptor getImageDescriptor(String path) {
        String pluginId = "org.ensta.uml.sim";
        Bundle bundle = Platform.getBundle(pluginId);
        URL fullPathString = BundleUtility.find(bundle, path);
        return ImageDescriptor.createFromURL(fullPathString);
    }

}

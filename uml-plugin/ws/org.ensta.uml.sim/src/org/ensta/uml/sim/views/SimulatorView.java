package org.ensta.uml.sim.views;

import java.io.File;
import java.util.Random;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.ensta.uml.sim.handlers.SimulatorControler;

import plug.simulation.ui.SimulationModel;

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

public class SimulatorView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.ensta.uml.sim.views.SimulatorView";

    private TableViewer viewer;

    private Action play;

    private Action stop;

    private Action doubleClickAction;

    SimulationModel model;

    SimulatorControler controler;

    Random random = new Random();

    File fichier = new File("/home/michael/Documents/Ensta/Stage/2A/uml-simulateur/plug-build/resources/test/PingPong0.tuml.uml");

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
        model = new SimulationModel();
        controler = new SimulatorControler();
        model.ajouterObservateur(controler);
        model.loadModel(fichier);
        model.initialize();
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    @Override
    public void createPartControl(Composite parent) {
        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setInput(new String[] { "Initialize" });
        viewer.setLabelProvider(new ViewLabelProvider());

        // Create the help context id for the viewer's control
        PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "org.ensta.uml.sim.viewer");
        getSite().setSelectionProvider(viewer);
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
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
        manager.add(play);
        manager.add(new Separator());
        manager.add(stop);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(play);
        manager.add(stop);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(play);
        manager.add(stop);
    }

    private void makeActions() {
        play = new Action() {
            @Override
            public void run() {
                showMessage("Action 1 realise");
            }
        };
        play.setText("Action 1");
        play.setToolTipText("Action 1 tooltip");
        play.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        stop = new Action() {
            @Override
            public void run() {
                showMessage("Action 2 executed");
            }
        };
        stop.setText("Action 2");
        stop.setToolTipText("Action 2 tooltip");
        stop.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        doubleClickAction = new Action() {
            @Override
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();

                showMessage("Double-click detected on " + obj.toString());
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

    @Override
    public Object getAdapter(Class arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}

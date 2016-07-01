package org.ensta.uml.sim.views;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
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
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.BorderedStyle;
import org.eclipse.sirius.diagram.DNodeListElement;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.diagram.description.style.SquareDescription;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.Style;
import org.eclipse.sirius.viewpoint.description.ColorDescription;
import org.eclipse.sirius.viewpoint.description.SystemColor;
import org.eclipse.sirius.viewpoint.description.style.StyleDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.ensta.uml.sim.simulateur.CommunicationSortantSimulateur;

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

    private Action play;

    private Action stop;

    private Action restart;

    private Action doubleClickAction;

    private CommunicationSimulateur comm;

    private CommunicationSortantSimulateur comm2;

    private String transitions;

    private Semaphore sem = new Semaphore(0);

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
        comm = new CommunicationSimulateur(9000);
        comm.start();
        comm.ajouterObservateur(this);
        comm2 = new CommunicationSortantSimulateur("/home/michael/Documents/Ensta/Stage/2A/uml-simulateur/plug-build/resources/test/PingPong0.tuml.uml");
        comm2.start();
        transitions = new String();
        // comm.ajouterObservateur(o);
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

    public void actualiserPartControl() {
        try {
            sem.acquire();
            System.out.println(transitions);
            viewer.setInput(transitions.split("#"));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void actualiserPartControl(String liste) {

        System.out.println("ok cool: " + liste);
        viewer.setInput(liste.split("#"));
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
        manager.add(restart);
        manager.add(new Separator());
        manager.add(play);
        manager.add(new Separator());
        manager.add(stop);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(restart);
        manager.add(play);
        manager.add(stop);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(restart);
        manager.add(play);
        manager.add(stop);
    }

    private void makeActions() {
        restart = new Action() {
            @Override
            public void run() {
                try {
                    comm.sendMessage("restart");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                actualiserPartControl("Initialize");
                showMessage("Restart");
            }
        };
        restart.setText("Restart");
        restart.setToolTipText("restart tooltip");
        restart.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));

        play = new Action() {
            @Override
            public void run() {
                showMessage("Play the simulateur");
            }
        };
        play.setText("Play");
        play.setToolTipText("Play tooltip");
        play.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));

        stop = new Action() {
            @Override
            public void run() {
                changerColor();
                showMessage("Stop the simulateur");
            }
        };
        stop.setText("Stop");
        stop.setToolTipText("Stop tooltip");
        stop.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_STOP));
        doubleClickAction = new Action() {
            @Override
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                try {
                    comm.sendMessage(obj.toString());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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

    @Override
    public void actualiser(Observable o) {
        // TODO Auto-generated method stub
        if (o instanceof CommunicationSimulateur) {
            CommunicationSimulateur comm = (CommunicationSimulateur) o;
            transitions = comm.getMessage();
        }
        sem.release();
    }

    public void changerColor() {
        Session mysession = SessionManager.INSTANCE.getSessions().toArray(new Session[0])[0];
        String phrase = "-----" + mysession.toString();

        if (mysession != null) {
            final Collection<DRepresentation> representations = DialectManager.INSTANCE.getAllRepresentations(mysession);

            for (final DRepresentation representation : representations) {

                if (representation instanceof DSemanticDiagram) {
                    DSemanticDiagram diagram = (DSemanticDiagram) representation;
                    phrase += "\n------------- " + diagram.getName() + "----------------";

                    for (int i = 0; i < diagram.getNodeListElements().size(); i++) {
                        phrase += "\n";
                        phrase += "nom:" + diagram.getNodeListElements().get(i).getName();
                        DNodeListElement dnode = diagram.getNodeListElements().get(i);

                        if (dnode.getName().startsWith("pinger")) {

                            Style style = dnode.getStyle();
                            StyleDescription description = style.getDescription();
                            if (style instanceof BorderedStyle) {
                                System.out.println("toto " + dnode.getName());
                                // BorderedStyle sd = (BorderedStyle) style;
                                SquareDescription sd = (SquareDescription) description;
                                TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(sd);
                                domain.getCommandStack().execute(new RecordingCommand(domain) {

                                    @Override
                                    protected void doExecute() {
                                        ColorDescription cd = sd.getBorderColor();
                                        SystemColor sc = (SystemColor) cd;

                                        sc.setBlue(0);
                                        sc.setGreen(255);
                                        sc.setRed(0);
                                        sd.setBorderColor(sc);
                                        sd.setWidth(10);
                                        sd.setBorderSizeComputationExpression("2");

                                        // RGBValues rgb =
                                        // VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.DARK_PURPLE_LITERAL);
                                        // sd.setBorderColor(rgb);
                                        // sd.getCustomFeatures().add(DiagramPackage.Literals.BORDERED_STYLE__BORDER_COLOR.getName());
                                        // sd.setBorderColor(rgb);
                                    }
                                });

                            }
                        }
                    }

                }

            }
        }

        showMessage(phrase);

    }
}

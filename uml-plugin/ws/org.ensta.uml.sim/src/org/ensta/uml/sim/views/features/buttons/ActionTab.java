package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPage;
import org.ensta.uml.sim.views.MainView;

public class ActionTab extends Action implements IAction {

    MainView view;

    public ActionTab(MainView view) {
        this.view = view;
        this.setText("Link with simulation");
        this.setToolTipText("Link with simulation");
        this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {

        String[] namess = new String[1];
        IEditorPart ed2 = null;
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IWorkbenchWindow[] activePages = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (IWorkbenchWindow active : activePages) {
            for (IEditorPart ed : active.getActivePage().getEditors()) {
                ed2 = ed;
                System.out.println("1:" + ed.getTitle() + " : " + (ed));

            }
        }
        IWorkbenchWindow[] act = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (IWorkbenchWindow active : act) {
            for (IEditorReference ed : active.getActivePage().getEditorReferences()) {
                System.out.println("ed    ---    " + ed.getTitle());
                try {

                    System.out.println(((WorkbenchPage) active.getActivePage()).getActiveEditor().getTitle());
                    CTabFolder t = ed.getEditorInput().getAdapter(CTabFolder.class);
                    if (t != null) {
                        System.out.println("toto");
                    }
                } catch (PartInitException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            System.out.println("shell " + active.getShell().getText());
            System.out.println(active.getShell().isVisible());
        }
        //
        // // view.showMessage("current tab");
        // Display display = Display.getCurrent();
        //
        // Shell shell = display.getActiveShell();
        // System.out.println("shell:" + shell);
        // for (Shell she : shell.getShells()) {
        // System.out.println("shell:" + she);
        // for (Control cont : she.getTabList()) {
        // System.out.println("controle:" + cont);
        // }
        // }
        //
        // Control[] tablist = shell.getTabList();
        // for (Control tab : tablist) {
        // System.out.println(tab.getClass());
        // if (tab instanceof Composite) {
        // Composite com = (Composite) tab;
        // for (Control c : com.getTabList()) {
        // Control b = c;
        // if (b instanceof ToolBar) {
        // System.out.println((b));
        // }
        // System.out.println(b.getClass());
        // }
        // }
        // if (tab instanceof TabFolder) {
        // TabFolder vtab = (TabFolder) tab;
        // System.out.println("hello");
        // System.out.println(vtab);
        // }
        // }
    }
}

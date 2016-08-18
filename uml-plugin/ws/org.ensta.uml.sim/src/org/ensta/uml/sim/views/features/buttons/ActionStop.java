package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.ensta.uml.sim.views.MainView;

/**
 * This class is a button which permit to stop the simulation if it has been
 * played
 * 
 * @author michael
 * @version 1.0
 */
public class ActionStop extends Action implements IAction {
    MainView view;

    public ActionStop(MainView view) {
        this.view = view;
        this.setText("Stop");
        this.setToolTipText("Stop tooltip");
        this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_STOP));
    }

    @Override
    public void run() {
        view.showMessage("Stop Simulation");
        Play.stopPlay();
    }
}

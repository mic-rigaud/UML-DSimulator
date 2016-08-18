package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.ensta.uml.sim.views.MainView;

/**
 * This class is a button which permit to restart the communication with the
 * simulator
 * 
 * @author michael
 * @version 1.0
 */
public class ActionRestartCommunication extends Action implements IAction {

    MainView view;

    public ActionRestartCommunication(MainView view) {
        this.view = view;
        this.setText("Restart default Simulator");
        this.setToolTipText("Restart default Simulator");
        this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
    }

    @Override
    public void run() {
        if (!view.getCommunicationP().isAlive()) {
            view.newCommunicationP();
            view.newCommunicationS();
            view.refreshPartControl("Initialize");
            view.showMessage("Simulator restart");
        } else {
            view.showMessage("You could first stop the simulator");
        }
    }
}

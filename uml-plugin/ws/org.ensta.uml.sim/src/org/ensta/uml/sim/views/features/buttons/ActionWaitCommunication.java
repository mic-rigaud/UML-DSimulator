package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.ensta.uml.sim.views.MainView;

/**
 * This class is a button which permit to wait for a new communication with a
 * new simulator
 * 
 * @author michael
 * @version 1.0
 */
public class ActionWaitCommunication extends Action implements IAction {

    MainView view;

    public ActionWaitCommunication(MainView view) {
        this.view = view;
        this.setText("Wait for a new Simulator");
        this.setToolTipText("Wait for a new Simulator");
        this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
    }

    @Override
    public void run() {
        if (!view.getCommunicationP().isAlive()) {
            view.newCommunicationP();
            view.showMessage("Wait for a new connection during 60s");
            if (view.getCommunicationP().waitConnection(60)) {
                view.showMessage("Connexion Establish");
                view.refreshPartControl("Initialize");
            } else {
                view.showMessage("Waiting time elapses");
            }
        } else {
            view.showMessage("You could first stop the simulator");
        }
    }

}

package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.ensta.uml.sim.views.SimulatorView;

public class ActionStopCommunication extends Action implements IAction {

    SimulatorView view;

    public ActionStopCommunication(SimulatorView view) {
        this.view = view;
        this.setText("Stop Simulator");
        this.setToolTipText("Stop Simulator");
        this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
    }

    @Override
    public void run() {
        view.getCommunicationP().close();
        view.showMessage("Simulator stopped");
        if (view.getCommunicationP().isAlive())
            System.out.println("coucou");
    }
}

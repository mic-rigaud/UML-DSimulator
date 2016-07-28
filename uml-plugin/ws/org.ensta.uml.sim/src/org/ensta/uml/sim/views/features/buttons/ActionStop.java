package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.ensta.uml.sim.views.SimulatorView;

public class ActionStop extends Action implements IAction {
    SimulatorView view;

    public ActionStop(SimulatorView view) {
        this.view = view;
        this.setText("Stop");
        this.setToolTipText("Stop tooltip");
        this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_STOP));
    }

    @Override
    public void run() {
        view.showMessage("Stop Simulation");
    }
}

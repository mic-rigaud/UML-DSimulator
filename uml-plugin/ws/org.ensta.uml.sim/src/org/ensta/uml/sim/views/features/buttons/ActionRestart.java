package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.ensta.uml.sim.views.SimulatorView;
import org.ensta.uml.sim.views.tools.Tools;

public class ActionRestart extends Action implements IAction {
    SimulatorView view;

    public ActionRestart(SimulatorView view) {
        this.view = view;
        this.setText("Restart");
        this.setToolTipText("restart tooltip");
        this.setImageDescriptor(Tools.getImageDescriptor("icons/replay.gif"));

    }

    @Override
    public void run() {
        view.getCommunicationP().putJson("restart");
        view.getCommunicationP().sendMessage();
        view.refreshPartControl("Initialize");
        view.showMessage("Restart");
    }
}

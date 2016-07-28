package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.ensta.uml.sim.views.SimulatorView;
import org.ensta.uml.sim.views.tools.Tools;

public class ActionPlay extends Action implements IAction {

    SimulatorView view;

    public ActionPlay(SimulatorView view) {
        this.view = view;
        this.setText("Play");
        this.setToolTipText("Play tooltip");
        this.setImageDescriptor(Tools.getImageDescriptor("icons/play.gif"));
    }

    @Override
    public void run() {
        view.showMessage("Play the simulateur");
    }
}

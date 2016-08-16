package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.ensta.uml.sim.views.SimulatorView;
import org.ensta.uml.sim.views.tools.Tools;

/**
 * This class is a button which permit to play the simulation
 * 
 * @author michael
 * @version 1.0
 */
public class ActionPlay extends Action implements IAction {

    protected SimulatorView view;

    protected static boolean protection = true;

    public ActionPlay(SimulatorView view) {
        this.view = view;
        this.setText("Play");
        this.setToolTipText("Play tooltip");
        this.setImageDescriptor(Tools.getImageDescriptor("icons/play.gif"));
    }

    @Override
    public void run() {
        if (protection) {
            Play p = new Play(view);
            protection = !protection;
            view.showMessage("Play");
            p.start();
        } else {
            view.showMessage("Error: une simulation est deja ne cour");
        }
    }

    public static void freeActionPlay() {
        protection = true;
    }

}

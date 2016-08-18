package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.ensta.uml.sim.views.MainView;
import org.ensta.uml.sim.views.tools.Tools;

/**
 * This class is a button which permit to restart the simulation
 * 
 * @author michael
 * @version 1.0
 */
public class ActionRestart extends Action implements IAction {
    MainView view;

    public ActionRestart(MainView view) {
        this.view = view;
        this.setText("Restart");
        this.setToolTipText("restart tooltip");
        this.setImageDescriptor(Tools.getImageDescriptor("icons/replay.gif"));

    }

    @Override
    public void run() {
        try {
            view.getCommunicationP().putJson("restart");
            view.getCommunicationP().sendMessage();
            view.refreshPartControl("Initialize");
            view.showMessage("Restart");
        } catch (Exception e) {
            view.showMessage("Erreur de Connection au simulateur");
            e.printStackTrace();
        }

    }
}

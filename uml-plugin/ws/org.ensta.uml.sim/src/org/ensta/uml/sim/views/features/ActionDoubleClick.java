package org.ensta.uml.sim.views.features;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.ensta.uml.sim.views.SimulatorView;
import org.ensta.uml.sim.views.features.view.ViewTransitions;

/**
 * this class develop how to react to a double click action
 * 
 * @author michael
 * @version 1.0
 */
public class ActionDoubleClick extends Action implements IAction {
    private SimulatorView view;

    private StructuredViewer table;

    public ActionDoubleClick(SimulatorView view, ViewTransitions table) {
        this.view = view;
        this.table = table;
    }

    @Override
    public void run() {
        ISelection selection = table.getSelection();
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        if (obj == null) {
            System.out.println("erreur: doubleClickAction: Null");
            return;
        }
        fillJsonOutCommunication(obj.toString());
        if (view.getCommunicationP().sendMessage())
            view.refreshPartControl();
        else
            view.showMessage("Erreur de Connection au simulateur");
    }

    private void fillJsonOutCommunication(String selection) {
        if (selection.equalsIgnoreCase("initialize")) {
            view.getCommunicationP().putJson("initialize");
        } else {
            view.getCommunicationP().putJson("state", selection);
        }
    }

}

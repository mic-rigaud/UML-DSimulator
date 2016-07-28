package org.ensta.uml.sim.views.features.menu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.ensta.uml.sim.views.SimulatorView;

public class ActionMenuProject extends Action implements IAction {
    SimulatorView view;

    public ActionMenuProject(SimulatorView view) {
        super("My action", Action.AS_DROP_DOWN_MENU);
        this.view = view;
        this.setText("Choix model");
        this.setToolTipText("Choix model");
        this.setChecked(true);
        this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_HOME_NAV));

    }

    @Override
    public void run() {
        MyMenuCreator creator = new MyMenuCreator();
        creator.setActions(createListActionsChoiceModel());
        this.setMenuCreator(creator);
        IMenuCreator imenu = this.getMenuCreator();
        Menu menu = imenu.getMenu(view.getViewer().getControl());
        menu.setVisible(true);
    }

    private List<Action> createListActionsChoiceModel() {
        List<Action> actions = new ArrayList<Action>();
        Session[] sessions = SessionManager.INSTANCE.getSessions().toArray(new Session[0]);
        for (int i = 0; i < sessions.length; i++) {
            ActionChoixProject action = new ActionChoixProject(sessions[i], view);
            actions.add(action);
        }
        return actions;
    }

}

package org.ensta.uml.sim.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MyMenuCreator extends Action implements IMenuCreator {

    private Menu fMenu;

    private List<Action> actions;

    public MyMenuCreator() {
        this.actions = new ArrayList<Action>();
        setText("My Actions");
        setMenuCreator(this);
    }

    @Override
    public void dispose() {
        if (fMenu != null) {
            fMenu.dispose();
            fMenu = null;
        }
    }

    @Override
    public Menu getMenu(Menu parent) {
        return null;
    }

    @Override
    public Menu getMenu(Control parent) {
        if (fMenu != null)
            fMenu.dispose();

        fMenu = new Menu(parent);

        for (int i = 0; i < actions.size(); i++) {
            addActionToMenu(fMenu, actions.get(i));
            if (i != actions.size() - 1) {
                new MenuItem(fMenu, SWT.SEPARATOR);
            }
        }
        return fMenu;
    }

    protected void addActionToMenu(Menu parent, Action action) {
        ActionContributionItem item = new ActionContributionItem(action);
        item.fill(parent, -1);
    }

    @Override
    public void run() {

    }

    /**
     * Get's rid of the menu, because the menu hangs on to the searches, etc.
     */
    void clear() {
        dispose();
    }

    public void addActions(Action action) {
        this.actions.add(action);
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

}

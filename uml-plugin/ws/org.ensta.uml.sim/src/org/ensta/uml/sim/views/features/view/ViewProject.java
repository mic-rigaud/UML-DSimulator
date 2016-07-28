package org.ensta.uml.sim.views.features.view;

import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.ensta.uml.sim.views.design.DesignModificateur;
import org.ensta.uml.sim.views.model.StateModel;

import json.JSONArray;
import json.JSONObject;

public class ViewProject {

    private Tree tree;

    public ViewProject(Composite parent, int style) {
        tree = new Tree(parent, style);
        tree.setHeaderVisible(true);
        TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
        column1.setText("Overview of the project");
        column1.setWidth(200);
        TreeItem item = new TreeItem(tree, SWT.NONE);
        item.setText(SessionManager.INSTANCE.getSessions().toArray(new Session[0])[0].getSessionResource().getURI().segment(1));
    }

    public void refreshTreeView() {
        tree.removeAll();
        TreeItem item = new TreeItem(tree, SWT.NONE);
        item.setText(StateModel.getCurrentProjectName());
        item.setExpanded(true);
        JSONArray currentState = StateModel.getCurrentState();
        for (Object obj : currentState) {
            JSONObject jsonClass = (JSONObject) obj;
            TreeItem subitem = new TreeItem(item, SWT.NONE);
            subitem.setText("Class : " + jsonClass.getString("class"));
            subitem.setExpanded(true);
            StateModel.putCurrentInstances(jsonClass.getString("class"));
            for (Object obj2 : jsonClass.getJSONArray("instance")) {
                JSONObject jsonInstance = (JSONObject) obj2;
                TreeItem subsubitem = new TreeItem(subitem, SWT.NONE);
                subsubitem.setText("Instance : " + jsonInstance.getString("name"));
                if (StateModel.isCurrentInstancesContains(jsonClass.getString("class"), jsonInstance.getString("name"))) {
                    Color newColor = new Color(subsubitem.getForeground().getDevice(), 255, 25, 25);
                    subsubitem.setForeground(newColor);
                } else if (StateModel.isCurrentInstancesContains(jsonClass.getString("class"), "all")) {
                    Color newColor = new Color(subitem.getForeground().getDevice(), 255, 25, 25);
                    subitem.setForeground(newColor);
                }
            }
        }
    }

    public void addListener(int selection, Listener listener) {
        tree.addListener(selection, listener);
    }

    public void addClickAction(DesignModificateur design) {
        tree.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (((TreeItem) event.item).getParentItem() != null) {
                    String fils = ((TreeItem) event.item).getText();
                    String pere = ((TreeItem) event.item).getParentItem().getText();
                    if (fils.startsWith("Instance : ") && pere.startsWith("Class : ")) {
                        StateModel.putCurrentInstances(pere.replace("Class : ", ""), fils.replace("Instance : ", ""));
                    }
                    if (fils.startsWith("Class : ")) {
                        StateModel.putCurrentInstances(fils.replace("Class : ", ""), "all");
                    }
                    refreshTreeView();
                    design.refreshColor();
                }
            }
        });
    }
}

package org.ensta.uml.sim.views.features.view;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class ViewTransitions extends TableViewer {
    ViewLabelProvider viewLabel;

    public ViewTransitions(Composite parent, int i) {
        super(parent, i);
        this.setContentProvider(ArrayContentProvider.getInstance());
        viewLabel = new ViewLabelProvider();
        this.setLabelProvider(viewLabel);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this.getControl(), "org.ensta.uml.sim.viewer");
    }

    public void changeViewLabel(String path) {
        viewLabel.changeImage(path);
    }
}
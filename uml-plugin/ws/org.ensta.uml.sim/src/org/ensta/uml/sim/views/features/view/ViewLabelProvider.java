package org.ensta.uml.sim.views.features.view;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.ensta.uml.sim.views.tools.Tools;

public class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

    private Image currentImage;

    public ViewLabelProvider() {
        currentImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
    }

    @Override
    public String getColumnText(Object obj, int index) {
        return getText(obj);
    }

    @Override
    public Image getColumnImage(Object obj, int index) {
        return getImage(obj);
    }

    @Override
    public Image getImage(Object obj) {
        return currentImage;
    }

    public void changeImage(String path) {
        currentImage = Tools.getImage(path);
    }
}

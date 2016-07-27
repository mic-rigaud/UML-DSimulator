package org.ensta.uml.sim.views.tools;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;

public class Tools {

    public static ImageDescriptor getImageDescriptor(String path) {
        String pluginId = "org.ensta.uml.sim";
        Bundle bundle = Platform.getBundle(pluginId);
        URL fullPathString = BundleUtility.find(bundle, path);
        return ImageDescriptor.createFromURL(fullPathString);
    }

    public static Image getImage(String path) {
        return getImageDescriptor(path).createImage();
    }
}

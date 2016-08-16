package org.ensta.uml.sim.views.features.menu;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.sirius.business.api.session.Session;
import org.ensta.uml.sim.views.SimulatorView;
import org.ensta.uml.sim.views.design.DesignModify;
import org.ensta.uml.sim.views.model.StateModel;

/**
 * This class is button to switch to other session
 * 
 * @see ActionMenuProject
 * @author michael
 * @version 1.0
 */
public class ActionChoixProject extends Action {

    private Session session;

    private SimulatorView view;

    /**
     * Constructor ActionChoixProject
     * 
     * @param session
     *            where you want to go
     * @param view
     */
    public ActionChoixProject(Session session, SimulatorView view) {
        super(session.getSessionResource().getURI().segment(1));
        this.session = session;
        this.view = view;
    }

    @Override
    public void run() {
        Resource res = session.getSessionResource();
        URI path = res.getURI();
        view.setDesign(new DesignModify(session));
        StateModel.setCurrentProjectPath(ResourcesPlugin.getWorkspace().getRoot().getFile(new org.eclipse.core.runtime.Path(res.getURI().toPlatformString(true))).getLocation().toPortableString()
                .replace(path.lastSegment(), "model.uml"));
        view.getCommunicationP().putJson("reload");
        view.getCommunicationP().sendMessage();
        StateModel.setCurrentProjectName(path.segment(1));
        view.refreshPartControl("Initialize");
        if (!view.getCommunicationP().isError())
            view.showMessage("Vous avez bien basculer sur la session: " + path.segment(1));
    }

}

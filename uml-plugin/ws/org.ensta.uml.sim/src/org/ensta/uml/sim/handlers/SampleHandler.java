package org.ensta.uml.sim.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import plug.simulation.ui.SimulationModel;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {

    SimulationModel model;

    PluginUI plug;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

        model = new SimulationModel();
        plug = new PluginUI();
        File fichier = new File("/home/michael/Documents/Ensta/Stage/2A/uml-simulateur/plug-build/resources/test/PingPong0.tuml.uml");

        model.loadModel(fichier);

        System.out.println(model.getCurrentState());

        MessageDialog.openInformation(window.getShell(), "Sim", "Hello, Eclipse world");
        return null;
    }
}

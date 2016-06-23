package org.ensta.uml.sim.handlers;

import java.io.File;
import java.util.Random;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import plug.core.IFireableTransition;
import plug.simulation.ui.SimulationModel;

public class PluginUI {

    SimulationModel model;

    SimulatorControler controler;

    Random random = new Random();

    File fichier = new File("/home/michael/Documents/Ensta/Stage/2A/uml-simulateur/plug-build/resources/test/PingPong0.tuml.uml");

    public PluginUI(final Composite parent) {
        model = new SimulationModel();
        controler = new SimulatorControler();
        model.ajouterObservateur(controler);

        // TODO: faire en sorte que le chemin du fichier ne soit pas en dur.
        final Display display = parent.getDisplay();
        parent.setLayout(new GridLayout());

        model.loadModel(fichier);
        model.initialize();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(controler.getListTransitionSize());

            IFireableTransition transition = controler.getTransition(index);
            model.nextStep(transition);

        }
    }

}

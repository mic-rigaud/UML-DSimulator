package org.ensta.uml.sim.handlers;

import plug.core.Observable;
import plug.core.Observateur;
import plug.simulation.ui.SimulationModel;

public class PluginUI implements Observateur {

    @Override
    public void actualiser(Observable o) {
        if (o instanceof SimulationModel) {
            SimulationModel sim = (SimulationModel) o;
            System.out.println("ok!");
        }
    }

}

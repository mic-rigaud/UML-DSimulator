package org.ensta.uml.sim.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import plug.core.IFireableTransition;
import plug.core.Observable;
import plug.core.Observateur;
import plug.simulation.ui.SimulationModel;

public class SimulatorControler implements Observateur {

    List<IFireableTransition> listTransition;

    public SimulatorControler() {
        this.listTransition = new ArrayList<IFireableTransition>();
    }

    @Override
    public void actualiser(Observable o) {
        if (o instanceof SimulationModel) {
            SimulationModel sim = (SimulationModel) o;
            this.actualiserListTransition(sim);
            System.out.println("Par actualisation: " + sim.getCurrentState());
        }
    }

    public void actualiserListTransition(SimulationModel sim) {
        listTransition.clear();
        Collection<IFireableTransition> transitions = sim.getTransition();
        for (IFireableTransition transition : transitions) {
            listTransition.add(transition);
        }
    }

    public IFireableTransition getTransition(int i) {
        System.out.println(listTransition.get(i).toString());
        return listTransition.get(i);
    }

    public int getListTransitionSize() {
        return listTransition.size();
    }

    public IFireableTransition getTransition(Object objet) {
        for (int i = 0; i < this.getListTransitionSize(); i++) {
            if (objet.toString().equals(this.getTransition(i).toString())) {
                return this.getTransition(i);
            }
        }
        return null;
    }

}

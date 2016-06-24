package org.ensta.uml.sim.simulateur;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import plug.core.IFireableTransition;
import plug.core.Observable;
import plug.core.Observateur;
import plug.simulation.ui.SimulationModel;

public class SimulatorControler implements Observateur {

    List<IFireableTransition> listTransition;

    Random random = new Random();

    public SimulatorControler() {
        this.listTransition = new ArrayList<IFireableTransition>();

    }

    @Override
    public void actualiser(Observable o) {
        if (o instanceof SimulationModel) {
            SimulationModel sim = (SimulationModel) o;
            this.actualiserListTransition(sim);

        }
    }

    public void actualiserListTransition(SimulationModel sim) {
        listTransition.clear();
        Collection<IFireableTransition> transitions = sim.getTransition();
        for (IFireableTransition transition : transitions) {
            listTransition.add(transition);
        }
    }

    public List<IFireableTransition> getListTransition() {
        return listTransition;
    }

    public IFireableTransition getTransition(int i) {
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
        return this.getRandomTransition();
    }

    public IFireableTransition getRandomTransition() {
        return listTransition.get(random.nextInt(this.getListTransitionSize()));

    }

    public IFireableTransition getTransition(String objet) {
        for (int i = 0; i < this.getListTransitionSize(); i++) {
            if (objet.equals(this.getTransition(i).toString())) {
                return this.getTransition(i);
            }
        }
        System.out.println("transition non trouve donc aleatoire...");
        return this.getRandomTransition();
    }

}

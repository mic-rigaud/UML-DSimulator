package simulateur;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import plug.core.IConfiguration;
import plug.core.IFireableTransition;
import plug.core.Observable;
import plug.core.Observateur;
import plug.simulation.ui.SimulationModel;

public class SimulatorControler implements Observateur {

    List<IFireableTransition> listTransition;

    Random random = new Random();

    IConfiguration conf;

    public SimulatorControler() {
        this.listTransition = new ArrayList<IFireableTransition>();
        this.conf = null;
    }

    @Override
    public void actualiser(Observable o) {
        if (o instanceof SimulationModel) {
            SimulationModel sim = (SimulationModel) o;
            this.refreshListTransition(sim);
            conf = sim.currentState();
        }
    }

    public void refreshListTransition(SimulationModel sim) {
        this.clearListTransition();
        Collection<IFireableTransition> transitions = sim.getTransition();
        if (transitions.size() == 0) {
            System.out.println("error pas de transitions");
        }
        for (IFireableTransition transition : transitions) {
            listTransition.add(transition);
        }
    }

    public void clearListTransition() {
        listTransition.clear();
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
        if (this.getListTransitionSize() == 0)
            return null;
        return listTransition.get(random.nextInt(this.getListTransitionSize()));
    }

    public IFireableTransition getTransition(String objet) {
        for (int i = 0; i < this.getListTransitionSize(); i++) {
            if (objet.equals(this.getTransition(i).toString())) {
                return this.getTransition(i);
            }
        }
        return null;
    }

    public IConfiguration getConf() {
        return this.conf;
    }

}

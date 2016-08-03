package org.ensta.uml.sim.simulateur;

import simulateur.Observable;
import simulateur.Observateur;

public class ObservateurMock implements Observateur {

    @Override
    public void actualiser(Observable o) {
        // if (o instanceof CommunicationP) {
        // System.out.println("test notifier fonctionne");
        // }
    }

}

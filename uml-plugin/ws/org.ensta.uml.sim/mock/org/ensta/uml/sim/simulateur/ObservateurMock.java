package org.ensta.uml.sim.simulateur;

import org.ensta.uml.sim.views.communication.CommunicationP;

public class ObservateurMock implements org.ensta.uml.sim.views.Observateur {

    @Override
    public void actualiser(org.ensta.uml.sim.views.communication.Observable o) {
        if (o instanceof CommunicationP) {
            System.out.println("test notifier fonctionne");
        }
    }

}

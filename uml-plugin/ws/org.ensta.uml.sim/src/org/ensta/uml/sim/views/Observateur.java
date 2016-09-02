package org.ensta.uml.sim.views;

import org.ensta.uml.sim.views.communication.Observable;

public interface Observateur {
    // Méthode appelée automatiquement lorsque l'état (position ou précision) du
    // GPS change.
    public void actualiser(Observable o);
}

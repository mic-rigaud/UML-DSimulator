package org.ensta.uml.sim.handlers;

import plug.core.Observable;
import plug.core.Observateur;

public class PluginUI implements Observateur {

    @Override
    public void actualiser(Observable arg0) {
        // TODO Auto-generated method stub
        System.out.println("Ca s'actualise!!");
    }

}

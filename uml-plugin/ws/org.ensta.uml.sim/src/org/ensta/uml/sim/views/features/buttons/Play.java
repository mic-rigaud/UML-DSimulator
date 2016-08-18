package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.swt.widgets.Display;
import org.ensta.uml.sim.views.MainView;

/**
 * This class is the behavior of the play of the simulation
 * 
 * @see ActionPlay
 * @author michael
 * @version 1.0
 *
 */
public class Play extends Thread {

    protected MainView view;

    protected static boolean cont = true;

    protected int timeSimulation = 1000;

    public Play(MainView view) {
        this.view = view;
    }

    @Override
    public void run() {
        try {
            while (cont) {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            view.getCommunicationP().putJson("random");
                            view.getCommunicationP().sendMessage();
                            view.refreshPartControl();
                        } catch (Exception e) {
                            e.printStackTrace();
                            view.showMessage("Erreur de Connection au simulateur");
                        }
                    }
                });
                Thread.sleep(timeSimulation);
            }
            cont = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void stopPlay() {
        cont = false;
        ActionPlay.freeActionPlay();
    }

}

package org.ensta.uml.sim.views.features.buttons;

import org.eclipse.swt.widgets.Display;
import org.ensta.uml.sim.views.SimulatorView;

public class Play extends Thread {

    protected SimulatorView view;

    protected static boolean cont = true;

    protected int timeSimulation = 1000;

    public Play(SimulatorView view) {
        this.view = view;
    }

    @Override
    public void run() {
        try {
            while (cont) {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        view.getCommunicationP().putJson("random");
                        if (view.getCommunicationP().sendMessage())
                            view.refreshPartControl();
                        else
                            view.showMessage("Erreur de Connection au simulateur");
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

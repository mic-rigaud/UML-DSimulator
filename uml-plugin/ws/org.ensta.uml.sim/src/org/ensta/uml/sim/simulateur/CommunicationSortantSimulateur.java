package org.ensta.uml.sim.simulateur;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import plug.core.IFireableTransition;
import plug.simulation.ui.SimulationModel;

public class CommunicationSortantSimulateur extends Thread {
    SimulationModel model;

    SimulatorControler controler;

    File fichier; // "/home/michael/Documents/Ensta/Stage/2A/uml-simulateur/plug-build/resources/test/PingPong0.tuml.uml");

    Socket client;

    private int port = 9000;

    private String serverName = "localhost";

    private String message;

    public CommunicationSortantSimulateur(String nomfichier) {
        model = new SimulationModel();
        controler = new SimulatorControler();
        model.ajouterObservateur(controler);
        fichier = new File(nomfichier);
        // TODO: faire en sorte que le chemin du fichier ne soit pas en dur.
        model.loadModel(fichier);
        model.initialize();
        message = new String();

    }

    public CommunicationSortantSimulateur() {

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                client = new Socket(serverName, port);
                while (true) {
                    DataInputStream in = new DataInputStream(client.getInputStream());
                    message = in.readUTF();
                    if (message.equalsIgnoreCase("initialize")) {
                        model.initialize();
                        model.nextStep(controler.getRandomTransition());
                    } else if (message.equalsIgnoreCase("restart")) {
                        model.loadModel(fichier);
                    } else {
                        model.nextStep(controler.getTransition(message));
                    }
                    this.sendMessage(controler.getListTransition());
                }
            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        return;
    }

    public void sendMessage(List<IFireableTransition> liste) throws IOException {
        String listeString = new String();
        for (IFireableTransition transition : liste) {
            if (listeString.isEmpty()) {
                listeString = transition.toString();
            } else {
                listeString = listeString + "#" + transition.toString();
            }
        }
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        try {
            out.writeUTF(listeString);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }

}

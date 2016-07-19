package org.ensta.uml.sim.simulateur;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.xtext.xbase.lib.Pair;

import json.JSONObject;
import plug.core.IFireableTransition;
import plug.simulation.ui.SimulationModel;
import tuml.interpreter.ActiveObject;

public class CommunicationSortantSimulateur extends Thread {
    SimulationModel model;

    SimulatorControler controler;

    File fichier;

    Socket client;

    private int port = 9000;

    private String serverName = "localhost";

    JSONObject jsonOut;

    HashMap<String, String> currentStates;

    public CommunicationSortantSimulateur(String nomfichier) {
        model = new SimulationModel();
        controler = new SimulatorControler();
        model.ajouterObservateur(controler);
        fichier = new File(nomfichier);
        model.loadModel(fichier);
        model.initialize();
        currentStates = new HashMap<String, String>();
        jsonOut = new JSONObject();
        initialiserJson(jsonOut);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                client = new Socket(serverName, port);
                while (true) {
                    DataInputStream in = new DataInputStream(client.getInputStream());
                    JSONObject json = new JSONObject(in.readUTF());
                    System.out.println("  ----   ");

                    if (json.getBoolean("initialize")) {
                        System.out.println("Init");
                        clearHash();
                        model.initialize();
                        model.nextStep(controler.getRandomTransition());
                    } else if (json.getBoolean("restart")) {
                        model.loadModel(fichier);
                        clearHash();
                    } else if (json.getBoolean("reload")) {
                        clearHash();
                        fichier = new File(json.getString("reload_path"));
                        // TODO initialiser la hashmap
                        model.loadModel(fichier); // TODO exception si fichier
                                                  // incorrect a traiter
                    } else if (json.getBoolean("random")) {
                        this.fillJsonOutLastState(controler.getTransition(json.getString("state")));
                        model.nextStep(controler.getRandomTransition());
                    } else {
                        this.fillJsonOutLastState(controler.getTransition(json.getString("state")));
                        model.nextStep(controler.getTransition(json.getString("state")));
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
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings({ "rawtypes" })
    private void fillJsonOutLastState(IFireableTransition transition) {
        ActiveObject a = (ActiveObject) ((Pair) transition.getAction()).getKey();
        currentStates.put(a.getName().toLowerCase(), a.getCurrentState().getName().toLowerCase());
        jsonOut.put("currentStates", currentStates);
        jsonOut.put("currentClass", a.getName().toLowerCase());
    }

    private void clearHash() {
        currentStates.clear();
        jsonOut.put("currentStates", currentStates);
    }

    public void sendMessage(List<IFireableTransition> liste) throws IOException {
        try {
            List<String> transitions = new ArrayList<String>();
            for (IFireableTransition transition : liste) {
                transitions.add(transition.toString());
            }
            jsonOut.put("transitions", transitions);
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeUTF(jsonOut.toString());
            initialiserJson(jsonOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public void initialiserJson(JSONObject json) {
        // TODO remplir currentState avec toutes les classes
        List<String> t = new LinkedList<String>();
        t.add("");
        json.put("transitions", t);
        json.put("error", false);
        json.put("error_message", "");
        json.put("currentStates", currentStates);
        json.put("currentClass", "");
    }

}

package org.ensta.uml.sim.simulateur;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.xtext.xbase.lib.Pair;

import json.JSONArray;
import json.JSONObject;
import plug.core.IFireableTransition;
import plug.simulation.ui.SimulationModel;
import tuml.interpreter.ActiveObject;

public class CommunicationS extends Thread {
    private SimulationModel model;

    private SimulatorControler controler;

    File fichier;

    private Socket client;

    private int port;

    private String serverName = "localhost";

    private JSONObject jsonOut;

    private ArrayList<JSONObject> listCurrentState;

    public CommunicationS(String nomfichier, int port) {
        this.port = port; 
        model = new SimulationModel();
        controler = new SimulatorControler();
        model.ajouterObservateur(controler);
        fichier = new File(nomfichier);
        jsonOut = new JSONObject();
        listCurrentState = new ArrayList<JSONObject>();
        initialiserJson(jsonOut);
        if (fichier.exists()) {
            model.loadModel(fichier);
            model.initialize();
        }
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

                    if (json.getBoolean("reload")) {
                        clearListCurrentState();
                        fichier = new File(json.getString("reload_path"));
                        if (!fichier.exists()) {
                            this.sendMessage("Erreur recharger un fichier");
                            continue;
                        } else
                            model.loadModel(fichier);
                    } else if (!fichier.exists()) {
                        this.sendMessage("Erreur recharger un fichier");
                        continue;
                    } else if (json.getBoolean("initialize")) {
                        System.out.println("Init");
                        clearListCurrentState();
                        model.initialize();
                        model.nextStep(controler.getRandomTransition());
                    } else if (json.getBoolean("restart")) {
                        model.loadModel(fichier);
                        clearListCurrentState();
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

    private void clearListCurrentState() {
        listCurrentState.clear();
        jsonOut.put("currentState", listCurrentState);
    }

    private void sendMessage(List<IFireableTransition> liste) throws IOException {
        List<String> transitions = new ArrayList<String>();
        for (IFireableTransition transition : liste) {
            transitions.add(transition.toString());
        }
        if (transitions.isEmpty()) {
            sendMessage("Il n'y a plus de transitions");
            return;
        }
        jsonOut.put("transitions", transitions);
        sendMessage();
        return;
    }

    private void sendMessage(String error_message) throws IOException {
        jsonOut.put("error", true);
        jsonOut.put("error_message", error_message);
        sendMessage();
        return;
    }

    private void sendMessage() throws IOException {
        try {
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeUTF(jsonOut.toString());
            initialiserJson(jsonOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    @SuppressWarnings({ "rawtypes" })
    private void fillJsonOutLastState(IFireableTransition transition) {
        ActiveObject a = (ActiveObject) ((Pair) transition.getAction()).getKey();
        String classe = a.getName().toLowerCase();
        String state = a.getCurrentState().getName().toLowerCase();
        fillListCurrentState(classe, "1", state);
        jsonOut.put("currentState", listCurrentState);
        jsonOut.put("currentClass", a.getName().toLowerCase());
    }

    private void fillListCurrentState(String classe, String instance, String state) {
        boolean classePresent = false;
        boolean instancePresent = false;
        for (JSONObject jsonClasse : listCurrentState) {
            if (jsonClasse.getString("class").equals(classe)) {
                classePresent = true;
                for (Object obj : jsonClasse.getJSONArray("instance")) {
                    JSONObject jsonInstance = (JSONObject) obj;
                    if (jsonInstance.getString("name").equals(instance)) {
                        instancePresent = true;
                        jsonInstance.put("state", new JSONArray().put(state));
                    }
                }
                if (!instancePresent) {
                    JSONArray list2 = jsonClasse.getJSONArray("instance");
                    JSONObject jsonInstance = new JSONObject();
                    jsonInstance.put("name", instance);
                    jsonInstance.put("state", new JSONArray().put(state));
                    list2.put(jsonInstance);
                }
            }
        }
        if (!classePresent) {
            JSONObject jsonClasse = new JSONObject();
            jsonClasse.put("class", classe);
            ArrayList<JSONObject> list2 = new ArrayList<JSONObject>();
            JSONObject jsonInstance = new JSONObject();
            jsonInstance.put("name", instance);
            jsonInstance.put("state", new JSONArray().put(state));
            list2.add(jsonInstance);
            jsonClasse.put("instance", list2);
            listCurrentState.add(jsonClasse);
        }
    }

    private void initialiserJson(JSONObject json) {
        // TODO remplir currentState avec toutes les classes
        List<String> t = new LinkedList<String>();
        t.add("");
        json.put("transitions", t);
        json.put("error", false);
        json.put("error_message", "");
        json.put("currentState", listCurrentState);
        json.put("currentClass", "");
    }
}

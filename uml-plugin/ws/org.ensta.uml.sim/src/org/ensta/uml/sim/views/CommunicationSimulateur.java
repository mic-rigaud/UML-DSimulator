package org.ensta.uml.sim.views;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import json.JSONArray;
import json.JSONObject;

public class CommunicationSimulateur extends Thread implements Observable {
    private ArrayList<Observateur> tabObservateur;

    private String[] keyOutput = { "state", "initialize", "reload", "play", "stop", "restart", "random" };

    private String[] keyInput = { "transitions", "error", "error_message", "currentClass", "currentState" };

    private JSONObject jsonIn;

    private ServerSocket serverSocket;

    private Socket server;

    private JSONObject jsonOut;

    public CommunicationSimulateur(int port) {
        tabObservateur = new ArrayList<Observateur>();
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonOut = new JSONObject();
        initialiserJson();
        jsonOut.put("reload_path", "");
        jsonIn = new JSONObject();
    }

    @Override
    public void run() {
        try {
            this.server = serverSocket.accept();
            System.out.println("Connexion accepte");
            while (true) {
                DataInputStream in = new DataInputStream(server.getInputStream());
                jsonIn = new JSONObject(in.readUTF());
                if (isMessageAcceptable()) {
                    this.notifierObservateurs();
                } else {
                    System.out.println("error jsonin");
                    this.notifierObservateurs();
                }
            }
        } catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Cette methode ne sert qu'a catch l exception
    public void sendMessage() {
        try {
            send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send() throws IOException {
        try {
            if (server == null) {
                System.out.println("Connection incomplete");
                return;
            }
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            out.writeUTF(jsonOut.toString());
            initialiserJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getTransitions() {
        List<Object> liste = jsonIn.getJSONArray("transitions").toList();
        String[] trans = liste.toArray(new String[liste.size()]);
        return trans;
    }

    public JSONArray getCurrentState() {
        return jsonIn.getJSONArray("currentState");
    }

    @Override
    public void ajouterObservateur(Observateur o) {
        tabObservateur.add(o);
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        tabObservateur.remove(o);

    }

    @Override
    public void notifierObservateurs() {
        for (int i = 0; i < tabObservateur.size(); i++) {
            Observateur o = tabObservateur.get(i);
            o.actualiser(this);
        }
    }

    public void initialiserJson() {
        jsonOut.put("initialize", false);
        jsonOut.put("play", false);
        jsonOut.put("stop", false);
        jsonOut.put("reload", false);
        jsonOut.put("state", "");
        jsonOut.put("restart", false);
        jsonOut.put("random", false);
    }

    public void putJson(String key, String valeur) {
        for (String keys : keyOutput) {
            if (keys.equals(key)) {
                if (key.equals("reload")) {
                    jsonOut.put(key, true);
                    jsonOut.put("reload_path", valeur);
                } else {
                    jsonOut.put(key, valeur);
                }
            }
        }
    }

    public void putJson(String key) {
        for (String keys : keyOutput) {
            if (keys.equals(key)) {
                jsonOut.put(key, true);
            }
        }

    }

    // TODO Il faudrait verifier qu'on a bien une liste de transition sous la
    // forme d une list<String>
    private boolean isMessageAcceptable() {
        for (String key : keyInput) {
            if (!jsonIn.has(key)) {
                return false;
            }
            if (key.equals("currentState")) {
                JSONArray list = jsonIn.getJSONArray("currentState");
                if (list.length() == 0)
                    break;
                for (Object obj : list) {
                    if (obj instanceof JSONObject) {
                        JSONObject json = (JSONObject) obj;
                        if (!json.has("class") || !json.has("instance")) {
                            return false;
                        }
                        for (Object obj2 : json.getJSONArray("instance")) {
                            if (obj2 instanceof JSONObject) {
                                JSONObject json2 = (JSONObject) obj2;
                                if (!json2.has("name") || !json2.has("state")) {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public String getCurrentClass() {
        return jsonIn.getString("currentClass");
    }

    public HashMap<String, String> getStates() {
        JSONObject json = (JSONObject) jsonIn.get("currentStates");
        HashMap<String, String> map = new HashMap<String, String>();
        for (String key : json.toMap().keySet()) {
            map.put(key, json.getString(key));
        }
        return map;
    }

}

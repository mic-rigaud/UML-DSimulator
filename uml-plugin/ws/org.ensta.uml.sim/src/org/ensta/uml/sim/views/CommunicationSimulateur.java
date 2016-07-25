package org.ensta.uml.sim.views;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
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

    private void initialiserJson() {
        this.jsonOut.put("initialize", false);
        this.jsonOut.put("play", false);
        this.jsonOut.put("stop", false);
        this.jsonOut.put("reload", false);
        this.jsonOut.put("state", "");
        this.jsonOut.put("restart", false);
        this.jsonOut.put("random", false);
    }

    public void putJson(String key, String valeur) {
        for (String keys : this.keyOutput) {
            if (keys.equals(key)) {
                if (key.equals("reload")) {
                    this.jsonOut.put(key, true);
                    this.jsonOut.put("reload_path", valeur);
                } else {
                    this.jsonOut.put(key, valeur);
                }
            }
        }
    }

    public void putJson(String key) {
        for (String keys : this.keyOutput) {
            if (keys.equals(key)) {
                this.jsonOut.put(key, true);
            }
        }

    }

    // TODO Il faudrait verifier qu'on a bien une liste de transition sous la
    // forme d une list<String>
    private boolean isMessageAcceptable() {
        for (String key : this.keyInput) {
            if (!this.jsonIn.has(key)) {
                return false;
            }
            if (key.equals("currentState")) {
                JSONArray list = this.jsonIn.getJSONArray("currentState");
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
        return this.jsonIn.getString("currentClass");
    }
}

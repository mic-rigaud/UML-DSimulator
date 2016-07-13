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

    private String[] keyInput = { "transitions", "error", "error_message" };

    private JSONObject jsonIn;

    private ServerSocket serverSocket;

    private Socket server;

    private JSONObject json;

    public CommunicationSimulateur(int port) {
        tabObservateur = new ArrayList<Observateur>();
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
        json = new JSONObject();
        initialiserJson(json);
        json.put("reload_path", "");
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
            out.writeUTF(json.toString());
            initialiserJson(json);
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
        JSONArray array = (JSONArray) jsonIn.get("transitions");
        List<Object> liste = array.toList();
        String[] trans = liste.toArray(new String[liste.size()]);

        return trans;
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

    public void initialiserJson(JSONObject json) {
        json.put("initialize", false);
        json.put("play", false);
        json.put("stop", false);
        json.put("reload", false);
        json.put("state", "");
        json.put("restart", false);
        json.put("random", false);
    }

    public void putJson(String key, String valeur) {
        for (String keys : keyOutput) {
            if (keys.equals(key)) {
                if (key.equals("reload")) {
                    json.put(key, true);
                    json.put("reload_path", valeur);
                } else {
                    json.put(key, valeur);
                }
            }
        }
    }

    public void putJson(String key) {
        for (String keys : keyOutput) {
            if (keys.equals(key)) {
                json.put(key, true);
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
        }
        return true;
    }

}

package org.ensta.uml.sim.views;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import json.JSONArray;
import json.JSONObject;

public class CommunicationP extends Thread implements Observable {
    private ArrayList<Observateur> tabObservateur;

    private String[] keyOutput = { "state", "initialize", "reload", "reloadPath", "play", "stop", "restart", "random" };

    private String[] keyInput = { "transitions", "error", "errorMessage", "currentClass", "currentState" };

    private JSONObject jsonIn;

    private ServerSocket serverSocket;

    private Socket server;

    private JSONObject jsonOut;

    private Semaphore semConnection = new Semaphore(0);

    public CommunicationP(int port) {
        tabObservateur = new ArrayList<Observateur>();
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonOut = new JSONObject();
        initialiserJson();
        jsonOut.put("reloadPath", "");
        jsonIn = new JSONObject();
    }

    @Override
    public void run() {
        try {
            this.server = serverSocket.accept();
            semConnection.release();
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

    public void waitConnection() {
        try {
            semConnection.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Cette methode ne sert qu'a catch l exception
    public boolean sendMessage() {
        try {
            return send();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean send() throws IOException {
        try {
            if (server == null) {
                System.out.println("Connection incomplete");
                return false;
            }
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            out.writeUTF(jsonOut.toString());
            initialiserJson();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            if (server != null)
                server.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getTransitions() {
        List<Object> liste = jsonIn.getJSONArray("transitions").toList();
        System.out.println(liste);
        String[] trans = liste.toArray(new String[liste.size()]);
        return trans;
    }

    public JSONArray getCurrentState() {
        return jsonIn.getJSONArray("currentState");
    }

    @Override
    public boolean ajouterObservateur(Observateur o) {
        return tabObservateur.add(o);
    }

    @Override
    public boolean supprimerObservateur(Observateur o) {
        return tabObservateur.remove(o);
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

    public boolean putJson(String key, String valeur) {
        for (String keys : this.keyOutput) {
            if (keys.equals(key)) {
                if (key.equals("reload")) {
                    this.jsonOut.put(key, true);
                    this.jsonOut.put("reloadPath", valeur);
                } else {
                    this.jsonOut.put(key, valeur);
                }
                return true;
            }
        }
        return false;
    }

    public boolean putJson(String key) {
        for (String keys : this.keyOutput) {
            if (keys.equals(key)) {
                this.jsonOut.put(key, true);
                return true;
            }
        }
        return false;
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

    public boolean isError() {
        if (jsonIn.has("error"))
            return jsonIn.getBoolean("error");
        return false;
    }

    public String getErrorMessage() {
        return jsonIn.getString("errorMessage");
    }
}

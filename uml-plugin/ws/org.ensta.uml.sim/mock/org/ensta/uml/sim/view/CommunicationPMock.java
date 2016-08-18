package org.ensta.uml.sim.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import org.ensta.uml.sim.views.tools.MySemaphore;

import json.JSONArray;
import json.JSONObject;

public class CommunicationPMock extends Thread {

    private Socket server;

    private ServerSocket serverSocket;

    public JSONObject json;

    private String[] keyInput = { "transitions", "error", "errorMessage", "currentClass", "currentState" };

    public JSONObject jsonOut = new JSONObject();

    Semaphore sem = new Semaphore(0);

    private MySemaphore semConnection = new MySemaphore(0, 1);

    public CommunicationPMock() {
        this.initialiserJson();
    }

    @Override
    public void run() {
        while (true) {
            try {
                serverSocket = new ServerSocket(5000);
                server = serverSocket.accept();
                semConnection.release();
                System.out.println("Connexion accepte Mock");
                while (true) {
                    DataInputStream in = new DataInputStream(server.getInputStream());
                    json = new JSONObject(in.readUTF());
                    sem.release();
                }
            } catch (IOException s) {
                close();
                System.out.println("Socket timed out! 1");
            }
        }
    }

    public void waitConnection() {
        try {
            semConnection.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startCommunication(String nouveauPath) {
        jsonOut.put("reload", true);
        jsonOut.put("reloadPath", nouveauPath);
        sendMessage();
    }

    public void close() {
        try {
            if (server != null)
                server.close();
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean isMessageAcceptable() {
        for (String key : this.keyInput) {
            if (!this.json.has(key)) {
                return false;
            }
            if (key.equals("currentState")) {
                JSONArray list = this.json.getJSONArray("currentState");
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

    public void waitSem() throws InterruptedException {
        sem.acquire();
    }

    public JSONObject getJson() {
        return json;
    }

    public void sendMessage() {
        try {
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            out.writeUTF(jsonOut.toString());
            initialiserJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
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

}

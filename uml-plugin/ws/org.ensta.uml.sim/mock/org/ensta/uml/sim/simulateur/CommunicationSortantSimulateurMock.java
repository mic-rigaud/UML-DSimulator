package org.ensta.uml.sim.simulateur;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import json.JSONObject;

public class CommunicationSortantSimulateurMock extends Thread {
    private Socket client;

    private JSONObject json;

    private String[] keyOutput = { "state", "initialize", "reload", "reload_path", "play", "stop", "restart", "random" };

    private Semaphore sem = new Semaphore(0);

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                client = new Socket("localhost", 5000);
                while (true) {
                    DataInputStream in = new DataInputStream(client.getInputStream());
                    json = new JSONObject(in.readUTF());
                    sem.release();
                }
            } catch (IOException | InterruptedException s) {
                System.out.println("Socket timed out! 2");
            }
        }
    }

    public boolean isJsonCorrect() {
        for (String key : keyOutput) {
            if (!json.has(key)) {
                return false;
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
            JSONObject jsonOut = new JSONObject(
                    "{\"error_message\":\"\",\"currentClass\":\"pinger\",\"transitions\":[\"ponger:Ponger::(org.eclipse.uml2.uml.internal.impl.TransitionImpl@5159ca66 (name: <unset>, visibility: <unset>) (isLeaf: false, kind: external))\"],\"error\":false,\"currentState\":[{\"instance\":[{\"name\":\"1\",\"state\":[\"idle\"]}],\"class\":\"ponger\"},{\"instance\":[{\"name\":\"1\",\"state\":[\"idle\"]}],\"class\":\"pinger\"}]}");
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeUTF(jsonOut.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public void sendMauvaisMessage() {
        try {
            JSONObject jsonOut = new JSONObject(
                    "{\"transitions\":[\"ponger:Ponger::(org.eclipse.uml2.uml.internal.impl.TransitionImpl@5159ca66 (name: <unset>, visibility: <unset>) (isLeaf: false, kind: external))\"],\"error\":false,\"currentState\":[{\"instance\":[{\"name\":\"1\",\"state\":[\"idle\"]}],\"class\":\"ponger\"},{\"instance\":[{\"name\":\"1\",\"state\":[\"idle\"]}],\"class\":\"pinger\"}]}");
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeUTF(jsonOut.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

}

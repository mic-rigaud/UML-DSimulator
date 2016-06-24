package org.ensta.uml.sim.views;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.ensta.uml.sim.simulateur.CommunicationSortantSimulateur;

public class CommunicationSimulateur extends Thread implements Observable {
    private ArrayList tabObservateur;

    private String message;

    private ServerSocket serverSocket;

    private Socket server;

    private CommunicationSortantSimulateur comm2;

    public CommunicationSimulateur(int port) {
        tabObservateur = new ArrayList();
        message = new String();
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                this.server = serverSocket.accept();
                System.out.println("Connexion accepte");
                while (true) {
                    DataInputStream in = new DataInputStream(server.getInputStream());
                    message = in.readUTF();
                    this.notifierObservateurs();
                }
            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        if (server == null) {
            System.out.println("Connection incomplete");
            return;
        }
        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            server.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void ajouterObservateur(Observateur o) {
        // TODO Auto-generated method stub
        tabObservateur.add(o);
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        // TODO Auto-generated method stub
        tabObservateur.remove(o);

    }

    @Override
    public void notifierObservateurs() {
        // TODO Auto-generated method stub
        for (int i = 0; i < tabObservateur.size(); i++) {
            Observateur o = (Observateur) tabObservateur.get(i);
            o.actualiser(this);// On utilise la méthode "tiré".
        }
    }

}

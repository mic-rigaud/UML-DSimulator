package org.ensta.uml.sim.views.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ensta.uml.sim.views.Observable;
import org.ensta.uml.sim.views.Observateur;
import org.ensta.uml.sim.views.model.StateModel;
import org.ensta.uml.sim.views.tools.MySemaphore;

import json.JSONArray;
import json.JSONObject;

/**
 * <b>This class permit to communicate with the simulator </b>
 * <p>
 * This class use json object to communicate. It send and receive json object.
 * If you want to send a message you need to had your message in a json object
 * before send a message
 * 
 * @author michael
 * @version 1.0
 * 
 */
public class CommunicationP extends Thread implements Observable {
    /**
     * List of observer that the class notify when it receive a message
     */
    private ArrayList<Observateur> tabObservateur;

    /**
     * Key possible for json object sent
     */
    private String[] keyOutput = { "state", "initialize", "reload", "reloadPath", "play", "stop", "restart", "random" };

    /**
     * Key possible for the json object received
     * 
     * @see CommunicationP#isMessageAcceptable()
     */
    private String[] keyInput = { "transitions", "error", "errorMessage", "currentClass", "currentState" };

    /**
     * The json object that it receive
     * 
     * @see CommunicationP#getCurrentClass()
     * @see CommunicationP#getCurrentState()
     * @see CommunicationP#getTransitions()
     * @see CommunicationP#isError()
     * @see CommunicationP#getErrorMessage()
     */
    private JSONObject jsonIn;

    /**
     * The serverSocket object
     */
    private ServerSocket serverSocket;

    /**
     * Our socket
     */
    private Socket server;

    /**
     * The json object that it send
     * 
     * @see CommunicationP#putJson(String)
     * @see CommunicationP#putJson(String, String)
     * @see CommunicationP#initializeJson()
     */
    private JSONObject jsonOut;

    /**
     * This semaphore object permit to wait for a new connection
     * 
     * @see CommunicationP#waitConnection(int)
     */
    private MySemaphore semConnection = new MySemaphore(0, 1);

    /**
     * This boolean is false if there is no socket connected and otherwise true
     */
    private boolean connected;

    /**
     * The number of the port where the socket is initiate
     * 
     * @see CommunicationP#CommunicationP(int)
     */
    private int port;

    /**
     * Constructor CommunicationP
     * 
     * @param port
     *            The number of the port for the socket
     */
    public CommunicationP(int port) {
        connected = false;
        tabObservateur = new ArrayList<Observateur>();
        this.port = port;
        jsonOut = new JSONObject();
        initializeJson();
        jsonOut.put("reloadPath", "");
        jsonIn = new JSONObject();
    }

    /**
     * This function initiate the simulator after the connection. It send the
     * path of the current project to the simulator.
     * 
     * @throws Exception
     */
    private void initializeCommunication() throws Exception {
        semConnection.release();
        System.out.println("Connexion accepte");
        putJson("reload");
        sendMessage();
        connected = true;
    }

    /**
     * Start the thread and the communication
     * 
     * After the first connection it sent to the simulator the path of the
     * project. After that the simulator will respond with a first empty
     * message. If there is an error with the file, there will be written on his
     * first message.
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            this.server = serverSocket.accept();
            initializeCommunication();
            while (connected) {
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
            System.out.println("Fin de la communication");
        } catch (Exception e) {
            System.out.println("Error: initialisation");
        }

    }

    /**
     * 
     * @param secondTime
     *            the time during we will wait the connection in second
     * @return true if it well connected, false otherwise (time exceeeded or
     *         InterruptedException)
     */
    public boolean waitConnection(int secondTime) {
        try {
            return semConnection.tryAcquire(secondTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This function send the message and return a boolean to know if it send
     * well
     * 
     * @return true if no problem during the sends, false otherwise
     * @throws Exception
     */
    public void sendMessage() throws Exception {
        if (server == null || server.isClosed()) {
            System.out.println("Connection incomplete");
            throw new Exception("Connection incomplete");
        }
        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        out.writeUTF(jsonOut.toString());
        initializeJson();
    }

    /**
     * Close the communication
     */
    public void close() {
        try {
            if (server != null)
                server.close();
            if (serverSocket != null)
                serverSocket.close();
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return transitions possible for the next step
     */
    public String[] getTransitions() {
        List<Object> liste = jsonIn.getJSONArray("transitions").toList();
        String[] trans = liste.toArray(new String[liste.size()]);
        return trans;
    }

    /**
     * 
     * @return the currentState of the project. It is a json object wich contain
     *         the name of all class, there instances for each class, and the
     *         state in the State Machine Diagram for each instances
     */
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

    /**
     * Create the jsonOut object with all its attributes
     */
    private void initializeJson() {
        this.jsonOut.put("initialize", false);
        this.jsonOut.put("play", false);
        this.jsonOut.put("stop", false);
        this.jsonOut.put("reload", false);
        this.jsonOut.put("state", "");
        this.jsonOut.put("restart", false);
        this.jsonOut.put("random", false);
    }

    /**
     * Verify if the key exist and if it is put the value in the jsonOut Object
     * 
     * @param key
     *            The name of the key
     * @param valeur
     *            The value for this key
     * @return true if no problem, false otherwise
     */
    public boolean putJson(String key, String valeur) {
        for (String keys : this.keyOutput) {
            if (keys.equals(key)) {
                this.jsonOut.put(key, valeur);
                return true;
            }
        }
        return false;
    }

    /**
     * Verify if the key exist and if it is put "true" for this key in the
     * jsonOut Object If the key if "reload" it change the "reloadPath"
     * 
     * @param key
     *            The name of the key
     * @return true if no problem, false otherwise
     */

    public boolean putJson(String key) {
        for (String keys : this.keyOutput) {
            if (keys.equals(key)) {
                this.jsonOut.put(key, true);
                if (key.equals("reload")) {
                    this.jsonOut.put("reloadPath", StateModel.getCurrentProjectPath());
                }
                return true;
            }
        }
        return false;
    }

    // TODO Il faudrait verifier qu'on a bien une liste de transition sous la
    // forme d une list<String>
    /**
     * Verify if the message received is correct. If the jsonIn have every key
     * with the good form
     * 
     * @return true if no problem, false otherwise
     */
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

    /**
     * Give the current class send by the simulator
     * 
     * @return the current class
     */
    public String getCurrentClass() {
        return this.jsonIn.getString("currentClass");
    }

    /**
     * Verify is the simulator send an error
     * 
     * @return true if there is an error
     */
    public boolean isError() {
        if (jsonIn.has("error"))
            return jsonIn.getBoolean("error");
        return false;
    }

    /**
     * Give the error message
     * 
     * @return error message
     */
    public String getErrorMessage() {
        return jsonIn.getString("errorMessage");
    }

}

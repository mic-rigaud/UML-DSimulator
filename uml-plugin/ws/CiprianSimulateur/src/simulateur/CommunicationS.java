package simulateur;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
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

    private File fichier;

    private Socket client;

    private int port;

    private String serverName = "localhost";

    private JSONObject jsonOut;

    private ArrayList<JSONObject> listCurrentState;

    private boolean fileError;

    public CommunicationS(int port) {
        this.port = port;
        model = new SimulationModel();
        controler = new SimulatorControler();
        model.ajouterObservateur(controler);
        jsonOut = new JSONObject();
        listCurrentState = new ArrayList<JSONObject>();
        initialiserJson(jsonOut);
        fileError = true;
    }

    private boolean loadModel() throws IOException {
        try {
            if (fichier.exists()) {
                model.loadModel(fichier);
                model.initialize();
                return true;
            } else {
                this.sendError("Erreur recharger un fichier");
                return false;
            }
        } catch (RuntimeException e) {
            this.sendError("Erreur fichier incompatible");
            return false;
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
                        fichier = new File(json.getString("reloadPath"));
                        if (fileError = !loadModel())
                            continue;
                    } else if (json.getBoolean("restart")) {
                        clearListCurrentState();
                        if (fileError = !loadModel())
                            continue;
                    } else if (fileError) {
                        this.sendError("Erreur recharger un fichier");
                        continue;
                    } else if (json.getBoolean("initialize")) {
                        System.out.println("Init");
                        clearListCurrentState();
                        if (!this.nextTransition(controler.getRandomTransition()))
                            continue;
                    } else if (json.getBoolean("random")) {
                        if (!this.nextTransition(controler.getRandomTransition()))
                            continue;
                    } else {
                        if (!this.nextTransition(controler.getTransition(json.getString("state"))))
                            continue;
                    }
                    this.sendMessage(controler.getListTransition());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private boolean nextTransition(IFireableTransition fire) throws IOException {
        if (fire != null) {
            this.fillJsonOutLastState(fire);
            model.nextStep(fire);
            return true;
        } else {
            this.sendError("Erreur transition non trouve");
            fileError = true;
            return false;
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
            sendError("Il n'y a pas (ou plus) de transitions");
            return;
        }
        jsonOut.put("transitions", transitions);
        sendMessage();
        return;
    }

    private void sendError(String errorMessage) throws IOException {
        jsonOut.put("error", true);
        jsonOut.put("errorMessage", errorMessage);
        jsonOut.put("transitions", new String[] { "Error" });
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
            // // Code pour test double instance
            // JSONObject jsonInstance2 = new JSONObject();
            // jsonInstance2.put("name", "2");
            // jsonInstance2.put("state", new JSONArray().put(state));
            // list2.add(jsonInstance2);
            // //////////////////////////////////
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
        json.put("errorMessage", "");
        json.put("currentState", listCurrentState);
        json.put("currentClass", "");
    }
}

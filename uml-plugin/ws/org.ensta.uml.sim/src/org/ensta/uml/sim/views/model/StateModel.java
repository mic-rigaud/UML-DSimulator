package org.ensta.uml.sim.views.model;

import java.util.HashMap;

import json.JSONArray;

/**
 * this class contain all object whiwh permit to know how is the simulation
 * 
 * @author michael
 * @version 1.0
 */
public class StateModel {
    /**
     * is the current project name of the session
     */
    private static String currentProjectName;

    /**
     * is the last class which the simulator change a state
     */
    private static String currentClasse;

    /**
     * is the current instance that the user want to see for all class
     * <p>
     * className : insanceName
     */
    private static HashMap<String, String> currentInstances;

    /**
     * is the states of the simulator with all class, instances and there states
     */
    private static JSONArray currentState;

    /**
     * is the path of the project file "model.uml"
     */
    private static String currentProjectPath;

    public static void initialize() {
        currentState = new JSONArray();
        currentClasse = new String();
        currentInstances = new HashMap<String, String>();
        currentProjectName = new String();
        currentProjectPath = new String();
    }

    public static void refreshElements(String classe, JSONArray state) {
        currentClasse = classe.toLowerCase();
        currentState = state;
    }

    public static JSONArray getCurrentState() {
        return currentState;
    }

    public static void putCurrentInstances(String className, String instanceName) {
        currentInstances.put(className, instanceName);
    }

    public static void putCurrentInstances(String className) {
        if (!currentInstances.containsKey(className))
            putCurrentInstances(className, "all");
    }

    public static boolean isCurrentInstancesContains(String className, String instanceName) {
        if (currentInstances.containsKey(className)) {
            if (currentInstances.get(className).equalsIgnoreCase(instanceName))
                return true;
        }
        return false;
    }

    public static String getCurrentClasse() {
        return currentClasse;
    }

    public static String getCurrentInstances(String key) {
        if (currentInstances.containsKey(key))
            return currentInstances.get(key);
        return "";
    }

    public static String getCurrentProjectName() {
        return currentProjectName;
    }

    public static void setCurrentProjectName(String currentProjectName) {
        StateModel.currentProjectName = currentProjectName;
    }

    public static String getCurrentProjectPath() {
        return currentProjectPath;
    }

    public static void setCurrentProjectPath(String currentProjectPath) {
        StateModel.currentProjectPath = currentProjectPath;
    }
}

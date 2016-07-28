package org.ensta.uml.sim.views.model;

import java.util.HashMap;

import json.JSONArray;

public class StateModel {
    private static String currentProjectName;

    private static String currentClasse;

    private static HashMap<String, String> currentInstances;

    private static JSONArray currentState;

    public static void initialize() {
        currentState = new JSONArray();
        currentClasse = new String();
        currentInstances = new HashMap<String, String>();
        currentProjectName = new String();
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
}

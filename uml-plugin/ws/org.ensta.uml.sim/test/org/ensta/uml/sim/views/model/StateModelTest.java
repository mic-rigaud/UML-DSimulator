package org.ensta.uml.sim.views.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import json.JSONArray;

public class StateModelTest {
    private static JSONArray currentState;

    private static String currentClass;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        currentState = new JSONArray("[{\"instance\":[{\"name\":\"1\",\"state\":[\"idle\"]}],\"class\":\"ponger\"},{\"instance\":[{\"name\":\"1\",\"state\":[\"idle\"]}],\"class\":\"pinger\"}]");
        currentClass = "ponger";
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        StateModel.initialize();
        StateModel.refreshElements(currentClass, currentState);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testInitialize() {
        StateModel.initialize();
        assertEquals("", StateModel.getCurrentClasse());
        assertEquals("", StateModel.getCurrentProjectName());
        assertEquals("", StateModel.getCurrentInstances("toto"));
        assertEquals(0, StateModel.getCurrentState().length());
    }

    @Test
    public void testGetCurrentInstances() {
        assertEquals("", StateModel.getCurrentInstances("toto"));
        StateModel.putCurrentInstances("ping");
        assertEquals("all", StateModel.getCurrentInstances("ping"));
    }

    @Test
    public void testRefreshElements() {
        assertEquals(currentState, StateModel.getCurrentState());
    }

    @Test
    public void testGetCurrentState() {
        assertEquals(currentState, StateModel.getCurrentState());
    }

    @Test
    public void testPutCurrentInstancesString() {
        StateModel.putCurrentInstances("coucou");
        assertEquals(true, StateModel.isCurrentInstancesContains("coucou", "all"));
        StateModel.putCurrentInstances("coucou", "bonjour");
        assertEquals(true, StateModel.isCurrentInstancesContains("coucou", "bonjour"));
        StateModel.putCurrentInstances("coucou");
        assertEquals(true, StateModel.isCurrentInstancesContains("coucou", "bonjour"));
    }

    @Test
    public void testPutCurrentInstancesStringString() {
        StateModel.putCurrentInstances("coucou", "bonjour");
        assertEquals(true, StateModel.isCurrentInstancesContains("coucou", "bonjour"));
        StateModel.putCurrentInstances("coucou", "tcho");
        assertEquals(true, StateModel.isCurrentInstancesContains("coucou", "tcho"));
    }

    @Test
    public void testIsCurrentInstancesContains() {
        assertEquals(false, StateModel.isCurrentInstancesContains("coucou", "bonjour"));
        StateModel.putCurrentInstances("coucou", "bonjour");
        assertEquals(true, StateModel.isCurrentInstancesContains("coucou", "bonjour"));
        assertEquals(false, StateModel.isCurrentInstancesContains("coucou", "pasbon"));
        assertEquals(false, StateModel.isCurrentInstancesContains("pasbon", "bonjour"));
    }
}

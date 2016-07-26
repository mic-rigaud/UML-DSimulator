package org.ensta.uml.sim.views;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import json.JSONArray;

public class DesignModificateurTest {

    private DesignModificateur design;

    private static JSONArray currentState;

    private static String currentClass;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        currentState = new JSONArray("[{\"instance\":[{\"name\":\"1\",\"state\":[\"idle\"]}],\"class\":\"ponger\"},{\"instance\":[{\"name\":\"1\",\"state\":[\"idle\"]}],\"class\":\"pinger\"}]");
        currentClass = "ponger";
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        return;
    }

    @Before
    public void setUp() throws Exception {
        design = new DesignModificateur();
        design.refreshElements(currentClass, currentState);
    }

    @After
    public void tearDown() throws Exception {
        design = null;
    }

    @Test
    public void testRefreshElements() {
        assertEquals("JsonArray not good", currentState, design.getCurrentState());
    }

    @Test
    public void testGetCurrentState() {
        assertEquals("JsonArray not good", currentState, design.getCurrentState());
    }

    @Test
    public void testPutCurrentInstancesString() {
        design.putCurrentInstances("coucou");
        assertEquals(true, design.isCurrentInstancesContains("coucou", "all"));
        design.putCurrentInstances("coucou", "bonjour");
        assertEquals(true, design.isCurrentInstancesContains("coucou", "bonjour"));
        design.putCurrentInstances("coucou");
        assertEquals(true, design.isCurrentInstancesContains("coucou", "bonjour"));
    }

    @Test
    public void testPutCurrentInstancesStringString() {
        design.putCurrentInstances("coucou", "bonjour");
        assertEquals(true, design.isCurrentInstancesContains("coucou", "bonjour"));
        design.putCurrentInstances("coucou", "tcho");
        assertEquals(true, design.isCurrentInstancesContains("coucou", "tcho"));
    }

    @Test
    public void testIsCurrentInstancesContains() {
        assertEquals(false, design.isCurrentInstancesContains("coucou", "bonjour"));
        design.putCurrentInstances("coucou", "bonjour");
        assertEquals(true, design.isCurrentInstancesContains("coucou", "bonjour"));
        assertEquals(false, design.isCurrentInstancesContains("coucou", "pasbon"));
        assertEquals(false, design.isCurrentInstancesContains("pasbon", "bonjour"));
    }

}

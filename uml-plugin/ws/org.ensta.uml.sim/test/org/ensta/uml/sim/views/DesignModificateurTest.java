package org.ensta.uml.sim.views;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import json.JSONArray;
import junit.framework.TestCase;

public class DesignModificateurTest extends TestCase {

    private DesignModificateur design;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        design = new DesignModificateur();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        design = null;
    }

    @Test
    public void testDesignModificateur() {
        fail("Not yet implemented");
    }

    @Test
    public void testInitialiser() {
        fail("Not yet implemented");
    }

    @Test
    public void testRefreshElements() {
        JSONArray json = new JSONArray();
        json.put(50);
        design.refreshElements("coucou", json);
        assertEquals("JsonArray not good", json, design.getCurrentState());
    }

    @Test
    public void testRefreshColor() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetCurrentState() {
        assertTrue("Erreur", design.getCurrentState().length() == 0);
    }

}

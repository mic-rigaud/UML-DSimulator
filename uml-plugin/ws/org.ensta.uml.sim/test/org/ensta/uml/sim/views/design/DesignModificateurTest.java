package org.ensta.uml.sim.views.design;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

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

    }

    @After
    public void tearDown() throws Exception {
        design = null;
    }

}

package org.ensta.uml.sim.views.tools;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ToolsTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetImageDescriptor() {
        assertNotNull(Tools.getImageDescriptor("icons/play.gif"));
        assertNotNull(Tools.getImageDescriptor("icons/imageInexistante.gif"));
    }

    @Test
    public void testGetImage() {
        assertNotNull(Tools.getImage("icons/play.gif"));
        assertNotNull(Tools.getImage("icons/imageInexistante.gif"));
    }

}

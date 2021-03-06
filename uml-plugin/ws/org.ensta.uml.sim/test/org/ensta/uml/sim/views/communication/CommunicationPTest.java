/**
 * 
 */
package org.ensta.uml.sim.views.communication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.ensta.uml.sim.simulateur.CommunicationSMock;
import org.ensta.uml.sim.simulateur.ObservateurMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author michael
 *
 */
public class CommunicationPTest {
    private CommunicationP comm;

    private static CommunicationSMock comm2;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        comm2 = new CommunicationSMock();
        comm2.start();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        comm = new CommunicationP(5000);
    }

    /**
     * @throws java.lang.Exception
     */
    @SuppressWarnings("deprecation")
    @After
    public void tearDown() throws Exception {
        comm.stop();
        comm.close();
        comm = null;
    }

    /**
     * Test method for
     * {@link org.ensta.uml.sim.views.communication.CommunicationP#run()}.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testRun() throws InterruptedException {
        System.out.println("toto");
        comm.start();
        comm.waitConnection(10);
        comm2.sendMessage();
        Thread.sleep(1000);
        comm2.sendMauvaisMessage();
    }

    /**
     * Test method for
     * {@link org.ensta.uml.sim.views.communication.CommunicationP#sendMessage()}.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testSendMessage() throws InterruptedException {
        try {
            comm.sendMessage();
            fail("Il devait y avoir une exception");
        } catch (Exception e) {
        }
        comm.start();
        comm.waitConnection(10);
        try {
            comm.sendMessage();
        } catch (Exception e) {
            fail("exception non voulu");
        }
        comm2.waitSem();
        assertNotNull(comm2.getJson());
        assertEquals(true, comm2.isJsonCorrect());
        assertEquals(false, comm2.getJson().getBoolean("initialize"));
        assertEquals(false, comm2.getJson().getBoolean("play"));
        assertEquals(false, comm2.getJson().getBoolean("stop"));
        assertEquals(true, comm2.getJson().getBoolean("reload"));
        assertEquals(false, comm2.getJson().getBoolean("restart"));
        assertEquals(false, comm2.getJson().getBoolean("random"));
        assertEquals("", comm2.getJson().getString("state"));
        assertEquals("", comm2.getJson().getString("reloadPath"));
    }

    /**
     * Test method for
     * {@link org.ensta.uml.sim.views.communication.CommunicationP#getTransitions()}.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGetTransitions() throws InterruptedException {
        comm.start();
        comm.waitConnection(10);
        comm2.sendMessage();
        Thread.sleep(1000);
        assertNotNull(comm.getTransitions());
        assertEquals(1, comm.getTransitions().length);
    }

    /**
     * Test method for
     * {@link org.ensta.uml.sim.views.communication.CommunicationP#getCurrentState()}.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGetCurrentState() throws InterruptedException {
        comm.start();
        comm.waitConnection(10);
        comm2.sendMessage();
        Thread.sleep(1000);
        assertNotNull(comm.getCurrentState());
    }

    /**
     * Test method for
     * {@link org.ensta.uml.sim.views.communication.CommunicationP#ajouterObservateur(org.ensta.uml.sim.views.Observateur)}.
     */
    @Test
    public void testAjouterObservateur() {
        ObservateurMock o = new ObservateurMock();
        assertEquals(true, comm.ajouterObservateur(o));
    }

    /**
     * Test method for
     * {@link org.ensta.uml.sim.views.communication.CommunicationP#supprimerObservateur(org.ensta.uml.sim.views.Observateur)}.
     */
    @Test
    public void testSupprimerObservateur() {
        ObservateurMock o = new ObservateurMock();
        comm.ajouterObservateur(o);
        assertEquals(true, comm.supprimerObservateur(o));
        assertEquals(false, comm.supprimerObservateur(o));
    }

    /**
     * Test method for
     * {@link org.ensta.uml.sim.views.communication.CommunicationP#notifierObservateurs()}.
     */
    @Test
    public void testNotifierObservateurs() {
        ObservateurMock o = new ObservateurMock();
        comm.ajouterObservateur(o);
        comm.notifierObservateurs();
    }

    /**
     * Test method for
     * {@link org.ensta.uml.sim.views.communication.CommunicationP#putJson(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testPutJsonStringString() {
        assertEquals(true, comm.putJson("state", "bonjour"));
        assertEquals(false, comm.putJson("toto", "toto"));
        assertEquals(true, comm.putJson("reload", "toto"));
    }

    /**
     * Test method for
     * {@link org.ensta.uml.sim.views.communication.CommunicationP#putJson(java.lang.String)}.
     */
    @Test
    public void testPutJsonString() {
        assertEquals(true, comm.putJson("play"));
        assertEquals(false, comm.putJson("toto"));
    }

    /**
     * Test method for
     * {@link org.ensta.uml.sim.views.communication.CommunicationP#getCurrentClass()}.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGetCurrentClass() throws InterruptedException {
        comm.start();
        comm.waitConnection(10);
        comm2.sendMessage();
        Thread.sleep(1000);
        assertEquals("pinger", comm.getCurrentClass());
    }

}

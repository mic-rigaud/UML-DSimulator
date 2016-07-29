package org.ensta.uml.sim.simulateur;

import static org.junit.Assert.assertEquals;

import org.ensta.uml.sim.view.CommunicationPMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommunicationSTest {

    private CommunicationS communicationS;

    private static CommunicationPMock comm2;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        comm2 = new CommunicationPMock();
        comm2.start();
        communicationS = new CommunicationS(5000);
        communicationS.start();
        comm2.waitConnection();
        comm2.startCommunication("/home/michael/Documents/Ensta/Stage/2A/uml-plugin/ws/org.ensta.uml.sim/test/resources/model.uml");
        comm2.waitSem();
    }

    @SuppressWarnings("deprecation")
    @After
    public void tearDown() throws Exception {
        communicationS.stop();
        comm2.stop();
        comm2.close();
    }

    @Test
    public void testCommunicationS() {
        CommunicationS communicationS2 = new CommunicationS(5000);
        // assertEquals(true, communicationS2.fichier.exists());
        // communicationS2 = new CommunicationS(5000);
        // assertEquals(false, communicationS2.fichier.exists());
    }

    @Test
    public void testReceiveStop() throws InterruptedException {
        comm2.jsonOut.put("stop", true);
        comm2.sendMessage();
    }

    @Test
    public void testReceiveInitialize() throws InterruptedException {
        comm2.jsonOut.put("initialize", true);
        comm2.sendMessage();
        comm2.waitSem();
        assertEquals(true, comm2.isMessageAcceptable());
    }

    @Test
    public void testReceiveRandom() throws InterruptedException {
        comm2.jsonOut.put("random", true);
        comm2.sendMessage();
        comm2.waitSem();
        assertEquals(true, comm2.isMessageAcceptable());
    }

    @Test
    public void testReceivereReload() throws InterruptedException {
        // test reload sans erreur
        comm2.jsonOut.put("reload", true);
        comm2.jsonOut.put("reloadPath", "/home/michael/Documents/Ensta/Stage/2A/uml-plugin/ws/org.ensta.uml.sim/test/resources/model.uml");
        comm2.sendMessage();
        comm2.waitSem();
        assertEquals(true, comm2.isMessageAcceptable());
        assertEquals(false, comm2.json.getBoolean("error"));
        // test reload avec mauvais fichier
        comm2.jsonOut.put("reload", true);
        comm2.jsonOut.put("reloadPath", "fichier_inexistant");
        comm2.sendMessage();
        comm2.waitSem();
        assertEquals(true, comm2.isMessageAcceptable());
        assertEquals(true, comm2.json.getBoolean("error"));
        // test ce qu il se passe si pas de fichier
        comm2.sendMessage();
        comm2.waitSem();
        assertEquals(true, comm2.isMessageAcceptable());
        assertEquals(true, comm2.json.getBoolean("error"));
    }

    @Test
    public void testReceivereRestart() throws InterruptedException {
        comm2.jsonOut.put("restart", true);
        comm2.sendMessage();
        comm2.waitSem();
        assertEquals(true, comm2.isMessageAcceptable());
    }

    @Test
    public void testReceivereSates() throws InterruptedException {
        comm2.sendMessage();
        comm2.waitSem();
        assertEquals(true, comm2.isMessageAcceptable());
    }
}

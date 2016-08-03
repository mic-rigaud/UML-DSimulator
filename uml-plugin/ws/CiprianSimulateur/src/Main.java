import simulateur.CommunicationS;

public class Main {

    public static void main(String[] args) {
        CommunicationS communicationS = new CommunicationS(9000);
        communicationS.start();
        while (communicationS.isAlive()) {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

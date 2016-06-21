
import java.io.*;
import java.util.Scanner;


public class fils
{
    public static void main(String arg[])
    {
	
	
	System.out.println("hello father");

	
	
	Scanner scan = new Scanner(System.in); //standard input stream
	String x = scan.nextLine();
	

	System.out.println("message of my father: "+x);
	
	
	    
    }


    public static void waitManySec(long s) {
     try {
       Thread.currentThread().sleep(s * 1000);
       }
     catch (InterruptedException e) {
       e.printStackTrace();
       }
     }

}

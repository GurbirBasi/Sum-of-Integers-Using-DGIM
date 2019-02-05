import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class P2 {

    static Lock lock = new ReentrantLock();

    public static void main(String[] args) {

        int errorPercentage = 0, portNumber = 0;

        //First input is the error percentage
        Scanner scan = new Scanner(System.in);
        Matcher m = Pattern.compile("\\d+").matcher(scan.next());
        if(m.find()){
            try{
                errorPercentage = Integer.parseInt(m.group());
                if(!(errorPercentage <= 50 && errorPercentage >0)){
                    System.out.println("Please Enter a number between [0-50]");
                    System.exit(0);
                }
            }catch (IllegalStateException e){
                System.out.println("Exception Found: " + e);
            }
        }else{
            System.out.println("No Digits Found." +
                    "\nPlease Enter a number between [0-50]:");
            System.exit(0);
        }
        // rValue (max number of containers of a specified size) associated with errorPercentage
        int rValue = 100/errorPercentage;

        //Second Line is the host pair in format "Host:Port"
        String[] hostPort = scan.next().split(":");
        String hostName = hostPort[0];
        try{
            portNumber = Integer.parseInt(hostPort[1]);
        }catch(Exception e){
            System.out.println("Port Number in wrong format: " + e);
        }

        //create the two threads
        new Server(hostName, portNumber, rValue);
        new inputRead();

    }
}


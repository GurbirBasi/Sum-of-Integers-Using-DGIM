import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class inputRead implements Runnable{

    static BufferedReader buffer;

    inputRead(){
        Thread t = new Thread(this, "Input");
        t.start();
    }

    public void run() {
        try {
            buffer = new BufferedReader(new InputStreamReader(System.in));
            while (!Thread.interrupted()) {

                if (buffer.ready()) {

                    System.out.println();
                    String data = buffer.readLine();
                    System.out.println(data);
                    P2.lock.lock();
                    if(!data.equals("end")){
                        Matcher m = Pattern.compile("What is the sum for last \\d+ integers\\?").matcher(data);
                        if(m.find()){
                            long inputKvalue = Long.parseLong(data.replaceAll("[^0-9]", ""));

                            if(Server.currentTimeStamp < inputKvalue){
                                System.out.println("There have not been " + inputKvalue + " integers from server yet. " +
                                        "Current timestamp is " + Server.currentTimeStamp + ".");
                                System.out.println();
                            }else{
                                System.out.println("The sum of last " + inputKvalue +
                                        " values is " + Server.getSum(inputKvalue) + ".");
                                System.out.println();
                            }
                        }
                        else{
                            System.out.println("The query is in the wrong format.\n" +
                                    "It should be like 'What is the sum for last <number> integers?'");
                        }

                    }else{
                        System.out.println("The end of file has been reached.");
                        System.exit(0);
                    }

                    P2.lock.unlock();

                }
            }
        }catch(IOException e){
            System.out.println("Exception: " + e);
        }
        System.out.println("Thread Interrupted!");
    }
}


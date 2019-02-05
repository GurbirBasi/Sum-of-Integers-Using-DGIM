
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Server implements Runnable {
    private String host;
    private int portNum;
    private BufferedReader in;
    private int r;
    static long currentTimeStamp = 0;
    static Buckets[] digitBit;

    Server(String host, int portNum, int r){
        this.r = r;
        this.host = host;
        this.portNum = portNum;
        new Thread(this, "Server Thread").start();
    }

    
    public void run(){
        try {
            Socket socket = new Socket(host, portNum);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            System.out.println("Problem with Socket: " + e);
            System.exit(0);
        }


        //create a placeholder for the buckets for each bitstream in a 16 bits number
        digitBit = new Buckets[16];
        for(int i = 0; i < 16; i++){
            digitBit[i] = null;
        }


        //Get integers from the server.
        try{
            P2.lock.lock();
            getDataFromServer();
            P2.lock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //gets data from Server
    private void getDataFromServer() throws IOException {
        String data = "";
        while ((data = in.readLine()) != null) {
            int integer = Integer.parseInt(data);
            //P2.lock.lock();
            //System.out.print(integer + "\t");
            //P2.lock.unlock();

            //Convert integer to bits
            String binaryString = Integer.toBinaryString(integer);
            int value = binaryString.length();
            if (value != 16) {
                for (int j = 0; j < 16 - value; j++) {
                    binaryString = "0" + binaryString;
                }
            }
            char[] bits = binaryString.toCharArray();

            for (int i = 0; i < 16; i++) {
                if (bits[i] == '1') {

                    if (digitBit[i] == null) {
                        // create new linked list Buckets that will store bucket information
                        digitBit[i] = new Buckets(currentTimeStamp);
                    } else {
                        // create a new bucket for Buckets with size 1
                        Bucket bucket = new Bucket();
                        bucket.size = 1;
                        bucket.startTimeStamp = bucket.endTimestamp = currentTimeStamp;
                        bucket.next = digitBit[i].bucket;
                        digitBit[i].bucket = bucket;
                        digitBit[i].bucketSizeFrequency[0]++;
                        // Combine buckets if number of size 1 buckets is more than r
                        combineBuckets(digitBit[i], r);
                    }
                }
            }
            currentTimeStamp++;
        }
    }

    //function to find the sum of last numbers num using DGIM
    public static double getSum(long num) {
        double total = 0;
        for(int i = 0; i < digitBit.length; i ++){
            double sum = 0;
            Bucket bkt = digitBit[i].bucket;
            //find sum of buckets of size (j+1)
            while(bkt.endTimestamp >= (currentTimeStamp - num + 1)){
                sum += bkt.size;
                if(bkt.next != null){
                    bkt = bkt.next;
                }else{
                    break;
                }
            }
            if(bkt.startTimeStamp  >= (currentTimeStamp - num + 1)){
                sum += (bkt.size)/2;
            }
            //add the contribution of ith position bits to the total
            total += sum * Math.pow(2, i);
        }
        return total;
    }
    //function to combine Buckets if there are total buckets of specific size more than rValue.
    static void combineBuckets(Buckets buckets, int r){
        int j = 0;
        Bucket bkt = buckets.bucket;
        while ((buckets.bucketSizeFrequency[j]) > r) {
            //combine last two buckets of size 2^j
            for(int i = 0; i < r - 1; i++){
                bkt = bkt.next;
            }
            bkt.size *= 2;

            //check if it is not the last bucket in the linked list
            if(bkt.next != null){
                bkt.endTimestamp = bkt.next.endTimestamp;
                bkt.next = bkt.next.next;
            }
            buckets.bucketSizeFrequency[j] -= 2;
            buckets.bucketSizeFrequency[j+1]++;
            j++;
        }
    }
}

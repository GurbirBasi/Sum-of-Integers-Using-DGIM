public class Bucket {
    Bucket next;
    long startTimeStamp, endTimestamp;
    int size;

    Bucket(){
        next = null;
        endTimestamp = 0;
    }
}

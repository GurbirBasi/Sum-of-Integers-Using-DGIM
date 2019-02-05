public class Buckets {
    int[] bucketSizeFrequency = new int[40];    //for a billion integers the max bucket size will be around 32 for r = 5.
    Bucket bucket;

    Buckets(long k){
            bucket = new Bucket();
            bucketSizeFrequency[0]++;
            bucket.startTimeStamp = k;
            bucket.endTimestamp = k;
            bucket.next = null;
            bucket.size = 1;

    }
}

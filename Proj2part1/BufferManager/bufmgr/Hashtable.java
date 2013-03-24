package bufmgr;
import global.*;
public class Hashtable {
	private final static int HTSIZE = 251;
/*A simple hash table should be used to figure out what frame a given disk page occupies. The hash table should be implemented (entirely in main memory) by using an array of pointers to lists of  <page number, frame number > pairs.
 * The array is called the directory and each list of pairs is called a bucket.
 * Given a page number, you should apply a hash function to find the directory entry pointing to the bucket that contains the frame number for this page, if the page is in the buffer pool.
 * If you search the bucket and don't find a pair containing this page number, the page is not in the pool. If you find such a pair, it will tell you the frame in which the page resides.*/
	
/*The hash function must distribute values in the domain of the search field uniformly over the collection of buckets. If we have HTSIZE buckets, numbered 0 through M-1, a hash function h of the form 
h(value) = (a*value+b) mod HTSIZE
 works well in practice. HTSIZE should be chosen to be a prime number.*/
	public static Bucket Directory[];
	
	public Hashtable(){
		Directory = new Bucket[HTSIZE];
	}
	
	int getHash(int val){
		return (7*val+61)%HTSIZE;
	}
	
	int getFrameNumberFromBucket(PageId pageNumber){
		Bucket b = Directory[getHash(pageNumber.pid)];
		if (b == null){
			return -1;
		}
		do{
			if(pageNumber.pid == b.getPageNumber().pid){
				return b.getFrameNumber();
			}
			b = b.getNextBucket();
		}while(b.getNextBucket() != null);
		return -2;
	}
	
	int insertBucket(Bucket bucketToInsert){
		Bucket b = Directory[getHash(bucketToInsert.getPageNumber().pid)];
		if(b == null){
			b = bucketToInsert;
		}
		else{
			while(b.getNextBucket() != null){
				b = b.getNextBucket();
			}
			b.setNextBucket(bucketToInsert);
		}
		return 1;
	}
	
	Bucket removeBucket(Bucket bucketToRemove){
		Bucket b = Directory[getHash(bucketToRemove.getPageNumber().pid)];
		if (b == null){
			return null;
		}
		if(b.equals(bucketToRemove)){
			if(b.getNextBucket() == null){
				Directory[getHash(bucketToRemove.getPageNumber().pid)] = null;
				return bucketToRemove;
			}
			Directory[getHash(bucketToRemove.getPageNumber().pid)] = b.getNextBucket();
			return bucketToRemove;
		}
		do{
			if(b.getNextBucket().equals(bucketToRemove)){
				b.setNextBucket(b.getNextBucket().getNextBucket());
				return bucketToRemove;
			}
			b = b.getNextBucket();
		}while(b.getNextBucket() != null);
		return null;
	}
}

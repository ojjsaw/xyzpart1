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
	public Bucket Directory[];
	
	public Hashtable(){
		Directory = new Bucket[HTSIZE];
	}
	
	int getHash(int val){
		return (7*val+61)%HTSIZE;
	}
	
	public int getFrameNumberFromBucket(PageId pageId){
		// Look for page in the buffer pool
		if(pageId == null)
			System.out.println("gfhdfg");
		int hash = getHash(pageId.pid);
		Bucket bucky = Directory[hash];
		while(bucky != null){
			if(bucky.getPageNumber().pid == pageId.pid){
				return bucky.getFrameNumber();
			}
			bucky = bucky.getNextBucket();
		}
		return -1;
	}
	
	int insertBucket(int frameno ,PageId pageNumber){
		int hash = getHash(pageNumber.pid);
		Bucket bucky = new Bucket(pageNumber, frameno);
		Bucket iter = Directory[hash];
		if(Directory[hash]!= null){
			while(iter.getNextBucket() != null){
				iter = iter.getNextBucket();
			}
			iter.setNextBucket(bucky);
			return 1;
		}
		Directory[hash] = bucky;
		return 1;
	}
	
	int removeBucket(PageId pageNumber){
		int hash = getHash(pageNumber.pid);
		Bucket b = Directory[hash];
		if (b == null){
			return -1;
		}
		if(b.getPageNumber().pid == pageNumber.pid){
			if(b.getNextBucket() == null){
				Directory[hash] = null;
				return 1;
			}
			Directory[hash] = b.getNextBucket();
			return 1;
		}
		do{
			if(b.getPageNumber().pid == pageNumber.pid){
				b.setNextBucket(b.getNextBucket().getNextBucket());
				return 1;
			}
			b = b.getNextBucket();
		}while(b.getNextBucket() != null);
		return -1;
	}
}

package bufmgr;
import global.*;
public class Bucket {
	
	private PageId pageNumber;
	private int frameNumber;
	private Bucket nextBucket;
	
	public Bucket(PageId pageNumber, int frameNumber){
		this.pageNumber = pageNumber;
		this.frameNumber = frameNumber;
	}
	PageId getPageNumber(){
		return pageNumber;
	}
	int getFrameNumber(){
		return frameNumber;
	}
	Bucket getNextBucket(){
		return nextBucket;
	}
	int setNextBucket(Bucket b){
		nextBucket = b;
		return 1;
	}
	boolean equals(Bucket b){
		if(this.pageNumber.pid == b.getPageNumber().pid && this.frameNumber == b.getFrameNumber()){
			return true;
		}
		return false;
	}
}

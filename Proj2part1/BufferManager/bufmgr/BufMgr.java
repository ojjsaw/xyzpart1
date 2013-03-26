package bufmgr;
import java.io.IOException;

import chainexception.ChainException;
import diskmgr.*;
import global.*;

public class BufMgr {
	 
/**
 * Create the BufMgr object.
 * Allocate pages (frames) for the buffer pool in main memory and
 * make the buffer manage aware that the replacement policy is
 * specified by replacerArg (i.e. LH, Clock, LRU, MRU etc.).
 *
 * @param numbufs number of buffers in the buffer pool
 * @param prefetchSize number of pages to be prefetched
 * @param replacementPolicy Name of the replacement policy
 */
	byte bufPool[][];
	Descriptor bufDescr[];
	int numbuf;
	Hashtable ht;
	String replacementPolicy;
	clock clockArray[];
	int clockPointer;
	int AvailableFrames;
	int referencePointer;
	int af;
public BufMgr(int numbufs, int prefetchSize, String replacementPolicy) {
	ht = new Hashtable();
	numbuf = numbufs;
	bufPool = new byte[numbuf][GlobalConst.PAGE_SIZE];
	bufDescr = new Descriptor[numbuf];
	AvailableFrames = numbuf;
	this.replacementPolicy = replacementPolicy;
	clockArray = new clock[numbuf];
	for(int i = 0; i < numbuf; i++){
		clockArray[i] = new clock(-1, i, true);
		bufDescr[i] = new Descriptor(null);
		bufDescr[i].setPinCount(0);
	}
	referencePointer = numbuf;
	clockPointer = 0;
	af = numbuf;
}

/** 
* Pin a page.
* First check if this page is already in the buffer pool.
* If it is, increment the pin_count and return a pointer to this 
* page.
* If the pin_count was 0 before the call, the page was a 
* replacement candidate, but is no longer a candidate.
* If the page is not in the pool, choose a frame (from the 
* set of replacement candidates) to hold this page, read the 
* page (using the appropriate method from {\em diskmgr} package) and pin it.
* Also, must write out the old page in chosen frame if it is dirty 
* before reading new page.__ (You can assume that emptyPage==false for
* this assignment.)
*
* @param pageno page number in the Minibase.
* @param page the pointer point to the page.
* @param emptyPage true (empty page); false (non-empty page)
 * @throws Exception 
*/
 public void pinPage(PageId pageno, Page page, boolean emptyPage) throws Exception {
	 if(ht.getFrameNumberFromBucket(pageno) > -1){
		int frameno = ht.getFrameNumberFromBucket(pageno);
		
		//Clock Buffer Management
	    for(int i = 0; i < numbuf; i++){
	    	if(clockArray[i].getFrameNumber() == frameno){
	    		clockArray[i].setReferenceNumber(referencePointer);
	    		referencePointer++;
	    		if(bufDescr[frameno].getPinCount() == 0){
	    			clockArray[i].setReplacementCandidateStatus(false);
	    		}
	    	}
	    }
	    
	    //System.out.println(pageno.pid);
	    bufDescr[frameno].incrementPinCount();
	    page.setpage(bufPool[frameno]);
	}
	else{
		int frameno = clock.leastRecentlyUsed(bufDescr, clockArray, numbuf, clockPointer);
		if(frameno == -1){
			frameno = 0;
		}
		
		if(bufDescr[frameno] != null && bufDescr[frameno].isDirty()){
			//this.flushPage(bufDescr[frameno].getPageNumber());
			
		}
		if(this.getNumUnpinned() == 0){
			throw new BufferPoolExceededException(null,null);
		}
		
		Minibase.DiskManager.read_page(pageno, page);
		
		clockArray[clockPointer].setReferenceNumber(clockPointer);
		ht.insertBucket(frameno, pageno);
		bufDescr[frameno] = new Descriptor(pageno);
		//System.out.println(af);
		clockArray[clockPointer].setReplacementCandidateStatus(false);
		af--;
	}
	 int data = 0;
	    try {
			data = Convert.getIntValue (0, page.getpage());
			//System.out.println("pinPage["+pageno.pid+", " + frameno +"] = " + data);
			//System.out.println("YOURS Looking for pid :" +  data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 }
 
/**
* Unpin a page specified by a pageId.
* This method should be called with dirty==true if the client has
* modified the page.
* If so, this call should set the dirty bit 
* for this frame.
* Further, if pin_count>0, this method should 
* decrement it. 
*If pin_count=0 before this call, throw an exception
* to report error.
*(For testing purposes, we ask you to throw
* an exception named PageUnpinnedException in case of error.)
*
* @param pageno page number in the Minibase.
* @param dirty the dirty bit of the frame
 * @throws Exception 
*/
public void unpinPage(PageId pageno, boolean dirty) throws Exception {
	if(dirty){
		int frameno = ht.getFrameNumberFromBucket(pageno);
		//System.out.println(frameno);
		if(frameno < 0){
			throw new HashEntryNotFoundException(null,null);
		}
		
		this.flushPage(bufDescr[frameno].getPageNumber());
		//page.setpage(bufPool[frameno]);
		bufDescr[frameno].setDirtyBit(dirty);
		if(bufDescr[frameno].getPinCount() > 0){
			bufDescr[frameno].decrementPinCount();
			if(bufDescr[frameno].getPinCount() == 0){
				af++;
			}
		}
		
	}
	int frameno = ht.getFrameNumberFromBucket(pageno);
	if(frameno < 0){
		throw new HashEntryNotFoundException(null,null);
	}
	else{
		if(bufDescr[frameno].getPinCount() > 0){
			bufDescr[frameno].decrementPinCount();
		}
	}
}
 
/** 
* Allocate new pages.
* Call DB object to allocate a run of new pages and 
* find a frame in the buffer pool for the first page
* and pin it. (This call allows a client of the Buffer Manager
* to allocate pages on disk.) If buffer is full, i.e., you 
* can't find a frame for the first page, ask DB to deallocate 
* all these pages, and return null.
*
* @param firstpage the address of the first page.
* @param howmany total number of allocated new pages.
*
* @return the first page id of the new pages.__ null, if error.
 * @throws Exception 
*/
public PageId newPage(Page firstpage, int howmany) throws Exception {
	/*if(this.getNumUnpinned() == 0){
		throw new OutOfFramesException(null,null);
	}*/
	PageId pid = new PageId();
	//System.out.println(this.getNumUnpinned());
	Minibase.DiskManager.allocate_page(pid, howmany);
	this.pinPage(pid, firstpage, true);
	return pid;
}
 
/**
* This method should be called to delete a page that is on disk.
* This routine must call the method in diskmgr package to 
* deallocate the page. 
*
* @param globalPageId the page number in the data base.
 * @throws ChainException 
 * @throws IOException 
*/
public void freePage(PageId globalPageId) throws ChainException, IOException {
		int frameno = ht.getFrameNumberFromBucket(globalPageId);
		if(bufDescr[frameno].getPinCount() > 1){
			throw new PagePinnedException(null, null);
		}
		if(frameno != -1){
			if(bufDescr[frameno].isDirty()){
				//flushPage(bufDescr[frameno].getPageNumber());
			}
			//bufDescr[frameno] = new Descriptor(null);
			bufDescr[frameno].setDirtyBit(false);
			bufDescr[frameno].setPinCount(0);
			ht.removeBucket(globalPageId);
			for(int i = 0; i < numbuf; i++){
				if(clockArray[i].getFrameNumber() == frameno){
					clockArray[i].setFrameNumber(-1);
					clockArray[i].setReferenceNumber(referencePointer);
					referencePointer++;
					clockArray[i].setReplacementCandidateStatus(true);
				}
			}
			af++;
			Minibase.DiskManager.deallocate_page(globalPageId);
		}
}
 
/**
* Used to flush a particular page of the buffer pool to disk.
* This method calls the write_page method of the diskmgr package.
*
* @param pageid the page number in the database.
 * @throws IOException 
 * @throws FileIOException 
 * @throws InvalidPageNumberException 
*/
public void flushPage(PageId pageid) throws InvalidPageNumberException, FileIOException, IOException {
	int frameno = ht.getFrameNumberFromBucket(pageid);
	if(frameno > -1){
		Page page = new Page(bufPool[frameno]);
			Minibase.DiskManager.write_page(pageid, page);
			int data = Convert.getIntValue (0, page.getpage());
		  	//System.out.println("written data from frame:"+ pageid.pid + "to disk : " + data);
	}
}
 
/**
* Used to flush all dirty pages in the buffer pool to disk
 * @throws IOException 
 * @throws FileIOException 
 * @throws InvalidPageNumberException 
*
*/
public void flushAllPages() throws InvalidPageNumberException, FileIOException, IOException {
	for(int i = 0; i < numbuf; i++){
		if(bufDescr[i].isDirty()){
			this.flushPage(bufDescr[i].getPageNumber());
		}
	}
}
 
/**
* Gets the total number of buffer frames.
*/
public int getNumBuffers() {
	return numbuf;
}
 
/**
* Gets the total number of unpinned buffer frames.
*/
public int getNumUnpinned() {
	return af;
}
 
};

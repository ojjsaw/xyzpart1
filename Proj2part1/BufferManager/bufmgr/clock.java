package bufmgr;

public class clock {
	private int referenceNumber;
	private int frameNumber;
	private boolean irc;
	
	public clock(int frameNumber, int referenceNumber, boolean irc){
		this.frameNumber = frameNumber;
		this.referenceNumber = referenceNumber;
		this.irc = irc;
	}
	
	int getReferenceNumber(){
		return referenceNumber;
	}
	
	int getFrameNumber(){
		return referenceNumber;
	}
	
	int setFrameNumber(int fn){
		frameNumber = fn;
		return 1;
	}
	
	boolean isReplacementCandidate(){
		return irc;
	}
	
	int setReferenceNumber(int val){
		referenceNumber = val;
		return 1;
	}
	
	int setReplacementCandidateStatus(boolean bool){
		irc = bool;
		return 1;
	}
	
	static int leastRecentlyUsed(Descriptor[] des, clock[] clockArray, int sizeOfArrays, int clockPointer) throws BufferPoolExceededException{
		int leastRecent;
		int lastLeastRecent = -1;
		do{
			leastRecent = clockPointer;
			for(int i = 0; i < sizeOfArrays; i++){
				if(clockArray[i].getReferenceNumber() < leastRecent && clockArray[i].getReferenceNumber() > lastLeastRecent && clockArray[i].frameNumber != -1 && des[i].getPinCount() == 0){
					leastRecent = clockArray[i].getReferenceNumber();
				}
			}
			clockArray[leastRecent].setReplacementCandidateStatus(true);
			lastLeastRecent = leastRecent;
		}while(!clockArray[leastRecent].isReplacementCandidate());
		return clockArray[leastRecent].frameNumber;
	}
}

package bufmgr;

public class clock {
	private int frameno;
	private boolean irc;
	
	public clock(int frameno, boolean irc){
		this.frameno = frameno;
		this.irc = irc;
	}
	
	int getFrameno(){
		return frameno;
	}
	
	boolean isReplacementCandidate(){
		return irc;
	}
	
	int setFrameno(int val){
		frameno = val;
		return 1;
	}
	
	int setReplacementCandidateStatus(boolean bool){
		irc = bool;
		return 1;
	}
}

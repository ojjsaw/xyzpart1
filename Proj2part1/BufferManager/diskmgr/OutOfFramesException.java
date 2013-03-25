package diskmgr;
import chainexception.*;

public class OutOfFramesException extends ChainException {
	  
		public OutOfFramesException(Exception e, String name)
	    { 
			super(e, name); 
	    }
}

package diskmgr;
import chainexception.*;


public class PageUnpinnedException extends ChainException {

  public PageUnpinnedException(Exception e, String name)
  
  { 
    super(e, name); 
  }


}
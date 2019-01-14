package simpledb;

import java.io.Serializable;

/**
 * A RecordId is a reference to a specific tuple on a specific page of a
 * specific table.
 */
public class RecordId implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected PageId pageID;
    protected int tupleNum;

    /**
     * Creates a new RecordId referring to the specified PageId and tuple
     * number.
     * 
     * @param pid
     *            the pageid of the page on which the tuple resides
     * @param tupleno
     *            the tuple number within the page.
     */
    public RecordId(PageId pid, int tupleno) {
        // some code goes here
    	this.pageID = pid;
    	this.tupleNum = tupleno;
    }

    /**
     * @return the tuple number this RecordId references.
     */
    public int tupleno() {
        // some code goes here
        return tupleNum;
    }

    /**
     * @return the page id this RecordId references.
     */
    public PageId getPageId() {
        // some code goes here
        return pageID;
    }

    /**
     * Two RecordId objects are considered equal if they represent the same
     * tuple.
     * 
     * @return True if this and o represent the same tuple
     */
    @Override
    public boolean equals(Object o) {
        // some code goes here
    	if (o == null)
    		return false;
    	if (o.getClass() == RecordId.class) //check to see if they are both a part of the recordid class
    	{
    		RecordId casted = (RecordId) o;
    		
    		if (pageID.hashCode() == (casted.pageID.hashCode()) && tupleNum == casted.tupleno())
    			return true; 
    		else 
    			return false;
    	}
    	else
    		return false;
        //throw new UnsupportedOperationException("implement this");
    }

    /**
     * You should implement the hashCode() so that two equal RecordId instances
     * (with respect to equals()) have the same hashCode().
     * 
     * @return An int that is the same for equal RecordId objects.
     */
    @Override
    public int hashCode() {
        // some code goes here
    	int hash = pageID.hashCode() + tupleno();
    	return hash;
        //throw new UnsupportedOperationException("implement this");

    }

}

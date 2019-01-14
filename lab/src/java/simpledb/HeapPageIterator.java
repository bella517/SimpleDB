package simpledb;

import java.util.*;

public class HeapPageIterator implements Iterator<Tuple>{
	
	private HeapPage heapPage;
    private int numValidTuples;
    private int currentTuple;
    private Tuple next;
        
    // Assumes pages cannot be modified while iterating over them
    // Iterates over only valid tuples
    public HeapPageIterator(HeapPage page) {
        this.heapPage = page;
        this.currentTuple = 0;
        this.numValidTuples = this.heapPage.numSlots - this.heapPage.getNumEmptySlots();
    }
    
    //returns true if the next tuple is valid
    public boolean hasNext() {
    	
        next = heapPage.getTuple(currentTuple);
    	if (next !=null)
    		return true;
    	else
    		return false;   
    }
    //returns the next tuple    
    public Tuple next() throws NoSuchElementException {
        if(next != null)
        {
        	currentTuple += 1;
        	return next;
        }
        else
        {
        	hasNext();
        	currentTuple += 1;
        	if (next != null)
        		return next;
        	else
        		throw new NoSuchElementException();
        	
        }
    }
    

        
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot remove on HeapPageIterator");
    }
}

    
	
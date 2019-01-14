package simpledb;

import java.util.*;

public class HeapFileIterator implements DbFileIterator {
	
	private TransactionId transactionId;
    private HeapFile heapFile;
    private int currentPageId;
    private int numPages;
    private Iterator<Tuple> tupleIterator;

    public HeapFileIterator(TransactionId tid, HeapFile file) {
        this.transactionId = tid;
        this.heapFile = file;
        this.numPages = this.heapFile.numPages();
        this.currentPageId = this.numPages;
    }

    public void open()
        throws DbException, TransactionAbortedException {
        this.currentPageId = -1;        
    }

    public boolean hasNext()
        throws DbException, TransactionAbortedException {
        if (this.tupleIterator==null || this.tupleIterator.hasNext() == false) 
        {
        	currentPageId += 1;
        	if (currentPageId >= numPages)
        		return false;
        	
        	PageId curPageId = new HeapPageId(heapFile.getId(), currentPageId);
        	Page page = (HeapPage)Database.getBufferPool().getPage(this.transactionId, curPageId, Permissions.READ_ONLY);
        	tupleIterator = page.iterator();

        	return tupleIterator.hasNext();
        }
        	
        return tupleIterator.hasNext();
        
      
    }

    public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException{
    
    	
    	if (this.hasNext())     
    		return tupleIterator.next();
    	else
    		throw new NoSuchElementException();
    
    }

    public void rewind()
        throws DbException, TransactionAbortedException {
        currentPageId = -1;
        tupleIterator = null;
    }

    public void close() {
        this.currentPageId = numPages;
        this.tupleIterator = null;
    }
}

 

   

         


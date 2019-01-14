package simpledb;

import java.util.*;


	
	
public class HeapFileIterator extends AbstractDbFileIterator {

	    Iterator<Tuple> it = null;
	    int curpgno = 0;

	    TransactionId tid;
	    HeapFile hf;

	    public HeapFileIterator(HeapFile hf, TransactionId tid) {
	        this.hf = hf;
	        this.tid = tid;
	    }

	    public void open() throws DbException, TransactionAbortedException {
	        curpgno = -1;
	    }

	    @Override
	    protected Tuple readNext() throws TransactionAbortedException, DbException {
	        if (it != null && !it.hasNext())
	            it = null;

	        while (it == null && curpgno < hf.numPages() - 1) {
	            curpgno++;
	            HeapPageId curpid = new HeapPageId(hf.getId(), curpgno);
	            HeapPage curp = (HeapPage) Database.getBufferPool().getPage(tid,
	                    curpid, Permissions.READ_ONLY);
	            it = curp.iterator();
	            if (!it.hasNext())
	                it = null;
	        }

	        if (it == null)
	            return null;
	        return it.next();
	    }

	    public void rewind() throws DbException, TransactionAbortedException {
	        close();
	        open();
	    }

	    public void close() {
	        super.close();
	        it = null;
	        curpgno = Integer.MAX_VALUE;
	    }
	}


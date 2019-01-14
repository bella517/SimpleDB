package simpledb;

import java.io.IOException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;
    private TransactionId tid;
    private DbIterator child;
    private TupleDesc td = null;
    private Boolean hasNotBeenCalled = true;

    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        // some code goes here
    	this.tid = t;
    	this.child = child;
    	
    	Type[] type = new Type[1];
        String[] name = new String[1];
         
        type[0] = Type.INT_TYPE;
        name[0] = "Number of Inserts";
        
        this.td = new TupleDesc(type, name);
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return td;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
    	child.open();
    	super.open();
    }

    public void close() {
        // some code goes here
    	child.close();
    	super.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
    	child.rewind();
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
    	BufferPool bufpool = Database.getBufferPool();
    	int count = 0;
    	if (!hasNotBeenCalled)
    		return null;
    	
    	while (child.hasNext())
    	{
    		Tuple t = child.next();
    		try {
				bufpool.deleteTuple(tid, t);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		count++;
    	}
        IntField intfield = new IntField(count);
        Tuple result = new Tuple(getTupleDesc());
        result.setField(0, intfield);
        hasNotBeenCalled = false;
        
        return result;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
    	return new DbIterator[] { this.child };
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
    	child = children[0];
    }

}

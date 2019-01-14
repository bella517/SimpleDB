package simpledb;

import java.io.IOException;

/**
 * Inserts tuples read from the child operator into the tableid specified in the
 * constructor
 */
public class Insert extends Operator {

    private static final long serialVersionUID = 1L;
    private TransactionId tID;
    private DbIterator child;
    private int tableID;
    private Boolean hasNotBeenCalled = true;
    private TupleDesc td = null;
    

    /**
     * Constructor.
     * 
     * @param t
     *            The transaction running the insert.
     * @param child
     *            The child operator from which to read tuples to be inserted.
     * @param tableid
     *            The table in which to insert tuples.
     * @throws DbException
     *             if TupleDesc of child differs from table into which we are to
     *             insert.
     */
    public Insert(TransactionId t,DbIterator child, int tableid)
            throws DbException {
        // some code goes here
    	
    	if (!child.getTupleDesc().equals(Database.getCatalog().getTupleDesc(tableid)))
    			throw new DbException("TupleDesc of child does not match table");
    	this.tID = t;
    	this.child = child;
    	this.tableID = tableid;
    	
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
     * Inserts tuples read from child into the tableid specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool. An
     * instances of BufferPool is available via Database.getBufferPool(). Note
     * that insert DOES NOT need check to see if a particular tuple is a
     * duplicate before inserting it.
     * 
     * @return A 1-field tuple containing the number of inserted records, or
     *         null if called more than once.
     * @throws IOException 
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        if (!hasNotBeenCalled)
        	return null;
        
        BufferPool bufpool = Database.getBufferPool();
        int count = 0;
        
        while (child.hasNext())
        {
        	Tuple t = child.next();
        	try {
				bufpool.insertTuple(tID, tableID, t);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	count++;
        }
        
        Tuple tup = new Tuple(this.getTupleDesc());
        IntField newCount = new IntField(count);
        tup.setField(0, newCount);
        
        hasNotBeenCalled = false;
        return tup;
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

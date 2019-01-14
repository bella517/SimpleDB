package simpledb;

import java.util.*;

/**
 * Filter is an operator that implements a relational select.
 */
public class Filter extends Operator {

    private static final long serialVersionUID = 1L;
    private Predicate pred;
    private DbIterator operator;

    /**
     * Constructor accepts a predicate to apply and a child operator to read
     * tuples to filter from.
     * 
     * @param p
     *            The predicate to filter tuples with
     * @param child
     *            The child operator
     */
    public Filter(Predicate p, DbIterator child) {
        // some code goes here DONE
    	this.pred = p;
    	this.operator = child;
    }

    public Predicate getPredicate() {
        // some code goes here DONE
        return pred;
    }

    public TupleDesc getTupleDesc() {
        // some code goes here DONE
        return operator.getTupleDesc();
    }

    public void open() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here DONE
    	operator.open();
    	super.open();
    }

    public void close() {
        // some code goes here DONE
    	operator.close();
    	super.close();

    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here DONE
    	operator.rewind();
    }

    /**
     * AbstractDbIterator.readNext implementation. Iterates over tuples from the
     * child operator, applying the predicate to them and returning those that
     * pass the predicate (i.e. for which the Predicate.filter() returns true.)
     * 
     * @return The next tuple that passes the filter, or null if there are no
     *         more tuples
     * @see Predicate#filter
     */
    protected Tuple fetchNext() throws NoSuchElementException,
            TransactionAbortedException, DbException {
        // some code goes here DONE
    	while (operator.hasNext()) //while there is a tuple in the iterator
    	{
    		Tuple tuple = operator.next();
    		if (tuple != null)
    		{
    			if (pred.filter(tuple)) //check to see if the tuple matched the predicate
    				return tuple;
    		}
    	}
        return null;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here DONE
    	return new DbIterator[] { this.operator };
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here DONE
    	operator = children[0];
    	
    }

}

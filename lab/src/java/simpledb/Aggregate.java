package simpledb;

import java.util.*;

/**
 * The Aggregation operator that computes an aggregate (e.g., sum, avg, max,
 * min). Note that we only support aggregates over a single column, grouped by a
 * single column.
 */
public class Aggregate extends Operator {

    private static final long serialVersionUID = 1L;
    private DbIterator child;
    private int afield;
    private int gfield;
    private Aggregator.Op aop;
    private Aggregator aggregator;
    private DbIterator iterator;

    /**
     * Constructor.
     * 
     * Implementation hint: depending on the type of afield, you will want to
     * construct an {@link IntAggregator} or {@link StringAggregator} to help
     * you with your implementation of readNext().
     * 
     * 
     * @param child
     *            The DbIterator that is feeding us tuples.
     * @param afield
     *            The column over which we are computing an aggregate.
     * @param gfield
     *            The column over which we are grouping the result, or -1 if
     *            there is no grouping
     * @param aop
     *            The aggregation operator to use
     */
    public Aggregate(DbIterator child, int afield, int gfield, Aggregator.Op aop) {
	// some code goes here DONE
    	this.child = child;
    	this.afield = afield;
    	this.gfield = gfield;
    	this.aop = aop;
    	Type AgFieldType = child.getTupleDesc().getFieldType(afield);
    	Type groupByFieldType = null;
    	
    	if (gfield != Aggregator.NO_GROUPING) //if there is a group-by field
    		groupByFieldType = child.getTupleDesc().getFieldType(gfield);
    	
    	if (AgFieldType == Type.STRING_TYPE)
    		aggregator = new StringAggregator(gfield, groupByFieldType, afield, aop); //create aggregators 
    	if (AgFieldType == Type.INT_TYPE)
    		aggregator = new IntegerAggregator(gfield, groupByFieldType, afield, aop);
    
    }

    /**
     * @return If this aggregate is accompanied by a groupby, return the groupby
     *         field index in the <b>INPUT</b> tuples. If not, return
     *         {@link simpledb.Aggregator#NO_GROUPING}
     * */
    public int groupField() {
	// some code goes here DONE
    	if (gfield == Aggregator.NO_GROUPING)
    		return Aggregator.NO_GROUPING; //if there is no group-by field
    	else
    		return gfield;
	
    }

    /**
     * @return If this aggregate is accompanied by a group by, return the name
     *         of the groupby field in the <b>OUTPUT</b> tuples If not, return
     *         null;
     * */
    public String groupFieldName() {
	// some code goes here DONE
    	if (gfield == Aggregator.NO_GROUPING)
    		return null; //null name is there is no groupby field
    	else 
    		return child.getTupleDesc().getFieldName(gfield);
    }

    /**
     * @return the aggregate field
     * */
    public int aggregateField() {
	// some code goes here DONE
	return afield;
    }

    /**
     * @return return the name of the aggregate field in the <b>OUTPUT</b>
     *         tuples
     * */
    public String aggregateFieldName() {
	// some code goes here DONE
	return child.getTupleDesc().getFieldName(afield);
    }

    /**
     * @return return the aggregate operator
     * */
    public Aggregator.Op aggregateOp() {
	// some code goes here DONE
	return aop;
    }

    public static String nameOfAggregatorOp(Aggregator.Op aop) {
	return aop.toString();
    }

    public void open() throws NoSuchElementException, DbException,
	    TransactionAbortedException {
	// some code goes here DONE
    	child.open();
    	while (child.hasNext())
    		aggregator.mergeTupleIntoGroup(child.next()); //add the next tuple into the group until they are all aggregated
    	iterator = aggregator.iterator(); //create an aggregator to iterate over these tuples
    	iterator.open();
    	super.open();
    	
    }

    /**
     * Returns the next tuple. If there is a group by field, then the first
     * field is the field by which we are grouping, and the second field is the
     * result of computing the aggregate, If there is no group by field, then
     * the result tuple should contain one field representing the result of the
     * aggregate. Should return null if there are no more tuples.
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
	// some code goes here DONE
    	if (iterator == null)
    		throw new DbException("not open");
    	if (iterator.hasNext())
    		return iterator.next();
    	else 
    		return null;
    }

    public void rewind() throws DbException, TransactionAbortedException {
	// some code goes here DONE
    	iterator.rewind();
    	child.rewind();
    }

    /**
     * Returns the TupleDesc of this Aggregate. If there is no group by field,
     * this will have one field - the aggregate column. If there is a group by
     * field, the first field will be the group by field, and the second will be
     * the aggregate value column.
     * 
     * The name of an aggregate column should be informative. For example:
     * "aggName(aop) (child_td.getFieldName(afield))" where aop and afield are
     * given in the constructor, and child_td is the TupleDesc of the child
     * iterator.
     */
    public TupleDesc getTupleDesc() {
	// some code goes here DONE
    	if (gfield != Aggregator.NO_GROUPING) //if there is a groupby field
    	{
    		String groupName = child.getTupleDesc().getFieldName(gfield);
    		Type groupType = child.getTupleDesc().getFieldType(gfield);
    		
    		String agName = child.getTupleDesc().getFieldName(afield);
    		Type agType = child.getTupleDesc().getFieldType(afield);
    		
    		Type[] types = new Type[2];
        	String[] names = new String[2];
    		types[0] = groupType;
    		names[0] = groupName;
    		types[1] = agType;
    		names[1] = agName;
    		
    		return new TupleDesc(types, names); //return a tuple description with fields for the group column and the aggregate column 
    	}
    	else //if there is no groupby field
    	{
    		String agName = child.getTupleDesc().getFieldName(afield);
    		Type agType = child.getTupleDesc().getFieldType(afield);
    		
    		Type[] types = new Type[1];
        	String[] names = new String[1];
    		types[0] = agType;
    		names[0] = agName;
    		
    		return new TupleDesc(types, names); //return a tuple description with only an aggregate column field
    	}
    	
    }

    public void close() {
	// some code goes here DONE
    	iterator.close();
    	child.close();
    	super.close();
    }

    @Override
    public DbIterator[] getChildren() {
	// some code goes here DONE
    	return new DbIterator[] { this.child };
    }

    @Override
    public void setChildren(DbIterator[] children) {
	// some code goes here DONE
    	this.child = children[0];
    }
    
}

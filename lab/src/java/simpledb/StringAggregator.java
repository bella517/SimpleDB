package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;



/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;
    private int groupFieldIndex;
    private Type groupFieldType;
    private int aggregateField;
    private Op operator;
    private HashMap<Field, Field> aggregatedTuples;

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
    	this.groupFieldIndex = gbfield;
    	this.groupFieldType = gbfieldtype;
    	this.aggregateField = afield;
    	if (what != Op.COUNT)
    		throw new IllegalArgumentException();
    	else
    		this.operator = what;
    	
    	aggregatedTuples = new HashMap<Field, Field>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
    	Field groupField;
    	if (groupFieldIndex == NO_GROUPING)
    		groupField = new IntField(NO_GROUPING);
    	else
    		groupField = tup.getField(groupFieldIndex);
    	
    	IntField aggregateValue = (IntField) aggregatedTuples.get(groupField);
    	
    	if (operator.equals(Op.COUNT))
    	{
    		int count = 0;
    		if (aggregateValue == null) //if there has not been a count operation done on this group by field yet and thus there is nothing in the hashmap
    			count = 1;
    		else 
    			count = aggregateValue.getValue() + 1;
    		
    		Field newAgValue = new IntField(count);
    		
    		aggregatedTuples.put(groupField, newAgValue);
    		
    		
    	}
    	
    	
    	
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
    	ArrayList<Tuple> tuples = new ArrayList<Tuple>();
    	Type[] types = new Type[2];
    	String[] names = new String[2];
    	
    	if (groupFieldIndex == NO_GROUPING)
    	{
    		names[0] = operator.toString();
    		types[0] = Type.INT_TYPE;
    		TupleDesc td = new TupleDesc(types, names);
    		Tuple t = new Tuple(td);
    		t.setField(0, aggregatedTuples.get(NO_GROUPING));
    		tuples.add(t);
    		return new TupleIterator(td, tuples);
    	}
    	else
    	{
    		names[0] = "Group Field";
    		types[0] = groupFieldType;
    		names[1] = operator.toString();
    		types[1] = Type.INT_TYPE;
    		TupleDesc td2 = new TupleDesc(types, names);
    	
    		Set<Field> sets = aggregatedTuples.keySet();
    		
    		for (Field f : sets)
    		{
    			Tuple t2 = new Tuple(td2);
    			Field next = aggregatedTuples.get(f);
    			t2.setField(0, f);
    			t2.setField(1, next);
    			System.out.println(t2);
    			tuples.add(t2);
    		}
    		return new TupleIterator(td2, tuples);
    	}
    	
    }

}

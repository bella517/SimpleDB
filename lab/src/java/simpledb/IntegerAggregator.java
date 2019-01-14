package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;
    private int groupFieldIndex;
    private Type groupFieldType;
    private int aggregateField;
    private Op operator;
    private HashMap<Field, Field> aggregatedTuples;
    private HashMap<Field, Integer> sumOfGroup = new HashMap<Field, Integer>(); //keep track of sum for AVG
    private HashMap<Field, Integer> countOfGroup = new HashMap<Field, Integer>(); //keep track of count for AVG
   
    

    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here DONE
    	this.groupFieldIndex = gbfield;
    	this.groupFieldType = gbfieldtype;
    	this.aggregateField = afield;
    	this.operator = what;
    	
    	aggregatedTuples = new HashMap<Field, Field>(); //hashmap mapping the groupby field to the aggregate field
    	
    	
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here DONE
    	
    	Field groupField;
    	if (groupFieldIndex == Aggregator.NO_GROUPING)
    		groupField = new IntField(NO_GROUPING); //create a null field for when there is no groupby field
    	else
    		groupField = tup.getField(groupFieldIndex);
    	
    	IntField aggregateValue = (IntField) aggregatedTuples.get(groupField); //map the groupby field to the value that the aggregate computes
    	
    	IntField tupleAgField = (IntField) tup.getField(aggregateField);
    	
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
    	
    	else if (operator.equals(Op.MAX))
    	{
    		int max = 0;
    		if (aggregateValue == null) //if there has not been an operator performed on this group by field
    			max = tupleAgField.getValue(); //make the max value the value in this tuples aggregate field
    		else 
    			max = Math.max(aggregateValue.getValue(), tupleAgField.getValue());
    		
    		Field newMaxValue = new IntField(max);
    		aggregatedTuples.put(groupField, newMaxValue);
    			
    	}
    	else if (operator.equals(Op.MIN))
    	{
    		int min = 0;
    		if (aggregateValue == null)
    			min = tupleAgField.getValue();
    		else
    			min = Math.min(aggregateValue.getValue(), tupleAgField.getValue());
    		
    		Field newMinValue = new IntField(min);
    		aggregatedTuples.put(groupField, newMinValue);
    	}
    	else if (operator.equals(Op.SUM))
    	{
    		int sum = 0;
    		if (aggregateValue == null)
    			sum = tupleAgField.getValue();
    		else
    			sum = aggregateValue.getValue() + tupleAgField.getValue();
    		
    		Field newSum = new IntField(sum);
    		aggregatedTuples.put(groupField, newSum);
    		
    		
    	}
    	else if (operator.equals(Op.AVG))
    	{
    		int avg = 0;
    		int sum = 0;
    		int num = 0;
    		if (aggregateValue == null)
    		{
    			avg = tupleAgField.getValue();
    			sum = tupleAgField.getValue();
    			num = 1;
    			sumOfGroup.put(groupField, sum);
    			countOfGroup.put(groupField, num);
    			
    		}
    		else
    		{
    			
    			sum = sumOfGroup.get(groupField) + tupleAgField.getValue();
    			num = countOfGroup.get(groupField) + 1;
    			
    			sumOfGroup.put(groupField, sum);
    			countOfGroup.put(groupField, num);
	
    		
    			avg = sum / num;
    		}
    		
    		Field newAvg = new IntField(avg);
    		aggregatedTuples.put(groupField, newAvg);
    	}
    		
    }

    /**
     * Create a DbIterator over group aggregate results.
     * 
     * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public DbIterator iterator() {
        // some code goes here DONE
    	ArrayList<Tuple> tuples = new ArrayList<Tuple>();
    	
    	if (groupFieldIndex == Aggregator.NO_GROUPING)
    	{
    		Type[] types = new Type[1];
        	String[] names = new String[1];
    		names[0] = operator.toString();
    		types[0] = Type.INT_TYPE;
    		TupleDesc td = new TupleDesc(types, names);
    		Tuple t = new Tuple(td);
    		t.setField(0, aggregatedTuples.get(new IntField(NO_GROUPING)));
    		tuples.add(t);
    		return new TupleIterator(td, tuples); //returns the iterator that iterates over a tuple that only has an aggregate field 
    	}
    	else
    	{
    		Type[] types = new Type[2];
        	String[] names = new String[2];
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
    			tuples.add(t2);
    		}
    		return new TupleIterator(td2, tuples);
    	}
    	
   
    }

}

package simpledb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {
	
	public Field[] fields;
	private TupleDesc description;
	private RecordId recordID;
	

    private static final long serialVersionUID = 1L;

    /**
     * Create a new tuple with the specified schema (type).
     * 
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        // some code goes here DONE
    	this.description = td;
    	int numFields = td.numFields();
    	this.fields = new Field[numFields];
    	
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here DONE
        return description;
    }

    /**
     * @return The RecordId representing the location of this tuple on disk. May
     *         be null.
     */
    public RecordId getRecordId() {
        // some code goes here DONE
        return recordID;
    }

    /**
     * Set the RecordId information for this tuple.
     * 
     * @param rid
     *            the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
        // some code goes here DONE
    	recordID = rid; 
    }

    /**
     * Change the value of the ith field of this tuple.
     * 
     * @param i
     *            index of the field to change. It must be a valid index.
     * @param f
     *            new value for the field.
     */
    public void setField(int i, Field f) {
        // some code goes here DONE
    	
    	//checks for valid index
    	if (i < fields.length && i >= 0)
    	{
    		if (f != null)
    			fields[i] = f;
    	}
    	else
    		System.out.print("Error: not a valid index");
    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     * 
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
        // some code goes here DONE
    	
    	//checks for valid index
        if (i < fields.length && i >= 0)
        {
        	if (fields[i] != null)	
        		return fields[i];
        	else
        		System.out.print("Error: not a valid index");
        	
        }
        return null;
    }

    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     * 
     * column1\tcolumn2\tcolumn3\t...\tcolumnN\n
     * 
     * where \t is any whitespace, except newline, and \n is a newline
     */
    public String toString() {
        // some code goes here DONE
    	String s = "";
    	
    	//cycles through every index in the field array, checks the type and then converts it to a string
    	for (int i = 0; i < fields.length; i++)
    		s += fields[i].toString() + "\t";
    	
    	s = s + "\n";
    	return s;
    
    }
    
    /**
     * @return
     *        An iterator which iterates over all the fields of this tuple
     * */
    public Iterator<Field> fields()
    {
        // some code goes here DONE
    	Iterator<Field> it = Arrays.asList(fields).iterator();
        return it;
    }
    
    /**
     * reset the TupleDesc of this tuple
     * */
    public void resetTupleDesc(TupleDesc td)
    {
        // some code goes here DONE
    	this.description = td;
    	this.fields = new Field[td.numFields()];
    }
}

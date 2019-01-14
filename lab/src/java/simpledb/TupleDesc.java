package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

	protected TDItem[] items;
    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        public final Type fieldType;
        
        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
        
    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // some code goes here DONE
    	Iterator<TDItem> it = Arrays.asList(items).iterator();
        return it;
     
    	
       
    }

    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // some code goes here DONE
    	if (typeAr.length == 0)
    	{
    		System.out.print("Error: type array empty"); 
    		return;
    	}
    	//makes a new array of tupledesc items with both the type and field
    	items = new TDItem[typeAr.length];
    	
    	for (int i = 0; i < typeAr.length; i++)
    		items[i] = new TDItem(typeAr[i], fieldAr[i]);
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here DONE
    	if (typeAr.length == 0)
    	{
    		System.out.print("Error: type array empty"); 
    		return;
    	}
    	
    	items = new TDItem[typeAr.length];    	
    	for (int i = 0; i < typeAr.length; i++) 
    		items[i] = new TDItem(typeAr[i], null); //anonymous field so null string
    	

    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here DONE
    	int numField = items.length;
    	return numField;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // some code goes here DONE
    		
    	if (i >= 0 && i < numFields())
    	{
    		TDItem tdi = items[i];
    		return tdi.fieldName;
    	}
    	else 
    		throw new NoSuchElementException("Invalid field index");
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // some code goes here DONE
    	if (i < numFields() && i >= 0)
    	{
    		TDItem tdi2 = items[i];
    		return tdi2.fieldType;
    	}
    	else
    		throw new NoSuchElementException("Invalid field index");
        
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here DONE
    	String current = "";
    	for (int i = 0; i < numFields(); i++)
    	{
    		current = getFieldName(i);
    		if (current != null)
    		{
    			if (current.equals(name)) //checks to see if the current index's fieldname matches the one we are looking for
    				return i;
    		}
    	}
    	throw new NoSuchElementException("Error: no field with matching name found");
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here DONE
    	int size = 0;
    	Type current;
		for (int i = 0; i < numFields(); i++) {
			current = getFieldType(i);
			if (current != null)
				size = size + current.getLen(); //returns how many bytes it takes to store a field of current type
		}
		return size;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // some code goes here DONE
    	int newTupleDescSize = td1.numFields() + td2.numFields();	
		Type[] typeAr = new Type[newTupleDescSize];
		String[] fieldAr = new String[newTupleDescSize];
		
		//initialization of counter to keep track of new array sizes 
		int currentField = 0;
		
		//fill arrays for type and field of the first tupledesc
		for (int i = 0; i < td1.numFields(); i++) {
			typeAr[i] = td1.getFieldType(i);
			fieldAr[i] = td1.getFieldName(i);
			currentField++;
		}
		
		//fill arrays for type and field of the second tupledesc
		for (int i = 0; i < td2.numFields(); i++) {
			typeAr[currentField] = td2.getFieldType(i);
			fieldAr[currentField] = td2.getFieldName(i);
			currentField++;
		}
		return new TupleDesc(typeAr, fieldAr);
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they are the same size and if the n-th
     * type in this TupleDesc is equal to the n-th type in td.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // some code goes here DONE
    	if (o == null)
    		return false;
    	//checks to see if object o is a tupledesc type
    	if (o.getClass() == TupleDesc.class)
    	{
    		TupleDesc castedTupleDesc = (TupleDesc) o;
    		//checks to see if the two are the same size
    		if (getSize() != castedTupleDesc.getSize())
    			return false;
    		for (int i = 0; i < numFields(); i++)
    		{
    			//checks to see if the nth type is equal
    			if (items[i].fieldType != castedTupleDesc.items[i].fieldType)
        			return false;
    		}
    		return true;
    	}
    	else 
    		return false;
    	
    	
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here DONE
    	String result = items.toString();
        return result;
    }
}

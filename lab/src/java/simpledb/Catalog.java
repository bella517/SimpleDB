package simpledb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Catalog keeps track of all available tables in the database and their
 * associated schemas.
 * For now, this is a stub catalog that must be populated with tables by a
 * user program before it can be used -- eventually, this should be converted
 * to a catalog that reads a catalog table from disk.
 * 
 * @Threadsafe
 */
public class Catalog {
	
	public Table[] tables;
	public int maxsize = 100000;
	public int numEntries = 0;
	
	
	//separate class within Catalog that implements a table structure
	public class Table implements Serializable {
		
		private String tableName;
		private String primaryKey;
		private DbFile DBFile;
		private int tableID = -1;

		private static final long serialVersionUID = 1L;
		  
		public Table(DbFile file, String name, String primaryKey) {
			this.tableName = name;
			this.DBFile = file;
			this.primaryKey = primaryKey;
			this.tableID = file.getId();
		}		
	}


    /**
     * Constructor.
     * Creates a new, empty catalog.
     */
    public Catalog() {
        // some code goes here DONE
    	this.tables = new Table[maxsize];
    }

    /**
     * Add a new table to the catalog.
     * This table's contents are stored in the specified DbFile.
     * @param file the contents of the table to add;  file.getId() is the identfier of
     *    this file/tupledesc param for the calls getTupleDesc and getFile
     * @param name the name of the table -- may be an empty string.  May not be null.  If a name
     * conflict exists, use the last table to be added as the table for a given name.
     * @param pkeyField the name of the primary key field
     */
    public void addTable(DbFile file, String name, String pkeyField) {
        // some code goes here DONE
    	
    	//check to see if file is not null and name is not null
    	if (file != null && name != null)
    	{
    		//makes a new table object in the array 
    		tables[numEntries] = new Table(file, name, pkeyField);
    		numEntries++;
    		for (int i = 0; i < numEntries; i++)
    		{
    			if (tables[i].tableName.equals(name)) //checks for duplicate names
    				tables[i] = this.tables[numEntries-1];
    		}
    		
    	}
    	

    }

    public void addTable(DbFile file, String name) {
        addTable(file, name, "");
    }

    /**
     * Add a new table to the catalog.
     * This table has tuples formatted using the specified TupleDesc and its
     * contents are stored in the specified DbFile.
     * @param file the contents of the table to add;  file.getId() is the identfier of
     *    this file/tupledesc param for the calls getTupleDesc and getFile
     */
    public void addTable(DbFile file) {
        addTable(file, (UUID.randomUUID()).toString());
    }

    /**
     * Return the id of the table with a specified name,
     * @throws NoSuchElementException if the table doesn't exist
     */
    public int getTableId(String name) throws NoSuchElementException {
        // some code goes here DONE
    	
    	for (int i = 0; i < numEntries; i++)
    	{
    		if (tables[i].tableName.equals(name)) //checks for equality 
    		{
    			int table = tables[i].tableID;
    			return table;
    		}
    	}
    	
    	throw new NoSuchElementException("Error: the element does not exist in the table"); 
        
    }

    /**
     * Returns the tuple descriptor (schema) of the specified table
     * @param tableid The id of the table, as specified by the DbFile.getId()
     *     function passed to addTable
     * @throws NoSuchElementException if the table doesn't exist
     */
    public TupleDesc getTupleDesc(int tableid) throws NoSuchElementException {
        // some code goes here DONE
    	int current = -1;
    	for (int i = 0; i < numEntries; i++)
    	{
    		if (tables[i].tableID == tableid)
    			current = i; //remembers index where there was a match but keeps cycling in case there are duplicates
    	}
    	if (current == -1)
    		throw new NoSuchElementException("Error: No element in table");
    	else
    		return tables[current].DBFile.getTupleDesc(); //we want to use the last index where the ids matched
    
    }

    /**
     * Returns the DbFile that can be used to read the contents of the
     * specified table.
     * @param tableid The id of the table, as specified by the DbFile.getId()
     *     function passed to addTable
     */
    public DbFile getDatabaseFile(int tableid) throws NoSuchElementException {
        // some code goes here
    	int current = -1;
    	for (int i = 0; i < numEntries; i++)
    	{
    		if (this.tables[i].tableID == tableid) //checks for equality 
    			current = i;
    	}
    	if (current == -1)
    		throw new NoSuchElementException("Error: no element in table");
    	else
    		return tables[current].DBFile; //we want to return the last instance where the IDs matched
        
    }

    public String getPrimaryKey(int tableid) throws NoSuchElementException {
        // some code goes here
    	int current = -1;
    	for (int i = 0; i < numEntries; i++)
    	{
    		if (tables[i].tableID == tableid)
    			current = i;
    	}
    	if (current == -1)
    		throw new NoSuchElementException("Error: no element in table");
    	else
    		return tables[current].primaryKey;
    }

    public Iterator<Integer> tableIdIterator() {
        // some code goes here
    	Integer[] tableList = new Integer[numEntries];
    	for (int i = 0; i < numEntries; i++)
    	{
    		int id = tables[i].tableID;
    		tableList[i] = id;
    	}
        Iterator<Integer> it = Arrays.asList(tableList).iterator();
        return it;
    }
    

    //Gets the TableName for a specified table id number
    public String getTableName(int id) {
        // some code goes here
    	String temp = "";
    	for (int i = 0; i < numEntries; i++)
    	{
    		if (this.tables[i].tableID == id)
    			temp = tables[i].tableName;
    			
    	}
        return temp;
    }
    
    /** Delete all tables from the catalog */
    public void clear() {
        // some code goes here DONE
    	tables = new Table[maxsize];
    	numEntries = 0;
    }
    
    /**
     * Reads the schema from a file and creates the appropriate tables in the database.
     * @param catalogFile
     */
    public void loadSchema(String catalogFile) {
        String line = "";
        String baseFolder=new File(new File(catalogFile).getAbsolutePath()).getParent();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(catalogFile)));
            
            while ((line = br.readLine()) != null) {
                //assume line is of the format name (field type, field type, ...)
                String name = line.substring(0, line.indexOf("(")).trim();
                //System.out.println("TABLE NAME: " + name);
                String fields = line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim();
                String[] els = fields.split(",");
                ArrayList<String> names = new ArrayList<String>();
                ArrayList<Type> types = new ArrayList<Type>();
                String primaryKey = "";
                for (String e : els) {
                    String[] els2 = e.trim().split(" ");
                    names.add(els2[0].trim());
                    if (els2[1].trim().toLowerCase().equals("int"))
                        types.add(Type.INT_TYPE);
                    else if (els2[1].trim().toLowerCase().equals("string"))
                        types.add(Type.STRING_TYPE);
                    else {
                        System.out.println("Unknown type " + els2[1]);
                        System.exit(0);
                    }
                    if (els2.length == 3) {
                        if (els2[2].trim().equals("pk"))
                            primaryKey = els2[0].trim();
                        else {
                            System.out.println("Unknown annotation " + els2[2]);
                            System.exit(0);
                        }
                    }
                }
                Type[] typeAr = types.toArray(new Type[0]);
                String[] namesAr = names.toArray(new String[0]);
                TupleDesc t = new TupleDesc(typeAr, namesAr);
                HeapFile tabHf = new HeapFile(new File(baseFolder+"/"+name + ".dat"), t);
                addTable(tabHf,name,primaryKey);
                System.out.println("Added table : " + name + " with schema " + t);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println ("Invalid catalog entry : " + line);
            System.exit(0);
        }
    }
}


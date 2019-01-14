package simpledb;

import java.io.Serializable;



public class Table implements Serializable {
	
	public String tableName;
	public String primaryKey;
	public DbFile DBFile;
	public int tableID;

	private static final long serialVersionUID = 1L;
	  
	public Table(DbFile file, String name, String primaryKey) {
		this.tableName = name;
		this.DBFile = file;
		this.primaryKey = primaryKey;
		this.tableID = file.getId();
	}
	  
	  
	
}

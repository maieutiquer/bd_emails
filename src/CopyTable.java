
import java.sql.SQLException;

/**
 * Creates empty table or a table with specific rows.
 * 
 * @author Daniel
 *
 */
public class CopyTable extends DataAccess{

	private static final long serialVersionUID = 1385824702364079515L;
	
	/**
	 * Creates empty table or a table with specific rows.
	 * 
	 * @param dbName the url with the database name
	 * @param user the username
	 * @param pwd the password
	 */
	public CopyTable() {
		super(dbName, user, pwd);
	}
	
	/**
	 * Creates a new table copy from an existing table with same structures and rows, 
	 * satisfying a condition, which is passed as a parameter.
	 * 
	 * @param sourceTable the table to take rows from
	 * @param newTable the table to be created with the same structure and with the rows copied
	 * @param where the condition that needs to be satisfied for the rows to be copied
	 */
	public void copyTableFromWhere(String sourceTable, String newTable, String where) {
		openConnection();
        try{
        	String createTableStatement = "CREATE TABLE "+newTable+" LIKE "+sourceTable+"; "; 
        	statement = con.prepareStatement(createTableStatement);
        	statement.executeUpdate();
        	System.out.println("Successful creation of "+newTable);
        	
        	String myStatement = "INSERT INTO "+newTable+" SELECT * FROM "+sourceTable;
        	if (where=="" || where==null) {
        		statement = con.prepareStatement(myStatement);
        	}else{
        		statement = con.prepareStatement(myStatement+" WHERE "+where+";");
        	}
			statement.executeUpdate();
			
			RowCounter counter = new RowCounter();
			System.out.println("Total rows inserted in "+newTable+" : " 
					+ counter.countFromWhere(newTable, where));
        }catch(SQLException s){
        	s.printStackTrace();
        }finally{
        	closeConnection();
        }
	}
	
	/**
	 * Copies rows with discinct values in a column from one table to another.
	 * 
	 * @param sourceTable the table to take rows from
	 * @param newTable the table to be created with the same structure and with the rows copied
	 * @param sourceColumns the column to be checked for distinct values
	 * @param newColumns
	 */
	public void copyDistinct(String sourceTable, String newTable, String sourceColumns){
		openConnection();
        try{
        	String createTableStatement = "CREATE TABLE "+newTable+" LIKE "+sourceTable+"; "; 
        	statement = con.prepareStatement(createTableStatement);
        	statement.executeUpdate();
        	System.out.println("Successful creation of "+newTable);
        	
        	String myStatement = 
        			"INSERT INTO "+newTable+" SELECT DISTINCT("+sourceColumns+") FROM "+sourceTable+";";
			statement = con.prepareStatement(myStatement);
			statement.executeUpdate();
			
			RowCounter counter = new RowCounter();
			System.out.println("Total rows in "+newTable+" : " 
					+ counter.countAll(newTable));
        	
        }catch(SQLException s){
        	s.printStackTrace();
        }finally{
        	closeConnection();
        }
	}
	
}


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creates empty table or a table with specific rows.
 * 
 * @author Daniel
 *
 */
public class CopyTable extends SQLException {

	private static final long serialVersionUID = 1385824702364079515L;
	private String dbName;
	private String user;
	private String pwd;
	
	/**
	 * Creates empty table or a table with specific rows.
	 * 
	 * @param dbName the url with the database name
	 * @param user the username
	 * @param pwd the password
	 */
	public CopyTable(String dbName, String user, String pwd) {
		super();
		this.dbName = dbName;
		this.user = user;
		this.pwd = pwd;
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
		Connection con = null; //opens connection
		PreparedStatement statement = null; //query statement
        //ResultSet result = null; //manages results
        
        try{
        	con = DriverManager.getConnection(dbName, user, pwd);
        	
        	String createTableStatement = "CREATE TABLE "+newTable+" LIKE "+sourceTable+"; "; 
        	statement = con.prepareStatement(createTableStatement);
        	statement.executeUpdate();
        	System.out.println("Successful creation of "+newTable);
        	
        	String myStatement = //"CREATE TABLE "+newTable+" LIKE "+sourceTable+"; " +
        			"INSERT INTO "+newTable+" SELECT * FROM "+sourceTable+" WHERE "+where+";";
        	System.out.println("Copy condition: " + where);
			statement = con.prepareStatement(myStatement);
			statement.executeUpdate();
			
			RowCounter counter = new RowCounter(dbName,user,pwd);
			System.out.println("Total rows inserted in "+newTable+" : " 
					+ counter.countFromWhere(newTable, where));
        	
        }catch(SQLException s){
        	s.printStackTrace();
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	try {
		        con.close();
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
			
        }
	}
	
}

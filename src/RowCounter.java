
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Contains methods to count the rows.
 * 
 * @author Daniel
 *
 */
public class RowCounter extends SQLException {
	
	private static final long serialVersionUID = 1000680432614489645L;

	private String dbName;
	private String user;
	private String pwd;
	
	/**
	 * Contains methods to count the rows.
	 * 
	 * @param dbName the url with the database name
	 * @param user the username
	 * @param pwd the password
	 */
	public RowCounter(String dbName, String user, String pwd) {
		super();
		this.dbName = dbName;
		this.user = user;
		this.pwd = pwd;
	}
	
	/**
	 * Counts rows, satisfying certain conditions, from a given table.
	 * @param where
	 * 
	 * @return
	 * @throws Exception
	 */
	public int countFromWhere(String table, String where)  {
		Connection con = null; //opens connection
		PreparedStatement statement = null; //query statement
        ResultSet result = null; //manages results
        int totalRows=-1;
		try{
			con = DriverManager.getConnection(dbName, user, pwd);
			
			String myStatement = "SELECT COUNT(*) FROM " + table;
			if (where=="" || where==null) {
				statement = con.prepareStatement(myStatement);
			}else{
				statement = con.prepareStatement(myStatement + " WHERE " + where); 
			}
			result = statement.executeQuery();
			while(result.next()){
        		totalRows=result.getInt("COUNT(*)");                              
               }
		}catch(SQLException e) {
			e.printStackTrace();
		}finally{
			try {
		        con.close();
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
		}
		return totalRows; //in case of exception
	}
	
	/**
	 * Counts all rows from a table.
	 * 
	 * @param table the table whose rows to be counted
	 * @return the total number of rows in the given table
	 */
	public int countAll(String table) {
		Connection con = null; //opens connection
		PreparedStatement statement = null; //query statement
        ResultSet result = null; //manages results
        int totalRows=-1;
        try{
        	con = DriverManager.getConnection(dbName, user, pwd);
        	statement=con.prepareStatement("SELECT COUNT(*) FROM "+table);
        	result = statement.executeQuery();
        	while(result.next()){
        		totalRows=result.getInt("COUNT(*)");                              
               }
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
		return totalRows;
	}
	
}

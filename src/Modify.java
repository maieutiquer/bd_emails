
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Updates columns of existing rows with new values.
 * 
 * @author User
 *
 */
public class Modify extends DataAccess {
	
	private static final long serialVersionUID = 1351738512414009001L;
	
	/**
	 * Modifies entries in a table.
	 * 
	 * @param dbName the url with the database name
	 * @param user the username
	 * @param pwd the password
	 */
	public Modify(String dbName, String user, String pwd) {
		super(dbName, user, pwd);
	}
	
	public void convertColumnValuesToLowercase(String table, String column) {
		openConnection();
		try{
			String myStatement = "UPDATE "+table+" SET "+column+" = lower("+column+")";
			statement = con.prepareStatement(myStatement);
			statement.executeUpdate();
		}catch(SQLException s){
			s.printStackTrace();
		}finally{
			closeConnection();
		}
	}
	
	/**
	 * Modifies stuff. 
	 * <br />Documentation to be completed.
	 * 
	 * @param table
	 * @param where
	 * @param column
	 * @param value
	 */
	public void modifyWhere(String table, String where, String column, String value) {
		
		try{
			String myStatement = "UPDATE "+table+" SET "+column+"="+value+" WHERE "+where+";";
			statement = con.prepareStatement(myStatement);
			statement.executeUpdate();
		}catch(SQLException s){
			s.printStackTrace();
		}
		
	}
	
	/**
	 * Tries opening a new connection.
	 */
	public void openConnection() {
		try{
			con = DriverManager.getConnection(dbName, user, pwd);
		}catch(SQLException s){
			System.out.println("Connection error!");
			s.printStackTrace();
		}
	}
	
	/**
	 * Tries closing the current connection.
	 */
	public void closeConnection() {
		try {
			if (!(con.isClosed())) {
				con.close();
			}else{
				// TODO: Connection already closed
			}
		}catch(SQLException e) {
			System.out.println("No opened connection!");
	        e.printStackTrace();
	    }
	}
}

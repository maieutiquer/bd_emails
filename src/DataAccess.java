
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Contains attributes and methods, common for other classes that connect to the database.
 * 
 * @author Daniel
 *
 */
public class DataAccess extends SQLException {
	
	private static final long serialVersionUID = 7150475321334571741L;
	protected String dbName;
	protected String user;
	protected String pwd;
	protected static Connection con = null; //opens connection
	protected PreparedStatement statement = null; //query statement
	protected ResultSet result = null; //manages results
	
	/**
	 * Contains attributes and methods, common for other classes that connect to the database.
	 * 
	 * @param dbName the url with the database name
	 * @param user the username
	 * @param pwd the password
	 */
	public DataAccess(String dbName, String user, String pwd) {
		super();
		this.dbName = dbName;
		this.user = user;
		this.pwd = pwd;
	}
	
	/**
	 * Resets the statement and result attributes.
	 * 
	 */
	public void resetResult(){
		// TODO: test the impact of this method on the application
		statement = null;
		result = null;
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
				System.out.println("Connection already closed");
			}
		}catch(SQLException e) {
			System.out.println("No opened connection!");
	        e.printStackTrace();
	    }
	}
	
}


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Updates columns of existing rows with new values.
 * 
 * @author User
 *
 */
public class Modify extends SQLException {
	
	private static final long serialVersionUID = 1351738512414009001L;
	private String dbName;
	private String user;
	private String pwd;
	
	/**
	 * Modifies entries in a table.
	 * 
	 * @param dbName the url with the database name
	 * @param user the username
	 * @param pwd the password
	 */
	public Modify(String dbName, String user, String pwd) {
		super();
		this.dbName = dbName;
		this.user = user;
		this.pwd = pwd;
	}
	
	public void modifyWhere(String table, String where, String column, String value) {
		Connection con = null; //opens connection
		PreparedStatement statement = null; //query statement
        ResultSet result = null; //manages results
		
		try{
			con = DriverManager.getConnection(dbName, user, pwd);
			String myStatement = "UPDATE "+table+" SET "+column+"="+value+" WHERE "+where+";";
			statement = con.prepareStatement(myStatement);
			result = statement.executeQuery();
			System.out.println(result.getString(0));
			
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


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
	public Modify() {
		super(dbName, user, pwd);
	}
	
	public void convertColumnValuesToLowercase(String table, String column) {
		try{
			String myStatement = "UPDATE "+table+" SET "+column+" = lower("+column+")";
			statement = con.prepareStatement(myStatement);
			statement.executeUpdate();
		}catch(SQLException s){
			s.printStackTrace();
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
	
}

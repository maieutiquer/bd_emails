
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class contains methods to count the rows.
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
	 * 
	 * @param dbName
	 * @param user
	 * @param pwd
	 */
	public RowCounter(String dbName, String user, String pwd) {
		super();
		this.dbName = dbName;
		this.user = user;
		this.pwd = pwd;
	}
	
	/**
	 * 
	 * 
	 * @param where
	 * @return
	 * @throws Exception
	 */
	public int countFromWhere(String table, String where, boolean printRows)  {
		Connection con = null;
		try{
			con = DriverManager.getConnection(dbName, user, pwd);
		
			String myStatement = "SELECT * FROM " + table;
			PreparedStatement statement;
			if (where=="") {
				statement = con.prepareStatement(myStatement);
			}else{
				statement = con.prepareStatement(myStatement + " WHERE " + where); 
			}
			ResultSet result = statement.executeQuery();
			int c=0; //the counter
			if(printRows){
				while (result.next()) {
					System.out.println("First Name: \" "+ result.getString(4) 
							+ " \" ; Last Name: \" " + result.getString(5) 
							+ " \" ; Email: \" " + result.getString(7)
							+ " \" ; ");
					c++;
				}
			}else{
				while (result.next()) {
					c++;
				}
			}
			return c;
		}catch(SQLException e) {
			e.printStackTrace();
		}finally{
			try {
		        con.close();
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
		}
		return -1; //in case of exception
	}
	
	public int countEmpty(boolean printRows)  {
		Connection con = null;
		try{
			con = DriverManager.getConnection(dbName, user, pwd);
		
			String myStatement = "SELECT * FROM bd_emails";
			PreparedStatement statement = con.prepareStatement(myStatement);
			
			ResultSet result = statement.executeQuery();
			int c=0; //the counter
			if(printRows){
				while (result.next()) {
					if (result.getString(4).equals("") && result.getString(5).equals("") && result.getString(7).equals("")) {
						System.out.println("First Name: \" "+ result.getString(4) 
								+ " \" ; Last Name: \" " + result.getString(5) 
								+ " \" ; Email: \" " + result.getString(7)
								+ " \" ; ");
						c++;
					}
				}
			}else{
				while (result.next()) {
					if (result.getString(4)=="" && result.getString(5)=="" && result.getString(7)=="") {
						c++;
					}
				}
			}
			return c;
		}catch(SQLException e) {
			e.printStackTrace();
		}finally{
			try {
		        con.close();
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
		}
		return -1; //in case of exception
	}
	
	/**
	 * Counts the rows of the result of a query, where the statement is passed as an argument.
	 * <br />To be used with caution - it does not filter the statement.
	 * 
	 * @param statement
	 * @return
	 * @throws Exception
	 */	
	public int countFromStatement(PreparedStatement statement) throws Exception {
		int i=0;
		ResultSet result = statement.executeQuery();
		while(result.next())
		{
			i++;
		}
		return i;
	}
	
	
	
}

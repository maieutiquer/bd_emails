
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Contains methods to count rows.
 * 
 * @author Daniel
 *
 */
public class RowCounter extends DataAccess {
	
	private static final long serialVersionUID = 1000680432614489645L;

	/**
	 * Contains methods to count rows.
	 * 
	 * @param dbName the url with the database name
	 * @param user the username
	 * @param pwd the password
	 */
	public RowCounter(String dbName, String user, String pwd) {
		super(dbName, user, pwd);
	}
	
	/**
	 * Counts rows, satisfying certain conditions, from a given table.
	 * @param where
	 * 
	 * @return
	 * @throws Exception
	 */
	public int countFromWhere(String table, String where)  {
        int totalRows=-1;
        openConnection();
		try{
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
		}
		closeConnection();
		return totalRows;
	}
	
	/**
	 * Counts distinct values in a column of a table. Takes a WHERE condition as a parameter.
	 * 
	 * @param table
	 * @param where
	 * @param column
	 * @return
	 */
	public int countDistinct(String table, String where, String column) {
        int totalRows=-1;
        openConnection();
		try{
			String myStatement = "SELECT COUNT(DISTINCT "+column+") FROM " + table;
			if (!(where=="" || where==null)) {
				statement = con.prepareStatement(myStatement + " WHERE " + where);
			}else{
				statement = con.prepareStatement(myStatement);
			}
			result = statement.executeQuery();
			while (result.next()) {
        		totalRows=result.getInt("COUNT(*)");                              
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		closeConnection();
		return totalRows;
	}
	
	/**
	 * Counts all rows from a table.
	 * 
	 * @param table the table whose rows to be counted
	 * @return the total number of rows in the given table
	 */
	public int countAll(String table) {
        int totalRows=-1;
        openConnection();
        try{
        	statement=con.prepareStatement("SELECT COUNT(*) FROM "+table);
        	result = statement.executeQuery();
        	while(result.next()){
        		totalRows=result.getInt("COUNT(*)");                              
               }
        }catch(SQLException s){
        	s.printStackTrace();
        }
        closeConnection();
		return totalRows;
	}
	
}

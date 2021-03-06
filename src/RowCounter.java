
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
	public RowCounter() {
		super(dbName, user, pwd);
	}
	
	/**
	 * Counts rows, satisfying certain condition, from a given table.
	 * 
	 * @param where the condition
	 * 
	 * @return the number of values that satisfy the condition
	 */
	public int countFromWhere(String table, String where)  {
        int totalRows=-1;
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
		return totalRows;
	}
	
	/**
	 * Counts distinct values in a column of a table. Takes a WHERE condition as a parameter.
	 * 
	 * @param column the column to get distinct values from
	 * @param table the table to count from
	 * @param where the condition
	 * @return the number of distinct values in the column, given the condition
	 */
	public int countDistinct(String column, String table, String where) {
        int totalRows=-1;
		try{
			String myStatement = "SELECT COUNT(DISTINCT "+column+") FROM " + table;
			if (!(where=="" || where==null)) {
				statement = con.prepareStatement(myStatement + " WHERE " + where);
			}else{
				statement = con.prepareStatement(myStatement);
			}
			result = statement.executeQuery();
			while (result.next()) {
        		totalRows=result.getInt("COUNT(DISTINCT "+column+")");                              
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
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
        try{
        	statement=con.prepareStatement("SELECT COUNT(*) FROM "+table);
        	result = statement.executeQuery();
        	while(result.next()){
        		totalRows=result.getInt("COUNT(*)");                              
               }
        }catch(SQLException s){
        	s.printStackTrace();
        }
		return totalRows;
	}
	
}

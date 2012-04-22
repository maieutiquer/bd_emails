import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Used to make various selections and get information about the database or a specific table.
 * <br />Not suitable for counting as much as for obtaining data.
 * 
 * 
 * @author Daniel
 *
 */
public class Select extends SQLException {
	
	private static final long serialVersionUID = 3188564773946400482L;
	private String dbName;
	private String user;
	private String pwd;
	
	/**
	 * Used to make various selections and get information about the database or a specific table.
	 * <br />Not suitable for counting as much as for obtaining data.
	 * 
	 * @param dbName the url with the database name
	 * @param user the username
	 * @param pwd the password
	 */
	public Select(String dbName, String user, String pwd) {
		super();
		this.dbName = dbName;
		this.user = user;
		this.pwd = pwd;
	}
	
	//under development
	public String[] selectValuesFromWhere(String table, String where)  {
		Connection con = null; //opens connection
		PreparedStatement statement = null; //query statement
        ResultSet result = null; //manages results
        String[] valuesList = null; //index 0 shows column number
		try{
			con = DriverManager.getConnection(dbName, user, pwd);
		
			String myStatement = "SELECT * FROM " + table;

			if (where=="") {
				statement = con.prepareStatement(myStatement);
			}else{
				statement = con.prepareStatement(myStatement + " WHERE " + where); 
			}
			result = statement.executeQuery();
			
			int totalColumns = getTotalColumns(table);
			valuesList = new String[totalColumns+1];
			while(result.next()){
				for (int i=1; i<totalColumns;i++) {
					valuesList[i] = result.getString(i+1);
				}
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
		        con.close();
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
		}
		return valuesList;
	}
	
	/**
	 * Returns the number of columns in a table.
	 * 
	 * @param table the table whose columns to count
	 * @return total number of columns
	 */
	public int getTotalColumns(String table){
		Connection con = null; //opens connection
		PreparedStatement statement = null; //query statement
        ResultSet result = null; //manages results
        int numberOfColumns = 0;
        try{
        	con = DriverManager.getConnection(dbName, user, pwd);
        	statement = con.prepareStatement("SELECT * FROM " + table);
        	result = statement.executeQuery();
        	ResultSetMetaData rsMetaData = result.getMetaData();
        	numberOfColumns = rsMetaData.getColumnCount();
        	
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
			try {
		        con.close();
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
		}
        return numberOfColumns;
	}
	
	/**
	 * Returns an array with names of the columns in a given table.
	 * 
	 * @param table the table whose columns names are needed
	 * @return a list of the columns in an array of strings
	 */
	public String[] getColumnsAsArray(String table) {
		Connection con = null; //opens connection
		PreparedStatement statement = null; //query statement
        ResultSet result = null; //manages results
        String[] columnNames = null; //list of column names as an array of strings
        try{
        	con = DriverManager.getConnection(dbName, user, pwd);
        	statement = con.prepareStatement("SELECT * FROM " + table);
        	result = statement.executeQuery();
        	ResultSetMetaData rsMetaData = result.getMetaData();
        	int numberOfColumns = rsMetaData.getColumnCount();
        	columnNames = new String[numberOfColumns+1];
        	
        	// get the column names; column indexes start from 1
        	for (int i = 1; i <= numberOfColumns; i++) {
        		String columnName = rsMetaData.getColumnName(i);
        	    // Get the name of the column's table name
        	    columnNames[i] = columnName;
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
			try {
		        con.close();
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
		}
        return columnNames;
	}
	
}

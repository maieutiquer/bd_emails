
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
public class Select extends DataAccess {
	
	private static final long serialVersionUID = 3188564773946400482L;
	
	/**
	 * Used to make various selections and get information about the database or a specific table.
	 * <br />Not suitable for counting as much as for obtaining data.
	 * 
	 * @param dbName the url with the database name
	 * @param user the username
	 * @param pwd the password
	 */
	public Select(String dbName, String user, String pwd) {
		super(dbName, user, pwd);
	}
	
	public int[] selectDistinct(String column, String table, String where) {
		int field[] = null;
		int totalRows = -1;
		openConnection();
        try{
        	String myCountStatement = "SELECT COUNT(DISTINCT "+column+") FROM " + table;
			if (!(where=="" || where==null)) {
				statement = con.prepareStatement(myCountStatement + " WHERE " + where);
			}else{
				statement = con.prepareStatement(myCountStatement);
			}
			result = statement.executeQuery();
			while (result.next()) {
        		totalRows=result.getInt("COUNT(DISTINCT "+column+")");                              
			}
			field = new int[totalRows];
			
			String mySelectStatement = "SELECT DISTINCT "+column+" FROM "+table;
			if (!(where=="" || where==null)) {
				statement = con.prepareStatement(myCountStatement + " WHERE " + where);
			}else{
				statement = con.prepareStatement(myCountStatement);
			}
			statement = con.prepareStatement(mySelectStatement);
			result = statement.executeQuery();
			int i=0;
			while(i<totalRows){ 
				result.next();
				field[i] = result.getInt(1);
				i++;
			}
			
					//= result.getString(1);
        }catch(SQLException s){
			s.printStackTrace();
        }finally{
        	closeConnection();
        }

		return field;
	}
	
	/**
	 * Returns the value of a field from a table. If more than one result, the first one is returned.
	 * @param column
	 * @param table
	 * @param where
	 * 
	 * @return
	 */
	public String selectField(String column, String table, String where){
        String field=null;
        openConnection();
        try{
        	String myStatement = "SELECT "+column+" FROM "+table+" WHERE "+where;
			statement = con.prepareStatement(myStatement);
			result = statement.executeQuery();
			result.next();
			field = result.getString(1);
        }catch(SQLException s){
			s.printStackTrace();
        }finally{
        	closeConnection();
        }
		return field;
	}
	
	//under development
	public int[] selectIntFromWhere(String column, String table, String where) {
		int field[] = null;
		int totalRows = -1;
		openConnection();
        try{
        	String myCountStatement = "SELECT COUNT("+column+") FROM " + table;
			if (!(where=="" || where==null)) {
				statement = con.prepareStatement(myCountStatement + " WHERE " + where);
			}else{
				statement = con.prepareStatement(myCountStatement);
			}
			result = statement.executeQuery();
			while (result.next()) {
        		totalRows=result.getInt("COUNT("+column+")");                              
			}
			field = new int[totalRows];
			
			String mySelectStatement = "SELECT "+column+" FROM "+table;
			if (!(where=="" || where==null)) {
				statement = con.prepareStatement(myCountStatement + " WHERE " + where);
			}else{
				statement = con.prepareStatement(myCountStatement);
			}
			statement = con.prepareStatement(mySelectStatement);
			result = statement.executeQuery();
			int i=0;
			while(i<totalRows){ 
				result.next();
				field[i] = result.getInt(1);
				i++;
			}
			
					//= result.getString(1);
        }catch(SQLException s){
			s.printStackTrace();
        }finally{
        	closeConnection();
        }

		return field;
	}
	
	public String[] selectStringFromWhere(String column, String table, String where) {
		String field[] = null;
		int totalRows = -1;
		openConnection();
        try{
        	String myCountStatement = "SELECT COUNT("+column+") FROM " + table;
			if (!(where=="" || where==null)) {
				statement = con.prepareStatement(myCountStatement + " WHERE " + where);
			}else{
				statement = con.prepareStatement(myCountStatement);
			}
			result = statement.executeQuery();
			while (result.next()) {
        		totalRows=result.getInt("COUNT("+column+")");                              
			}
			field = new String[totalRows];
			
			String mySelectStatement = "SELECT "+column+" FROM "+table;
			if (!(where=="" || where==null)) {
				statement = con.prepareStatement(myCountStatement + " WHERE " + where);
			}else{
				statement = con.prepareStatement(myCountStatement);
			}
			statement = con.prepareStatement(mySelectStatement);
			result = statement.executeQuery();
			int i=0;
			while(i<totalRows){ 
				result.next();
				field[i] = result.getString(1);
				i++;
			}
			
					//= result.getString(1);
        }catch(SQLException s){
			s.printStackTrace();
        }finally{
        	closeConnection();
        }

		return field;
	}
	
	/**
	 * Returns the number of columns in a table.
	 * 
	 * @param table the table whose columns to count
	 * @return total number of columns
	 */
	public int getTotalColumns(String table) {
        int numberOfColumns = 0;
        openConnection();
        try{
        	statement = con.prepareStatement("SELECT * FROM " + table);
        	result = statement.executeQuery();
        	ResultSetMetaData rsMetaData = result.getMetaData();
        	numberOfColumns = rsMetaData.getColumnCount();
        }catch(SQLException s){
        	s.printStackTrace();
        }finally{
        	closeConnection();
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
        String[] columnNames = null; //list of column names as an array of strings
        openConnection();
        try{
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
        }catch(SQLException s){
        	s.printStackTrace();
        }finally{
        	closeConnection();
        }
        return columnNames;
	}
	
}

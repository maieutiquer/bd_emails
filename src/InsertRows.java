
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Has different methods to insert rows into tables.
 * 
 * @author Daniel
 *
 */
public class InsertRows extends SQLException {

	private static final long serialVersionUID = -2838546018705486974L;
	
	private String dbName;
	private String user;
	private String pwd;
	private int val = 0;
	
	/**
	 * Has different methods to insert rows into tables.
	 * 
	 * @param dbName the url with the database name
	 * @param user the username
	 * @param pwd the password
	 */
	public InsertRows(String dbName, String user, String pwd) {
		super();
		this.dbName = dbName;
		this.user = user;
		this.pwd = pwd;
	}
	
	/**
	 * Copies rows satisfying certain condition from one table to another.
	 * 
	 * @param fromTable the table to take rows from
	 * @param toTable the table to insert rows into
	 * @param where the condition for the rows to be copied
	 */
	public void copyRowsFromWhere(String fromTable, String toTable, String where)  {
		Connection con = null; //opens connection
		PreparedStatement statement = null; //query statement
        ResultSet result = null; //manages results
        String[] valuesList = null; //index 0 shows column number
        int numberOfErrors=0;
        
		try{
			con = DriverManager.getConnection(dbName, user, pwd);
		
			String myStatement = "SELECT * FROM " + fromTable;

			if (where=="" || where==null) {
				statement = con.prepareStatement(myStatement);
			}else{
				statement = con.prepareStatement(myStatement + " WHERE " + where + ";"); 
			}
			result = statement.executeQuery();
			
			Select select = new Select(dbName, user, pwd);
			
			String[] columnsList = select.getColumnsAsArray(fromTable);
			String columns="";
			for (int i=1; i<columnsList.length; i++) {
				columns += "`" + columnsList[i] + "`, ";
			}
			columns = columns.substring(0, (columns.length()-2) ); //takes the last comma out of the string
			
			int totalColumns = select.getTotalColumns(fromTable);
			valuesList = new String[totalColumns+1];
			
			while(result.next()){
				for (int i=1; i<=totalColumns;i++) {
					valuesList[i] = result.getString(i);
				}
				String values="";
				for (int i=1; i<valuesList.length; i++) {
					values += "'" + valuesList[i].replaceAll("'", "\\\\'") + "', "; //the char ' is replaced with \'
				}
				values = values.substring(0, (values.length()-2) ); //takes the last comma out of the string
				//System.out.println("values: " + values);
				try{
					insertRow(toTable, columns, values);					
				}catch(Exception e){
					numberOfErrors++;
					e.printStackTrace();
				}
				
				
				//String myInsertStatement = "INSERT INTO `"+toTable+"` ("+columns+") VALUES ("+values+");";
				//statement = con.prepareStatement(myInsertStatement);
				//this.val += statement.executeUpdate();
			}
			
		}catch(SQLException e) {
			numberOfErrors++;
			System.out.println("Error!");
			e.printStackTrace();
		}catch(Exception e){
			numberOfErrors++;
			System.out.println("Error!");
			e.printStackTrace();
		}finally{
			try {
		        con.close();
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
			
		}
		System.out.println("Number of errors: " + numberOfErrors);
		System.out.println("Values: " + val);
	}
	
	/**
	 * Inserts one row in a given table.
	 * 
	 * @param table the table name in format: my_table
	 * @param columns a list of columns in format: `col1`, `col2`, `col3`
	 * @param values a list of values, corresponding to the columns, in format: 'val1', val2', 'val3'
	 */
	public int insertRow(String table, String columns, String values) {
		Connection con = null; //opens connection
		PreparedStatement statement = null; //query statement
        //ResultSet result = null; //manages results
		try{
			con = DriverManager.getConnection(dbName, user, pwd);
			
			String myStatement = "INSERT INTO `"+table+"` ("+columns+") VALUES ("+values+");";
			statement = con.prepareStatement(myStatement);
			
			this.val += statement.executeUpdate();
			//System.out.println("Success");
			//System.out.println(result.getWarnings());
		}catch(SQLException e) {
			e.printStackTrace();
			return 1;
		}finally{
			try {
		        con.close();
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
		}
		return 0;
		//insert example: INSERT INTO `isp_domains` (`domain`) VALUES ('@hotmail.fr');
	}
	
}

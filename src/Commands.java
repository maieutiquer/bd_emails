
public class Commands {
	
	private static String dbName; //DB name
	private static String user; //DB username
	private static String pwd; //DB password
	private static final String emptyGenderCond = 
			"ct_genre IN ('', 'A DEFINIR', '68', '-', '.', 'definir', '?', '???', 'z', 'Z', 'nc', 'NC')";
	private static final String emptyFirstnameCond = 
			"ct_prenom IN ('', 'A DEFINIR', '68', '-', '.', 'definir', '?', '???', 'z', 'Z', 'nc', 'NC')";
	private static final String emptyLastnameCond = 
			"ct_nom IN ('', 'A DEFINIR', '68', '-', '.', 'definir', '?', '???', 'z', 'Z', 'nc', 'NC')";
	public static final String emptyEmailCond = 
			"ct_mail IN ('', 'A DEFINIR', '68', '-', '.', 'definir', '?', '???', '84/12/39/44', " +
			"'05/65/77/85/37', 'z', 'Z', 'nc', 'NC', 'non', 'PAS DE MAIL', 'N° INDISPONIBLE', 'REPONDEUR', " +
			"'en retraite', 'n\\'a pas de mail', 'pas de demarchage', 'email', '01.01.1981', 'fr', " +
			"'REMPLACE MME SUINOT EN MALADIE', '01 47 68 12 63', '661261090', 'b', '603707107', " +
			"'pas d\\'adresse e-mail', '630108886', 'pas adresse', 'aucune', '664998228', " +
			"'pas de mail', 'pas d\\'e-mail', 'pas d\\'adresse', 'ne veut pas communiquer')";
	private static final String emptyRowsCond = 
			"("+emptyFirstnameCond+") AND ("+emptyLastnameCond+") AND ("+emptyEmailCond+")"; 
	@SuppressWarnings("unused") //simply here to show a full model of empty rows condition
	private static final String emptyRowsCondFull = 
			"(ct_prenom IN ('', 'A DEFINIR', '68', '-', '.', 'definir', '?', '???', 'z', 'Z', 'nc', 'NC')) " +
			"AND (ct_nom IN ('', 'A DEFINIR', '68', '-', '.', 'definir', '?', '???', 'z', 'Z', 'nc', 'NC')) " +
			"AND (ct_mail IN ('', 'A DEFINIR', '68', '-', '.', 'definir', '?', '???', 'z', 'Z', 'nc', 'NC'," +
			"'84/12/39/44', '05/65/77/85/37',  'non', 'PAS DE MAIL', 'N° INDISPONIBLE', 'REPONDEUR', " +
			"'en retraite', 'n\\'a pas de mail', 'pas de demarchage', 'email', '01.01.1981', 'fr', " +
			"'REMPLACE MME SUINOT EN MALADIE', '01 47 68 12 63', '661261090', 'b', '603707107', " +
			"'pas d\\'adresse e-mail', '630108886', 'pas adresse', 'aucune', '664998228', " +
			"'pas de mail', 'pas d\\'e-mail', 'pas d\\'adresse', 'ne veut pas communiquer'))";
	public Commands(String dbName, String user, String pwd) {
		super();
		Commands.dbName = dbName;
		Commands.user = user;
		Commands.pwd = pwd;
	}
	
	/**
	 * Copies empty rows to a new table
	 * 
	 * @param sourceTable the table to take empty rows from
	 * @param newTable the table to be created with the same structure and with the empty rows copied
	 */
	public void copyEmpty(String sourceTable, String newTable){
		
		//condition for copying
		String where = emptyRowsCond; 
		CopyTable copy = new CopyTable(dbName, user, pwd);
		copy.copyTableFromWhere(sourceTable, newTable, where);
	}
	
	/**
	 * Counts all rows from a table.
	 * 
	 * @param table the table whose rows to count
	 */
	public void countAll(String table) {
		RowCounter counter = new RowCounter(dbName,user,pwd);
		
		//no condition, select all
		String where = ""; 
		boolean printRows = false;
		
		System.out.println("Total rows in "+table+" : " 
				+ counter.countFromWhere(table, where, printRows));
	}
	
	/**
	 * Counts the number of rows without gender from a given table.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmptyGender(String table) {
		RowCounter counter = new RowCounter(dbName,user,pwd);
		
		//we define the condition to select specific rows and to count them
		String where = emptyGenderCond; 
		boolean printRows = false;
		
		System.out.println("Total rows with empty gender in "+table+" : " 
				+ counter.countFromWhere(table, where, printRows));
	}
	
	/**
	 * Counts the number of rows without email from a given table.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmptyEmail(String table) {
		RowCounter counter = new RowCounter(dbName,user,pwd);
		
		//we define the condition to select specific rows and to count them
		String where = emptyEmailCond; 
		
		boolean printRows = false;
		
		System.out.println("Total rows with empty email in "+table+" : " 
				+ counter.countFromWhere(table, where, printRows));
	}
	
	/**
	 * Counts the number of rows without first name from a given table.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmptyLastname(String table) {
		RowCounter counter = new RowCounter(dbName,user,pwd);
		
		//we define the condition to select specific rows and to count them
		String where = emptyLastnameCond; 
		boolean printRows = false;
		
		System.out.println("Total rows with empty last name in "+table+" : " 
				+ counter.countFromWhere(table, where, printRows));
	}
	
	/**
	 * Counts the number of rows without last name from a given table.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmptyFirstname(String table) {
		RowCounter counter = new RowCounter(dbName,user,pwd);
		
		//we define the condition to select specific rows and to count them
		String where = emptyFirstnameCond; 
		boolean printRows = false;
		
		System.out.println("Total rows with empty first name in "+table+" : " 
				+ counter.countFromWhere(table, where, printRows));
	}

	/**
	 * Prints clients with empty <b>first name</b>, <b>family name</b> and <b>email</b> at the same time. 
	 * <br />Needs a table which is a version of bd_emails and has at least the columns 
	 * <b>ct_prenom</b>, <b>ct_nom</b> and <b>ct_mail</b>.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmpty(String table){
		
		RowCounter counter = new RowCounter(dbName,user,pwd);
		
		//we define the condition to select specific rows and to count them
		String where = emptyRowsCond; 
		boolean printRows = false;
		
		System.out.println("Total empty rows in "+table+" : " 
				+ counter.countFromWhere(table, where, printRows));
	}

	/**
	 * Prints clients with empty <b>first name</b>, <b>family name</b> and <b>email</b> at the same time. 
	 * <br />Needs a table which is a version of bd_emails and has at least the columns 
	 * <b>ct_prenom</b>, <b>ct_nom</b> and <b>ct_mail</b>.
	 * 
	 * @param table the table that should be checked
	 */
	public void countNonEmpty(String table){
		
		RowCounter counter = new RowCounter(dbName,user,pwd);
		
		//we define the condition to select specific rows and to count them
		String where = "NOT ("+emptyRowsCond+")"; 
		boolean printRows = false;
		
		System.out.println("Total non-empty rows in "+table+" : " 
				+ counter.countFromWhere(table, where, printRows));
	}
	
	/**
	 * Gives total columns number and prints each column's name of a given table from the database. 
	 * 
	 * @param table the table whose columns should be taken into consideration
	 */
	public void showColumnNames(String table){
		Select select = new Select(dbName, user, pwd);
		String[] columnsList = select.getColumnsAsArray(table);
		System.out.println("Total number of columns: " + (columnsList.length-1));
		for (int i=1;i<columnsList.length;i++){
			System.out.println("Column " + i + " : " + columnsList[i]);
		}
	}
	
	/**
	 * Takes non-empty clients and writes them to a new table, thus avoiding deletion.
	 * <br />Should be run once per destination table 
	 * 
	 * @param fromTable the source table to takes values from
	 * @param toTable the destination table to write values into
	 */
	public void copyNonEmpty(String fromTable, String toTable){
		
		//we define the condition to select specific rows and insert them in another table
		String where = "NOT ("+emptyRowsCond+")"; 
		
		CopyTable copy = new CopyTable(dbName, user, pwd);
		copy.copyTableFromWhere(fromTable, toTable, where);
		
	}
	
	/**
	 * Takes empty clients and writes them to a new table, allowing export of empty rows.
	 * <br />Should be <b>run once</b> per destination table.
	 * 
	 * @param fromTable
	 * @param toTable
	 */
	public void insertEmpty(String fromTable, String toTable){
		
		//we define the condition to select specific rows and insert them in another table
		String where = emptyRowsCond; 
		
		InsertRows insert = new InsertRows(dbName, user, pwd);
		insert.copyRowsFromWhere(fromTable, toTable, where);
		
	}
	
}


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Has different commands that should be called from the main method.
 * 
 * @author Daniel
 *
 */
public class Commands extends DataAccess {
	
	private static final long serialVersionUID = 2448602180321858504L;
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
	
	/**
	 * Has different commands that should be called from the main method.
	 * 
	 * @param dbName the url with the database name
	 * @param user the username
	 * @param pwd the password
	 */
	public Commands(String dbName, String user, String pwd) {
		super(dbName, user, pwd);
	}
	
	/**
	 * Determines the rule for first name in emails 
	 * and writes it in the corresponding column.
	 * 
	 * @param table the table whose rows to process
	 */
	public void determineFirstNameRule(String table) {
		Modify modify = new Modify();
		Select select = new Select();
		int[] ct_refs = select.selectIntFromWhere("ct_ref", table, null);
		String[] firstNames = select.selectStringFromWhere("ct_prenom", table, null);
		String[] lastNames = select.selectStringFromWhere("ct_nom", table, null);
		String[] emails = select.selectStringFromWhere("ct_mail", table, null);
		String[] userRules = select.selectStringFromWhere("regle_user", table, null);
		String[] ruleTexts = select.selectStringFromWhere("rule", "regle_prenom", null);
		int[] ruleIds = select.selectIntFromWhere("id", "regle_prenom", null);
		for (int i=0; i<emails.length; i++) {
			int rule=-1;
			int ct_ref = ct_refs[i];
			String firstName = firstNames[i].toLowerCase();
			String lastName = lastNames[i].toLowerCase();
			String email = emails[i];
			String userRule = userRules[i];
			String ruleText = ruleTexts[i];
			int ruleId = ruleIds[i];
			
			//TODO: process each value to determine the prenom rule, -1 for error
			
			
			modify.modifyWhere(table, "ct_ref="+ct_ref, "regle_prenom", Integer.toString(rule));
		}
	}
	
	/**
	 * Determines the rule for username in emails 
	 * and writes it in the corresponding column.
	 * 
	 * @param table the table whose rows to process
	 */
	public void determineUserRule(String table) {
		Modify modify = new Modify();
		Select select = new Select();
		int[] ct_refs = select.selectIntFromWhere("ct_ref", table, null);
		String[] firstNames = select.selectStringFromWhere("ct_prenom", table, null);
		String[] lastNames = select.selectStringFromWhere("ct_nom", table, null);
		String[] users = select.selectStringFromWhere("user", table, null);
		String[] ruleTexts = select.selectStringFromWhere("rule", "regle_user", null);
		int[] ruleIds = select.selectIntFromWhere("id", "regle_user", null);
		
		HashMap<Integer,String> ruleMap = new HashMap<Integer,String>();
		for (int i=0;i<ruleIds.length;i++) {
			ruleMap.put(ruleIds[i], ruleTexts[i]);
			System.out.println(ruleMap.get(i+1));
		}
		
		for (int i=0; i<users.length; i++) {
			int rule=-1;
			int ct_ref = ct_refs[i];
			String firstName = firstNames[i].toLowerCase();
			String lastName = lastNames[i].toLowerCase();
			String user = users[i].toLowerCase();
			//TODO: process each value in the above arrays to determine the user rule, -1 for error
			user = user.replaceAll(firstName, "prenom");
			user = user.replaceAll(lastName, "nom");
			if (ruleMap.containsValue(user)) {
				System.out.println(getKeyByValue(ruleMap, user));
			}
//			modify.modifyWhere(table, "ct_ref="+ct_ref, "regle_user", Integer.toString(rule));
		}
	}
	
	/**
	 * Determines the username of the email. Takes the part of the email before the domain.
	 * 
	 * @param table the table to process
	 */
	public void determineUserAndDomain(String table){
		Modify modify = new Modify();
		Select select = new Select();
		String[] emails = select.selectStringFromWhere("ct_mail", table, null);
		int[] ctRefs = select.selectIntFromWhere("ct_ref", table, null);
		int position=-1;
		String domain="";
		String email="";
		for (int i=0; i<emails.length; i++) {
			int ctRef = ctRefs[i];
			email = emails[i];
			String user = null;
			position = email.indexOf('@');
			if (position != -1) {
				domain = email.substring(position);
				user = email.substring(0, position);
				// the char ' is replaced with \'
				user = user.replaceAll("'", "\\\\'");
				domain = domain.replaceAll("'", "\\\\'");
				modify.modifyWhere(table, "`ct_ref`="+ctRefs[i], "`domaines`", "'"+domain+"'");
				modify.modifyWhere(table, "`ct_ref`="+ctRef, "`user`", "'"+user+"'");
			}
		}
	}
	
	/**
	 * Copies clients with non-isp domains to their own table.
	 * 
	 * @param sourceTable the table to read rows from
	 * @param newTable the table to insert rows with non-isp domain to
	 */
	public void copyClientsWithOtherDomains(String sourceTable, String newTable) {
		CopyTable copy = new CopyTable();
		Select select = new Select();
		String[] ispDomains = select.selectStringFromWhere("name", "isp_domains_2", null);
		String ispDomainsString = "";
		for(int i=0;i<ispDomains.length;i++) {
			ispDomainsString += "'" + ispDomains[i] + "', ";
		}
		String isp = ispDomainsString.substring(0, ispDomainsString.length()-2);
		String where = "`domaines` NOT IN ("+isp+")";
		copy.copyTableFromWhere(sourceTable, newTable, where);
	}
	
	/**
	 * Copies clients with ips domains to their own table.
	 * 
	 * @param sourceTable the table to take rows from
	 * @param newTable the table to insert rows with isp domain to
	 */
	public void copyClientsWithIspDomains(String sourceTable, String newTable) {
		CopyTable copy = new CopyTable();
		Select select = new Select();
		String[] ispDomains = select.selectStringFromWhere("name", "isp_domains_2", null);
		String ispDomainsString = "";
		for(int i=0;i<ispDomains.length;i++) {
			ispDomainsString += "'" + ispDomains[i] + "', ";
		}
		String isp = ispDomainsString.substring(0, ispDomainsString.length()-2);
		String where = "`domaines` IN ("+isp+")";
		copy.copyTableFromWhere(sourceTable, newTable, where);
	}
	
	/**
	 * Cleans the cl_ref of a certain table. Depricated because useless.
	 * 
	 * @param table the table to be cleaned
	 */
	public void cleanClrefs(String table){
//		RowCounter counter = new RowCounter(dbName, user, pwd);
		Modify modify = new Modify();
		Select select = new Select();
		int[] companies = select.selectIntFromWhere("cl_ref", table, null);
		String[] domains = select.selectStringFromWhere("domaines", table, null);
		String[] ct_ref = select.selectStringFromWhere("ct_ref", table, null);
		String[] ispDomains = select.selectStringFromWhere("name", "isp_domains_2", null);
		int count = 0;
		HashMap<Integer,String> clientMap = new HashMap<Integer,String>();
		System.out.println("Total entries: "+companies.length);
		for (int i=0; i<companies.length;i++) {
			if (!(domains[i].equals("#value!") || Arrays.asList(ispDomains).contains(domains[i]))) {
				if (clientMap.containsValue(domains[i])) {
					if (companies[i] != getKeyByValue(clientMap, domains[i])) {
						modify.modifyWhere(table, "ct_ref="+ct_ref[i], "cl_ref", getKeyByValue(clientMap, domains[i]).toString());
//						System.out.println("just did modification at: "+ct_ref[i]+", " +
//								"put at cl_ref: "+getKeyByValue(clientMap, domains[i]));
						count++;
					}
				}else if (!(clientMap.containsKey(companies[i]))) {
					clientMap.put(companies[i], domains[i]);
				}
			}
		}
		System.out.println("Number of modifications: "+count);
	}
	
	/**
	 * Returns the key, corresponding to a certain value 
	 * 
	 * @param map the map to be processed
	 * @param value the value to be looked for
	 * @return the key that corresponds to the value
	 */
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	/**
	 * Converts all emails from a table to lowercase letters.
	 * 
	 * @param table the table whose emails to put in lowercase
	 */
	public void convertEmailsToLowercase(String table) {
		Modify modify = new Modify();
		modify.convertColumnValuesToLowercase(table, "ct_mail");
		System.out.println("Converted to minuscule all email values from table "+table);
	}
	
	/**
	 * Converts all domains from a table to lowercase letters.
	 * 
	 * @param table the table whose domains to put in lowercase
	 */
	public void convertDomainsToLowercase(String table) {
		Modify modify = new Modify();
		modify.convertColumnValuesToLowercase(table, "domaines");
		System.out.println("Converted to minuscule all domain values from table "+table);
	}
	
	
	/**
	 * Prints the number of dedoubled clients.
	 * 
	 * @param table the table with dedoubled clients
	 */
	public void countDedoubledClients(String table){
		RowCounter counter = new RowCounter();
		String where="`cl_ref` < 12000";
		int dedoubled=counter.countDistinct("cl_ref_original", table, where);
		System.out.println("Number of dedoubled clients: "+dedoubled);
	}
	
	/**
	 * Fills a new table with cl_ref
	 * 
	 * @param sourceTable
	 * @param clrefDomainsTable
	 */
	public void createMoreClref(String sourceTable, String clrefDomainsTable) {
		Select select = new Select();
		RowCounter counter = new RowCounter();
		InsertRows insert = new InsertRows();
//		int totalClref = counter.countDistinct("cl_ref", sourceTable, null);
		int[] companies = select.selectIntFromWhere("cl_ref", sourceTable, null);
		String[] domains = select.selectStringFromWhere("domaines", sourceTable, null);
		
		System.out.println("Total cl_ref entries in table "+sourceTable+" : "+companies.length);
		System.out.println("Total domaines entries in table "+sourceTable+" : "+domains.length);
		
		int insertedNewClref = 0;
	    for(int i=0; i < companies.length; i++){
	    	domains[i] = domains[i].replaceAll("'", "\\\\'"); // the char ' is replaced with \'
	    	if(counter.countFromWhere("isp_domains_2", "`name`='"+domains[i]+"'")==0){
	    		if(counter.countFromWhere(clrefDomainsTable, "cl_ref_original="+companies[i])==0){
	    			insert.insertRow(clrefDomainsTable, 
	    					"`cl_ref`, `cl_ref_original`, `nom_domaine`", "'"+companies[i]+"', '"+companies[i]+"', '"+domains[i]+"'");
	    		} else if ((counter.countFromWhere(clrefDomainsTable, 
	    				"(`cl_ref_original`='"+companies[i]+"' AND `nom_domaine`='"+domains[i]+"')")==0)) {
	    			insert.insertRow(clrefDomainsTable, "`cl_ref_original`, `nom_domaine`", "'"+companies[i]+"', '"+domains[i]+"'");
	    			insertedNewClref++;
	    		}
	    	}
	    }
	    System.out.println("Inserted new cl_ref : "+insertedNewClref);
	}
	
	/**
	 * Writes the number of users of each domain into the domains table.
	 * 
	 * @param sourceTable  the table with a list of all users
	 * @param ispDomainsTable the table with a list of all domains
	 */
	public void updateIspDomainUsersNumber(String sourceTable, String ispDomainsTable) {
		Select select = new Select();
		RowCounter counter = new RowCounter();
		Modify modify = new Modify();
		int totalDomains = counter.countAll(ispDomainsTable);
		
		for(int i=0;i<totalDomains;i++){
			String whereId = "id="+(i+1);
			String domainName = select.selectField("name", ispDomainsTable, whereId);
			domainName = domainName.replaceAll("'", "\\\\'"); // the char ' is replaced with \'
			String whereDomainName="domaines='"+domainName+"'";
			int totalUsers = counter.countFromWhere(sourceTable, whereDomainName);
			modify.modifyWhere(ispDomainsTable, whereId, "number_of_users", Integer.toString(totalUsers));
		}
	}
	
	public void countDomainsWithOneAddress(String sourceTable, String domainsTable) {
		Select select = new Select();
		RowCounter counter = new RowCounter();
		int totalDomains = counter.countAll(domainsTable);
		int result=0;
		for(int i=0;i<totalDomains;i++){
			String whereId = "id="+(i+1);
			String domainName = select.selectField("name", domainsTable, whereId);
			domainName = domainName.replaceAll("'", "\\\\'"); // the char ' is replaced with \'
			String whereDomainName="domaines='"+domainName+"'";
			int totalEmails = counter.countFromWhere(sourceTable, whereDomainName);
			if(totalEmails==1){
				result++;
			}
		}
		System.out.println("Total domains in "+domainsTable+
				" with one address in "+sourceTable+" : "+result);
	}
	
	/**
	 * Counts distinct values in a column of a given table. 
	 * 
	 * @param table the table to count from
	 * @param column the column whose distinct values should be counted
	 */
	public void countDistinct(String table, String column) {
		RowCounter counter = new RowCounter();
		int distinctValues = counter.countDistinct(column, table, null);
		System.out.println("Dinstinct values in column "+column+" of table "+table+" : "+distinctValues);
	}
	
	/**
	 * Counts domains with a given number of users.
	 * 
	 * @param table the table with the list of domains
	 * @param numberOfUsers the number of users to check
	 */
	public void countDomainsWithXCompanies(String table, int numberOfCompanies) {
		RowCounter counter = new RowCounter();
		String where = "number_of_companies="+numberOfCompanies;
		int result = counter.countFromWhere(table, where);
		System.out.println("Number of domains (in table "+table+") with "+numberOfCompanies+" companies: "+result);
	}
	
	/**
	 * Writes the number of distinct companies of each domain into the domains table.
	 * 
	 * @param sourceTable  the table with a list of all users
	 * @param domainsTable the table with a list of all domains
	 */
	public void updateDomainCompaniesNumber(String sourceTable, String domainsTable){
		Select select = new Select();
		RowCounter counter = new RowCounter();
		Modify modify = new Modify();
		int totalDomains = counter.countAll(domainsTable);
		for(int i=0;i<totalDomains;i++){
			String whereId = "id="+(i+1);
			String domainName = select.selectField("name", domainsTable, whereId);
			domainName = domainName.replaceAll("'", "\\\\'"); // the char ' is replaced with \'
			String whereDomainName="domaines='"+domainName+"'";
			int totalUsers = counter.countDistinct("cl_ref", sourceTable, whereDomainName);
			modify.modifyWhere(domainsTable, whereId, "number_of_companies", Integer.toString(totalUsers));
		}
	}
	
	/**
	 * Counts domains with a given number of users.
	 * 
	 * @param table the table with the list of domains
	 * @param numberOfUsers the number of users to check
	 */
	public void countDomainsWithXUsers(String table, int numberOfUsers) {
		RowCounter counter = new RowCounter();
		String where = "number_of_users="+numberOfUsers;
		int result = counter.countFromWhere(table, where);
		System.out.println("Number of domains (in table "+table+") with "+numberOfUsers+" users: "+result);
	}
	
	/**
	 * Writes the number of users of each domain into the domains table.
	 * 
	 * @param sourceTable  the table with a list of all users
	 * @param domainsTable the table with a list of all domains
	 */
	public void updateDomainUsersNumber(String sourceTable, String domainsTable) {
		Select select = new Select();
		RowCounter counter = new RowCounter();
		Modify modify = new Modify();
		int totalDomains = counter.countAll(domainsTable);
		for(int i=0;i<totalDomains;i++){
			String whereId = "id="+(i+1);
			String domainName = select.selectField("name", domainsTable, whereId);
			domainName = domainName.replaceAll("'", "\\\\'"); // the char ' is replaced with \'
			String whereDomainName="domaines='"+domainName+"'";
			int totalUsers = counter.countFromWhere(sourceTable, whereDomainName);
			modify.modifyWhere(domainsTable, whereId, "number_of_users", Integer.toString(totalUsers));
		}
	}
	
	public void cloneTable(String source, String cloned){
		CopyTable copy = new CopyTable();
		copy.copyTableFromWhere(source, cloned, null);
	}
	
	/**
	 * Copies distinct domains to a new table
	 * 
	 * @param sourceTable the table to take empty rows from
	 * @param newTable the table to be created with the same structure and with the empty rows copied
	 */
	public void insertDistinctDomains(String sourceTable, String newTable){
		InsertRows insert = new InsertRows();
		insert.insertDistinct(sourceTable, newTable, "domaines", "name");
	}
	
	/**
	 * Copies empty rows to a new table.
	 * 
	 * @param sourceTable the table to take empty rows from
	 * @param newTable the table to be created with the same structure and with the empty rows copied
	 */
	public void copyEmpty(String sourceTable, String newTable){
		//condition for copying
		String where = emptyRowsCond; 
		CopyTable copy = new CopyTable();
		copy.copyTableFromWhere(sourceTable, newTable, where);
	}
	
	/**
	 * Counts all rows from a table.
	 * 
	 * @param table the table whose rows to count
	 */
	public void countAll(String table) {
		RowCounter counter = new RowCounter();
		System.out.println("Total rows in "+table+" : " 
				+ counter.countAll(table));
	}
	
	/**
	 * Counts the number of rows without gender from a given table.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmptyDomain(String table) {
		RowCounter counter = new RowCounter();
		
		//we define the condition to select specific rows and to count them
		String where = "domaines IS NULL"; 
		
		System.out.println("Total rows with empty domain in "+table+" : " 
				+ counter.countFromWhere(table, where));
	}
	
	/**
	 * Counts the number of rows without gender from a given table.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmptyUser(String table) {
		RowCounter counter = new RowCounter();
		
		//we define the condition to select specific rows and to count them
		String where = "user IS NULL"; 
		
		System.out.println("Total rows with empty user in "+table+" : " 
				+ counter.countFromWhere(table, where));
	}
	
	/**
	 * Counts the number of rows without gender from a given table.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmptyGender(String table) {
		RowCounter counter = new RowCounter();
		
		//we define the condition to select specific rows and to count them
		String where = emptyGenderCond; 
		
		System.out.println("Total rows with empty gender in "+table+" : " 
				+ counter.countFromWhere(table, where));
	}
	
	/**
	 * Counts the number of rows without email from a given table.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmptyEmail(String table) {
		RowCounter counter = new RowCounter();
		
		//we define the condition to select specific rows and to count them
		String where = emptyEmailCond; 
		
		System.out.println("Total rows with empty email in "+table+" : " 
				+ counter.countFromWhere(table, where));
	}
	
	/**
	 * Counts the number of rows without first name from a given table.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmptyLastname(String table) {
		RowCounter counter = new RowCounter();
		
		//we define the condition to select specific rows and to count them
		String where = emptyLastnameCond; 
		
		System.out.println("Total rows with empty last name in "+table+" : " 
				+ counter.countFromWhere(table, where));
	}
	
	/**
	 * Counts the number of rows without last name from a given table.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmptyFirstname(String table) {
		RowCounter counter = new RowCounter();
		
		//we define the condition to select specific rows and to count them
		String where = emptyFirstnameCond; 
		
		System.out.println("Total rows with empty first name in "+table+" : " 
				+ counter.countFromWhere(table, where));
	}

	/**
	 * Prints clients with empty <b>first name</b>, <b>family name</b> and <b>email</b> at the same time. 
	 * <br />Needs a table which is a version of bd_emails and has at least the columns 
	 * <b>ct_prenom</b>, <b>ct_nom</b> and <b>ct_mail</b>.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmpty(String table){
		
		RowCounter counter = new RowCounter();
		
		//we define the condition to select specific rows and to count them
		String where = emptyRowsCond; 
		
		System.out.println("Total empty rows in "+table+" : " 
				+ counter.countFromWhere(table, where));
	}

	/**
	 * Prints clients with empty <b>first name</b>, <b>family name</b> and <b>email</b> at the same time. 
	 * <br />Needs a table which is a version of bd_emails and has at least the columns 
	 * <b>ct_prenom</b>, <b>ct_nom</b> and <b>ct_mail</b>.
	 * 
	 * @param table the table that should be checked
	 */
	public void countNonEmpty(String table){
		
		RowCounter counter = new RowCounter();
		
		//we define the condition to select specific rows and to count them
		String where = "NOT ("+emptyRowsCond+")";
		
		System.out.println("Total non-empty rows in "+table+" : " 
				+ counter.countFromWhere(table, where));
	}
	
	/**
	 * Gives total columns number and prints each column's name of a given table from the database. 
	 * 
	 * @param table the table whose columns should be taken into consideration
	 */
	public void showColumnNames(String table){
		Select select = new Select();
		String[] columnsList = select.getColumnsAsArray(table);
		System.out.println("Total number of columns in "+table+" : " + (columnsList.length-1));
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
		
		CopyTable copy = new CopyTable();
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
		
		InsertRows insert = new InsertRows();
		insert.copyRowsFromWhere(fromTable, toTable, where);
	}
	
	
}

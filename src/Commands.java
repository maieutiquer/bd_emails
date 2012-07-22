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
public class TreatmentCommands extends DataAccess {

	private static final long serialVersionUID = 2448602180321858504L;
	private static final String emptyGenderCond = 
			"ct_genre IN ('', 'A DEFINIR', '68', '-', '.', 'definir', '?', '???', 'z', 'Z', 'nc', 'NC')";
	private static final String emptyFirstnameCond = 
			"ct_prenom IN ('', 'A DEFINIR', '68', '-', '.', 'definir', '?', '???', 'z', 'Z', 'nc', 'NC')";
	private static final String emptyLastnameCond = 
			"ct_nom IN ('', 'A DEFINIR', '68', '-', '.', 'definir', '?', '???', 'z', 'Z', 'nc', 'NC')";
	public static final String emptyEmailCond = 
			"ct_mail IN ('', 'A DEFINIR', '68', '-', '.', 'definir', '?', '???', '84/12/39/44', " +
			"'05/65/77/85/37', 'z', 'Z', 'nc', 'NC', 'non', 'PAS DE MAIL', 'N� INDISPONIBLE', 'REPONDEUR', " +
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
	public TreatmentCommands(String dbName, String user, String pwd) {
		super(dbName, user, pwd);
	}
	
	public void exportUseful(String fromTable, String toTable) {
		Counter counter = new Counter();
		Select select = new Select();
		Modify modify = new Modify();
		InsertRows insert = new InsertRows();
		String[] ctRefs = select.selectStringFromWhere("ct_ref", fromTable, null);
		String[] clRefs = select.selectStringFromWhere("cl_ref", fromTable, null);
		String[] firstNames = select.selectStringFromWhere("ct_prenom", fromTable, null);
		String[] lastNames = select.selectStringFromWhere("ct_nom", fromTable, null);
		String[] emails = select.selectStringFromWhere("ct_mail", fromTable, null);
		boolean[] treated = new boolean[12000];
		
		String[][] localTable = new String[5][ctRefs.length];
		localTable[0] = ctRefs;
		localTable[1] = clRefs;
		localTable[2] = firstNames;
		localTable[3] = lastNames;
		localTable[4] = emails;
		
		for (int i=0; i<ctRefs.length; i++) {
			int clRef = Integer.parseInt(clRefs[i]);
			boolean existsMainClient = false;
			boolean existsNoNameClient = false;
			boolean existsNoEmailClient = false;
			boolean[] toCopyClRef = new boolean[12000];
			boolean[] toCopyCtRef = new boolean[120000];
			boolean[] mainClients = new boolean[120000];
			boolean mainClientCopied = false;
			if (treated[clRef] == true || clRef == 10624) {
				System.out.println("This client's enterprise ("+clRef+") has already been treated. Jumping to next client.");
				continue;
			}else{
				treated[clRef] = true;
				System.out.println("Beginning treatment of enterprise "+clRef+" ...");
			}
			for (int j=0; j<ctRefs.length; j++) {
				if (Integer.parseInt(localTable[1][j])==clRef) {
					boolean hasName = false;
					boolean hasEmail = false;
					if ( !isEmpty(localTable, 2, j)  || !isEmpty(localTable, 3, j)) {
						System.out.println("Client "+j+" has name.");
						hasName=true;
					}
					if (!isEmpty(localTable, 4, j)) {
						System.out.println("Client "+j+" has email.");
						hasEmail=true;
					}
					if (hasName && hasEmail) {
						System.out.println("This client can be used to form rules (main client).");
						existsMainClient = true;
						mainClients[j] = true;
					}
					if (!hasName && hasEmail) {
						System.out.println("This client should have its name filled.");
						existsNoNameClient = true;
					}
					if (hasName && !hasEmail) {
						System.out.println("This client should have its email filled.");
						existsNoEmailClient = true;
					}
					if (hasName || hasEmail) {
						toCopyCtRef[j] = true;
					}
				}
			}
			if (existsMainClient &&  existsNoEmailClient) {
				toCopyClRef[clRef] = true;
			}
			for (int j=0; j<ctRefs.length; j++) {
				if (mainClients[j] && mainClientCopied) {
					continue;
				}else if (Integer.parseInt(localTable[1][j])==clRef) {
					if (toCopyCtRef[j] && toCopyClRef[clRef]) {
						System.out.print("Writing client "+j+" to DB...");
						insert.insertRow(toTable, "`ct_ref`, `cl_ref`, `ct_prenom`, `ct_nom`, `ct_mail`", "'"+j+"', '"+clRef+"', '"
						+localTable[2][j].replaceAll("'", "\\\\'")
						+"', '"+localTable[3][j].replaceAll("'", "\\\\'")
						+"', '"+localTable[4][j].replaceAll("'", "\\\\'")+"'");
						System.out.println(" success.");
					}
				}
			}
			System.out.println("Treatment of this enterprise finished.");
		}
		
		
		
//		for (int i=0; i<5; i++) {
//			for (int j=0; j<ctRefs.length; j++) {
//				System.out.println(localTable[i][j]);
//			}
//		}
		
	}
	
	public boolean isEmpty(String[][] localTable, int i, int j) {
		if (  localTable[i][j].equals("") || localTable[i][j].equals("A DEFINIR") || localTable[i][j].equals("68") ||
				localTable[i][j].equals("-") || localTable[i][j].equals(".") || localTable[i][j].equals("definir") || 
				localTable[i][j].equals("?") || localTable[i][j].equals("???") || localTable[i][j].equals("z") || 
				localTable[i][j].equals("Z") || localTable[i][j].equals("nc") || localTable[i][j].equals("NC") ||
				localTable[i][j].equals(" DEFINIR") || localTable[i][j].equals("68") || localTable[i][j].equals("-") || 
				localTable[i][j].equals(".") || localTable[i][j].equals("definir") || localTable[i][j].equals("?") || localTable[i][j].equals("???") || 
				localTable[i][j].equals("84/12/39/44") || localTable[i][j].equals("05/65/77/85/37") || localTable[i][j].equals("z") || localTable[i][j].equals("Z") || 
				localTable[i][j].equals("nc") || localTable[i][j].equals("NC") || localTable[i][j].equals("non") || localTable[i][j].equals("PAS DE MAIL") || 
				localTable[i][j].equals("Nº INDISPONIBLE") || localTable[i][j].equals("REPONDEUR") || localTable[i][j].equals("en retraite") || localTable[i][j].equals("n'a pas de mail") || 
				localTable[i][j].equals("pas de demarchage") || localTable[i][j].equals("email") || localTable[i][j].equals("01.01.1981") || localTable[i][j].equals("fr") || 
				localTable[i][j].equals("REMPLACE MME SUINOT EN MALADIE") || localTable[i][j].equals("01 47 68 12 63") || localTable[i][j].equals("661261090") || localTable[i][j].equals("b") || 
				localTable[i][j].equals("603707107") || localTable[i][j].equals("pas d'adresse e-mail") || localTable[i][j].equals("630108886") || localTable[i][j].equals("pas adresse") || 
				localTable[i][j].equals("aucune") || localTable[i][j].equals("664998228") || localTable[i][j].equals("pas de mail") || localTable[i][j].equals("pas d'e-mail") || 
				localTable[i][j].equals("pas d'adresse") || localTable[i][j].equals("ne veut pas communiquer")) {
			return true;
		}
		return false;
	}
	
	public void selectCurrent(String table, String firstName, String lastName, int ctRef, int clRef ) {
		Select select = new Select();
		String[] currentFirstNames = select.selectStringFromWhere("ct_prenom", table, "cl_ref="+clRef);
		String[] currentLastNames = select.selectStringFromWhere("ct_nom", table, "cl_ref="+clRef);
		int[] currentCtRefs = select.selectIntFromWhere("ct_ref", table, "cl_ref="+clRef);
//		for (int i=0; i<currentCtRefs.length;i++) {
//			System.out.println(clRef+" and "+currentFirstNames[i]+" and "+currentLastNames[i]+" and "+currentCtRefs[i]);
//		}
		currentFirstNames=null;
		currentLastNames=null;
		currentCtRefs=null;
	}
	
	public void countMastersAndSlaves(String table) {
		Counter counter = new Counter();
		Select select = new Select();
		int[] companiesWithRules = select.selectIntFromWhere("rule", "cl_ref_regles", "rule");
		
		System.out.println("Nb avec une règle /contrôle=88 AND " +
				"TOTAL : " +
				counter.countFromWhere(table, "controle=1"));
		System.out.println("Nb avec une règle /contrôle=888 AND " +
				"TOTAL : " +
				counter.countFromWhere(table, "controle=2"));
		System.out.println("Number of rows with rule : " +
				counter.countFromWhere(table, "regle_user NOT IN (13, 14)"));
		System.out.println("Number of rows from company with defined rule and empty email : " +
				counter.countFromWhere(table, "regle_user NOT IN (13, 14) AND "+emptyEmailCond));
	}
	
	/**
	 * Creates a table with distinct cl_refs
	 * 
	 * @param sourceTable
	 * @param destinationTable
	 */
	public void fillClRefs(String sourceTable, String destinationTable) {
		InsertRows insert = new InsertRows();
		insert.insertDistinct(sourceTable, destinationTable, "cl_ref", "cl_ref");
	}
	
	/**
	 * Determines the empty rule. Doc to be completed.
	 * 
	 * @param table the table whose empty rule to determine
	 */
	public void determineEmptyUserRule(String table) {
		Modify modify = new Modify();
		Select select = new Select();
		String emptyCond = "("+emptyFirstnameCond+") AND ("+emptyLastnameCond+")";
		String[] ruleTexts = select.selectStringFromWhere("rule", "regle_user", null);
		int[] ruleIds = select.selectIntFromWhere("id", "regle_user", null);
		HashMap<Integer,String> ruleMap = new HashMap<Integer,String>();
		for (int i=0;i<ruleIds.length;i++) {
			ruleMap.put(ruleIds[i], ruleTexts[i]);
			System.out.println(ruleMap.get(i+1));
		}
		modify.modifyWhere(table, emptyCond, "regle_user", getKeyByValue(ruleMap, "v").toString());
		int[] clRefRangeArray = select.selectIntFromWhere("cl_ref", table, emptyCond);
		String clRefRange = "";
		for (int i=0; i<clRefRangeArray.length; i++) {
			clRefRange += Integer.toString(clRefRangeArray[i]) + ", ";
		}
		clRefRange = clRefRange.substring(0, clRefRange.length()-2); //removes the last comma
		modify.modifyWhere("cl_ref_regles", "cl_ref IN ("+clRefRange+")", "rule", getKeyByValue(ruleMap, "v").toString());
	}
	
	/**
	 * Inserts hardcoded rules into the bd_emails table
	 * 
	 * @param table
	 */
	public void insertHardcodedRules(String table) {
		Modify modify = new Modify();
		Select select = new Select();
		int[] ruleIds = select.selectIntFromWhere("id", "regle_user", null);
		String[] ruleTexts = select.selectStringFromWhere("rule", "regle_user", null);
		
		HashMap<Integer,String> ruleMap = new HashMap<Integer,String>();
		for (int i=0;i<ruleIds.length;i++) {
			ruleMap.put(ruleIds[i], ruleTexts[i]);
			System.out.println(ruleMap.get(i+1));
		}
		
		modify.modifyWhere(table, "ct_ref=38737", "regle_user", "2");
		modify.modifyWhere(table, "ct_ref=38737", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=38737"), "rule", "2");
		modify.modifyWhere(table, "ct_ref=1687", "regle_user", "2");
		modify.modifyWhere(table, "ct_ref=1687", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=1687"), "rule", "2");
		modify.modifyWhere(table, "ct_ref=109771", "regle_user", "2");
		modify.modifyWhere(table, "ct_ref=109771", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=109771"), "rule", "2");
		modify.modifyWhere(table, "ct_ref=46919", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=46919", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=46919"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=113414", "regle_user", "2");
		modify.modifyWhere(table, "ct_ref=113414", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=113414"), "rule", "2");
		modify.modifyWhere(table, "ct_ref=109771", "regle_user", "2");
		modify.modifyWhere(table, "ct_ref=109771", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=109771"), "rule", "2");
		modify.modifyWhere(table, "ct_ref=5142", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=5142", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=5142"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=113475", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=113475", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=113475"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=23646", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=23646", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=23646"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=47936", "regle_user", "7");
		modify.modifyWhere(table, "ct_ref=47936", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=47936"), "rule", "7");
		modify.modifyWhere(table, "ct_ref=46594", "regle_user", "7");
		modify.modifyWhere(table, "ct_ref=46594", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=46594"), "rule", "7");
		modify.modifyWhere(table, "ct_ref=20782", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=20782", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=20782"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=21232", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=21232", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=21232"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=108149", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=108149", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=108149"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=22597", "regle_user", "12");
		modify.modifyWhere(table, "ct_ref=22597", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=22597"), "rule", "12");
		modify.modifyWhere(table, "ct_ref=6061", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=6061", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=6061"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=54630", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=54630", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=54630"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=24422", "regle_user", "12");
		modify.modifyWhere(table, "ct_ref=24422", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=24422"), "rule", "12");
		modify.modifyWhere(table, "ct_ref=10338", "regle_user", getKeyByValue(ruleMap, "n").toString());
		modify.modifyWhere(table, "ct_ref=10338", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=10338"), "rule", getKeyByValue(ruleMap, "n").toString());
		modify.modifyWhere(table, "ct_ref=10337", "regle_user", getKeyByValue(ruleMap, "n").toString());
		modify.modifyWhere(table, "ct_ref=10337", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=10337"), "rule", getKeyByValue(ruleMap, "n").toString());
		modify.modifyWhere(table, "ct_ref=48763", "regle_user", "12");
		modify.modifyWhere(table, "ct_ref=48763", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=48763"), "rule", "12");
		modify.modifyWhere(table, "ct_ref=39107", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=39107", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=39107"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=99537", "regle_user", "7");
		modify.modifyWhere(table, "ct_ref=99537", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=99537"), "rule", "7");
		modify.modifyWhere(table, "ct_ref=47936", "regle_user", "7");
		modify.modifyWhere(table, "ct_ref=47936", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=47936"), "rule", "7");
		modify.modifyWhere(table, "ct_ref=38988", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=38988", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=38988"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=23646", "regle_user", "3");
		modify.modifyWhere(table, "ct_ref=23646", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=23646"), "rule", "3");
		modify.modifyWhere(table, "ct_ref=82219", "regle_user", getKeyByValue(ruleMap, "n").toString());
		modify.modifyWhere(table, "ct_ref=82219", "controle", "1");
		modify.modifyWhere("cl_ref_regles", "cl_ref="+select.selectField("cl_ref", table, "ct_ref=82219"), "rule", getKeyByValue(ruleMap, "n").toString());
	}
	
	/**
	 * Determines the rule for username in emails 
	 * and writes it in the corresponding column.
	 * 
	 * @param table the table whose rows to process
	 */
	public void determineUserRules(String table) {
		Modify modify = new Modify();
		Select select = new Select();
		String[] firstNames = select.selectStringFromWhere("ct_prenom", table, null);
		String[] lastNames = select.selectStringFromWhere("ct_nom", table, null);
		String[] users = select.selectStringFromWhere("user", table, null);
		String[] ruleTexts = select.selectStringFromWhere("rule", "regle_user", null);
		int[] ct_refs = select.selectIntFromWhere("ct_ref", table, null);
		int[] clRefs = select.selectIntFromWhere("cl_ref", table, null);
		int[] ruleIds = select.selectIntFromWhere("id", "regle_user", null);
		int[] allRules = select.selectIntFromWhere("regle_user", table, null);
		int[] clRefsOrdered = select.selectIntFromWhere("cl_ref", "cl_ref_regles", null);
		int[] clRefsOrderedRules = select.selectIntFromWhere("rule", "cl_ref_regles", null);
		
		HashMap<Integer,String> ruleMap = new HashMap<Integer,String>();
		for (int i=0;i<ruleIds.length;i++) {
			ruleMap.put(ruleIds[i], ruleTexts[i]);
			System.out.println(ruleMap.get(i+1));
		}
		int ctRef=0;
		int rule=0;
		int numberOfErrors=0;
		int numberOfWrites=0;
		int clRefPosition=0;
		int inherited=0;
		boolean write = false;
		boolean changeFirstName = false;
		boolean changeLastName = false;
		String user = "";
		Arrays.sort(clRefsOrdered);
		
//		try{
		for (int i=0; i<users.length; i++) {
			user = users[i];
			clRefPosition = Arrays.binarySearch(clRefsOrdered, clRefs[i]);
			if (allRules[i]!=getKeyByValue(ruleMap, "v") && !user.equals("")){
				if (Integer.parseInt(select.selectField("rule", "cl_ref_regles", "cl_ref="+clRefs[i]))==0) {
					changeFirstName = false;
					ctRef = ct_refs[i];
					String firstName = firstNames[i].toLowerCase();
					String lastName = lastNames[i].toLowerCase();
					if (!firstName.equals("") && firstName.length()>1) {
						if (user.contains(firstName)) {
							user = user.replaceAll(firstName, "e");
						}else{
							changeFirstName = true;
						}
					}
					if(!lastName.equals("") && lastName.length()>1){
						if (user.contains(lastName) ) {
							user = user.replaceAll(lastName, "o");
						}else{
							changeLastName = true;
						}
					}
					if (!(firstName.equals("")) && firstName.charAt(0)==users[i].charAt(0) && changeFirstName) {
						user = user.replaceFirst(Character.toString(users[i].charAt(0)), "p");
					}
					if (!(lastName.equals("")) && lastName.charAt(0)==users[i].charAt(0) && changeLastName) {
						user = user.replaceFirst(Character.toString(users[i].charAt(0)), "m");
					}
					if (ruleMap.containsValue(user)) {
						write=false;
						rule=getKeyByValue(ruleMap, user);
						switch (rule) {
						case 1:
							if (!firstName.equals("") && !lastName.equals("") 
									&& firstName.equals(users[i].substring(0, users[i].indexOf('.'))) 
									&& lastName.equals(users[i].substring(users[i].indexOf('.')+1))) {
								write=true;
							}else{
								numberOfErrors++;
								System.out.println("Error for rule "+rule+", prenom "+firstName+", nom "+lastName+", user "+users[i]+" and ct_ref "+ctRef);
							}
							break;
						case 2:
							if (!firstName.equals("") && !lastName.equals("") 
									&& firstName.charAt(0) == users[i].charAt(0) 
									&& lastName.equals(users[i].substring(users[i].indexOf('.')+1))) {
								write=true;
							}else{
								numberOfErrors++;
								System.out.println("Error for rule "+rule+", prenom "+firstName+", nom "+lastName+", user "+users[i]+" and ct_ref "+ctRef);
							}
							break;
						case 3:
							if (!firstName.equals("") && !lastName.equals("") 
									&& firstName.charAt(0) == users[i].charAt(0) 
									&& lastName.equals(users[i].substring(1))) {
								write=true;
							}else{
								numberOfErrors++;
								System.out.println("Error for rule "+rule+", prenom "+firstName+", nom "+lastName+", user "+users[i]+" and ct_ref "+ctRef);
							}
							break;
						case 4:
							if (lastName.equals(users[i])) {
								write=true;
							}else{
								numberOfErrors++;
								System.out.println("Error for rule "+rule+", prenom "+firstName+", nom "+lastName+", user "+users[i]+" and ct_ref "+ctRef);
							}
							break;
						case 5:
							if (!firstName.equals("") && !lastName.equals("") 
									&& lastName.equals(users[i].substring(0, users[i].indexOf('.'))) 
									&& firstName.equals(users[i].substring(users[i].indexOf('.')+1))) {
								write=true;
							}else{
								numberOfErrors++;
								System.out.println("Error for rule "+rule+", prenom "+firstName+", nom "+lastName+", user "+users[i]+" and ct_ref "+ctRef);
							}
							break;
						case 6:
							if (firstName.equals(users[i])) {
								write=true;
							}else{
								numberOfErrors++;
								System.out.println("Error for rule "+rule+", prenom "+firstName+", nom "+lastName+", user "+users[i]+" and ct_ref "+ctRef);
							}
							break;
						case 7:
							if (!firstName.equals("") && !lastName.equals("") 
									&& lastName.equals(users[i].substring(0, users[i].indexOf('.'))) 
									&& firstName.charAt(0) == users[i].charAt(users[i].indexOf('.')+1)) {
								write=true;
							}else{
								numberOfErrors++;
								System.out.println("Error for rule "+rule+", prenom "+firstName+", nom "+lastName+", user "+users[i]+" and ct_ref "+ctRef);
							}
							break;
						case 9:
							if (!firstName.equals("") && !lastName.equals("") 
									&& firstName.equals(users[i].substring(0, users[i].indexOf('_'))) 
									&& lastName.equals(users[i].substring(users[i].indexOf('_')+1))) {
								write=true;
							}else{
								numberOfErrors++;
								System.out.println("Error for rule "+rule+", prenom "+firstName+", nom "+lastName+", user "+users[i]+" and ct_ref "+ctRef);
							}
							break;
						case 10:
							if (!firstName.equals("") && !lastName.equals("") 
									&& firstName.charAt(0) == users[i].charAt(0) 
									&& lastName.equals(users[i].substring(users[i].indexOf('-')+1))) {
								write=true;
							}else{
								numberOfErrors++;
								System.out.println("Error for rule "+rule+", prenom "+firstName+", nom "+lastName+", user "+users[i]+" and ct_ref "+ctRef);
							}
							break;
						case 11:
							if (!firstName.equals("") && !lastName.equals("") 
									&& firstName.equals(users[i].substring(0, firstName.length()))
									&& lastName.equals(users[i].substring(firstName.length()))) {
								write=true;
							}else{
								numberOfErrors++;
								System.out.println("Error for rule "+rule+", prenom "+firstName+", nom "+lastName+", user "+users[i]+" and ct_ref "+ctRef);
							}
							break;
						case 12:
							if (!firstName.equals("") && !lastName.equals("") 
									&& firstName.charAt(0) == users[i].charAt(0) 
									&& lastName.charAt(0) == users[i].charAt(1) ) {
								write=true;
							}else{
								numberOfErrors++;
								System.out.println("Error for rule "+rule+", prenom "+firstName+", nom "+lastName+", user "+users[i]+" and ct_ref "+ctRef);
							}
							break;
						default:
							write=false;
							break;
						}

						if (write) {
							write=false;
							numberOfWrites++;
							System.out.println("At "+ctRef+" is "+getKeyByValue(ruleMap, user));
							modify.modifyWhere(table, "ct_ref="+ctRef, "regle_user", getKeyByValue(ruleMap, user).toString());
							modify.modifyWhere(table, "ct_ref="+ctRef, "controle", "1");
							modify.modifyWhere("cl_ref_regles", "cl_ref="+clRefPosition, "rule", Integer.toString(rule));
						}
					}
					
				}else if (Integer.parseInt(select.selectField("rule", "cl_ref_regles", "cl_ref="+clRefs[i]))!=getKeyByValue(ruleMap, "v")) {
					ctRef = ct_refs[i];
					user = users[i];
					System.out.println("At "+ctRef+" is inherited from "+clRefs[i]);
					modify.modifyWhere(table, "ct_ref="+ctRef, "regle_user", 
							select.selectField("rule", "cl_ref_regles", "cl_ref="+clRefs[i]));
					modify.modifyWhere(table, "ct_ref="+ctRef, "controle", "2");
				}
			}
		}
		System.out.println("Number of treated records: "+numberOfWrites);
		System.out.println("Number of caught errors: "+numberOfErrors);
		System.out.println("Number of inherited rules: "+inherited);
//		}catch(Exception e){
//			e.printStackTrace();
//			System.out.println("error at ct_ref: "+ct_ref);
//		}


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
		Counter counter = new Counter();
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
		Counter counter = new Counter();
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
		Counter counter = new Counter();
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
		Counter counter = new Counter();
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
		Counter counter = new Counter();
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
		Counter counter = new Counter();
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
		Counter counter = new Counter();
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
		Counter counter = new Counter();
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
		Counter counter = new Counter();
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
		Counter counter = new Counter();
		System.out.println("Total rows in "+table+" : " 
				+ counter.countAll(table));
	}

	/**
	 * Counts the number of rows without gender from a given table.
	 * 
	 * @param table the table that should be checked
	 */
	public void countEmptyDomain(String table) {
		Counter counter = new Counter();

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
		Counter counter = new Counter();

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
		Counter counter = new Counter();

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
		Counter counter = new Counter();

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
		Counter counter = new Counter();

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
		Counter counter = new Counter();

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

		Counter counter = new Counter();

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

		Counter counter = new Counter();

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
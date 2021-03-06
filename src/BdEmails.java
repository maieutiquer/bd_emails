
/**
 * <p>1. Nettoyage initial
 * <p>2. Deternimer les noms de domaines
 * <p>3. Determiner les regles de constitution des emails entreprises
 * <p>4. Reconstituer les adresses emails manquants
 * <p>5. Reconstituer nom prenom
 * <p>6. Determiner genre (M, Mme)
 * <p>7. Identifier les emails differents du nom prenom
 * 
 * @author Daniel
 * 
 */
public class BdEmails {

	private static String url = "jdbc:mysql://localhost:3306/"; //DB name
	private static String dbName = "bd_emails"; //DB name
	private static String user = "root"; //DB username
	private static String pwd = "asd123"; //DB password

	/**
	 * Methods are called from here with the desired parameters.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Commands commands = new Commands(url+dbName, user, pwd);
		commands.openConnection();

//		commands.showColumnNames("bd_emails_03");

//		insertNonEmpty("bd_emails", "bd_emails_05");
//		insertEmpty("bd_emails_05","empty_deleted");

//		commands.copyEmpty("bd_emails_03", "empty_deleted_3");
//		commands.copyNonEmpty("bd_emails_03", "bd_emails_temp3");

//		commands.countAll("empty_deleted_3");
//		System.out.println();
//		commands.countAll("bd_emails_05");
//		commands.countEmpty("bd_emails_05");
//		commands.countNonEmpty("bd_emails_05");
//		System.out.println();
//		commands.countEmptyFirstname("bd_emails_05");
//		commands.countEmptyLastname("bd_emails_05");
//		commands.countEmptyEmail("bd_emails_05");
//		commands.countEmptyGender("bd_emails_05");

//		commands.insertDistinctDomains("bd_emails_05", "domains_1");
//		commands.cloneTable("domains_1", "domains_2");
//		commands.updateDomainUsersNumber("bd_emails_05", "domains_2");
//		commands.showColumnNames("domains_2");
//		commands.updateDomainUsersNumber("bd_emails_05", "domains_2");
//		commands.updateDomainCompaniesNumber("bd_emails_05", "domains_3");
//		commands.countAll("domains_3");
//		commands.countDomainsWithXCompanies("domains_3", 0);
//		commands.countDomainsWithXCompanies("domains_3", 1);

//		commands.countDomainsWithOneAddress("bd_emails_05", "domains_3");

//		commands.updateIspDomainUsersNumber("bd_emails_05", "isp_domains_2");

//		commands.countDistinct("bd_emails_06", "cl_ref");
//		commands.createMoreClref("bd_emails_06", "cl_ref_domains_3");
//		commands.countAll("cl_ref_domains_3");
//		commands.countDistinct("cl_ref_domains_3", "cl_ref_original");
//		
//		commands.countDedoubledClients("cl_ref_domains_2");

//		commands.convertDomainsToLowercase("bd_emails_05_autres_1");
//		commands.convertEmailsToLowercase("bd_emails_05_autres_1");
//		commands.cleanClrefs("bd_emails_05_autres_1");
//		commands.createMoreClref("bd_emails_05_autres_1", "cl_ref_domains_4");

//		commands.copyClientsWithOtherDomains("bd_emails_05", "bd_emails_05_autres");
//		commands.copyClientsWithIspDomains("bd_emails_05", "bd_emails_05_isp");
//		commands.countAll("bd_emails_05_isp");

//		commands.determineUser("bd_emails_05_autres_2");
		commands.countAll("bd_emails_05_autres_6");

//		commands.determineUserAndDomain("bd_emails_05_isp_3");
//		
//		commands.countEmptyFirstname("bd_emails_05_isp_3");
//		commands.countEmptyLastname("bd_emails_05_isp_3");
//		commands.countEmptyUser("bd_emails_05_isp_3");
//		commands.countEmptyDomain("bd_emails_05_isp_3");
//		commands.countEmptyEmail("bd_emails_05_isp_3");
//		commands.countEmptyGender("bd_emails_05_isp_3");

//		commands.fillClRefs("bd_emails_05_autres_6", "cl_ref_regles");
//		commands.determineEmptyUserRule("bd_emails_05_autres_6");
//		commands.insertHardcodedRules("bd_emails_05_autres_6");
//		commands.determineUserRules("bd_emails_05_autres_6");
		commands.countMastersAndSlaves("bd_emails_05_autres_6");

		commands.closeConnection();
		System.exit(0);
	}

}

package org.dimensinfin.neocom.support;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.neocom.support.adapters.NeoComComponentFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAcceptanceTestDataApplication {
	private static Logger logger = LoggerFactory.getLogger(CreateAcceptanceTestDataApplication.class);
	private static CreateAcceptanceTestDataApplication singleton;

	public static void main( final String[] args ) throws IOException {
		logger.info(">> [CreateAcceptanceTestDataApplication.main]");
		singleton = new CreateAcceptanceTestDataApplication();
		singleton.extractCredentialData4DB();
		logger.info("<< [CreateAcceptanceTestDataApplication.main]");
	}

	// - C O M P O N E N T S
	private CredentialRepository credentialRepository;

	protected void extractCredentialData4DB() {
		logger.info(">> [CreateAcceptanceTestDataApplication.extractCredentialData4DB]");
		if (null == this.credentialRepository)
			this.credentialRepository = NeoComComponentFactory.getSingleton().getCredentialRepository();
		this.printCredentials4Acceptance(); // Generate the string to be used in acceptance tests.
		this.printCredentials4VisualTesting(); // Generate the code snippet to be used in Visual testing.
		logger.info("<< [CreateAcceptanceTestDataApplication.extractCredentialData4DB]");
	}

	protected void printCredentials4Acceptance() {
		String output = "\nGiven the following Credentials into my repository\n";
		output += "| uniqueCredential | accountId | accountName | dataSource | accessToken | refreshToken | scope | walletBalance | assetsCount | raceName |\n";
		for (Credential cred : this.credentialRepository.accessAllCredentials()) {
			output += "| " + Credential.createUniqueIdentifier(cred.getDataSource(), cred.getAccountId());
			output += " | " + cred.getAccountId();
			output += " | " + cred.getAccountName();
			output += " | " + cred.getDataSource().toLowerCase();
			output += " | " + cred.getAccessToken();
			output += " | " + cred.getRefreshToken();
			output += " | " + cred.getScope();
			output += " | " + cred.getWalletBalance();
			output += " | " + cred.getAssetsCount();
			if (null == cred.getRaceName()) output += " | " + " ";
			else output += " | " + cred.getRaceName();
			output += " |\n";
		}
		logger.info(output);
	}

	protected void printCredentials4VisualTesting() {
		String output = "\n";
		for (Credential cred : this.credentialRepository.accessAllCredentials()) {
			output += "final Credential credential = new Credential.Builder(" + cred.getAccountId() + ")\n" +
					         "\t\t\t\t                              .withAccountId(" + cred.getAccountId() + ")\n" +
					         "\t\t\t\t                              .withAccountName(\"" + cred.getAccountName() + "\")\n" +
					         "\t\t\t\t                              .withAccessToken(\"" + cred.getAccessToken() + "\")\n" +
					         "\t\t\t\t                              .withRefreshToken(\"" + cred.getRefreshToken() + "\")\n" +
					         "\t\t\t\t                              .withDataSource(\"" + cred.getDataSource() + "\")\n" +
					         "\t\t\t\t                              .withScope(\"" + cred.getScope() + "\")\n" +
					         "\t\t\t\t                              .withAssetsCount(" + cred.getAssetsCount() + ")\n" +
					         "\t\t\t\t                              .withWalletBalance(" + cred.getWalletBalance() + ")\n" +
					         "\t\t\t\t                              .withRaceName(\"" + cred.getRaceName() + "\")\n" +
					         "\t\t\t\t                              .build();\n";
		}
		logger.info(output);
	}
}

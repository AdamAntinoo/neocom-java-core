package org.dimensinfin.neocom.support;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAcceptanceTestDataApplication {
	private static Logger logger = LoggerFactory.getLogger(CreateAcceptanceTestDataApplication.class);
	private static CreateAcceptanceTestDataApplication singleton;

	// - C O M P O N E N T S
	private CredentialRepository credentialRepository;

	public static void main( final String[] args ) throws IOException {
		logger.info(">> [CreateAcceptanceTestDataApplication.main]");
		singleton = new CreateAcceptanceTestDataApplication();
		singleton.extractCredentialData4DB();
		logger.info("<< [CreateAcceptanceTestDataApplication.main]");
	}

	protected void extractCredentialData4DB() {
		logger.info(">> [CreateAcceptanceTestDataApplication.extractCredentialData4DB]");
		if (null == this.credentialRepository)
			this.credentialRepository = NeoComComponentFactory.getSingleton().getCredentialRepository();
		logger.info("<< [CreateAcceptanceTestDataApplication.extractCredentialData4DB]");
	}
}

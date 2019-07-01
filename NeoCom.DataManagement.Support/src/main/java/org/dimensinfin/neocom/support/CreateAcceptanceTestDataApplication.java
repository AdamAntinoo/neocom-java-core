package org.dimensinfin.neocom.support;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAcceptanceTestDataApplication {
	private static Logger logger = LoggerFactory.getLogger(CreateAcceptanceTestDataApplication.class);
	private static CreateAcceptanceTestDataApplication singleton;

	public static void main( final String[] args ) throws IOException {
		logger.info(">> [CreateAcceptanceTestDataApplication.main]");
		singleton = new CreateAcceptanceTestDataApplication();
		logger.info("<< [CreateAcceptanceTestDataApplication.main]");
	}

	protected void extractCredentialData4DB() {
		logger.info(">> [CreateAcceptanceTestDataApplication.extractCredentialData4DB]");

		logger.info("<< [CreateAcceptanceTestDataApplication.extractCredentialData4DB]");
	}
}

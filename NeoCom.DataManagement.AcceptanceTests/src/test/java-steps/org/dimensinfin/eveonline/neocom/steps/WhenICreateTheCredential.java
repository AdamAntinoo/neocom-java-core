package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.support.CucumberTableConverter;
import org.dimensinfin.eveonline.neocom.support.credential.CredentialWorld;
import org.dimensinfin.eveonline.neocom.support.credential.CucumberTableToCredentialConverter;

import cucumber.api.java.en.When;

public class WhenICreateTheCredential {
	private CredentialWorld credentialWorld;
	private CucumberTableConverter<Credential> date2CredentialConverter;

	public WhenICreateTheCredential( final CredentialWorld credentialWorld,
	                                 final CucumberTableToCredentialConverter date2CredentialConverter ) {
		this.credentialWorld = credentialWorld;
		this.date2CredentialConverter = date2CredentialConverter;
	}

	@When("I create the Credential")
	public void i_create_the_Credential() {
		this.credentialWorld.setCredentialUnderTest(
				this.date2CredentialConverter.convert(this.credentialWorld.getAuthorisationData().get(0)));
	}
}

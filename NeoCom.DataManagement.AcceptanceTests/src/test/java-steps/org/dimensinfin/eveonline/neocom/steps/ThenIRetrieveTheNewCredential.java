package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.support.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.credential.CredentialWorld;

import java.sql.SQLException;
import java.util.Objects;

import cucumber.api.java.en.Then;

public class ThenIRetrieveTheNewCredential {
	private CredentialWorld credentialWorld;
	private CredentialRepository credentialRepository;

	public ThenIRetrieveTheNewCredential( final CredentialWorld credentialWorld ) {
		this.credentialWorld = credentialWorld;
		this.credentialRepository = NeoComComponentFactory.getSingleton().getCredentialRepository();
		Objects.requireNonNull(this.credentialWorld);
		Objects.requireNonNull(this.credentialRepository);
	}

	@Then("I retrieve the new credential with id {string}")
	public void i_retrieve_the_new_credential_with_id( final String credentialId ) throws SQLException {
		this.credentialWorld.setCredentialUnderTest(this.credentialRepository.findCredentialById(credentialId));
	}
}

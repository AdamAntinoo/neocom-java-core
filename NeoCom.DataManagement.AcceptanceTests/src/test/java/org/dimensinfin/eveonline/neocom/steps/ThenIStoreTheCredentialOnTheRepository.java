package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.support.adapters.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.credential.CredentialWorld;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.Objects;

import cucumber.api.java.en.Then;

public class ThenIStoreTheCredentialOnTheRepository {
	private CredentialWorld credentialWorld;
	private CredentialRepository credentialRepository;

	@Autowired
	public ThenIStoreTheCredentialOnTheRepository( final CredentialWorld credentialWorld ) {
		this.credentialWorld = credentialWorld;
		this.credentialRepository = NeoComComponentFactory.getSingleton().getCredentialRepository();
		Objects.requireNonNull(this.credentialWorld);
		Objects.requireNonNull(this.credentialRepository);
	}

	@Then("I store the credential on the repository")
	public void i_store_the_credential_on_the_repository() throws SQLException {
		this.credentialRepository.persist(this.credentialWorld.getCredentialUnderTest());
	}
}

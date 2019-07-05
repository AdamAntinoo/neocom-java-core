package org.dimensinfin.eveonline.neocom.steps;

import java.sql.SQLException;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.support.credential.CredentialWorld;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;

public class GivenAStaleCredentialWithId {
	private CredentialWorld credentialWorld;
	private CredentialRepository credentialRepository;

	@Autowired
	public GivenAStaleCredentialWithId( final CredentialWorld credentialWorld ) {
		this.credentialWorld = credentialWorld;
		this.credentialRepository = NeoComComponentFactory.getSingleton().getCredentialRepository();
		Objects.requireNonNull(this.credentialWorld);
		Objects.requireNonNull(this.credentialRepository);
	}

	@Given("a stale Credential with id {string}")
	public void a_stale_Credential_with_id( String credentialId ) throws SQLException {
		final Credential credential = this.credentialRepository.findCredentialById(credentialId);
		this.credentialWorld.setCredentialRead(credential);
	}
}

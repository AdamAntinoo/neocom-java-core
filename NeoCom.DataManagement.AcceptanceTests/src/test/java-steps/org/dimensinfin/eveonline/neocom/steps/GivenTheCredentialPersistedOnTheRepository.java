package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.support.CucumberTableConverter;
import org.dimensinfin.eveonline.neocom.support.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.credential.CredentialWorld;
import org.dimensinfin.eveonline.neocom.support.credential.CucumberTableToCredentialConverter;
import org.junit.Assert;

import java.sql.SQLException;
import java.util.Objects;

import cucumber.api.java.en.Given;

public class GivenTheCredentialPersistedOnTheRepository {
	private CredentialWorld credentialWorld;
	private CucumberTableConverter<Credential> date2CredentialConverter;
	private CredentialRepository credentialRepository;

	public GivenTheCredentialPersistedOnTheRepository( final CredentialWorld credentialWorld,
	                                                   final CucumberTableToCredentialConverter date2CredentialConverter ) {
		this.credentialWorld = credentialWorld;
		this.date2CredentialConverter = date2CredentialConverter;
		this.credentialRepository = NeoComComponentFactory.getSingleton().getCredentialRepository();
		Objects.requireNonNull(this.credentialWorld);
		Objects.requireNonNull(this.credentialRepository);
	}

	@Given("the Credential persisted on the repository")
	public void the_Credential_persisted_on_the_repository() throws SQLException {
		this.credentialWorld.setCredentialUnderTest(this.date2CredentialConverter.convert(this.credentialWorld.getAuthorisationData().get(0)));
		this.credentialRepository.persist(this.credentialWorld.getCredentialUnderTest());
		Assert.assertNotNull(this.credentialRepository.findCredentialById(this.credentialWorld.getCredentialId()));
	}
}

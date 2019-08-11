package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.support.credential.CredentialWorld;

import java.util.List;
import java.util.Map;

import cucumber.api.java.en.Given;

public class GivenTheNextAuthorisationData {
	private CredentialWorld credentialWorld;

	public GivenTheNextAuthorisationData( final CredentialWorld credentialWorld ) {
		this.credentialWorld = credentialWorld;
	}

	@Given("the next authorisation data")
	public void the_next_authorisation_data( final List<Map<String, String>> cucumberTable ) {
		this.credentialWorld.setAuthorisationData(cucumberTable);
	}
}

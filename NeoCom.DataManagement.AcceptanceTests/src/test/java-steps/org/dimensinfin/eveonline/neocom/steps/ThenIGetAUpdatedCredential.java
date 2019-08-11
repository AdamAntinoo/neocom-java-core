package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.support.CucumberTableConverter;
import org.dimensinfin.eveonline.neocom.support.credential.CredentialWorld;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

import cucumber.api.java.en.Then;

public class ThenIGetAUpdatedCredential {
	private CredentialWorld credentialWorld;
	private CucumberTableConverter<Credential> date2CredentialConverter;

	public ThenIGetAUpdatedCredential( final CredentialWorld credentialWorld,
	                                 final CucumberTableConverter<Credential> date2CredentialConverter ) {
		this.credentialWorld = credentialWorld;
		this.date2CredentialConverter = date2CredentialConverter;
	}
	@Then("I get a updated Credential with the next valid data")
	public void i_get_a_updated_Credential_with_the_next_valid_data(final List<Map<String, String>> cucumberTable) {
		final Credential testData = this.date2CredentialConverter.convert(cucumberTable.get(0));
		final Credential target = this.credentialWorld.getCredentialUnderTest();
		Assert.assertEquals("The two credentials should match in updated fields.",
				testData.getAccountName(), target.getAccountName());
//		Assert.assertEquals("The two credentials should match in updated fields.",
//				testData.getWalletBalance(), target.getWalletBalance(), 0.1);
		Assert.assertEquals("The two credentials should match in updated fields.",
				testData.getAssetsCount(), target.getAssetsCount());
		Assert.assertEquals("The two credentials should match in updated fields.",
				testData.getRaceName(), target.getRaceName());
	}
}

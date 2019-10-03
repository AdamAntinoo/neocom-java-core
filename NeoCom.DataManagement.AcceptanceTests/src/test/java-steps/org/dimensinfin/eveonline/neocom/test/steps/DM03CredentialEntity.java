package org.dimensinfin.eveonline.neocom.test.steps;

import org.dimensinfin.core.domain.EEvents;
import org.dimensinfin.core.domain.IntercommunicationEvent;
import org.dimensinfin.core.interfaces.IEventReceiver;
import org.dimensinfin.eveonline.neocom.adapters.NeoComRetrofitFactory;
import org.dimensinfin.eveonline.neocom.updaters.NeoComUpdaterFactory;
import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.support.credential.CredentialWorld;
import org.dimensinfin.eveonline.neocom.support.credential.CucumberTableToCredentialConverter;
import org.dimensinfin.eveonline.neocom.test.support.NeoComComponentFactory;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DM03CredentialEntity implements IEventReceiver {
	private CredentialWorld credentialWorld;
	private CucumberTableToCredentialConverter cucumberTableToCredentialConverter;

	public DM03CredentialEntity( final CredentialWorld credentialWorld,
	                             final CucumberTableToCredentialConverter cucumberTableToCredentialConverter ) {
		this.credentialWorld = credentialWorld;
		this.cucumberTableToCredentialConverter = cucumberTableToCredentialConverter;
	}

	@And("I get a valid credential with the next valid data")
	public void iGetAValidCredentialWithTheNextValidData( final List<Map<String, String>> cucumberTable ) {
		final Credential testData = this.cucumberTableToCredentialConverter.convert(cucumberTable.get(0));
		final boolean result = testData.equals(this.credentialWorld.getCredentialUnderTest());
		Assert.assertTrue("The two credentials should match in all fields after being stored and retrieved.", result);
	}

	@Then("I get a updated Credential with the next valid data")
	public void iGetAUpdatedCredentialWithTheNextValidData( final List<Map<String, String>> cucumberTable ) {
		final Credential testData = this.cucumberTableToCredentialConverter.convert(cucumberTable.get(0));
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

	@When("I check Credential update state")
	public void iCheckCredentialUpdateState() {
		NeoComRetrofitFactory.add2MockList("getCharactersCharacterIdAssets");
		final Credential model = this.credentialWorld.getCredentialUnderTest();
		final NeoComUpdater updater = NeoComUpdaterFactory.buildUpdater(model); // Create the updater for this model.
		updater.addEventListener(this);
		NeoComUpdater.injectsEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
		updater.onRun(); // Start the refresh process if required before leaving this thread.
		final Credential credential = this.credentialWorld.getCredentialUnderTest();
		Assert.assertTrue(credential.getAssetsCount() > 0);
	}

	@Override
	public void receiveEvent( final IntercommunicationEvent event ) {
		Assert.assertNotNull(event);
		if (event.getPropertyName().equalsIgnoreCase(EEvents.EVENT_REFRESHDATA.name())) {
			Assert.assertTrue(event.getPropertyName().equalsIgnoreCase(EEvents.EVENT_REFRESHDATA.name()));
		} else throw new NeoComRuntimeException("The event received is not on the list.");
	}
}
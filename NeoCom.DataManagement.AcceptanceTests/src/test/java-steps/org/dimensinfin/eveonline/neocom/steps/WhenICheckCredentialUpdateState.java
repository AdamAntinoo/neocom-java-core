package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.eveonline.neocom.adapters.NeoComRetrofitFactory;
import org.dimensinfin.eveonline.neocom.adapters.NeoComUpdaterFactory;
import org.dimensinfin.eveonline.neocom.core.EEvents;
import org.dimensinfin.eveonline.neocom.core.IEventReceiver;
import org.dimensinfin.eveonline.neocom.core.NeoComEvent;
import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.support.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.credential.CredentialWorld;
import org.junit.Assert;

import cucumber.api.java.en.When;

public class WhenICheckCredentialUpdateState implements IEventReceiver {
	private CredentialWorld credentialWorld;

	public WhenICheckCredentialUpdateState( final CredentialWorld credentialWorld ) {
		this.credentialWorld = credentialWorld;
		// Initialise the Update injection.
		NeoComUpdater.injectsEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
	}

	@When("I check Credential update state")
	public void i_check_Credential_update_state() {
		NeoComRetrofitFactory.add2MockList("getCharactersCharacterIdAssets");
		final Credential model = this.credentialWorld.getCredentialUnderTest();
		final NeoComUpdater updater = NeoComUpdaterFactory.buildUpdater(model); // Create the updater for this model.
		updater.addEventListener(this);
		updater.onRun(); // Start the refresh process if required before leaving this thread.
		final Credential credential = this.credentialWorld.getCredentialUnderTest();
		Assert.assertTrue(credential.getAssetsCount() > 0);
	}

	@Override
	public void eventReceived( final NeoComEvent event ) {
		Assert.assertNotNull(event);
		if (event.getPropertyName().equalsIgnoreCase(EEvents.EVENT_REFRESHDATA.name())) {
			Assert.assertTrue(event.getPropertyName().equalsIgnoreCase(EEvents.EVENT_REFRESHDATA.name()));
		} else throw new NeoComRuntimeException("The event received is not on the list.");
	}
}

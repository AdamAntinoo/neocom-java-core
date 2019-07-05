package org.dimensinfin.eveonline.neocom.steps;

import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.adapters.NeoComUpdaterFactory;
import org.dimensinfin.eveonline.neocom.core.EEvents;
import org.dimensinfin.eveonline.neocom.core.IEventReceiver;
import org.dimensinfin.eveonline.neocom.core.NeoComEvent;
import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.support.credential.CredentialWorld;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.When;

public class WhenICheckCredentialUpdateState implements IEventReceiver {
	private CredentialWorld credentialWorld;

	@Autowired
	public WhenICheckCredentialUpdateState( final CredentialWorld credentialWorld ) {
		this.credentialWorld = credentialWorld;
		// Initialise the Update injection.
		NeoComUpdater.injectsEsiDataAdapter(NeoComComponentFactory.getSingleton().getEsiDataAdapter());
	}

	@When("I check Credential update state")
	public void i_check_Credential_update_state() {
		final Credential model = this.credentialWorld.getCredentialRead();
		final NeoComUpdater updater = NeoComUpdaterFactory.buildUpdater(model); // Create the updater for this model.
		updater.addEventListener(this);
		updater.refresh(); // Start the refresh process if required before leaving this thread.
		Awaitility.await().atMost(100, TimeUnit.SECONDS).until(() -> {
					Thread.sleep(TimeUnit.SECONDS.toMillis(10));
					final Credential credential = this.credentialWorld.getCredentialRead();
					Assert.assertTrue(credential.getAssetsCount() > 0);
					return true;
				}
		);
	}

	@Override
	public void eventReceived( final NeoComEvent event ) {
		Assert.assertNotNull(event);
		if (event.getPropertyName().equalsIgnoreCase(EEvents.EVENT_REFRESHDATA.name())) {
			Assert.assertTrue(event.getPropertyName().equalsIgnoreCase(EEvents.EVENT_REFRESHDATA.name()));
		} else throw new NeoComRuntimeException("The event received is not on the list.");
	}
}

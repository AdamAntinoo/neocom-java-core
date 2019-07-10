package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.android.mvc.controller.ControllerFactory;
import org.dimensinfin.android.mvc.controller.IAndroidController;
import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.app.controller.CredentialController;
import org.dimensinfin.eveonline.neocom.core.updaters.CredentialUpdater;
import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;

public class TestControllerUpdaterFactory extends ControllerFactory {
	public TestControllerUpdaterFactory( final String selectedVariant ) {
		super(selectedVariant);
	}

	@Override
	public IAndroidController createController( final ICollaboration node ) {
		logger.info("-- [AppControllerFactory.createController]> Node class: {}", node.getClass().getSimpleName());
		if (node instanceof Credential) {
			return new CredentialController((Credential) node, this);
		}
		return super.createController(node);
	}

	public NeoComUpdater buildUpdater( final ICollaboration model ) {
		if (model instanceof Credential)
			return new CredentialUpdater((Credential) model);
		return null;
	}
}

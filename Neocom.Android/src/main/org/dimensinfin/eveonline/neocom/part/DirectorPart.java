//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.part;

import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.eveonline.neocom.model.Director;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.dimensinfin.eveonline.neocom.render.DirectorHolder;

import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class DirectorPart extends AbstractAndroidPart implements OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 2354400290130765711L;
	private static Logger			logger						= Logger.getLogger("DirectorPart");

	// - F I E L D - S E C T I O N ............................................................................
	private NeoComCharacter		targetPilot				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DirectorPart(final Director node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean checkActivation() {
		return this.getCastedModel().checkActivation(targetPilot);
	}

	public int getActiveIcon() {
		return this.getCastedModel().getActiveIcon();
	}

	public int getDimmedIcon() {
		return this.getCastedModel().getDimmedIcon();
	}

	@Override
	public long getModelID() {
		return this.getCastedModel().getName().hashCode();
	}

	public String getName() {
		return this.getCastedModel().getName();
	}

	public void onClick(final View v) {
		DirectorPart.logger.info(">> [DirectorPart.onClick]");
		// Activate the manager.
		this.getCastedModel().launchActivity(targetPilot);
		DirectorPart.logger.info("<< [DirectorPart.onClick]");
	}

	@Override
	public Vector<IPart> runPolicies(final Vector<IPart> targets) {
		return targets;
	}

	public void setPilot(final NeoComCharacter pilot) {
		targetPilot = pilot;
	}

	@Override
	protected AbstractHolder selectHolder() {
		return new DirectorHolder(this, _activity);
	}

	private Director getCastedModel() {
		return (Director) this.getModel();
	}
}

// - UNUSED CODE ............................................................................................

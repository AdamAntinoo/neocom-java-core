//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.activity.DirectorsBoardActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.enums.EVARIANT;
import org.dimensinfin.evedroid.interfaces.INamedPart;
import org.dimensinfin.evedroid.model.NeoComCharacter;
import org.dimensinfin.evedroid.model.Pilot;
import org.dimensinfin.evedroid.render.PilotInfoHolder;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotInfoPart extends EveAbstractPart implements INamedPart, OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -1731066477259354660L;
	private static Logger			logger						= Logger.getLogger("PilotInfoPart");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotInfoPart(final AbstractComplexNode pilot) {
		super(pilot);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The result of this method depends on the variant use but this is not already supported. For the initial
	 * usage of this part at the Pilot List Activity we just expand to itself.
	 */
	@Override
	public ArrayList<AbstractAndroidPart> collaborate2View() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		result.add(this);
		return result;
	}

	public Pilot getCastedModel() {
		return (Pilot) this.getModel();
	}

	@Override
	public long getModelID() {
		return 0;
	}

	public String getName() {
		return this.getCastedModel().getName();
	}

	public String getTransformedAssetsCount() {
		final DecimalFormat formatter = new DecimalFormat("###,### Items");
		final long assetCount = this.getCastedModel().getAssetCount();
		final String countString = formatter.format(assetCount);
		return countString;
	}

	public String getTransformedBalance() {
		final DecimalFormat formatter = new DecimalFormat("#,###.00 ISK");
		final String strbalance = formatter.format(this.getCastedModel().getAccountBalance());
		return strbalance;
	}

	/**
	 * If the Pilot is active the click has to show the Pilot Dashboard Activity. Id the Pilot is not active the
	 * event should be discarded.
	 */
	public void onClick(final View view) {
		PilotInfoPart.logger.info(">> [PilotInfoPart.onClick]");
		// Set the pilot selected on the context and then go to the Pilot Dashboard.
		final Object pilotPart = view.getTag();
		if (pilotPart instanceof PilotInfoPart) {
			// TODO This is to keep compatibility with the old data management.
			// Pilot are expected to be at the global context
			final NeoComCharacter pilot = ((PilotInfoPart) pilotPart).getCastedModel();
			AppModelStore.getSingleton().activatePilot(pilot.getCharacterID());
			final Intent intent = new Intent(this.getActivity(), DirectorsBoardActivity.class);
			intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID, pilot.getCharacterID());
			EVEDroidApp.getAppStore().getActivity().startActivity(intent);
		}
		PilotInfoPart.logger.info("<< [PilotInfoPart.onClick]");
	}

	@Override
	protected AbstractHolder selectHolder() {
		// Get the proper holder set for the render mode.
		if (this.getRenderMode() == EVARIANT.CAPSULEER_LIST.hashCode()) return new PilotInfoHolder(this, _activity);
		// If holder not located return a default view for a sample and modeless Part.
		return super.selectHolder();
	}
}

// - UNUSED CODE ............................................................................................

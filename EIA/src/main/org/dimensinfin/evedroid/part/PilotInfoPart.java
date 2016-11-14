//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

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
import org.dimensinfin.evedroid.core.INamedPart;
import org.dimensinfin.evedroid.holder.PilotInfoHolder;
import org.dimensinfin.evedroid.model.EveChar;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotInfoPart extends AbstractAndroidPart implements INamedPart, OnClickListener {
	// - S T A T I C - S E C T I O N
	// ..........................................................................
	private static final long	serialVersionUID	= -1731066477259354660L;
	private static Logger			logger						= Logger.getLogger("PilotInfoPart");

	// - F I E L D - S E C T I O N
	// ............................................................................
	// private AbstractPilotBasedActivity _activity = null;
	// private Fragment _fragment = null;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public PilotInfoPart(final AbstractComplexNode pilot) {
		super(pilot);
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	public String get_assetsCount() {
		final DecimalFormat formatter = new DecimalFormat("###,### Items");
		final long assetCount = getCastedModel().getAssetCount();
		final String countString = formatter.format(assetCount);
		return countString;
	}

	public String get_balance() {
		final DecimalFormat formatter = new DecimalFormat("#,###.00 ISK");
		final String strbalance = formatter.format(getCastedModel().getBalance());
		return strbalance;
	}

	public EveChar getCastedModel() {
		return (EveChar) getModel();
	}

	@Override
	public long getModelID() {
		return 0;
	}

	public String getName() {
		return getCastedModel().getName();
	}

	public void onClick(final View view) {
		logger.info(">> PilotInfoPart.onClick");
		// Set the pilot selected on the context and then go to the Director
		// board.
		final Object pilotPart = view.getTag();
		if (pilotPart instanceof PilotInfoPart) {
			// TODO This is to keep compatibility with the old data management.
			// Pilot are expected to be at the global context
			final EveChar pilot = ((PilotInfoPart) pilotPart).getCastedModel();
			EVEDroidApp.getAppStore().activatePilot(pilot.getCharacterID());
			final Intent intent = new Intent(getActivity(), DirectorsBoardActivity.class);
			intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID, pilot.getCharacterID());
			EVEDroidApp.getAppStore().getActivity().startActivity(intent);
		}
		logger.info("<< PilotInfoPart.onClick");
	}

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

	@Override
	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		return new PilotInfoHolder(this, this._activity);
	}
}

// - UNUSED CODE
// ............................................................................................

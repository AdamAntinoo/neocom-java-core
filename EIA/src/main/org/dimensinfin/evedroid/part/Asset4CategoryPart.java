//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.evedroid.holder.Asset4CategoryHolder;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

import android.app.Activity;
import android.app.Fragment;
import android.text.Spanned;

// - CLASS IMPLEMENTATION ...................................................................................
public class Asset4CategoryPart extends AssetPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long								serialVersionUID	= -5668037121788316389L;
	private static Logger										logger						= Logger.getLogger("AssetStackPart");
	private static HashMap<String, String>	securityLevels		= new HashMap<String, String>();
	static {
		Asset4CategoryPart.securityLevels.put("1.0", "#2FEFEF");
		Asset4CategoryPart.securityLevels.put("0.9", "#48F0C0");
		Asset4CategoryPart.securityLevels.put("0.8", "#00EF47");
		Asset4CategoryPart.securityLevels.put("0.7", "#00F000");
		Asset4CategoryPart.securityLevels.put("0.6", "#8FEF2F");
		Asset4CategoryPart.securityLevels.put("0.5", "#EFEF00");
		Asset4CategoryPart.securityLevels.put("0.4", "#D77700");
		Asset4CategoryPart.securityLevels.put("0.3", "#F06000");
		Asset4CategoryPart.securityLevels.put("0.2", "#F04800");
		Asset4CategoryPart.securityLevels.put("0.1", "#D73000");
		Asset4CategoryPart.securityLevels.put("0.0", "#F00000");
	}

	// - F I E L D - S E C T I O N ............................................................................
	//	protected AbstractPilotBasedActivity	activity					= null;
	//	private int															renderMode				= BundlesAndMessages.layoutmodel.NORMAL;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Asset4CategoryPart(final NeoComAsset asset) {
		super(asset);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public Spanned get_assetLocation() {
		return this.colorFormatLocation(this.getCastedModel().getLocation());
	}

	@Override
	public AbstractHolder getHolder(final Activity activity) {
		_activity = activity;
		return new Asset4CategoryHolder(this, _activity);
	}

	@Override
	public AbstractHolder getHolder(final Fragment fragment) {
		_fragment = fragment;
		_activity = fragment.getActivity();
		return new Asset4CategoryHolder(this, _activity);
	}

	@Override
	public long getModelID() {
		return this.getCastedModel().getAssetID();
	}
}

// - UNUSED CODE ............................................................................................

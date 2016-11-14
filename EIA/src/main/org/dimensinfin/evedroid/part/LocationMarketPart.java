//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.part;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.render.Location4MarketRender;

import android.text.Html;
import android.text.Spanned;
// - IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public class LocationMarketPart extends LocationPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -5791588352964568708L;

	// - F I E L D - S E C T I O N ............................................................................
	//	private boolean						isContainer				= false;
	//	private String						containerName			= "Hangar";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public LocationMarketPart(final EveLocation location) {
		super(location);
	}

	public Spanned get_title() {
		StringBuffer htmlLocation = new StringBuffer();
		double security = getCastedModel().getSecurityValue();
		htmlLocation.append(generateSecurityColor(security, securityFormatter.format(security)));
		htmlLocation.append(" ").append(getCastedModel().getStation());
		return Html.fromHtml(getCastedModel().getRegion() + AppWideConstants.FLOW_ARROW_RIGHT
				+ getCastedModel().getConstellation() + AppWideConstants.FLOW_ARROW_RIGHT + htmlLocation.toString());
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	protected AbstractHolder selectHolder() {
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_LOCATIONMAKETHUB)
			return new Location4MarketRender(this, _activity);
		return new Location4MarketRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................

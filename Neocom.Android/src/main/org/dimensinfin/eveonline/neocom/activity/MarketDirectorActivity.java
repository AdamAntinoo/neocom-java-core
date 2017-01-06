//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.activity;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.activity.core.PilotPagerActivity;
import org.dimensinfin.eveonline.neocom.fragment.MarketOrdersFragment;
import org.dimensinfin.eveonline.neocom.interfaces.INeoComDirector;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

import android.os.Bundle;
import android.util.Log;

/**
 * Director Activities are an special brand of activities that export an Interface to allow they rendering on
 * the pilot page. This particular Director will provide the market information.<br>
 * The market information page contains a 3 page Viewer that has currently implemented the first page.<br>
 * The single only Market Data Page displys the scheduled sells, scheduled buys, current sells, current buys
 * and historical data recorded for the current character. The other two pending pages will show the historic
 * closed market orders for Sells and for Buys respectively and with more detail information in expandable
 * blocks. <br>
 * Implementation follows the current standard where the Activity just implements the Director actions and
 * just creates the pages that form the Activity. All customization and data related actions are coded inside
 * the specific Fragments that compose each of the created pages.
 * 
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class MarketDirectorActivity extends PilotPagerActivity implements INeoComDirector {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Checks if there are the conditions to activate this particular manager. Each one will have it different
	 * rules to reach the activation point.<br>
	 * The BPOManager need that there are at least one BPO on the list of assets of the pilot.
	 */
	public boolean checkActivation(final NeoComCharacter checkPilot) {
		if (checkPilot.getMarketOrders().size() > 0) return true;
		if (checkPilot.getAssetsManager().searchT2Modules().size() > 0) return true;
		return false;
	}

	public int getIconReferenceActive() {
		return R.drawable.marketdirector;
	}

	public int getIconReferenceInactive() {
		return R.drawable.marketdirectordimmed;
	}

	public String getName() {
		return "Market";
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> MarketDirectorActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		try {
			// Create the pages that form this Activity. Each page implemented
			// by a Fragment.
			this.addPage(new MarketOrdersFragment(), 0);
		} catch (final Exception rtex) {
			Log.e("NEOCOM", "RTEX> MarketDirectorActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> MarketDirectorActivity.onCreate - " + rtex.getMessage()));
		}
		// Reinitialize the tile and subtitle from the first page.
		this.updateInitialTitle();
		Log.i("NEOCOM", "<< MarketDirectorActivity.onCreate"); //$NON-NLS-1$
	}
}
// - UNUSED CODE ............................................................................................

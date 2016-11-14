//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.activity;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.PilotPagerActivity;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.fragment.IndustryBlueprintsFragment;
import org.dimensinfin.evedroid.fragment.InventionBlueprintsFragment;
import org.dimensinfin.evedroid.interfaces.INeoComDirector;
import org.dimensinfin.evedroid.model.Blueprint;
import org.dimensinfin.evedroid.model.EveChar;

import android.os.Bundle;
import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Activity to display the different list of Blueprints and allow action with
 * them, like get information to lauch jobs or see the coverage of material
 * requirements and create a goddies list.
 * 
 * @author Adam Antinoo
 */
public class IndustryDirectorActivity extends PilotPagerActivity implements INeoComDirector {
	// - S T A T I C - S E C T I O N
	// ..........................................................................
	// public static Logger logger =
	// Logger.getLogger("IndustryDirectorActivity");

	// - F I E L D - S E C T I O N
	// ............................................................................
	private int _T3Count = 0;
	private int _T2Count = 0;
	private int _T1Count = 0;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	/**
	 * Checks if there are the conditions to activate this particular manager.
	 * Each one will have it different rules to reach the activation point.<br>
	 * The BPOManager need that there are at least one BPO on the list of assets
	 * of the pilot.
	 */
	public boolean checkActivation(final EveChar checkPilot) {
		final ArrayList<Blueprint> bps = checkPilot.getAssetsManager().getBlueprints();
		if (bps.size() > 0) {
			// Get the counts of the different blueprint categories.
			for (final Blueprint blueprint : bps) {
				if (blueprint.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
					this._T1Count++;
				}
				if (blueprint.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
					this._T2Count++;
				}
				if (blueprint.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechIII)) {
					this._T3Count++;
				}
			}
			return true;
		} else
			return false;
	}

	public int getIconReferenceActive() {
		return R.drawable.industrydirector;
	}

	public int getIconReferenceInactive() {
		return R.drawable.industrydirectordimmed;
	}

	public String getName() {
		return "Industry";
	}

	/**
	 * During the creation of a new IndustryDirector we have to check for the
	 * Industry Store data. If we found that data we can restore a previous
	 * state. Otherwise we can createa new set of data so next time we enter
	 * this Director we can resume the activity with all that data loaded and
	 * ready. During the load of the data we need to display the Industry pages
	 * we need ready access to the list of blueprints, both the T2 and the T1
	 * blueprints. Since the blueprints are tied to assets we can assume that we
	 * have a cached copy of the XML source file we downloaded when we did the
	 * assets download. Instead reading the bleuprint information from the
	 * database with the DAO we can parse the cached XML with the eveapi and
	 * process directly the list of blueprints.<br>
	 * Afterwards we can store all that data relations becuase they will not
	 * change until the assets are processed again.<br>
	 * In case we have no network we can fall back again to the last XML file
	 * blueprint list but if that file is not present then we can assume we have
	 * no ona blueprint and deactivate this Director returning to the Pilot
	 * Information page.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> IndustryDirectorActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		try {
			// Get the number of blueprints of T3 and then T2 and T1 to activate
			// the corresponding pages.
			checkActivation(this._store.getPilot());
			// Reset the page position.
			int page = 0;
			if (this._T3Count > 0) {
				addPage(new IndustryBlueprintsFragment().setTechLevel(ModelWideConstants.eveglobal.TechIII), page++);
			}
			if (this._T2Count > 0) {
				addPage(new IndustryBlueprintsFragment().setTechLevel(ModelWideConstants.eveglobal.TechII), page++);
			}
			if (this._T1Count > 0) {
				addPage(new IndustryBlueprintsFragment().setTechLevel(ModelWideConstants.eveglobal.TechI), page++);
				addPage(new InventionBlueprintsFragment(), page++);
			}
		} catch (final Exception rtex) {
			Log.e("NEOCOM", "RTEX> IndustryDirectorActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> IndustryDirectorActivity.onCreate - " + rtex.getMessage()));
		}
		// Reinitialize the tile and subtitle from the first page.
		updateInitialTitle();
		Log.i("NEOCOM", "<< IndustryDirectorActivity.onCreate"); //$NON-NLS-1$
	}
}
// - UNUSED CODE
// ............................................................................................

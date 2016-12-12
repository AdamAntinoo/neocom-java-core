//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.activity;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.activity.PagerFragment;
import org.dimensinfin.evedroid.activity.core.DefaultNewPagerActivity;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.factory.IndustryLOMResourcesDataSource;
import org.dimensinfin.evedroid.factory.StackByItemDataSource;
import org.dimensinfin.evedroid.model.NeoComBlueprint;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.part.BlueprintPart;
import org.dimensinfin.evedroid.part.ItemHeader4IndustryPart;

import android.os.Bundle;
import android.util.Log;

//- CLASS IMPLEMENTATION ...................................................................................
public class ItemDetailsActivity extends DefaultNewPagerActivity {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public ItemDetailsActivity() {
	//		super();
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.i("ItemDetailsActivity", ">> ItemDetailsActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		this._actionBar.setTitle("Item Detail - Stacks");
		try {
			// Instantiate the item from the ID that is an activity argument
			final Bundle extras = getIntent().getExtras();
			final int typeid = extras.getInt(AppWideConstants.extras.EXTRA_EVEITEMID);
			if (typeid > 0) {
				final EveItem item = AppConnector.getDBConnector().searchItembyID(typeid);
				//				PagerFragment frag = (PagerFragment) getFragmentManager().findFragmentByTag(
				//						Integer.valueOf(AppWideConstants.fragment.FRAGMENT_ITEMMODULESTACKS).toString());
				//				if (null == frag) {
				PagerFragment frag = new PagerFragment();
				frag.setIdentifier(AppWideConstants.fragment.FRAGMENT_ITEMMODULESTACKS);
				frag.setTitle("Item Detail - Stacks");
				final StackByItemDataSource ds = new StackByItemDataSource(this._store).setItem(item);
				frag.setDataSource(ds);
				frag.clearHeader();
				frag.addtoHeader(new ItemHeader4IndustryPart(item));
				this._pageAdapter.addPage(frag);
				//				}

				// Check the item selected can me obtained from any type of job.
				final boolean manufacturable = AppConnector.getDBConnector().checkManufacturable(typeid);
				if (manufacturable) {
					// Create the Manufacture Resources Page.
					// Get the product created by the job from the blueprint part process.
					final int bpid = AppConnector.getDBConnector().searchBlueprint4Module(typeid);
					final BlueprintPart bppart = new BlueprintPart(new NeoComBlueprint(bpid));
					bppart.setActivity(ModelWideConstants.activities.MANUFACTURING);
					final int productID = typeid;
					final EveItem productItem = item;
					//					frag = (PagerFragment) getFragmentManager().findFragmentByTag(
					//							Integer.valueOf(AppWideConstants.fragment.FRAGMENT_INDUSTRYLOMRESOURCES).toString());
					//					if (null == frag) {
					frag = new PagerFragment();
					frag.setIdentifier(AppWideConstants.fragment.FRAGMENT_INDUSTRYLOMRESOURCES);
					frag.setTitle("Industry");
					// Set the subtitle depending on the process activity.
					// TODO This label may change when the implementation uses the generic manufacture activity code
					frag.setSubtitle(bppart.getSubtitle());
					//						this.getFragmentManager().beginTransaction()
					//								.add(frag, Integer.valueOf(AppWideConstants.fragment.FRAGMENT_INDUSTRYLOMRESOURCES).toString()).commit();
					//					}
					final IndustryLOMResourcesDataSource ds2 = new IndustryLOMResourcesDataSource(this._store);
					frag.setDataSource(ds2);
					ds2.setBlueprint(bppart);
					frag.clearHeader();
					frag.addtoHeader(new ItemHeader4IndustryPart(productItem));
					this._pageAdapter.addPage(frag);
				}
				activateIndicator();
			}
		} catch (final Exception rtex) {
			Log.e("EVEI", "R> Runtime Exception on ItemDetailsActivity.onResume." + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
		}
		Log.i("ItemDetailsActivity", "<< ItemDetailsActivity.onCreate"); //$NON-NLS-1$
	}
}
//- UNUSED CODE ............................................................................................

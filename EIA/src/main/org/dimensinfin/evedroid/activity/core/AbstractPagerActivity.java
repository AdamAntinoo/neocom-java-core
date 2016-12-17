//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.activity.core;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.activity.TitledFragment;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.ERequestClass;
import org.dimensinfin.evedroid.core.EvePagerAdapter;
import org.dimensinfin.evedroid.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.evedroid.fragment.core.AbstractPagerFragment;
import org.dimensinfin.evedroid.model.Fitting;
import org.dimensinfin.evedroid.storage.AppModelStore;

import com.viewpagerindicator.CirclePageIndicator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * This abstract Activity will collect all the common code that is being used on the new Activity pattern.
 * Most of the new activities change minor actions on some methods while sharing all the rest of the code.<br>
 * This class implements a generic Activity with a swipe gesture multi page layout and Titled pages that will
 * show names only if the number of pages is more than 1. Current implementation ises a cicle indicator but
 * will be transistioned to a Titled indicator. The base code will take care of the menu and the Action tool
 * bar.
 * 
 * @author Adam Antinoo
 */
public abstract class AbstractPagerActivity extends Activity {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	protected ActionBar					_actionBar			= null;
	private ViewPager						_pageContainer	= null;
	private EvePagerAdapter			_pageAdapter		= null;
	private ImageView						_back						= null;
	// private AppModelStore _store = null;
	private CirclePageIndicator	_indicator			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.eiabasemenu, menu);
		EVEDroidApp.getAppStore().setAppMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_settings:
				final Intent intent = new Intent(this, SettingsActivity.class);
				this.startActivity(intent);
				return false;
			case R.id.action_fullreload:
				this.startActivity(new Intent(this, SplashActivity.class));
				return true;
			case R.id.action_downloadlocations:
				// Insert into the download queue the action to download the locations.
				EVEDroidApp.getTheCacheConnector().addLocationUpdateRequest(ERequestClass.CITADELUPDATE);
				EVEDroidApp.getTheCacheConnector().addLocationUpdateRequest(ERequestClass.OUTPOSTUPDATE);
				break;
			case R.id.action_updateaccounts:
				// Refresh the store data
				AppModelStore.initialize();
				break;
			case R.id.action_createFitting:
				// Create demo fittings to test the save/restore and continue the development.
				Fitting onConstructionFit = new Fitting();
				onConstructionFit.setName("Testing - Crusader");
				onConstructionFit.addHull(11184);
				onConstructionFit.fitModule(6719, 4);
				onConstructionFit.fitModule(5973);
				onConstructionFit.fitModule(5405);
				onConstructionFit.fitModule(5839);
				onConstructionFit.fitModule(5849);
				onConstructionFit.fitModule(11563);
				onConstructionFit.fitModule(33076);
				onConstructionFit.fitRig(26929);
				onConstructionFit.fitRig(26929);
				onConstructionFit.addCargo(244, 4);
				onConstructionFit.addCargo(240, 4);
				AppModelStore.getSingleton().addFitting(onConstructionFit, onConstructionFit.getName());
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void activateIndicator() {
		// If the Indicator is active then set the listener.
		if (null != _indicator) {
			_indicator.setVisibility(View.VISIBLE);
			_indicator.setViewPager(_pageContainer);
			_indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
				}

				public void onPageScrollStateChanged(final int arg0) {
				}

				public void onPageSelected(final int position) {
					_actionBar.setTitle(_pageAdapter.getTitle(position));
					// Clear empty subtitles.
					if ("" == _pageAdapter.getSubTitle(position))
						_actionBar.setSubtitle(null);
					else
						_actionBar.setSubtitle(_pageAdapter.getSubTitle(position));
				}
			});
		} else
			_pageContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
				}

				public void onPageScrollStateChanged(final int arg0) {
				}

				public void onPageSelected(final int position) {
					_actionBar.setTitle(_pageAdapter.getTitle(position));
					// Clear empty subtitles.
					if ("" == _pageAdapter.getSubTitle(position))
						_actionBar.setSubtitle(null);
					else
						_actionBar.setSubtitle(_pageAdapter.getSubTitle(position));
				}
			});
	}

	protected void addPage(final AbstractNewPagerFragment newFrag, final int position) {
		Log.i("NEOCOM", ">> AbstractPagerActivity.addPage"); //$NON-NLS-1$
		final TitledFragment frag = (TitledFragment) this.getFragmentManager()
				.findFragmentByTag(_pageAdapter.getFragmentId(position));
		if (null == frag)
			_pageAdapter.addPage(newFrag);
		else
			_pageAdapter.addPage(frag);
		// Check the number of pages to activate the indicator when more the
		// one.
		if (_pageAdapter.getCount() > 1) this.activateIndicator();
		Log.i("NEOCOM", "<< AbstractPagerActivity.addPage"); //$NON-NLS-1$
	}

	protected void addPage(final AbstractPagerFragment newFrag, final int position) {
		Log.i("NEOCOM", ">> AbstractPagerActivity.addPage"); //$NON-NLS-1$
		final TitledFragment frag = (TitledFragment) this.getFragmentManager()
				.findFragmentByTag(_pageAdapter.getFragmentId(position));
		if (null == frag)
			_pageAdapter.addPage(newFrag);
		else
			_pageAdapter.addPage(frag);
		// Check the number of pages to activate the indicator when more the
		// one.
		if (_pageAdapter.getCount() > 1) this.activateIndicator();
		Log.i("NEOCOM", "<< AbstractPagerActivity.addPage"); //$NON-NLS-1$
	}

	protected void disableIndicator() {
		if (null != _indicator) _indicator.setVisibility(View.GONE);
	}

	protected EvePagerAdapter getPageAdapter() {
		return _pageAdapter;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.i("EVEI", ">> AbstractPagerActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_pager);
		try {
			// Gets the activity's default ActionBar
			_actionBar = this.getActionBar();
			_actionBar.show();
			_actionBar.setDisplayHomeAsUpEnabled(true);

			// Locate the elements of the page and store in global data.
			_pageContainer = (ViewPager) this.findViewById(R.id.pager);
			_back = (ImageView) this.findViewById(R.id.backgroundFrame);
			_indicator = (CirclePageIndicator) this.findViewById(R.id.indicator);
			// Check page structure.
			if (null == _pageContainer) this.stopActivity(new RuntimeException("UNXER. Expected UI element not found."));
			if (null == _back) this.stopActivity(new RuntimeException("UNXER. Expected UI element not found."));

			// Add the adapter for the page switching.
			_pageAdapter = new EvePagerAdapter(this.getFragmentManager(), _pageContainer.getId());
			_pageContainer.setAdapter(_pageAdapter);
			this.disableIndicator();

			// // Process the parameters into the context.
			// final Bundle extras = getIntent().getExtras();
			// if (null == extras)
			// throw new RuntimeException(
			// "RT IndustryDirectorActivity.onCreate - Unable to continue.
			// Required parameters not defined on Extras.");
			// //Instantiate the pilot from the characterID.
			// final long characterid =
			// extras.getLong(AppWideConstants.extras.EXTRA_EVECHARACTERID);
			// if (characterid > 0) {
			// // Initialize the access to the global structures.
			// this._store = EVEDroidApp.getAppStore();
			// this._store.activatePilot(characterid);
			// this._store.activateActivity(this);
			// }
		} catch (final Exception rtex) {
			Log.e("EVEI", "RTEX> AbstractPagerActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> AbstractPagerActivity.onCreate - " + rtex.getMessage()));
		}
		Log.i("EVEI", "<< AbstractPagerActivity.onCreate"); //$NON-NLS-1$
	}

	@Override
	protected void onStart() {
		super.onStart();
		EVEDroidApp.updateProgressSpinner();
	}

	/**
	 * This is a test to try to close the databases when the activity is closed to see if this affects to the
	 * data availability at the local database. This is a test and can cause later access problems to the
	 * database data.
	 */
	@Override
	protected void onStop() {
		Log.i("EVEI", ">> AbstractPagerActivity.onStop"); //$NON-NLS-1$
		EVEDroidApp.getSingletonApp().closeDB();
		super.onStop();
	}

	/**
	 * For really unrecoverable or undefined exceptions the application should go to a safe spot. That spot is
	 * defined by the application so this is another abstract method.
	 * 
	 * @param exception
	 */
	protected void stopActivity(final Exception exception) {
		final Intent intent = new Intent(this, SplashActivity.class);
		// Pass the user message to the activity for display.
		intent.putExtra(AppWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
		this.startActivity(intent);
	}

	protected void updateInitialTitle() {
		TitledFragment firstFragment = (TitledFragment) this.getPageAdapter().getInitialPage();
		_actionBar.setTitle(firstFragment.getTitle());
		_actionBar.setSubtitle(firstFragment.getSubtitle());
	}
}
// - UNUSED CODE
// ............................................................................................

//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.activity.core;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.core.AbstractContextActivity;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EvePagerAdapter;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.app.ActionBar;
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

import com.viewpagerindicator.CirclePageIndicator;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * This abstract Activity will collect all the common code that is being used on the new Activity pattern.
 * Most of the new activities change minor actions on some methods while sharing all the rest of the code.<br>
 * The class starts almost empty to perform better code refactoring.
 * 
 * @author Adam Antinoo
 */
public abstract class DefaultNewPagerActivity extends AbstractContextActivity {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	public static Logger			logger					= Logger.getLogger("DefaultPagerActivity");

	// - F I E L D - S E C T I O N ............................................................................
	protected ActionBar				_actionBar			= null;
	protected ViewPager				_pageContainer	= null;
	protected EvePagerAdapter	_pageAdapter		= null;
	protected ImageView				_back						= null;
	protected AppModelStore		_store					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
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
				startActivity(intent);
				return false;
			case R.id.action_fullreload:
				//				EVEDroidApp.setFullReload(true);
				startActivity(new Intent(this, SplashActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void activateIndicator() {
		// If the Indicator is active set there the listener.
		final CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		if (null != indicator) {
			indicator.setVisibility(View.VISIBLE);
			indicator.setViewPager(this._pageContainer);
			indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				@Override
				public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
				}

				@Override
				public void onPageScrollStateChanged(final int arg0) {
				}

				@Override
				public void onPageSelected(final int position) {
					DefaultNewPagerActivity.this._actionBar.setTitle(DefaultNewPagerActivity.this._pageAdapter.getTitle(position));
					// Clear empty subtitles.
					if ("" == DefaultNewPagerActivity.this._pageAdapter.getSubTitle(position))
						DefaultNewPagerActivity.this._actionBar.setSubtitle(null);
					else
						DefaultNewPagerActivity.this._actionBar.setSubtitle(DefaultNewPagerActivity.this._pageAdapter
								.getSubTitle(position));
				}
			});
		} else
			this._pageContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				@Override
				public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
				}

				@Override
				public void onPageScrollStateChanged(final int arg0) {
				}

				@Override
				public void onPageSelected(final int position) {
					DefaultNewPagerActivity.this._actionBar.setTitle(DefaultNewPagerActivity.this._pageAdapter.getTitle(position));
					// Clear empty subtitles.
					if ("" == DefaultNewPagerActivity.this._pageAdapter.getSubTitle(position))
						DefaultNewPagerActivity.this._actionBar.setSubtitle(null);
					else
						DefaultNewPagerActivity.this._actionBar.setSubtitle(DefaultNewPagerActivity.this._pageAdapter
								.getSubTitle(position));
				}
			});
	}

	protected void disableIndicator() {
		final CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		if (null != indicator) indicator.setVisibility(View.GONE);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.i("EVEI", ">> DefaultPagerActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pager);
		try {
			// Gets the activity's default ActionBar
			this._actionBar = getActionBar();
			this._actionBar.show();
			this._actionBar.setDisplayHomeAsUpEnabled(true);

			// Locate the elements of the page and store in global data.
			this._pageContainer = (ViewPager) findViewById(R.id.pager);
			this._back = (ImageView) findViewById(R.id.backgroundFrame);
			// Check page structure.
			if (null == this._pageContainer) stopActivity(new RuntimeException("UNXER. Expected UI element not found."));
			if (null == this._back) stopActivity(new RuntimeException("UNXER. Expected UI element not found."));

			// Add the adapter for the page switching.
			this._pageAdapter = new EvePagerAdapter(getFragmentManager(), this._pageContainer.getId());
			this._pageContainer.setAdapter(this._pageAdapter);
			disableIndicator();

			// Process the parameters into the context.
			final Bundle extras = getIntent().getExtras();
			if (null == extras)
				throw new RuntimeException(
						"RT IndustryDirectorActivity.onCreate - Unable to continue. Required parameters not defined on Extras.");
			//Instantiate the pilot from the characterID.
			final long characterid = extras.getLong(AppWideConstants.extras.EXTRA_EVECHARACTERID);
			if (characterid > 0) {
				this._store = EVEDroidApp.getAppStore();
				this._store.activatePilot(characterid);
				this._store.activateActivity(this);
			}
		} catch (final Exception rtex) {
			Log.e("EVEI", "R> Runtime Exception on DefaultPagerActivity.onCreate." + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
		}
		Log.i("EVEI", "<< DefaultPagerActivity.onCreate"); //$NON-NLS-1$
	}

	/**
	 * Save the store to their persistent file before releasing the control to another activity that will then
	 * be able to make use of that data structures.
	 */
	@Override
	protected void onPause() {
		Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.onPause");
		// Check store state and update cache on disk if it has changed.
		if (this._store.isDirty()) this._store.save();
		super.onPause();
		Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.onPause");
	}

	//	@Override
	//	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
	//		// TODO Auto-generated method stub
	//		super.onRestoreInstanceState(savedInstanceState);
	//		logger.info(">> DefaultPagerActivity.onRestoreInstanceState"); //$NON-NLS-1$
	//	}

	@Override
	protected void onSaveInstanceState(final Bundle savedInstanceState) {
		Log.i("IndustryDirectorActivity", ">> IndustryDirectorActivity.onSaveInstanceState"); //$NON-NLS-1$
		super.onSaveInstanceState(savedInstanceState);
		// Add current model data dependencies. EVECHARACTERID
		savedInstanceState.putLong(AppWideConstants.extras.EXTRA_EVECHARACTERID, this._store.getPilot().getCharacterID());
		this._store.save();
		Log.i("IndustryDirectorActivity", "<< IndustryDirectorActivity.onSaveInstanceState"); //$NON-NLS-1$
	}

	@Override
	protected void onStart() {
		super.onStart();
		EVEDroidApp.updateProgressSpinner();
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
		startActivity(intent);
	}
}
//- UNUSED CODE ............................................................................................

//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.activity.core;

//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.PilotListActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EvePagerAdapter;
import org.dimensinfin.evedroid.model.NeoComCharacter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * This abstract Activity will collect all the common code that is being used on the new Activity pattern.
 * Most of the new activities change minor actions on some methods while sharing all the rest of the code.<br>
 * The class starts almost empty to perform better code refactoring.
 * 
 * @author Adam Antinoo
 */
public abstract class DefaultPagerActivity extends Activity {
	// - S T A T I C - S E C T I O N
	// ..........................................................................
	public static Logger			logger					= Logger.getLogger("DefaultPagerActivity");

	// - F I E L D - S E C T I O N
	// ............................................................................
	protected ActionBar				_actionBar			= null;
	protected ViewPager				_pageContainer	= null;
	protected EvePagerAdapter	_pageAdapter		= null;
	protected ImageView				_back						= null;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	public NeoComCharacter getPilot() {
		return EVEDroidApp.getAppStore().getPilot();
	}

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
				// EVEDroidApp.setFullReload(true);
				startActivity(new Intent(this, SplashActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		DefaultPagerActivity.logger.info(">> DefaultPagerActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		// Debug.startMethodTracing("EAI");
		setContentView(R.layout.activity_pager);
		EVEDroidApp.getAppStore().activateActivity(this);
		try {
			// Gets the activity's default ActionBar
			_actionBar = getActionBar();
			_actionBar.show();
			_actionBar.setDisplayHomeAsUpEnabled(true);

			// Locate the elements of the page and store in global data.
			_pageContainer = (ViewPager) findViewById(R.id.pager);
			_back = (ImageView) findViewById(R.id.backgroundFrame);
			// Check page structure.
			if (null == _pageContainer) this.stopActivity(new RuntimeException("UNXER. Expected UI element not found."));
			if (null == _back) this.stopActivity(new RuntimeException("UNXER. Expected UI element not found."));

			// Add the adapter for the page switching.
			_pageAdapter = new EvePagerAdapter(getFragmentManager());
			_pageContainer.setAdapter(_pageAdapter);
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

			// Change the background image of the page depending on theme
			// _back.setImageDrawable(EVEDroidApp.getAppContext().getTheme().getThemeBackground());
		} catch (final Exception rtex) {
			DefaultPagerActivity.logger.severe("R> Runtime Exception on DefaultPagerActivity.onCreate." + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(rtex);
		}
		DefaultPagerActivity.logger.info("<< DefaultPagerActivity.onCreate"); //$NON-NLS-1$
	}

	@Override
	protected void onDestroy() {
		// Debug.stopMethodTracing();
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		DefaultPagerActivity.logger.info(">> DefaultPagerActivity.onRestart"); //$NON-NLS-1$
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
		final Intent intent = new Intent(this, PilotListActivity.class);
		// Pass the user message to the activity for display.
		intent.putExtra(AppWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
		startActivity(intent);
	}
}
// - UNUSED CODE
// ............................................................................................

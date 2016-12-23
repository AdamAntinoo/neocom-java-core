//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.activity;

//- IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractContextActivity;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.core.IActivityCallback;
import org.dimensinfin.android.mvc.core.IDataSource;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.SafeStopActivity;
import org.dimensinfin.evedroid.activity.core.SettingsActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.IDirector;
import org.dimensinfin.evedroid.fragment.MiningSessionFragment;
import org.dimensinfin.evedroid.model.Asteroid;
import org.dimensinfin.evedroid.model.NeoComCharacter;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.part.AsteroidOnProgressPart;
import org.dimensinfin.evedroid.theme.ITheme;
import org.dimensinfin.evedroid.theme.RubiconRedTheme;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

//- CLASS IMPLEMENTATION ...................................................................................
public class MiningSessionActivity extends AbstractContextActivity implements IDirector, IActivityCallback {
	//- CLASS IMPLEMENTATION ...................................................................................
	private class Stacks4SessionDataSource extends AbstractDataSource {
		// - S T A T I C - S E C T I O N ..........................................................................
		private static final long	serialVersionUID	= 1148837147204094347L;

		// - F I E L D - S E C T I O N ............................................................................

		public void addAsteroid(final Asteroid asteroid) {
			_root.add(0, new AsteroidOnProgressPart(asteroid));
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		public void createContentHierarchy() {
			logger.info(">> MiningSessionActivity.Stacks4SessionDataSource.createHierarchy");
			// Add test data
			_root.add(0, new AsteroidOnProgressPart(new Asteroid(17464, 32449)));
			//			// Clear the current list of elements.
			//			_root.clear();
			//
			//			// Get the list of on progress asteroids.
			//			try {
			//				AssetsManager manager = getPilot().getAssetsManager();
			//				// Depending on the Setting group Locations into Regions
			//				ArrayList<Asset> assets = manager.stacks4Item(getItem());
			//				for (Asset as : assets) {
			//					AssetStackPart part = new AssetStackPart(as);
			//					_root.add(part);
			//				}
			//			} catch (SQLException sqle) {
			//				sqle.printStackTrace();
			//				logger.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
			//			}
			logger.info("<< MiningSessionActivity.Stacks4SessionDataSource.createHierarchy [" + _root.size() + "]");
		}

		@Override
		public ArrayList<AbstractAndroidPart> getPartHierarchy() {
			logger.info(">> MiningSessionActivity.Stacks4SessionDataSource.getPartHierarchy");
			ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
			for (AbstractAndroidPart node : _root) {
				result.add(node);
			}
			_adapterData = result;
			logger.info("<< MiningSessionActivity.Stacks4SessionDataSource.getPartHierarchy");
			return result;
		}

		@Override
		public void propertyChange(final PropertyChangeEvent event) {
			if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
				fireStructureChange(AppWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
						event.getNewValue());
			}
			if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES)) {
				fireStructureChange(AppWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
						event.getNewValue());
			}
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	public static Logger							logger						= Logger.getLogger("MiningSessionActivity");

	// - F I E L D - S E C T I O N ............................................................................
	private ActionBar									_actionBar				= null;
	protected ITheme									_theme						= null;
	private ImageView									_back							= null;
	private ViewPager									_pageContainer		= null;
	private FragmentStatePagerAdapter	_pageAdapter			= null;
	private Stacks4SessionDataSource	miningDataSource	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MiningSessionActivity() {
		super();
		_theme = new RubiconRedTheme(this);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean checkActivation(final NeoComCharacter pilot) {
		return true;
	}

	public IDataSource getDataSource(final int fragmentID) {
		if (null == miningDataSource)
			if (fragmentID == AppWideConstants.fragment.FRAGMENT_MININGSESSIONS)
				miningDataSource = new Stacks4SessionDataSource();
		if (null == miningDataSource)
			throw new RuntimeException("Fragment Identifier did not matched any DataSource.");
		else
			return miningDataSource;
	}

	public int getInterfaceCycleTime() {
		return 151;
	}

	public int getInterfaceCycleVolume() {
		return 856;
	}

	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.eiabasemenu, menu);
		EVEDroidApp.getAppContext().setAppMenu(menu);
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
				// DEBUG Must activate the setting page soon.
				//			case R.id.action_settings:
				//				openSettingsActivity();
				//				return false;
			case R.id.action_settings:
				final Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				return false;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onStartMiningAsteroid(final View target) {
		StartMiningAsteroidDialog dialog = new StartMiningAsteroidDialog();
		IDataSource ds = getDataSource(AppWideConstants.fragment.FRAGMENT_MININGSESSIONS);
		if (ds instanceof Stacks4SessionDataSource) {
			Asteroid asteroid = new Asteroid(17464, 32698);
			((Stacks4SessionDataSource) ds).addAsteroid(asteroid);
			((Stacks4SessionDataSource) ds).fireStructureChange(AppWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES,
					null, null);
		}
	}

	protected void onCreate(final Bundle savedInstanceState) {
		logger.info(">> ItemDetailsActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pager);

		try {
			// Gets the activity's default ActionBar
			_actionBar = getActionBar();
			_actionBar.show();
			_actionBar.setDisplayHomeAsUpEnabled(true);

			// Change the title and the background of the activity.
			_actionBar.setTitle("MINING");
			_actionBar.setSubtitle("Session Def.");

			// Locate the elements of the page and store in global data.
			_pageContainer = (ViewPager) findViewById(R.id.pager);
			_back = (ImageView) findViewById(R.id.backgroundFrame);

			// Add the adapter for the page switching.
			_pageAdapter = new MiningSessionPagerAdapter(getFragmentManager());
			_pageContainer.setAdapter(_pageAdapter);
			_pageContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
				}

				public void onPageScrollStateChanged(final int arg0) {
				}

				public void onPageSelected(final int position) {
					switch (position) {
						case 0:
							_actionBar.setTitle("Item Detail - Stacks");
							break;
						case 1:
							_actionBar.setTitle("Item Detail - Description");
							break;
					}
				}
			});

			// Check page structure.
			if (null == _pageContainer) stopActivity(new RuntimeException("UNXER. Expected UI element not found."));
			if (null == _back) stopActivity(new RuntimeException("UNXER. Expected UI element not found."));

			// Change the background image of the page depending on theme
			_back.setImageDrawable(_theme.getThemeBackground());
			EVEDroidApp.getAppContext().setCurrentActivity(this);
		} catch (Exception rtex) {
			logger.severe("R> Runtime Exception on ItemDetailsActivity.onCreate." + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
		}
		logger.info("<< ItemDetailsActivity.onCreate"); //$NON-NLS-1$
	}

	/**
	 * For really unrecoverable or undefined exceptions the application should go to a safe spot. That spot is
	 * defined by the application so this is another abstract method.
	 * 
	 * @param exception
	 */
	protected void stopActivity(final Exception exception) {
		final Intent intent = new Intent(this, SafeStopActivity.class);
		// Pass the user message to the activity for display.
		intent.putExtra(AppWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
		startActivity(intent);
	}

	private EveItem getItem() {
		return EVEDroidApp.getAppContext().getItem();
	}

	private NeoComCharacter getPilot() {
		return EVEDroidApp.getAppContext().getPilot();
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class MiningSessionPagerAdapter extends FragmentStatePagerAdapter {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger	= Logger.getLogger("MultipageFragmentAdapter");

	// - F I E L D - S E C T I O N ............................................................................
	//	private AbstractActivity	_activity	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MiningSessionPagerAdapter(final FragmentManager fm) {
		super(fm);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Fragment getItem(final int position) {
		MiningSessionFragment frag = null;
		switch (position) {
			case 0:
				frag = new MiningSessionFragment();
				break;
		}
		if (null == frag) {
			frag = new MiningSessionFragment();
		}
		return frag;
	}
}
//- UNUSED CODE ............................................................................................

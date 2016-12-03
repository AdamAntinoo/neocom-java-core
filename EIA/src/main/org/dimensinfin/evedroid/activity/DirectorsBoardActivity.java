//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.activity;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.activity.PagerFragment;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.PilotPagerActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.factory.DataSourceFactory;
import org.dimensinfin.evedroid.fragment.NeoComDashboardFragment;
import org.dimensinfin.evedroid.fragment.core.ExpandableFragment;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This Sctivity as an special implementation because has an addition Layout container and has no ListView. So
 * it will not require any DataSource and the fillup of the contents can be simplified and performed on the
 * initial life cycle. We then should copy some code from the more generic Activity and Fragments to be able
 * to compose this special Activity. <br>
 * It's role s to show the Selected Pilot information and to create the list of directors active for this
 * Pilot.
 * 
 * @author Adam Antinoo
 */

public class DirectorsBoardActivity extends PilotPagerActivity {
	// - S T A T I C - S E C T I O N ..........................................................................
	enum EDirectorCode {
		ASSETDIRECTOR, SHIPDIRECTOR, INDUSTRYDIRECTOR, MARKETDIRECTOR, JOBDIRECTOR, MININGDIRECTOR, FITDIRECTOR
	}

	private static Logger									logger							= Logger.getLogger("DirectorsBoardActivity");
	private static final EDirectorCode[]	activeDirectors			= { EDirectorCode.ASSETDIRECTOR, EDirectorCode.SHIPDIRECTOR,
			EDirectorCode.INDUSTRYDIRECTOR, EDirectorCode.JOBDIRECTOR, EDirectorCode.MARKETDIRECTOR,
			EDirectorCode.FITDIRECTOR };
	protected static Typeface							daysFace						= Typeface
			.createFromAsset(EVEDroidApp.getSingletonApp().getApplicationContext().getAssets(), "fonts/Days.otf");

	// - F I E L D - S E C T I O N ............................................................................
	private final ActionBar								_actionBar					= null;
	private final ImageView								_back								= null;
	private final ViewGroup								_directorContainer	= null;
	private final ViewGroup								_fragmentContainer	= null;
	private final AppModelStore						_store							= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DirectorsBoardActivity() {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Return the display name of this activity. Also used for the label on the NeoCom menu.
	 * 
	 * @return
	 */
	public String getName() {
		return "Dashboard";
	}

	/**
	 * Create the test page to show the list of Manufacture actions. The real fitting Activity may have more
	 * pages than this test page..
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		logger.info(">> [DirectorsBoardActivity.onCreate]"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		try {
			// Reset the page position.
			int page = 0;
			// Get the parameters from the bundle. If not defined then use the demo.
			final Bundle extras = getIntent().getExtras();
			// Create the pages that form this Activity. Each page implemented by a Fragment.
			addPage(new NeoComDashboardFragment().setVariant(AppWideConstants.EFragment.NEOCOM_DASHBOARD).setExtras(extras),
					page++);
		} catch (final Exception rtex) {
			Log.e("NEOCOM", "RTEX> DirectorsBoardActivity.onCreate - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> DirectorsBoardActivity.onCreate - " + rtex.getMessage()));
		}
		// Reinitialize the tile and subtitle from the first page.
		updateInitialTitle();
		logger.info("<< [DirectorsBoardActivity.onCreate]"); //$NON-NLS-1$
	}

	//	/**
	//	 * On the Activity create phase we will set the layout, then create the action bar and all other UI elements
	//	 * and finally creates and sets the fragments. This is to avoid the multiple creation and addition of more
	//	 * fragments when the activity is put again on the foreground.
	//	 */
	//	@Override
	//	protected void onCreate(final Bundle savedInstanceState) {
	//		logger.info(">> [DirectorsBoardActivity.onCreate]"); //$NON-NLS-1$
	//		super.onCreate(savedInstanceState);
	//		//		setContentView(R.layout.activity_directorsboard);
	//		// Disable go home for this activity since this is home.
	//		_actionBar.setDisplayHomeAsUpEnabled(false);
	//		try {
	//			// Process the parameters into the context.
	//			final Bundle extras = getIntent().getExtras();
	//			if (null == extras) throw new RuntimeException(
	//					"RT DirectorsBoardActivity.onCreate - Unable to continue. Required parameters not defined on Extras.");
	//			// Instantiate the pilot from the characterID.
	//			final long characterid = extras.getLong(AppWideConstants.extras.EXTRA_EVECHARACTERID);
	//			Log.i("DirectorsBoardActivity", "-- DirectorsBoardActivity.onCreate -- Detected "
	//					+ AppWideConstants.extras.EXTRA_EVECHARACTERID + "=" + characterid);
	//			// Create the pages that form this Activity. Each page implemented by a Fragment.
	//			int page = 0;
	//			// Register this Activity as the current active Activity.
	//			EVEDroidApp.getAppStore().activateActivity(this);
	//			addPage(new NeoComDashboardFragment().setVariant(AppWideConstants.EFragment.NEOCOM_DASHBOARD), page++);
	//		} catch (final Exception rtex) {
	//			Log.e("EVEI", "RTEX> Runtime Exception on DirectorsBoardActivity.onCreate." + rtex.getMessage());
	//			rtex.printStackTrace();
	//			stopActivity(rtex);
	//		}
	//		// Reinitialize the tile and subtitle from the first page.
	//		updateInitialTitle();
	//		logger.info("<< [DirectorsBoardActivity.onCreate]"); //$NON-NLS-1$
	//	}
	//[01]

	/**
	 * Save the store to their persistent file before releasing the control to another activity that will then
	 * be able to make use of that data structures.
	 */
	@Override
	protected void onPause() {
		Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.onPause");
		// Check store state and update cache on disk if it has changed.
		if (_store.isDirty()) {
			_store.save();
		}
		super.onPause();
		Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.onPause");
	}

	//	/**
	//	 * When the Activity is activated and accessible to the user this event is signaled to report that we are
	//	 * back. On the list of managers we have to check manager by manager if they should activate and if they are
	//	 * or not visible.<br>
	//	 * This is done with a manager factory that will generate the view items for the managers if they should be
	//	 * present.
	//	 */
	//	@SuppressLint("NewApi")
	//	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	//	@SuppressWarnings("deprecation")
	//	@Override
	//	protected void onResume() {
	//		logger.info(">> DirectorsBoardActivity.onResume");
	//		super.onResume();
	//		try {
	//			// // Clean user data structures before querying.
	//			// getPilot().clean();
	//			// Before activating the directors make sure the user has declared
	//			// the MANUFACTURE location.
	//			// final EveLocation location =
	//			// _store.getPilot().getFunctionLocation("MANUFACTURE");
	//			// EDirectorCode[] directors;
	//			// if (null != location)
	//			final EDirectorCode[] directors = activeDirectors;
	//			// else
	//			// directors = passiveDirectors;
	//			final DirectorsBoardActivity parentActivity = this;
	//			for (final EDirectorCode directorCode : directors) {
	//				ImageView activator = null;
	//				switch (directorCode) {
	//					case ASSETDIRECTOR:
	//						final IDirector adirector = new AssetsDirectorActivity();
	//						if (adirector.checkActivation(this._store.getPilot())) {
	//							logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
	//							activator = (ImageView) findViewById(R.id.assetsDirectorIcon);
	//							activator.setImageDrawable(getDrawable(R.drawable.assetsdirector));
	//							activator.setClickable(true);
	//							activator.setOnClickListener(new View.OnClickListener() {
	//								public void onClick(final View view) {
	//									Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.ASSETDIRECTOR.onClick");
	//									// Activate the manager.
	//									final Intent intent = new Intent(parentActivity, adirector.getClass());
	//									// Send the pilot id and transfer it to the next
	//									// Activity
	//									intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
	//											DirectorsBoardActivity.this._store.getPilot().getCharacterID());
	//									startActivity(intent);
	//									Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.ASSETDIRECTOR.onClick");
	//								}
	//							});
	//							final TextView label = (TextView) findViewById(R.id.assetsDirectorLabel);
	//							label.setTypeface(daysFace);
	//							activator.invalidate();
	//						}
	//					case SHIPDIRECTOR:
	//						final IDirector sdirector = new ShipDirectorActivity();
	//						if (sdirector.checkActivation(this._store.getPilot())) {
	//							logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
	//							activator = (ImageView) findViewById(R.id.shipsDirectorIcon);
	//							activator.setImageDrawable(getDrawable(R.drawable.shipsdirector));
	//							activator.setClickable(true);
	//							activator.setOnClickListener(new View.OnClickListener() {
	//								public void onClick(final View view) {
	//									Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.SHIPDIRECTOR.onClick");
	//									// Activate the manager.
	//									final Intent intent = new Intent(parentActivity, sdirector.getClass());
	//									// Send the pilot id and transfer it to the next
	//									// Activity
	//									intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
	//											DirectorsBoardActivity.this._store.getPilot().getCharacterID());
	//									startActivity(intent);
	//									Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.ASSETDIRECTOR.onClick");
	//								}
	//							});
	//							final TextView label = (TextView) findViewById(R.id.shipsDirectorLabel);
	//							label.setTypeface(daysFace);
	//							activator.invalidate();
	//						}
	//					case INDUSTRYDIRECTOR:
	//						final IDirector thedirector = new IndustryDirectorActivity();
	//						if (thedirector.checkActivation(this._store.getPilot())) {
	//							logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
	//							activator = (ImageView) findViewById(R.id.industryDirectorIcon);
	//							activator.setImageDrawable(getDrawable(R.drawable.industrydirector));
	//							activator.setClickable(true);
	//							activator.setOnClickListener(new View.OnClickListener() {
	//								public void onClick(final View view) {
	//									Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.INDUSTRYDIRECTOR.onClick");
	//									// Activate the manager.
	//									final Intent intent = new Intent(parentActivity, thedirector.getClass());
	//									// Send the pilot id and transfer it to the next
	//									// Activity
	//									intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
	//											DirectorsBoardActivity.this._store.getPilot().getCharacterID());
	//									startActivity(intent);
	//									Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.INDUSTRYDIRECTOR.onClick");
	//								}
	//							});
	//							final TextView label = (TextView) findViewById(R.id.industryDirectorLabel);
	//							label.setTypeface(daysFace);
	//							activator.invalidate();
	//						}
	//						break;
	//					case JOBDIRECTOR:
	//						final IDirector jdirector = new FittingActivity();
	//						if (jdirector.checkActivation(this._store.getPilot())) {
	//							logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
	//							activator = (ImageView) findViewById(R.id.jobDirectorIcon);
	//							activator.setImageDrawable(getDrawable(R.drawable.jobdirector));
	//							activator.setClickable(true);
	//							activator.setOnClickListener(new View.OnClickListener() {
	//								public void onClick(final View view) {
	//									// Activate the manager.
	//									final Intent intent = new Intent(parentActivity, jdirector.getClass());
	//									// Send the pilot id and transfer it to the next
	//									// Activity
	//									intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
	//											DirectorsBoardActivity.this._store.getPilot().getCharacterID());
	//									startActivity(intent);
	//								}
	//							});
	//							activator.invalidate();
	//						}
	//						break;
	//					case MARKETDIRECTOR:
	//						final IDirector director = new MarketDirectorActivity();
	//						if (director.checkActivation(this._store.getPilot())) {
	//							logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
	//							activator = (ImageView) findViewById(R.id.marketDirectorIcon);
	//							activator.setImageDrawable(getDrawable(R.drawable.marketdirector));
	//							activator.setClickable(true);
	//							activator.setOnClickListener(new View.OnClickListener() {
	//								public void onClick(final View view) {
	//									// Activate the manager.
	//									final Intent intent = new Intent(parentActivity, director.getClass());
	//									// Send the pilot id and transfer it to the next
	//									// Activity
	//									intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
	//											DirectorsBoardActivity.this._store.getPilot().getCharacterID());
	//									startActivity(intent);
	//								}
	//							});
	//							activator.invalidate();
	//						}
	//						break;
	//					case FITDIRECTOR:
	//						final IDirector fdirector = new FittingActivity();
	//						if (fdirector.checkActivation(this._store.getPilot())) {
	//							logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
	//							activator = (ImageView) findViewById(R.id.marketDirectorIcon);
	//							activator.setImageDrawable(getDrawable(R.drawable.fitsdirector));
	//							activator.setClickable(true);
	//							activator.setOnClickListener(new View.OnClickListener() {
	//								public void onClick(final View view) {
	//									// Activate the manager.
	//									final Intent intent = new Intent(parentActivity, fdirector.getClass());
	//									// Send the pilot id and transfer it to the next
	//									// Activity
	//									intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
	//											DirectorsBoardActivity.this._store.getPilot().getCharacterID());
	//									startActivity(intent);
	//								}
	//							});
	//							activator.invalidate();
	//						}
	//						break;
	//					// case MININGDIRECTOR:
	//					// final IDirector mdirector = new MiningSessionActivity();
	//					// if (mdirector.checkActivation(getPilot())) {
	//					// logger.info("-- DirectorsBoardActivity.onResume - activated "
	//					// + directorCode);
	//					// activator = (ImageView)
	//					// findViewById(R.id.miningDirectorIcon);
	//					// activator.setImageDrawable(getDrawable(R.drawable.miningdirector));
	//					// activator.setClickable(true);
	//					// activator.setOnClickListener(new View.OnClickListener() {
	//					// public void onClick(final View view) {
	//					// // Activate the manager.
	//					// final Intent intent = new Intent(parentActivity,
	//					// mdirector.getClass());
	//					// // Send the pilot id and transfer it to the next Activity
	//					// intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
	//					// parentActivity.getPilot()
	//					// .getCharacterID());
	//					// startActivity(intent);
	//					// }
	//					// });
	//					// activator.invalidate();
	//					// }
	//					// break;
	//					// case TASKDIRECTOR:
	//					// final IDirector tdirector = new TasksDirectorActivity();
	//					// if (tdirector.checkActivation(getPilot())) {
	//					// logger.info("-- DirectorsBoardActivity.onResume - activated "
	//					// + directorCode);
	//					// activator = (ImageView) findViewById(R.id.taskDirectorIcon);
	//					// activator.setImageDrawable(getDrawable(R.drawable.taskdirector));
	//					// activator.setClickable(true);
	//					// activator.setOnClickListener(new View.OnClickListener() {
	//					// public void onClick(final View view) {
	//					// // Activate the manager.
	//					// final Intent intent = new Intent(parentActivity,
	//					// tdirector.getClass());
	//					// // Send the pilot id and transfer it to the next Activity
	//					// intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
	//					// parentActivity.getPilot()
	//					// .getCharacterID());
	//					// startActivity(intent);
	//					// }
	//					// });
	//					// }
	//					// break;
	//					// case FITDIRECTOR:
	//					// // final IDirector fdirector = new FitsActivity();
	//					// // if (fdirector.checkActivation(getPilot())) {
	//					// // logger.info("-- DirectorsBoardActivity.onResume -
	//					// activated " + directorCode);
	//					// activator = (ImageView) findViewById(R.id.fitDirectorIcon);
	//					// activator.setImageDrawable(getDrawable(R.drawable.fitsdirector));
	//					// activator.setClickable(true);
	//					// activator.setOnClickListener(new View.OnClickListener() {
	//					// public void onClick(final View view) {
	//					// // Activate the manager.
	//					// final Intent intent = new Intent(parentActivity,
	//					// FittingActivity.class);
	//					// // Send the pilot id and transfer it to the next Activity
	//					// intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
	//					// _store.getPilot().getCharacterID());
	//					// startActivity(intent);
	//					// }
	//					// });
	//					// // }
	//					// break;
	//					// [01]
	//				}
	//			}
	//		} catch (final Exception rtex) {
	//			rtex.printStackTrace();
	//			stopActivity(
	//					new RuntimeException("R> Runtime Exception on DirectorsBoardActivity.onResume." + rtex.getMessage()));
	//		}
	//		logger.info("<< DirectorsBoardActivity.onResume");
	//	}

	@Override
	protected void onSaveInstanceState(final Bundle savedInstanceState) {
		Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.onSaveInstanceState"); //$NON-NLS-1$
		super.onSaveInstanceState(savedInstanceState);
		// Add current model data dependencies. EVECHARACTERID
		savedInstanceState.putLong(AppWideConstants.extras.EXTRA_EVECHARACTERID, _store.getPilot().getCharacterID());
		// _store.save();
		Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.onSaveInstanceState"); //$NON-NLS-1$
	}

	private void addExpandableFragment(final String label, final int fragmentIdentifier) {
		final FragmentManager manager = getFragmentManager();
		if (null != manager) if (null == manager.findFragmentByTag(label)) {
			// Create a new Fragment to be placed in the activity layout
			final ExpandableFragment thefrag = new ExpandableFragment();
			thefrag.setLabel(label);
			thefrag.setIdentifier(fragmentIdentifier);
			thefrag.setDataSource(DataSourceFactory.createDataSource(fragmentIdentifier));
			final Bundle args = new Bundle();
			args.putString("TAG", Integer.valueOf(fragmentIdentifier).toString());
			thefrag.setArguments(args);

			// Add the fragment to the 'fragmentContainer' Layout
			getFragmentManager().beginTransaction().add(R.id.fragmentContainer, thefrag).commit();
		}
	}

	/**
	 * Adds a new fragment to the fragment manager in case that fragment is not already registered. Fragment
	 * registration uses the fragment identifier converted to an string as the unique locator. If the fragment
	 * is not registered the methods creates a new instance and initializes its structures.
	 * 
	 * @param label
	 *          string that is used for fragment tagging and registration
	 * @param fragmentIdentifier
	 *          fragment unique id to identify the corresponding data source.
	 */
	private void addFragment(final String label, final int fragmentIdentifier) {
		final FragmentManager manager = getFragmentManager();
		if (null != manager) if (null == manager.findFragmentByTag(label)) {
			// Create a new Fragment to be placed in the activity layout
			final PagerFragment thefrag = new PagerFragment();
			thefrag.setIdentifier(fragmentIdentifier);
			thefrag.setDataSource(DataSourceFactory.createDataSource(fragmentIdentifier));
			final Bundle args = new Bundle();
			args.putString("TAG", Integer.valueOf(fragmentIdentifier).toString());
			thefrag.setArguments(args);

			// Add the fragment to the 'fragmentContainer' Layout
			getFragmentManager().beginTransaction().add(R.id.fragmentContainer, thefrag).commit();
		}
	}

	//	/**
	//	 * Clear the state of all the Directors to a dimmed and inactive state. It will be recalculated while on the
	//	 * onResume phase.
	//	 */
	//	@Override
	//	@SuppressLint("NewApi")
	//	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	//	@SuppressWarnings("deprecation")
	//	protected void onStart() {
	//		logger.info(">> DirectorsBoardActivity.onStart");
	//		super.onStart();
	//		ImageView directorButton = (ImageView) findViewById(R.id.assetsDirectorIcon);
	//		directorButton.setImageDrawable(getDrawable(R.drawable.assetsdirectordimmed));
	//		directorButton.setClickable(false);
	//
	//		directorButton = (ImageView) findViewById(R.id.industryDirectorIcon);
	//		directorButton.setImageDrawable(getDrawable(R.drawable.industrydirectordimmed));
	//		directorButton.setClickable(false);
	//
	//		directorButton = (ImageView) findViewById(R.id.marketDirectorIcon);
	//		directorButton.setImageDrawable(getDrawable(R.drawable.marketdirectordimmed));
	//		directorButton.setClickable(false);
	//		// directorButton = (ImageView) findViewById(R.id.nmarketDirectorIcon);
	//		// directorButton.setImageDrawable(getDrawable(R.drawable.marketdirectordimmed));
	//		// directorButton.setClickable(false);
	//
	//		directorButton = (ImageView) findViewById(R.id.jobDirectorIcon);
	//		directorButton.setImageDrawable(getDrawable(R.drawable.jobdirectordimmed));
	//		directorButton.setClickable(false);
	//
	//		// directorButton = (ImageView) findViewById(R.id.taskDirectorIcon);
	//		// directorButton.setImageDrawable(getDrawable(R.drawable.taskdirectordimmed));
	//		// directorButton.setClickable(false);
	//
	//		directorButton = (ImageView) findViewById(R.id.fitDirectorIcon);
	//		directorButton.setImageDrawable(getDrawable(R.drawable.fitdirectordimmed));
	//		directorButton.setClickable(true);
	//		logger.info("<< DirectorsBoardActivity.onStart");
	//	}

	//	/**
	//	 * For really unrecoverable or undefined exceptions the application should go to a safe spot. That spot is
	//	 * defined by the application so this is another abstract method.
	//	 * 
	//	 * @param exception
	//	 */
	//	protected void stopActivity(final Exception exception) {
	//		final Intent intent = new Intent(this, PilotListActivity.class);
	//		// Pass the user message to the activity for display.
	//		intent.putExtra(AppWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
	//		startActivity(intent);
	//	}

	private Activity getActivity() {
		return this;
	}

	@SuppressLint("Override")
	private Drawable getDrawable(final int reference) {
		// ContextCompat.getDrawable(getActivity(), reference);
		return getActivity().getResources().getDrawable(reference);
	}

	// private EveChar getPilot() {
	// return _store.getPilot();
	// }
}

// - UNUSED CODE ............................................................................................
//`[01]
//	@Override
//	public boolean onCreateOptionsMenu(final Menu menu) {
//		final MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.eiabasemenu, menu);
//		EVEDroidApp.getAppStore().setAppMenu(menu);
//		return super.onCreateOptionsMenu(menu);
//	}

//	@Override
//	public boolean onOptionsItemSelected(final MenuItem item) {
//		switch (item.getItemId()) {
//			case android.R.id.home:
//				// This ID represents the Home or Up button. In the case of this
//				// activity, the Up button is shown. Use NavUtils to allow users
//				// to navigate up one level in the application structure. For
//				// more details, see the Navigation pattern on Android Design:
//				//
//				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
//				//
//				NavUtils.navigateUpFromSameTask(this);
//				return true;
//			case R.id.action_settings:
//				startActivity(new Intent(this, SettingsActivity.class));
//				return true;
//			case R.id.action_fullreload:
//				// EVEDroidApp.setFullReload(true);
//				startActivity(new Intent(this, SplashActivity.class));
//				return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

//	@Override
//	protected void onCreate(final Bundle savedInstanceState) {
//		logger.info(">> DirectorsBoardActivity.onCreate");
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_directorsboard);
//		try {
//			// Process the parameters into the context.
//			final Bundle extras = getIntent().getExtras();
//			if (null == extras) throw new RuntimeException(
//					"RT DirectorsBoardActivity.onCreate - Unable to continue. Required parameters not defined on Extras.");
//			// Instantiate the pilot from the characterID.
//			final long characterid = extras.getLong(AppWideConstants.extras.EXTRA_EVECHARACTERID);
//			Log.i("DirectorsBoardActivity", "-- DirectorsBoardActivity.onCreate -- Detected "
//					+ AppWideConstants.extras.EXTRA_EVECHARACTERID + "=" + characterid);
//			if (characterid > 0) {
//				this._store = EVEDroidApp.getAppStore();
//				this._store.activatePilot(characterid);
//				this._store.activateActivity(this);
//			} else
//				throw new RuntimeException(
//						"RT DirectorsBoardActivity.onCreate - Unable to continue. Required parameters not defined on Extras.");
//
//			// Gets the activity's default ActionBar
//			this._actionBar = getActionBar();
//			this._actionBar.show();
//			this._actionBar.setDisplayHomeAsUpEnabled(true);
//
//			// Change the title and the background of the activity.
//			this._actionBar.setTitle(this._store.getPilot().getName());
//
//			// Locate the elements of the page and store in global data.
//			this._directorContainer = (ViewGroup) findViewById(R.id.neocomContainer);
//			this._fragmentContainer = (ViewGroup) findViewById(R.id.fragmentContainer);
//			this._back = (ImageView) findViewById(R.id.backgroundFrame);
//			// Check page structure.
//			if (null == this._directorContainer) {
//				stopActivity(new RuntimeException("UNXER. Expected UI element not found."));
//			}
//			if (null == this._fragmentContainer) {
//				stopActivity(new RuntimeException("UNXER. Expected UI element not found."));
//			}
//			if (null == this._back) {
//				stopActivity(new RuntimeException("UNXER. Expected UI element not found."));
//			}
//		} catch (final Exception rtex) {
//			logger.severe("R> Runtime Exception on DirectorsBoardActivity.onCreate." + rtex.getMessage());
//			rtex.printStackTrace();
//			stopActivity(rtex);
//		}
//
//		// Compose the page adding the fragments.
//		try {
//			// Add the fragments to the fragment container if they are not
//			// already there.
//			addFragment("PilotInformation", AppWideConstants.fragment.FRAGMENT_PILOTINFO_INFO);
//
//			// final FragmentManager manager = getFragmentManager();
//		} catch (final Exception rtex) {
//			logger.severe("R> Runtime Exception on DirectorsBoardActivity.onCreate." + rtex.getMessage());
//			rtex.printStackTrace();
//			stopActivity(
//					new RuntimeException("R> Runtime Exception on DirectorsBoardActivity.onCreate." + rtex.getMessage()));
//		}
//		logger.info("<< DirectorsBoardActivity.onCreate");
//	}

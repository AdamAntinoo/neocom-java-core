//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.fragment;

//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.RootNode;
import org.dimensinfin.android.mvc.interfaces.IEditPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.AppWideConstants.EFragment;
import org.dimensinfin.evedroid.datasource.DataSourceLocator;
import org.dimensinfin.evedroid.datasource.SpecialDataSource;
import org.dimensinfin.evedroid.factory.PartFactory;
import org.dimensinfin.evedroid.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.evedroid.model.Fitting;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.part.DirectorPart;
import org.dimensinfin.evedroid.part.GroupPart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComDashboardFragment extends AbstractNewPagerFragment {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("NeoComDashboardFragment");
	static enum EDirectorCode {
		ASSETDIRECTOR, SHIPDIRECTOR, INDUSTRYDIRECTOR, MARKETDIRECTOR, JOBDIRECTOR, MININGDIRECTOR, FITDIRECTOR
	}
	private static final EDirectorCode[]	activeDirectors			= { EDirectorCode.ASSETDIRECTOR, EDirectorCode.SHIPDIRECTOR,
			EDirectorCode.INDUSTRYDIRECTOR, EDirectorCode.JOBDIRECTOR, EDirectorCode.MARKETDIRECTOR,
			EDirectorCode.FITDIRECTOR };

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComDashboardFragment() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public String getSubtitle() {
		return "";
	}

	@Override
	public String getTitle() {
		return getPilotName();
	}

	/**
	 * This implementation is somehow special because the Dashboard has a new lateral layout that will contain
	 * the list of Directors active for this pilot. So the fragment should implement the standard fragment and
	 * an additional layout so I have to change the layout on the creation phase. <br>
	 * Also the layout is special so I have to call that super method with the right layout.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		logger.info(">> [NeoComDashboardFragment.onCreateView]");
		int layout = R.layout.activity_directorsboard;
		final View theView = this.onCreateViewSuper(layout, inflater, container, savedInstanceState);
		try {
			setIdentifier(_variant.hashCode());
			registerDataSource();
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "RTEX> NeoComDashboardFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> NeoComDashboardFragment.onCreateView - " + rtex.getMessage()));
		}
		logger.info("<< [NeoComDashboardFragment.onCreateView]");
		return theView;
	}

	/**
	 * This DataSource will add the model information for a Pilot. Currently there is only one element but next
	 * will follow skills or other elements to be shown on the initial Pilot dashboard..
	 */
	@Override
	protected void registerDataSource() {
		logger.info(">> [NeoComDashboardFragment.registerDataSource]");
		Bundle extras = getExtras();
		long capsuleerid = 0;
		if (null != extras) {
			capsuleerid = extras.getLong(AppWideConstants.extras.EXTRA_EVECHARACTERID);
		}
		DataSourceLocator locator = new DataSourceLocator().addIdentifier(_variant.name()).addIdentifier(capsuleerid);
		// Register the datasource. If this same datasource is already at the manager we get it instead creating a new one.
		SpecialDataSource ds = new PilotDashboardDataSource(locator, new PilotPartFactory(_variant));
		ds.setVariant(_variant);
		ds.addParameter(AppWideConstants.EExtras.CAPSULEERID.name(), getPilot().getCharacterID());
		ds = (SpecialDataSource) EVEDroidApp.getAppStore().getDataSourceConector().registerDataSource(ds);
		setDataSource(ds);
		logger.info("<< [NeoComDashboardFragment.registerDataSource]");
	}

	//	/**
	//	 * This code is identical on all Fragment implementations so can be moved to the super class.
	//	 */
	//	@Override
	//	public void onStart() {
	//		logger.info(">> [NeoComDashboardFragment.onStart]");
	//		try {
	//			registerDataSource();
	////			// This fragment has a header. Populate it with the datasource header contents.
	////			setHeaderContents();
	//			// And also has the special neocom menu. Add to it the active Directors.
	//			setDirectors();
	//		} catch (final RuntimeException rtex) {
	//			Log.e("EVEI", "RTEX> NeoComDashboardFragment.onStart - " + rtex.getMessage());
	//			rtex.printStackTrace();
	//			stopActivity(new RuntimeException("RTEX> NeoComDashboardFragment.onStart - " + rtex.getMessage()));
	//		}
	//		super.onStart();
	//		logger.info(">> [NeoComDashboardFragment.onStart]");
	//	}
	/**
	 * This method add the icon and button of each director to the lateral menu implemented as a list.
	 */
	private void setDirectors() {
					for (final EDirectorCode directorCode : activeDirectors) {
						//						ImageView activator = null;
						switch (directorCode) {
							case ASSETDIRECTOR:
								// Create the part, configure it and add to the layout.
								DirectorPart dirPart = new DirectorPart(new Director(new AssetsDirectorActivity()));
dirPart.addPropertyChangeListener(this);
								addtoHeader(dirPart);

								
								final IDirector adirector = new AssetsDirectorActivity();
								if (adirector.checkActivation(this._store.getPilot())) {
									logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
									activator = (ImageView) findViewById(R.id.assetsDirectorIcon);
									activator.setImageDrawable(getDrawable(R.drawable.assetsdirector));
									activator.setClickable(true);
									activator.setOnClickListener(new View.OnClickListener() {
										public void onClick(final View view) {
											Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.ASSETDIRECTOR.onClick");
											// Activate the manager.
											final Intent intent = new Intent(parentActivity, adirector.getClass());
											// Send the pilot id and transfer it to the next
											// Activity
											intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
													DirectorsBoardActivity.this._store.getPilot().getCharacterID());
											startActivity(intent);
											Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.ASSETDIRECTOR.onClick");
										}
									});
									final TextView label = (TextView) findViewById(R.id.assetsDirectorLabel);
									label.setTypeface(daysFace);
									activator.invalidate();
								}
							case SHIPDIRECTOR:
								final IDirector sdirector = new ShipDirectorActivity();
								if (sdirector.checkActivation(this._store.getPilot())) {
									logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
									activator = (ImageView) findViewById(R.id.shipsDirectorIcon);
									activator.setImageDrawable(getDrawable(R.drawable.shipsdirector));
									activator.setClickable(true);
									activator.setOnClickListener(new View.OnClickListener() {
										public void onClick(final View view) {
											Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.SHIPDIRECTOR.onClick");
											// Activate the manager.
											final Intent intent = new Intent(parentActivity, sdirector.getClass());
											// Send the pilot id and transfer it to the next
											// Activity
											intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
													DirectorsBoardActivity.this._store.getPilot().getCharacterID());
											startActivity(intent);
											Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.ASSETDIRECTOR.onClick");
										}
									});
									final TextView label = (TextView) findViewById(R.id.shipsDirectorLabel);
									label.setTypeface(daysFace);
									activator.invalidate();
								}
							case INDUSTRYDIRECTOR:
								final IDirector thedirector = new IndustryDirectorActivity();
								if (thedirector.checkActivation(this._store.getPilot())) {
									logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
									activator = (ImageView) findViewById(R.id.industryDirectorIcon);
									activator.setImageDrawable(getDrawable(R.drawable.industrydirector));
									activator.setClickable(true);
									activator.setOnClickListener(new View.OnClickListener() {
										public void onClick(final View view) {
											Log.i("DirectorsBoardActivity", ">> DirectorsBoardActivity.INDUSTRYDIRECTOR.onClick");
											// Activate the manager.
											final Intent intent = new Intent(parentActivity, thedirector.getClass());
											// Send the pilot id and transfer it to the next
											// Activity
											intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
													DirectorsBoardActivity.this._store.getPilot().getCharacterID());
											startActivity(intent);
											Log.i("DirectorsBoardActivity", "<< DirectorsBoardActivity.INDUSTRYDIRECTOR.onClick");
										}
									});
									final TextView label = (TextView) findViewById(R.id.industryDirectorLabel);
									label.setTypeface(daysFace);
									activator.invalidate();
								}
								break;
							case JOBDIRECTOR:
								final IDirector jdirector = new FittingActivity();
								if (jdirector.checkActivation(this._store.getPilot())) {
									logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
									activator = (ImageView) findViewById(R.id.jobDirectorIcon);
									activator.setImageDrawable(getDrawable(R.drawable.jobdirector));
									activator.setClickable(true);
									activator.setOnClickListener(new View.OnClickListener() {
										public void onClick(final View view) {
											// Activate the manager.
											final Intent intent = new Intent(parentActivity, jdirector.getClass());
											// Send the pilot id and transfer it to the next
											// Activity
											intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
													DirectorsBoardActivity.this._store.getPilot().getCharacterID());
											startActivity(intent);
										}
									});
									activator.invalidate();
								}
								break;
							case MARKETDIRECTOR:
								final IDirector director = new MarketDirectorActivity();
								if (director.checkActivation(this._store.getPilot())) {
									logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
									activator = (ImageView) findViewById(R.id.marketDirectorIcon);
									activator.setImageDrawable(getDrawable(R.drawable.marketdirector));
									activator.setClickable(true);
									activator.setOnClickListener(new View.OnClickListener() {
										public void onClick(final View view) {
											// Activate the manager.
											final Intent intent = new Intent(parentActivity, director.getClass());
											// Send the pilot id and transfer it to the next
											// Activity
											intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
													DirectorsBoardActivity.this._store.getPilot().getCharacterID());
											startActivity(intent);
										}
									});
									activator.invalidate();
								}
								break;
							case FITDIRECTOR:
								final IDirector fdirector = new FittingActivity();
								if (fdirector.checkActivation(this._store.getPilot())) {
									logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
									activator = (ImageView) findViewById(R.id.marketDirectorIcon);
									activator.setImageDrawable(getDrawable(R.drawable.fitsdirector));
									activator.setClickable(true);
									activator.setOnClickListener(new View.OnClickListener() {
										public void onClick(final View view) {
											// Activate the manager.
											final Intent intent = new Intent(parentActivity, fdirector.getClass());
											// Send the pilot id and transfer it to the next
											// Activity
											intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
													DirectorsBoardActivity.this._store.getPilot().getCharacterID());
											startActivity(intent);
										}
									});
									activator.invalidate();
								}
								break;
							// case MININGDIRECTOR:
							// final IDirector mdirector = new MiningSessionActivity();
							// if (mdirector.checkActivation(getPilot())) {
							// logger.info("-- DirectorsBoardActivity.onResume - activated "
							// + directorCode);
							// activator = (ImageView)
							// findViewById(R.id.miningDirectorIcon);
							// activator.setImageDrawable(getDrawable(R.drawable.miningdirector));
							// activator.setClickable(true);
							// activator.setOnClickListener(new View.OnClickListener() {
							// public void onClick(final View view) {
							// // Activate the manager.
							// final Intent intent = new Intent(parentActivity,
							// mdirector.getClass());
							// // Send the pilot id and transfer it to the next Activity
							// intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
							// parentActivity.getPilot()
							// .getCharacterID());
							// startActivity(intent);
							// }
							// });
							// activator.invalidate();
							// }
							// break;
							// case TASKDIRECTOR:
							// final IDirector tdirector = new TasksDirectorActivity();
							// if (tdirector.checkActivation(getPilot())) {
							// logger.info("-- DirectorsBoardActivity.onResume - activated "
							// + directorCode);
							// activator = (ImageView) findViewById(R.id.taskDirectorIcon);
							// activator.setImageDrawable(getDrawable(R.drawable.taskdirector));
							// activator.setClickable(true);
							// activator.setOnClickListener(new View.OnClickListener() {
							// public void onClick(final View view) {
							// // Activate the manager.
							// final Intent intent = new Intent(parentActivity,
							// tdirector.getClass());
							// // Send the pilot id and transfer it to the next Activity
							// intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
							// parentActivity.getPilot()
							// .getCharacterID());
							// startActivity(intent);
							// }
							// });
							// }
							// break;
							// case FITDIRECTOR:
							// // final IDirector fdirector = new FitsActivity();
							// // if (fdirector.checkActivation(getPilot())) {
							// // logger.info("-- DirectorsBoardActivity.onResume -
							// activated " + directorCode);
							// activator = (ImageView) findViewById(R.id.fitDirectorIcon);
							// activator.setImageDrawable(getDrawable(R.drawable.fitsdirector));
							// activator.setClickable(true);
							// activator.setOnClickListener(new View.OnClickListener() {
							// public void onClick(final View view) {
							// // Activate the manager.
							// final Intent intent = new Intent(parentActivity,
							// FittingActivity.class);
							// // Send the pilot id and transfer it to the next Activity
							// intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
							// _store.getPilot().getCharacterID());
							// startActivity(intent);
							// }
							// });
							// // }
							// break;
							// [01]
						}
					}
	}

	//	private void setHeaderContents() {
	//		RootNode headModel = ((FittingDataSource) getDataSource()).getHeaderModel();
	//		for (AbstractComplexNode model : headModel.collaborate2Model(_variant.name())) {
	//			// Set the datasource as the listener for this parts events.
	//			AbstractAndroidPart pt = createPart(model);
	//			pt.addPropertyChangeListener(getDataSource());
	//			addtoHeader(pt);
	//		}
	//	}

}

//- CLASS IMPLEMENTATION ...................................................................................
final class PilotDashboardDataSource extends SpecialDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 7810087592108417570L;
	private static Logger			logger						= Logger.getLogger("FittingDataSource");

	// - F I E L D - S E C T I O N ............................................................................
	private final Fitting			fit								= null;

	//	private final ArrayList<Asset>						ships							= null;

	//- C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotDashboardDataSource(final DataSourceLocator locator, final IPartFactory factory) {
		super(locator, factory);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public RootNode collaborate2Model() {
		return _dataModelRoot;
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class PilotPartFactory extends PartFactory implements IPartFactory {
	// - S T A T I C - S E C T I O N ..........................................................................
	// - F I E L D - S E C T I O N ............................................................................
	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotPartFactory(final EFragment _variant) {
		super(_variant);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The method should create the matching part for the model received but there is no other place where we
	 * should create the next levels of the hierarchy. So we will create the part trasnformationes here.
	 */
	@Override
	public IEditPart createPart(final IGEFNode node) {
		//	if (node instanceof Action) {
		//		ActionPart part = new ActionPart((AbstractComplexNode) node);
		//		return part;
		//	}
		//	if (node instanceof EveTask) {
		//		TaskPart part = new TaskPart((AbstractComplexNode) node);
		//		return part;
		//	}
		//	if (node instanceof Separator) {
		//		GroupPart part = new GroupPart((Separator) node);
		//		return part;
		//	}
		//	// This is the part element for the Fitting that going in the head.
		//	if (node instanceof Fitting) {
		//		FittingPart part = (FittingPart) new FittingPart((Fitting) node)
		//				.setRenderMode(AppWideConstants.rendermodes.RENDER_FITTINGHEADER);
		//		return part;
		//	}
		return new GroupPart(new Separator("-NO data-"));
	}
}
//- UNUSED CODE ............................................................................................

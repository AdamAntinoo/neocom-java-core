//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.fragment;

//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.activity.AssetsDirectorActivity;
import org.dimensinfin.eveonline.neocom.activity.FittingListActivity;
import org.dimensinfin.eveonline.neocom.activity.IndustryDirectorActivity;
import org.dimensinfin.eveonline.neocom.activity.ShipDirectorActivity;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants.EExtras;
import org.dimensinfin.eveonline.neocom.datasource.DataSourceLocator;
import org.dimensinfin.eveonline.neocom.datasource.SpecialDataSource;
import org.dimensinfin.eveonline.neocom.factory.PilotPartFactory;
import org.dimensinfin.eveonline.neocom.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.eveonline.neocom.model.Director;
import org.dimensinfin.eveonline.neocom.model.Fitting;
import org.dimensinfin.eveonline.neocom.part.DirectorPart;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComDashboardFragment extends AbstractNewPagerFragment {
	static enum EDirectorCode {
		ASSETDIRECTOR, SHIPDIRECTOR, INDUSTRYDIRECTOR, MARKETDIRECTOR, JOBDIRECTOR, MININGDIRECTOR, FITDIRECTOR
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger									logger							= Logger.getLogger("NeoComDashboardFragment");
	private static final EDirectorCode[]	activeDirectors			= { EDirectorCode.ASSETDIRECTOR, EDirectorCode.SHIPDIRECTOR,
			EDirectorCode.INDUSTRYDIRECTOR, EDirectorCode.JOBDIRECTOR, EDirectorCode.MARKETDIRECTOR,
			EDirectorCode.FITDIRECTOR };

	// - F I E L D - S E C T I O N ............................................................................
	/** The view that represent the list view and the space managed though the adapter. */
	private ViewGroup											neoComMenuContainer	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * This forces the Fragment to create the PartFactory required to get all the Parts used on this fragment
	 * set.
	 */
	@Override
	public void createFactory() {
		this.setFactory(new PilotPartFactory(this.getVariant()));
	}

	@Override
	public String getSubtitle() {
		return "";
	}

	@Override
	public String getTitle() {
		return this.getPilotName();
	}

	/**
	 * This implementation is somehow special because the Dashboard has a new lateral layout that will contain
	 * the list of Directors active for this pilot. So the fragment should implement the standard fragment and
	 * an additional layout so I have to change the layout on the creation phase. <br>
	 * Also the layout is special so I have to call that super method with the right layout.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		NeoComDashboardFragment.logger.info(">> [NeoComDashboardFragment.onCreateView]");
		int layout = R.layout.activity_directorsboard;
		final View theView = this.onCreateViewSuper(layout, inflater, container, savedInstanceState);
		try {
			this.createFactory();
			this.registerDataSource();
			this.setHeaderContents();
			this.setNeoComMenu(theView);
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "RTEX> NeoComDashboardFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> NeoComDashboardFragment.onCreateView - " + rtex.getMessage()));
		}
		NeoComDashboardFragment.logger.info("<< [NeoComDashboardFragment.onCreateView]");
		return theView;
	}

	/**
	 * This DataSource will add the model information for a Pilot. Currently there is only one element but next
	 * will follow skills or other elements to be shown on the initial Pilot dashboard..
	 */
	@Override
	protected void registerDataSource() {
		NeoComDashboardFragment.logger.info(">> [NeoComDashboardFragment.registerDataSource]");
		Bundle extras = this.getExtras();
		long capsuleerid = 0;
		if (null != extras) {
			capsuleerid = extras.getLong(EExtras.EXTRA_CAPSULEERID.name());
		}
		DataSourceLocator locator = new DataSourceLocator().addIdentifier(this.getVariant()).addIdentifier(capsuleerid);
		// Register the datasource. If this same datasource is already at the manager we get it instead creating a new one.
		SpecialDataSource ds = new PilotDashboardDataSource(locator, this.getFactory());
		ds.setVariant(this.getVariant());
		ds.addParameter(EExtras.EXTRA_CAPSULEERID.name(), this.getPilot().getCharacterID());
		ds = (SpecialDataSource) AppModelStore.getSingleton().getDataSourceConector().registerDataSource(ds);
		this.setDataSource(ds);
		NeoComDashboardFragment.logger.info("<< [NeoComDashboardFragment.registerDataSource]");
	}

	@Override
	protected void setHeaderContents() {
		IPart pilotPart = this.getFactory().createPart(AppModelStore.getSingleton().getPilot());
		((AbstractPropertyChanger) pilotPart).addPropertyChangeListener(this.getDataSource());
		this.addtoHeader((AbstractAndroidPart) pilotPart);
	}

	/**
	 * This method adds the icon and button of each director to the header container that this time it is at the
	 * left of the view. <br>
	 * This should be implemented as a new List View because it is the only structure able to hangle Part click
	 * interactions.
	 */
	private void setNeoComMenu(final View theView) {
		// Get access to the ListView where to connect the parts.
		neoComMenuContainer = (ViewGroup) _container.findViewById(R.id.neocomContainer);
		if (null != neoComMenuContainer) {
			for (final EDirectorCode directorCode : NeoComDashboardFragment.activeDirectors) {
				AbstractAndroidPart dirPart = null;
				switch (directorCode) {
					case ASSETDIRECTOR:
						// Create the part, configure it and add to the layout.
						dirPart = (AbstractAndroidPart) this.getFactory().createPart(new Director(new AssetsDirectorActivity()));
						break;
					case SHIPDIRECTOR:
						// Create the part, configure it and add to the layout.
						dirPart = (AbstractAndroidPart) this.getFactory().createPart(new Director(new ShipDirectorActivity()));
						break;
					case INDUSTRYDIRECTOR:
						// Create the part, configure it and add to the layout.
						dirPart = (AbstractAndroidPart) this.getFactory().createPart(new Director(new IndustryDirectorActivity()));
						break;
					//							case JOBDIRECTOR:
					//								final IDirector jdirector = new FittingActivity();
					//							case MARKETDIRECTOR:
					//								final IDirector director = new MarketDirectorActivity();
					case FITDIRECTOR:
						// Create the part, configure it and add to the layout.
						dirPart = (AbstractAndroidPart) this.getFactory().createPart(new Director(new FittingListActivity()));
						break;
				}
				if (null != dirPart) {
					dirPart.addPropertyChangeListener(this.getDataSource());
					((DirectorPart) dirPart).setPilot(this.getPilot());
					try {
						final AbstractHolder holder = dirPart.getHolder(this);
						holder.initializeViews();
						holder.updateContent();
						final View hv = holder.getView();
						neoComMenuContainer.addView(hv);
					} catch (final RuntimeException rtex) {
						Log.e("PageFragment", "R> PageFragment.addViewtoHeader RuntimeException. " + rtex.getMessage());
						rtex.printStackTrace();
					}
				}
			}
		}
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class PilotDashboardDataSource extends SpecialDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 7810087592108417570L;
	private static Logger			logger						= Logger.getLogger("FittingDataSource");

	// - F I E L D - S E C T I O N ............................................................................
	private final Fitting			fit								= null;

	//- C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotDashboardDataSource(final DataSourceLocator locator, final IPartFactory factory) {
		super(locator, factory);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public RootNode collaborate2Model() {
		this.setDataModel(new RootNode());
		return _dataModelRoot;
	}
}
//- UNUSED CODE ............................................................................................

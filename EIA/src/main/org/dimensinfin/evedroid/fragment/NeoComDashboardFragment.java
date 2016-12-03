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
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.AssetsDirectorActivity;
import org.dimensinfin.evedroid.activity.FittingActivity;
import org.dimensinfin.evedroid.activity.ShipDirectorActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.AppWideConstants.EFragment;
import org.dimensinfin.evedroid.datasource.DataSourceLocator;
import org.dimensinfin.evedroid.datasource.SpecialDataSource;
import org.dimensinfin.evedroid.factory.PartFactory;
import org.dimensinfin.evedroid.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.evedroid.model.Director;
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
	static enum EDirectorCode {
		ASSETDIRECTOR, SHIPDIRECTOR, INDUSTRYDIRECTOR, MARKETDIRECTOR, JOBDIRECTOR, MININGDIRECTOR, FITDIRECTOR
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger									logger					= Logger.getLogger("NeoComDashboardFragment");
	private static final EDirectorCode[]	activeDirectors	= { EDirectorCode.ASSETDIRECTOR, EDirectorCode.SHIPDIRECTOR,
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
			AbstractAndroidPart dirPart = null;
			switch (directorCode) {
				case ASSETDIRECTOR:
					// Create the part, configure it and add to the layout.
					dirPart = new DirectorPart(new Director(new AssetsDirectorActivity()));
					dirPart.setPilot(getPilot());
					addtoHeader(dirPart);
					break;
				case SHIPDIRECTOR:
					// Create the part, configure it and add to the layout.
					dirPart = new DirectorPart(new Director(new ShipDirectorActivity()));
					addtoHeader(dirPart);
					break;
				//							case INDUSTRYDIRECTOR:
				//								final IDirector thedirector = new IndustryDirectorActivity();
				//							case JOBDIRECTOR:
				//								final IDirector jdirector = new FittingActivity();
				//							case MARKETDIRECTOR:
				//								final IDirector director = new MarketDirectorActivity();
				case FITDIRECTOR:
					// Create the part, configure it and add to the layout.
					dirPart = new DirectorPart(new Director(new FittingActivity()));
					addtoHeader(dirPart);
					break;
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

//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.fragment;

import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.RootNode;
import org.dimensinfin.android.mvc.interfaces.IEditPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.AppWideConstants.EFragment;
import org.dimensinfin.evedroid.datasource.DataSourceLocator;
import org.dimensinfin.evedroid.datasource.FittingDataSource;
import org.dimensinfin.evedroid.datasource.IExtendedDataSource;
import org.dimensinfin.evedroid.datasource.SpecialDataSource;
import org.dimensinfin.evedroid.factory.PartFactory;
import org.dimensinfin.evedroid.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.evedroid.model.Action;
import org.dimensinfin.evedroid.model.EveTask;
import org.dimensinfin.evedroid.model.Fitting;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.part.ActionPart;
import org.dimensinfin.evedroid.part.FittingPart;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.part.TaskPart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This is a test implementation that will run a testing configuration. The sources for fittings maybe already
 * fitted ships or XML fitting configuration files but there is no code now to import from such sources. <br>
 * <br>
 * Fragment implementation that will get some input form the user to select a fitting and a count of copies to
 * calculate the item requirements to cover that request. By default fittings are matched against the GARAGE
 * function Location. The GARAGE function may not be unique. If that case the matching should be against each
 * of the GARAGE locations.
 * 
 * @author Adam Antinoo
 */
public class FittingFragment extends AbstractNewPagerFragment {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger				logger	= Logger.getLogger("FittingFragment");

	// - F I E L D - S E C T I O N ............................................................................
	private FittingPartFactory	factory	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public String getSubtitle() {
		return "";
	}

	@Override
	public String getTitle() {
		return "Fitting - Under Test";
	}

	/**
	 * This code is identical on all Fragment implementations so can be moved to the super class.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> FittingFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			setIdentifier(_variant.hashCode());
			registerDataSource();
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "RTEX> FittingFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> FittingFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< FittingFragment.onCreateView");
		return theView;
	}

	/**
	 * This code is identical on all Fragment implementations so can be moved to the super class.
	 */
	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> FittingFragment.onStart");
		try {
			registerDataSource();
			// This fragment has a header. Populate it with the datasource header contents.
			setHeaderContents();
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "RTEX> FittingFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> FittingFragment.onCreateView - " + rtex.getMessage()));
		}
		super.onStart();
		Log.i("NEOCOM", "<< FittingFragment.onStart");
	}

	private AbstractAndroidPart createPart(final AbstractGEFNode model) {
		IPartFactory factory = getFactory();
		IEditPart part = factory.createPart(model);
		part.setParent(null);
		return (AbstractAndroidPart) part;
	}

	private IExtendedDataSource getDataSource() {
		return _datasource;
	}

	private IPartFactory getFactory() {
		if (null == factory) {
			factory = new FittingPartFactory(_variant);
		}
		return factory;
	}

	/**
	 * This is the single piece f code specific for this fragment. It should create the right class DataSource
	 * and connect it to the Fragment for their initialization during the <b>start</b> phase. <br>
	 * Current implementation is a test code to initialize the DataSorue with a predefined and testing fitting.
	 */
	private void registerDataSource() {
		Log.i("NEOCOM", ">> FittingFragment.registerDataSource");
		Bundle extras = getExtras();
		long capsuleerid = 0;
		String fittingLabel = "Purifier";
		if (null != extras) {
			capsuleerid = extras.getLong(AppWideConstants.extras.EXTRA_EVECHARACTERID);
			fittingLabel = extras.getString(AppWideConstants.EExtras.FITTINGID.name());
		}
		DataSourceLocator locator = new DataSourceLocator().addIdentifier(_variant.name()).addIdentifier(capsuleerid)
				.addIdentifier(fittingLabel);
		// Register the datasource. If this same datasource is already at the manager we get it instead creating a new one.
		SpecialDataSource ds = new FittingDataSource(locator, new FittingPartFactory(_variant));
		ds.setVariant(_variant);
		ds.addParameter(AppWideConstants.EExtras.CAPSULEERID.name(), getPilot().getCharacterID());
		ds.addParameter(AppWideConstants.EExtras.FITTINGID.name(), fittingLabel);
		ds = (SpecialDataSource) EVEDroidApp.getAppStore().getDataSourceConector().registerDataSource(ds);
		setDataSource(ds);
	}

	private void setHeaderContents() {
		RootNode headModel = ((FittingDataSource) getDataSource()).getHeaderModel();
		for (AbstractComplexNode model : headModel.collaborate2Model(_variant.name())) {
			addtoHeader(createPart(model));
		}
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class FittingPartFactory extends PartFactory implements IPartFactory {
	// - S T A T I C - S E C T I O N ..........................................................................
	// - F I E L D - S E C T I O N ............................................................................
	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingPartFactory(final EFragment _variant) {
		super(_variant);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The method should create the matching part for the model received but there is no other place where we
	 * should create the next levels of the hierarchy. So we will create the part trasnformationes here.
	 */
	@Override
	public IEditPart createPart(final IGEFNode node) {
		if (node instanceof Action) {
			ActionPart part = new ActionPart((AbstractComplexNode) node);
			return part;
		}
		if (node instanceof EveTask) {
			TaskPart part = new TaskPart((AbstractComplexNode) node);
			return part;
		}
		if (node instanceof Separator) {
			GroupPart part = new GroupPart((Separator) node);
			return part;
		}
		// This is the part element for the Fitting that going in the head.
		if (node instanceof Fitting) {
			FittingPart part = (FittingPart) new FittingPart((Fitting) node)
					.setRenderMode(AppWideConstants.rendermodes.RENDER_FITTINGHEADER);
			return part;
		}
		return new GroupPart(new Separator("-NO data-"));
	}
}

////- CLASS IMPLEMENTATION ...................................................................................
//final class FittingHeaderHolder extends AbstractHolder {
//	// - S T A T I C - S E C T I O N ..........................................................................
//	// - F I E L D - S E C T I O N ............................................................................
//	//	private ITheme	_theme					= null;
//
//	private Spinner fittingsSpinner = null;
//
//	// - C O N S T R U C T O R - S E C T I O N ................................................................
//	public FittingHeaderHolder(final FittingHeaderPart target, final Activity context) {
//		super(target, context);
//		//		_theme = new RubiconRedTheme(context);
//	}
//
//	// - M E T H O D - S E C T I O N ..........................................................................
//	@Override
//	public FittingHeaderPart getPart() {
//		return (FittingHeaderPart) super.getPart();
//	}
//
//	@Override
//	public void initializeViews() {
//		super.initializeViews();
//		// Create spinner contents from part.
//		fittingsSpinner = (Spinner) _convertView.findViewById(R.id.fittingspinner);
//		if (null != fittingsSpinner) {
//			fittingsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//				public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position,
//						final long id) {
//					Object item = fittingsSpinner.getSelectedItem();
//					if (item instanceof String) {
//						String itemName = (String) item;
//						Toast toast = Toast.makeText(getPart().getActivity(), "Item selected: " + itemName, Toast.LENGTH_SHORT);
//						toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//						toast.show();
//					}
//				}
//
//				public void onNothingSelected(final AdapterView<?> parentView) {
//					// your code here
//				}
//
//			});
//			List<String> ships = getPart().getFittings();
//			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, ships);
//			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//			fittingsSpinner.setAdapter(dataAdapter);
//		}
//	}
//
//	public void setView(final View newView) {
//		_convertView = newView;
//	}
//
//	@Override
//	public void updateContent() {
//		super.updateContent();
//	}
//
//	@Override
//	protected void createView() {
//		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//		_convertView = mInflater.inflate(R.layout.fitting_4header, null);
//		_convertView.setTag(this);
//	}
//
//	protected void loadEveIcon(final ImageView targetIcon, final int typeID) {
//		if (null != targetIcon) {
//			final String link = EVEDroidApp.getTheCacheConnector().getURLForItem(typeID);
//			final Drawable draw = EVEDroidApp.getTheCacheConnector().getCacheDrawable(link, targetIcon);
//			targetIcon.setImageDrawable(draw);
//		}
//	}
//}
//
////- CLASS IMPLEMENTATION ...................................................................................
//final class FittingHeaderPart extends EveAbstractPart {
//	// - S T A T I C - S E C T I O N ..........................................................................
//	private static final long serialVersionUID = -4642153502498052929L;
//
//	// - F I E L D - S E C T I O N ............................................................................
//
//	// - C O N S T R U C T O R - S E C T I O N ................................................................
//	public FittingHeaderPart(final AbstractGEFNode item) {
//		super(item);
//	}
//
//	// - M E T H O D - S E C T I O N ..........................................................................
//	public EveItem getCastedModel() {
//		return (EveItem) getModel();
//	}
//
//	public List<String> getFittings() {
//		List<String> fittings = new ArrayList<String>();
//
//		// Get a reference to the special DataSource.
//		if (getActivity() instanceof FittingActivity) {
//			FittingActivity act = (FittingActivity) getActivity();
//			FittingsDataSource ds = (FittingsDataSource) act.getDataSource(AppWideConstants.fragment.FRAGMENT_FITTINGS);
//			fittings.addAll(ds.getFittings());
//		}
//		return fittings;
//	}
//
//	@Override
//	public long getModelID() {
//		return 0;
//	}
//
//	@Override
//	protected AbstractHolder selectHolder() {
//		// Get the proper holder from the render mode.
//		return new FittingHeaderHolder(this, _activity);
//	}
//}

// - UNUSED CODE ............................................................................................

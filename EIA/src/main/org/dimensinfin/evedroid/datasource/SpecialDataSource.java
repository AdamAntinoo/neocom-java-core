//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.datasource;

//- IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractCorePart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.core.RootPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.enums.EVARIANT;
import org.dimensinfin.evedroid.interfaces.IExtendedDataSource;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class implements the most common code and flow for all datasources to allow the best code
 * generalisation. The datasource methods implement a generic process that calls the specific datasource
 * callback methods for specialization when rendering object in an specific way. That shuld be rare cases.
 * 
 * @author Adam Antinoo
 */
public abstract class SpecialDataSource extends AbstractDataSource implements IExtendedDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long							serialVersionUID	= -9083587546700227219L;
	public static Logger									logger						= Logger.getLogger("SpecialDataSource");

	// - F I E L D - S E C T I O N ............................................................................
	private DataSourceLocator							_locator					= null;
	private EVARIANT											_variant					= EVARIANT.DEFAULT_VARIANT;
	private boolean												_cacheable				= true;
	//	protected AppModelStore		_store						= null;
	//	private final int					_version					= 0;
	protected RootNode										_dataModelRoot		= null;
	/** Part hierarchy that matches the data model hierarchy. */
	protected RootPart										_partModelRoot		= null;
	protected ArrayList<AbstractCorePart>	_bodyParts				= new ArrayList<AbstractCorePart>();
	protected ArrayList<AbstractCorePart>	_headParts				= new ArrayList<AbstractCorePart>();

	private final HashMap<String, Object>	_parameters				= new HashMap<String, Object>();

	private DataSourceManager							_dsManager;

	protected IPartFactory								_partFactory			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public SpecialDataSource(final DataSourceLocator locator, final IPartFactory factory) {
		_locator = locator;
		_partFactory = factory;
	}

	public SpecialDataSource addParameter(final String name, final int value) {
		_parameters.put(name, Integer.valueOf(value));
		return this;
	}

	public SpecialDataSource addParameter(final String name, final long value) {
		_parameters.put(name, Long.valueOf(value));
		return this;
	}

	public SpecialDataSource addParameter(final String name, final String value) {
		_parameters.put(name, value);
		return this;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Deprecated
	public void connect(final DataSourceManager dataSourceManager) {
		_dsManager = dataSourceManager;
	}

	/**
	 * After the model is created we have to transform it into the Part list expected by the DataSourceAdapter.
	 * <br>
	 * The Part creation is performed by the corresponding PartFactory we got at the DataSource creation.<br>
	 * We transform the model recursively and keeping the already available Part elements. We create a
	 * duplicated of the resulting Part model and we move already parts from the current model to the new model
	 * or create new part and finally remove what is left and unused.
	 */
	@Override
	public void createContentHierarchy() {
		try {
			SpecialDataSource.logger.info(">> [SpecialDataSource.createContentHierarchy]");
			// Check if we have already a Part model.
			// But do not forget to associate the new Data model even of the old exists.
			if (null == _partModelRoot)
				_partModelRoot = new RootPart(_dataModelRoot, _partFactory);
			else
				_partModelRoot.setModel(_dataModelRoot);

			SpecialDataSource.logger.info(
					"-- [SpecialDataSource.createContentHierarchy]> Initiating the refreshChildren() for the _partModelRoot");
			// Intercept any exception on the creation of the model but do not cut the progress of the already added items
			try {
				_partModelRoot.refreshChildren();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Get the list of Parts that will be used for the ListView
			_bodyParts = new ArrayList<AbstractCorePart>();
			_bodyParts.addAll(_partModelRoot.collaborate2View());
			SpecialDataSource.logger.info("<< [SpecialDataSource.createContentHierarchy]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//	public AppModelStore getStore() {
	//		return _store;
	//	}

	//	public int getVersion() {
	//		return _version;
	//	}

	//	/**
	//	 * Sets the initial values for this datasource. This should be done only the first time the datasource is
	//	 * used because after that the model gets populated from the already seeded elements.<br>
	//	 * The initial list in empty because there can be locations or categories.
	//	 */
	//	public abstract void initModel();

	/**
	 * This is the method to initialize the copy of the model structures on the datasource. Every time this
	 * method is called, the complete model is recreated. There are two ways to recreate it, comparing with the
	 * old copy and inserting/deleting different nodes or recreating completely the new model copy. Once this
	 * method is called we can create the depending part hierarchy. <br>
	 * I have to search for a better name for this method. This is not clear and currently the
	 * <code>initModel</code> is already on use by the class but can be reused later.
	 */
	@Deprecated
	public void createPartsHierarchy() {
	}
	//	{
	//		Log.i("NEOCOM", ">> SpecialDataSource.createPartsHierarchy");
	//		if (_modelRoot.size() < 1) initModel();
	//
	//		// Process the model and generate the list of model elements that are visible.
	//		_modelContents.clear();
	//		for (AbstractAndroidNode node : _modelRoot)
	//			_modelContents.addAll(node.collaborate2Model(_version));
	//
	//		// Create the part list from the updated model list.
	//		_bodyParts.clear();
	//		for (final AbstractAndroidNode node : _modelContents)
	//			createPart4Node(node);
	//	}

	public ArrayList<AbstractAndroidPart> getBodyParts() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		for (AbstractCorePart node : _bodyParts)
			result.add((AbstractAndroidPart) node);
		return result;
	}

	public DataSourceLocator getDataSourceLocator() {
		return _locator;
	}

	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		for (AbstractCorePart node : _headParts)
			result.add((AbstractAndroidPart) node);
		return result;
	}

	@Override
	@Deprecated
	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		for (AbstractCorePart node : _bodyParts)
			result.add((AbstractAndroidPart) node);
		return result;
	}

	public EVARIANT getVariant() {
		return _variant;
	}

	/**
	 * This method is called whenever there is an event on any Part related to this DataSource. We just process
	 * structure changes that need the DataSource to reconstruct the Part model from the new Model state. Wrong.
	 * The model does not change but the result from the collaborate2View transformation does.
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
			_bodyParts = new ArrayList<AbstractCorePart>();
			_bodyParts.addAll(_partModelRoot.collaborate2View());
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES)) // The DataSourceAdapter will call getPartHierarchy() and this will return the list of parts on the body. So update the list.
			// But we have changes a key value, so recalculate the model
			// Just activate the refresh because some  refresh.
			//			_bodyParts = new ArrayList<AbstractCorePart>();
			//			_bodyParts.addAll(_partModelRoot.collaborate2View());
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		// THis event is when the user changes the preferred action so I have to calculate the model again.
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE)) {
			this.collaborate2Model();
			this.createContentHierarchy();
			//			_bodyParts = new ArrayList<AbstractCorePart>();
			//			_bodyParts.addAll(_partModelRoot.collaborate2View());
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
	}

	public void setCacheable(final boolean cacheState) {
		_cacheable = cacheState;
	}

	//	@Deprecated
	//	public void createPart4Node(final AbstractAndroidNode node) {
	//		if (node instanceof ShipLocation) {
	//			LocationIndustryPart locpart = new LocationIndustryPart(node);
	//			locpart.setContainerLocation(false);
	//			_bodyParts.add(locpart);
	//			return;
	//		}
	//		if (node instanceof Separator) {
	//			TerminatorPart gp = new TerminatorPart(node);
	//			gp.setRenderMode(getVersion());
	//			_bodyParts.add(gp);
	//			return;
	//		}
	//	}
	public void setDataModel(final RootNode root) {
		_dataModelRoot = root;
	}

	public SpecialDataSource setVariant(final EVARIANT variant) {
		_variant = variant;
		return this;
	}

	protected int getParameterInteger(final String name) {
		Object param = _parameters.get(name);
		if (null != param) if (param instanceof Integer) return ((Integer) param).intValue();
		return 0;
	}

	protected long getParameterLong(final String name) {
		Object param = _parameters.get(name);
		if (null != param) if (param instanceof Long) return ((Long) param).longValue();
		return 0;
	}

	protected String getParameterString(final String name) {
		Object param = _parameters.get(name);
		if (null != param) if (param instanceof String) return (String) param;
		return "";
	}
}

// - UNUSED CODE ............................................................................................

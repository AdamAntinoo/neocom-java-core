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
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.core.RootPart;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.constant.CVariant;
import org.dimensinfin.evedroid.constant.CVariant.EDefaultVariant;
import org.dimensinfin.evedroid.enums.EVARIANT;
import org.dimensinfin.evedroid.interfaces.IExtendedDataSource;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class implements the most common code and flow for all DataSources to allow the best code
 * generalization. This class has the common code to make the model transformation to the Part hierarchy and
 * from it to the list of Parts used to renden the view.
 * 
 * @author Adam Antinoo
 */
public abstract class SpecialDataSource extends AbstractDataSource implements IExtendedDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long							serialVersionUID	= -9083587546700227219L;
	public static Logger									logger						= Logger.getLogger("SpecialDataSource");

	// - F I E L D - S E C T I O N ............................................................................
	private DataSourceLocator							_locator					= null;
	private String												_variant					= CVariant
			.getName4Variant(EDefaultVariant.DEFAULT_VARIANT.hashCode());
	private boolean												_cacheable				= true;
	private final HashMap<String, Object>	_parameters				= new HashMap<String, Object>();
	protected IPartFactory								_partFactory			= null;

	/** The initial node where to store the model. Model elements are children of this root. */
	protected RootNode										_dataModelRoot		= null;
	/** The root node for the Part hierarchy that matches the data model hierarchy. */
	protected RootPart										_partModelRoot		= null;
	/** The list of Parts to show on the viewer. This is the body section that is scrollable. */
	protected ArrayList<IPart>						_bodyParts				= new ArrayList<IPart>();
	/** The list of Parts to show on the header. */
	protected ArrayList<IPart>						_headParts				= new ArrayList<IPart>();
	private DataSourceManager							_dsManager;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public SpecialDataSource(final DataSourceLocator locator, final IPartFactory factory) {
		_locator = locator;
		_partFactory = factory;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
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
			_bodyParts = new ArrayList<IPart>();
			// Select for the body contents only the viewable Parts from the Part model. Make it a list.
			_bodyParts.addAll(_partModelRoot.collaborate2View());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SpecialDataSource.logger.info("<< [SpecialDataSource.createContentHierarchy]");
	}

	//	/**
	//	 * This is the method to initialize the copy of the model structures on the datasource. Every time this
	//	 * method is called, the complete model is recreated. There are two ways to recreate it, comparing with the
	//	 * old copy and inserting/deleting different nodes or recreating completely the new model copy. Once this
	//	 * method is called we can create the depending part hierarchy. <br>
	//	 * I have to search for a better name for this method. This is not clear and currently the
	//	 * <code>initModel</code> is already on use by the class but can be reused later.
	//	 */
	//	@Deprecated
	//	public void createPartsHierarchy() {
	//	}

	/**
	 * Return just the list of viewable Parts. During the composition of the list we transform it of class
	 * because we should change the final class level returned to the higher level possible and now for
	 * compatibility we keep the <code>AbstractAndroidPart</code>.
	 */
	public ArrayList<AbstractAndroidPart> getBodyParts() {
		// Get the list of Parts that will be used for the ListView
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		if (null != _bodyParts) // Transform the list of IParts to a list of AbstractAndroidParts.
			for (IPart part : _bodyParts)
			if (part instanceof AbstractAndroidPart) result.add((AbstractAndroidPart) part);
		return result;
	}

	public DataSourceLocator getDataSourceLocator() {
		return _locator;
	}

	/**
	 * This method is also deprecated because the Part generation for the header is kept outside the DataSource.
	 * The management of the header is now performed at the Fragment level and once it is changed to another
	 * level we can check this code. This method is kept for backward compatibility.
	 */
	@Deprecated
	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		for (IPart node : _headParts)
			result.add((AbstractAndroidPart) node);
		return result;
	}

	//	@Override
	//	@Deprecated
	//	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
	//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		for (AbstractAndroidPart node : this.getBodyParts())
	//			result.add(node);
	//		return result;
	//	}

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
		// The expand/collapse state has changed.
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
			_bodyParts = new ArrayList<IPart>();
			_bodyParts.addAll(_partModelRoot.collaborate2View());
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
			return;
		}
		// TODO Check if we should get this event and fire it again.
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES)) {
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
			return;
		}
		super.propertyChange(event);
	}

	public void setCacheable(final boolean cacheState) {
		_cacheable = cacheState;
	}

	//[01]
	public void setDataModel(final RootNode root) {
		_dataModelRoot = root;
	}

	public SpecialDataSource setVariant(final String variant) {
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
//[01]
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

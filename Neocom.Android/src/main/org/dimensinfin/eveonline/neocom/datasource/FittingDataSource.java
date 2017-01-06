//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.datasource;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.Action;
import org.dimensinfin.eveonline.neocom.model.Fitting;
import org.dimensinfin.eveonline.neocom.model.Separator;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

/**
 * This DataSource will need some inputs to create the list of actions to be performed to complete the number
 * of fittings required. We need a number of hulls, modules and other items to complete the fit. During the
 * manufacturing process the user can select manufacturing or refining instead of the deafult BUY action. <br>
 * From the list of items that make the fit, multiplied by the number of fits to complete we should get a list
 * of actions to get all that components to the selected destination station. <br>
 * We need a Fitting, a Capsuleer for the list of assets, a number of ships to get at the final stage and some
 * configuration records that define the preferred action for each of the items on the manufacturing chain.
 * The first level nodes are Actions, then an Action has a connection to an item and to a cound of that item
 * (this is represented as a Resource). <br>
 * We can use the same concepts used on the Blueprint manufacturing result.
 * 
 * @author Adam Antinoo
 */
//- CLASS IMPLEMENTATION ...................................................................................
public class FittingDataSource extends SpecialDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 7810087592108417570L;
	private static Logger			logger						= Logger.getLogger("FittingDataSource");

	// - F I E L D - S E C T I O N ............................................................................
	private Fitting						fit								= null;

	//- C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingDataSource(final DataSourceLocator locator, final IPartFactory factory) {
		super(locator, factory);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public RootNode collaborate2Model() {
		FittingDataSource.logger.info(">> [FittingDataSource.collaborate2Model]");
		try {
			this.initDataSourceModel();

			// Add the classification groups and the get the first level model. The model elements are added to the
			// right group depending on their properties.
			_dataModelRoot = new RootNode();
			this.doGroupInit();
			ArrayList<AbstractComplexNode> modelList = fit
					.collaborate2Model(AppWideConstants.EFragment.FITTING_MANUFACTURE.name());
			this.classifyModel(modelList);
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
			FittingDataSource.logger.severe(
					"RTEX> FittingDataSource.collaborate2Model-There is a problem with the access to the Assets database when getting the Manager.");
		}
		FittingDataSource.logger.info("<< [FittingDataSource.collaborate2Model]");
		return _dataModelRoot;
	}

	/**
	 * Returns the header root element that contains the header elements to show on the Activity.<br>
	 * For this implementation we just return the fitting that is the only element to include on the head.
	 */
	public RootNode getHeaderModel() {
		this.initDataSourceModel();
		RootNode root = new RootNode();
		root.addChild(fit);
		return root;
	}

	private void add2Group(final AbstractComplexNode action, final EIndustryGroup igroup) {
		for (IGEFNode group : _dataModelRoot.getChildren())
			if (group instanceof Separator)
				if (((Separator) group).getTitle().equalsIgnoreCase(igroup.toString())) group.addChild(action);
	}

	/**
	 * Installs each of the model nodes into the corresponding group depending on the Category.
	 * 
	 * @param modelList
	 * @return
	 */
	private void classifyModel(final ArrayList<AbstractComplexNode> modelList) {
		FittingDataSource.logger.info(">> [FittingDataSource.classifyModel]");
		for (AbstractComplexNode node : modelList)
			if (node instanceof Action) {
				Action action = (Action) node;
				this.add2Group(action, action.getResource().getItem().getIndustryGroup());
			}
		FittingDataSource.logger.info("<< [FittingDataSource.classifyModel]");
	}

	private Fitting createTestFitting(final AssetsManager manager) {
		Fitting onConstructionFit = new Fitting(manager);
		onConstructionFit.addHull(11184);
		onConstructionFit.fitModule(6719, 4);
		onConstructionFit.fitModule(5973);
		onConstructionFit.fitModule(5405);
		onConstructionFit.fitModule(5839);
		onConstructionFit.fitModule(5849);
		onConstructionFit.fitModule(11563);
		onConstructionFit.fitModule(33076);
		onConstructionFit.fitRig(26929);
		onConstructionFit.fitRig(26929);
		onConstructionFit.addCargo(244, 4);
		onConstructionFit.addCargo(240, 4);
		return onConstructionFit;
	}

	private void doGroupInit() {
		boolean renderEmptyState = false;
		_dataModelRoot.addChild(new Separator(EIndustryGroup.SKILL.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.BLUEPRINT.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.HULL.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.COMPONENTS.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.CHARGE.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.COMMODITY.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.REFINEDMATERIAL.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.SALVAGEDMATERIAL.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.DATACORES.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.DATAINTERFACES.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.DECRIPTORS.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.MINERAL.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.ITEMS.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot
				.addChild(new Separator(EIndustryGroup.PLANETARYMATERIALS.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot
				.addChild(new Separator(EIndustryGroup.REACTIONMATERIALS.name()).setRenderWhenEmpty(renderEmptyState));
		_dataModelRoot.addChild(new Separator(EIndustryGroup.UNDEFINED.name()).setRenderWhenEmpty(renderEmptyState));
	}

	/**
	 * The data model exported by this method can have two or three levels. If the region grouping is off then
	 * we return the list of locations that contain ships. If the Region grouping is on then we return the list
	 * of Regions that point also to the contained Locations. <br>
	 * The DataSource keeps the list of ships and compares it to the current list so if it is the same then we
	 * do not do any processing. <br>
	 * There are two models that can be returned, the ships by Location model and also the ship by Category.
	 * Both are generated at the same time. <br>
	 * The first action is to go to the Pilot asset list and get all the assets with the Category Ship. This
	 * will return a list of Assets we can transform into Ships. There are two classes for this. The packaged
	 * ships are simple assets that will not expand to anything else while the other class, the active ships can
	 * have contents and a fit. That ones are the ones being converted to Ships. <br>
	 * This new ships will inherit the content management properties of a Container and some of the logic of the
	 * ShipPart.
	 * 
	 * @return
	 */
	private void initDataSourceModel() {
		AppModelStore store = AppModelStore.getSingleton();
		long capsuleerId = this.getParameterLong(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name());
		String fittingLabel = this.getParameterString(AppWideConstants.EExtras.EXTRA_FITTINGID.name());
		final AssetsManager manager = store.getPilot().getAssetsManager();
		if (capsuleerId == 0)
			fit = this.createTestFitting(manager);
		else
			fit = store.searchFitting(fittingLabel);
		if (null == fit) fit = this.createTestFitting(manager);
	}
}
// - UNUSED CODE ............................................................................................

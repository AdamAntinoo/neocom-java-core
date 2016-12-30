//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.datasource;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;

import org.dimensinfin.android.mvc.activity.PagerFragment;
import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.core.AppContext;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.NeoComApp;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.industry.EJobClasses;
import org.dimensinfin.eveonline.neocom.industry.IJobProcess;
import org.dimensinfin.eveonline.neocom.industry.JobManager;
import org.dimensinfin.eveonline.neocom.model.Action;
import org.dimensinfin.eveonline.neocom.model.Separator;
import org.dimensinfin.eveonline.neocom.model.Skill;
import org.dimensinfin.eveonline.neocom.part.ActionPart;
import org.dimensinfin.eveonline.neocom.part.BlueprintPart;
import org.dimensinfin.eveonline.neocom.part.GroupPart;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * DataSource to retrieve the resources required to perform a T2 Manufacture job from a parameter stack. That
 * stack will contain the pack of T2 BPC to be used on the manufacture process. This is NOT a single blueprint
 * but a pack that can contain a set of equal or similar blueprints. We do not consider differences between
 * them and will use the first hit to search for the ME/TE parameters.<br>
 * The hierarchy are the Actions to complete the job pack. Those actions will contain Tasks that are completed
 * if there are enough resources at the blueprint location or other activities needed to complete the task and
 * then the action.<br>
 * The locations to take on account are the location where is located the blueprint stack.<br>
 * The resources are the character available resources at that location less the resources used on scheduled
 * jobs. Scheduled jobs are only valid at the <code>MANUFACTURE</code> location
 * 
 * @author Adam Antinoo
 */
public class IndustryT2ManufactureDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long		serialVersionUID	= -1904434849082581300L;

	// - F I E L D - S E C T I O N ............................................................................
	private BlueprintPart				_bppart						= null;
	private final PagerFragment	_fragment					= null;
	private final AppContext		_context					= null;
	private AppModelStore				_store						= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public IndustryT2ManufactureDataSource(final AppModelStore store) {
		super();
		if (null != store) _store = store;
	}

	public RootNode collaborate2Model() {
		// TODO Auto-generated method stub
		return null;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The hierarchy contains two levels of elements. The first level are the actions and the second level are
	 * the tasks to complete and fulfill those actions.
	 */
	@Override
	public void createContentHierarchy() {
		AbstractDataSource.logger.info(">> IndustryT2ManufactureDataSource.createHierarchy");
		// Clear the current list of elements.
		_root.clear();

		// Add The classification groups with their weights.
		_root.add(new GroupPart(new Separator(EIndustryGroup.SKILL.toString())).setPriority(800));
		_root.add(new GroupPart(new Separator(EIndustryGroup.BLUEPRINT.toString())).setPriority(100));
		_root.add(new GroupPart(new Separator(EIndustryGroup.REFINEDMATERIAL.toString())).setPriority(300));
		_root.add(new GroupPart(new Separator(EIndustryGroup.SALVAGEDMATERIAL.toString())).setPriority(300));
		_root.add(new GroupPart(new Separator(EIndustryGroup.COMPONENTS.toString())).setPriority(400));
		_root.add(new GroupPart(new Separator(EIndustryGroup.DATACORES.toString())).setPriority(400));
		_root.add(new GroupPart(new Separator(EIndustryGroup.DATAINTERFACES.toString())).setPriority(400));
		_root.add(new GroupPart(new Separator(EIndustryGroup.DECRIPTORS.toString())).setPriority(400));
		_root.add(new GroupPart(new Separator(EIndustryGroup.MINERAL.toString())).setPriority(500));
		_root.add(new GroupPart(new Separator(EIndustryGroup.ITEMS.toString())).setPriority(450));
		_root.add(new GroupPart(new Separator(EIndustryGroup.PLANETARYMATERIALS.toString())).setPriority(600));
		_root.add(new GroupPart(new Separator(EIndustryGroup.REACTIONMATERIALS.toString())).setPriority(700));

		// Check we have received the blueprint part from the Fragment.
		if (null == _bppart) throw new RuntimeException("Blueprint Part not defined on IndustryT2ManufactureDataSource.");
		// If there are children that means we have already created the tasks.
		if (_bppart.getChildren().size() > 0)
			return;
		else {
			// From the blueprint get the process to obtain the list of resources.
			IJobProcess process = JobManager.generateJobProcess(_store.getPilot(), _bppart.getCastedModel(),
					EJobClasses.MANUFACTURE);
			ArrayList<Action> actions = process.generateActions4Blueprint();
			for (Action action : actions) {
				ActionPart apart = new ActionPart(action);
				if (action instanceof Skill) apart.setRenderMode(3000);
				apart.createHierarchy();
				_bppart.addChild(apart);
			}
		}

		// Process the actions and set each one on the matching group.
		for (IPart action : _bppart.getChildren())
			if (action instanceof ActionPart) {
				String category = ((ActionPart) action).get_category();
				String group = ((ActionPart) action).get_group();
				if (group.equalsIgnoreCase("Tool")) {
					this.add2Group((ActionPart) action, EIndustryGroup.ITEMS);
					continue;
				}
				if (category.equalsIgnoreCase("Commodity")) {
					this.add2Group((ActionPart) action, EIndustryGroup.COMPONENTS);
					continue;
				}
				if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
					this.add2Group((ActionPart) action, EIndustryGroup.BLUEPRINT);
					continue;
				}
				if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
					this.add2Group((ActionPart) action, EIndustryGroup.SKILL);
					continue;
				}
				if (group.equalsIgnoreCase(ModelWideConstants.eveglobal.Mineral)) {
					this.add2Group((ActionPart) action, EIndustryGroup.REFINEDMATERIAL);
					continue;
				}
				if (category.equalsIgnoreCase("Module")) {
					this.add2Group((ActionPart) action, EIndustryGroup.COMPONENTS);
					continue;
				}
				if (category.equalsIgnoreCase("Planetary Commodities")) {
					this.add2Group((ActionPart) action, EIndustryGroup.PLANETARYMATERIALS);
					continue;
				}
				if (group.equalsIgnoreCase("Datacores")) {
					this.add2Group((ActionPart) action, EIndustryGroup.DATACORES);
					continue;
				}
				_root.add((AbstractAndroidPart) action);
			}
		AbstractDataSource.logger.info("<< IndustryT2ManufactureDataSource.createHierarchy [" + _root.size() + "]");
	}

	//	/**
	//	 * This datasource expects an asset id to retrieve the blueprint.
	//	 */
	//	public void processArguments(final Bundle arguments) {
	//		//Instantiate the pilot from the characterID.
	//		long characterid = arguments.getLong(AppWideConstants.extras.EXTRA_EVECHARACTERID);
	//		EveChar pilot = null;
	//		if (characterid > 0) pilot = EVEDroidApp.getAppStore().searchCharacter(characterid);
	//		if (null == pilot) throw new RuntimeException("Unable to continue. Required parameters not define on Extras.");
	//		long bpassetid = arguments.getLong(AppWideConstants.extras.EXTRA_BLUEPRINTID);
	//		if (bpassetid > 0) {
	//			Blueprint blueprint = pilot.getAssetsManager().searchBlueprintByID(bpassetid);
	//			setBlueprint(new BlueprintPart(blueprint));
	//		} else
	//			throw new RuntimeException("Unable to continue. Required parameters not define on Extras.");
	//	}

	@Override
	public ArrayList<AbstractAndroidPart> getBodyParts() {
		AbstractDataSource.logger.info(">> IndustryT2ManufactureDataSource.getPartHierarchy");
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		try {
			Collections.sort(_root, NeoComApp.createComparator(AppWideConstants.comparators.COMPARATOR_PRIORITY));
			for (AbstractAndroidPart node : _root) {
				if (node instanceof GroupPart) if (node.getChildren().size() == 0) continue;
				result.add(node);
				// Check if the node is expanded. Then add its children.
				if (node.isExpanded()) for (IPart part : node.collaborate2View())
					result.add((AbstractAndroidPart) part);
			}
		} catch (RuntimeException rtex) {
		}
		_adapterData = result;
		AbstractDataSource.logger.info("<< IndustryT2ManufactureDataSource.getPartHierarchy");
		return result;
	}

	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE))
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
	}

	public void setBlueprint(final BlueprintPart blueprintPart) {
		_bppart = blueprintPart;
	}

	private void add2Group(final ActionPart action, final EIndustryGroup igroup) {
		for (AbstractAndroidPart group : _root)
			if (group instanceof GroupPart)
				if (((GroupPart) group).getCastedModel().getTitle().equalsIgnoreCase(igroup.toString())) group.addChild(action);
	}

	//	private void taskCreation(final AbstractAndroidPart part) {
	//		if (part instanceof BlueprintPart) {
	//			((BlueprintPart) part).generateT2ManufactureTasks();
	//		} else {
	//			Vector<IGEFNode> ch = part.getChildren();
	//			for (IGEFNode node : ch) {
	//				taskCreation((AbstractAndroidPart) node);
	//			}
	//		}
	//	}
}
// - UNUSED CODE ............................................................................................

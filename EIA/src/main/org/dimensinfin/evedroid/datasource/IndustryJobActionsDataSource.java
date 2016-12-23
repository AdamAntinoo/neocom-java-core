//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.datasource;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Vector;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.enums.EIndustryGroup;
import org.dimensinfin.evedroid.industry.JobManager;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.interfaces.IItemPart;
import org.dimensinfin.evedroid.model.Action;
import org.dimensinfin.evedroid.model.NeoComBlueprint;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.model.Skill;
import org.dimensinfin.evedroid.part.ActionPart;
import org.dimensinfin.evedroid.part.BlueprintPart;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.part.JobTimePart;
import org.dimensinfin.evedroid.part.ResourcePart;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.util.Log;

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
public class IndustryJobActionsDataSource extends AbstractNewDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -1904434849082581300L;

	// - F I E L D - S E C T I O N ............................................................................
	//	private AppModelStore				_store						= null;
	//	private int									_flavor						= AppWideConstants.fragment.FRAGMENT_DEFAULTID_EMPTY;
	private BlueprintPart			_bppart						= null;

	//	private final PagerFragment	_fragment					= null;
	//	private final AppContext		_context					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public IndustryJobActionsDataSource(final AppModelStore store) {
		super(store);
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
		Log.i("EVEI", ">> IndustryJobActionsDataSource.createContentHierarchy");
		super.createContentHierarchy();
		// Check we have received the blueprint part from the Fragment.
		if (null == _bppart) throw new RuntimeException("Blueprint Part not defined on IndustryJobActionsDataSource.");
		// From the blueprint get the process to obtain the list of resources.
		// If there are children that means we have already created the tasks. Then skip the generation.
		if (_bppart.getChildren().size() < 1) {
			final ArrayList<Action> actions = _bppart.generateActions();
			for (final Action action : actions) {
				final ActionPart apart = new ActionPart(action);
				apart.setBlueprintID(_bppart.getCastedModel().getAssetID());
				if (action instanceof Skill) apart.setRenderMode(AppWideConstants.rendermodes.RENDER_SKILLACTION);
				apart.createHierarchy();
				_bppart.addChild(apart);
			}
		}

		// Depending on fragment generate the corresponding model.
		//		if (_flavor == AppWideConstants.fragment.FRAGMENT_INDUSTRYJOBACTIONS) {
		// Get the module item that is going to be produced.
		final NeoComBlueprint blueprint = _bppart.getCastedModel();
		// Add the manufacture time section.
		final JobTimePart time = new JobTimePart(new Separator(""));
		time.setRunTime(_bppart.getCycleTime());
		time.setRunCount(_bppart.getPossibleRuns());
		time.setActivity(_bppart.getJobActivity());
		_root.add(time);

		// Add The classification groups with their weights.
		final GroupPart output = (GroupPart) new GroupPart(new Separator(EIndustryGroup.OUTPUT.toString()))
				.setPriority(100);
		_root.add(output);
		// Add the rest of the groups.
		this.doGroupInit();
		// To the Output group add the resource part that represents the output.
		final int productID = _bppart.getProductID();
		final ResourcePart outputResource = new ResourcePart(new Resource(productID, _bppart.getPossibleRuns()));
		// Set the render depending on the blueprint job activity.
		if (_bppart.getJobActivity() == ModelWideConstants.activities.MANUFACTURING)
			outputResource.setRenderMode(AppWideConstants.rendermodes.RENDER_RESOURCEOUTPUTJOB);
		if (_bppart.getJobActivity() == ModelWideConstants.activities.INVENTION)
			outputResource.setRenderMode(AppWideConstants.rendermodes.RENDER_RESOURCEOUTPUTBLUEPRINT);
		output.addChild(outputResource);
		// Now classify each resource in their Industry group.
		this.classifyResources(_bppart.getChildren());
		//		}
		Log.i("EVEI", "<< IndustryJobActionsDataSource.createContentHierarchy [" + _root.size() + "]");
	}

	@Override
	public ArrayList<AbstractAndroidPart> getBodyParts() {
		AbstractDataSource.logger.info(">> IndustryT2ManufactureDataSource.getPartHierarchy");
		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		try {
			//		Collections.sort(_root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_PRIORITY));
			for (final AbstractAndroidPart node : _root) {
				if (node instanceof GroupPart) if (node.getChildren().size() == 0) continue;
				result.add(node);
				// Check if the node is expanded. Then add its children.
				if (node.isExpanded()) for (IPart part : node.collaborate2View())
					result.add((AbstractAndroidPart) part);
			}
		} catch (final RuntimeException rtex) {
		}
		_adapterData = result;
		AbstractDataSource.logger.info("<< IndustryT2ManufactureDataSource.getPartHierarchy");
		return result;
	}

	//	@Override
	//	public ArrayList<AbstractAndroidPart> getHeaderPartsHierarchy(final int panelMarketordersbody) {
	//		int productID = _bppart.getProductID();
	//		EveItem productItem = AppConnector.getDBConnector().searchItembyID(productID);
	//		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		result.add(new ItemHeader4IndustryPart(productItem));
	//		return result;
	//	}

	@Override
	public ArrayList<AbstractAndroidPart> getBodyPartsHierarchy(final int panelMarketordersbody) {
		return this.getBodyParts();
	}

	public BlueprintPart getBPPart() {
		return _bppart;
	}

	//	public ArrayList<AbstractAndroidPart> getHeaderPartHierarchy() {
	//		//		createHeaderContents();
	//		//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		int productID = _bppart.getProductID();
	//		EveItem productItem = AppConnector.getDBConnector().searchItembyID(productID);
	//		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		result.add(new ItemHeader4IndustryPart(productItem));
	//		return result;
	//		//		return result;
	//
	//	
	//	}
	public ArrayList<AbstractAndroidPart> getHeaderPartHierarchy() {
		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		result
				.add((AbstractAndroidPart) _bppart.setRenderMode(AppWideConstants.rendermodes.RENDER_BLUEPRINTINDUSTRYHEADER));
		return result;
	}

	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<AbstractAndroidPart> getHeaderPartsHierarchy(final int panelMarketordersbody) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE))
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_NEEDSREFRESH))
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE)) {
			// Clean all asset managers before restarting the action list.
			_bppart.clean();
			JobManager.initializeAssets(_store.getPilot());
			_bppart.setActivity(_bppart.getJobActivity());
			this.createContentHierarchy();
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
	}

	public void setBlueprint(final BlueprintPart blueprintPart) {
		_bppart = blueprintPart;
	}

	@Override
	protected void add2Group(final IItemPart action, final EIndustryGroup igroup) {
		for (final AbstractAndroidPart group : _root)
			if (group instanceof GroupPart)
				if (((GroupPart) group).getCastedModel().getTitle().equalsIgnoreCase(igroup.toString()))
					group.addChild((IPart) action);
	}

	@Override
	protected void classifyResources(final Vector<IPart> vector) {
		// Process the actions and set each one on the matching group.
		for (final IPart node : vector)
			if (node instanceof IItemPart) {
				final IItemPart action = (IItemPart) node;
				this.add2Group(action, action.getIndustryGroup());
			}
	}

	private void doGroupInit() {
		_root.add(new GroupPart(new Separator(EIndustryGroup.SKILL.toString())).setPriority(200));
		_root.add(new GroupPart(new Separator(EIndustryGroup.BLUEPRINT.toString())).setPriority(300));
		_root.add(new GroupPart(new Separator(EIndustryGroup.REFINEDMATERIAL.toString())).setPriority(400));
		_root.add(new GroupPart(new Separator(EIndustryGroup.SALVAGEDMATERIAL.toString())).setPriority(400));
		_root.add(new GroupPart(new Separator(EIndustryGroup.COMPONENTS.toString())).setPriority(500));
		_root.add(new GroupPart(new Separator(EIndustryGroup.DATACORES.toString())).setPriority(600));
		_root.add(new GroupPart(new Separator(EIndustryGroup.DATAINTERFACES.toString())).setPriority(600));
		_root.add(new GroupPart(new Separator(EIndustryGroup.DECRIPTORS.toString())).setPriority(600));
		_root.add(new GroupPart(new Separator(EIndustryGroup.MINERAL.toString())).setPriority(700));
		_root.add(new GroupPart(new Separator(EIndustryGroup.ITEMS.toString())).setPriority(800));
		_root.add(new GroupPart(new Separator(EIndustryGroup.PLANETARYMATERIALS.toString())).setPriority(900));
		_root.add(new GroupPart(new Separator(EIndustryGroup.REACTIONMATERIALS.toString())).setPriority(900));
		_root.add(new GroupPart(new Separator(EIndustryGroup.OREMATERIALS.toString())).setPriority(400));
		_root.add(new GroupPart(new Separator(EIndustryGroup.UNDEFINED.toString())).setPriority(999));
	}
}
// - UNUSED CODE ............................................................................................

//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.factory;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Vector;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.interfaces.IEditPart;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.enums.EIndustryGroup;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.interfaces.IItemPart;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.part.BlueprintPart;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.part.ItemHeader4IndustryPart;
import org.dimensinfin.evedroid.part.JobTimePart;
import org.dimensinfin.evedroid.part.ResourcePart;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.util.Log;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * The Data Source generates the hierarchy of resources required to produce an item. There are some kinds of
 * items. Some of them can be manufactured though a blueprint, other by refining, other come from reactions
 * and other only can be get from the market. The first action to be executed is to detect the type and then
 * the right job process to get the List Of Materials that are the information required from this DataSource.
 * <br>
 * The hierarchy has groups by resource type as shown on the EVE UI client.
 * 
 * @author Adam Antinoo
 */
public class IndustryLOMResourcesDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 5845417980378961177L;

	// - F I E L D - S E C T I O N ............................................................................
	private AppModelStore			_store						= null;
	private BlueprintPart			_bppart						= null;
	private double						balance						= 0.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public IndustryLOMResourcesDataSource(final AppModelStore store) {
		if (null != store) {
			_store = store;
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void createContentHierarchy() {
		Log.i("DataSource", ">> IndustryManufactureResourcesDataSource.createHierarchy");
		super.createContentHierarchy();

		// Check we have received the blueprint part from the Fragment.
		if (null == _bppart) throw new RuntimeException("Blueprint Part not defined on IndustryT2ManufactureDataSource.");
		// Add the manufacture time section.
		JobTimePart time = new JobTimePart(new Separator(""));
		time.setRunTime(_bppart.getCycleTime());
		time.setRunCount(Math.min(1, _bppart.getPossibleRuns()));
		time.setActivity(_bppart.getJobActivity());
		_root.add(time);

		// Add The classification groups with their weights.
		GroupPart output = (GroupPart) new GroupPart(new Separator(EIndustryGroup.OUTPUT.toString())).setPriority(100);
		_root.add(output);
		// Add the rest of the groups.
		doGroupInit();
		// To the Output group add the resource part that represents the output.
		ResourcePart outputResource = new ResourcePart(new Resource(_bppart.getProductID(), 1));
		// Set the render depending on the blueprint job activity.
		if (_bppart.getJobActivity() == ModelWideConstants.activities.MANUFACTURING) {
			outputResource.setRenderMode(AppWideConstants.rendermodes.RENDER_RESOURCEOUTPUTJOB);
		}
		if (_bppart.getJobActivity() == ModelWideConstants.activities.INVENTION) {
			outputResource.setRenderMode(AppWideConstants.rendermodes.RENDER_RESOURCEOUTPUTBLUEPRINT);
		}
		output.addChild(outputResource);

		// From the blueprint list of resources needed to perform the job.
		ArrayList<Resource> lom = _bppart.getLOM();
		balance = 0.0;
		// Process the list of materials to generate the Parts but remove some items not consumed.
		Vector<IGEFNode> lomParts = new Vector<IGEFNode>();
		for (Resource resource : lom) {
			String category = resource.item.getCategory();
			if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
				balance += 0.0;
			} else if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
				balance += 0.0;
			} else {
				double realcost = resource.getQuantity() * resource.getItem().getLowestSellerPrice().getPrice();
				balance += realcost;
			}
			// Process the actions and set each one on the matching group.
			ResourcePart respart = new ResourcePart(resource);
			if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
				respart.setRenderMode(AppWideConstants.rendermodes.RENDER_RESOURCESKILLJOB);
			}
			if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
				respart.setRenderMode(AppWideConstants.rendermodes.RENDER_RESOURCEBLUEPRINTJOB);
			}
			// Now classify each resource in their Industry group.
			if (respart instanceof IItemPart) {
				add2Group(respart, respart.getIndustryGroup());
			}
		}
	}

	public ArrayList<AbstractAndroidPart> getHeaderPartHierarchy() {
		int productID = _bppart.getProductID();
		EveItem productItem = AppConnector.getDBConnector().searchItembyID(productID);
		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		result.add(new ItemHeader4IndustryPart(productItem));
		return result;
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
		logger.info(">> IndustryManufactureResourcesDataSource.getPartHierarchy");
		//	Collections.sort(_root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_RESOURCE_TYPE));
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		for (AbstractAndroidPart node : _root) {
			if (node instanceof GroupPart) if (node.getChildren().size() == 0) {
				continue;
			}
			result.add(node);
			// Check if the node is expanded. Then add its children.
			if (node.isExpanded()) {
				ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
				result.addAll(grand);
			}
		}
		_adapterData = result;
		logger.info("<< IndustryManufactureResourcesDataSource.getPartHierarchy");
		return result;
	}

	public void setBlueprint(final BlueprintPart blueprintPart) {
		_bppart = blueprintPart;
	}

	protected void add2Group(final IItemPart action, final EIndustryGroup igroup) {
		for (AbstractAndroidPart group : _root) {
			if (group instanceof GroupPart) {
				if (((GroupPart) group).getCastedModel().getTitle().equalsIgnoreCase(igroup.toString())) {
					group.addChild((IEditPart) action);
				}
			}
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
		_root.add(new GroupPart(new Separator(EIndustryGroup.UNDEFINED.toString())).setPriority(999));
	}
}
// - UNUSED CODE ............................................................................................

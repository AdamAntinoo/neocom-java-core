//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.factory;

// - IMPORT SECTION .........................................................................................
import java.util.Vector;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.core.IEditPart;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.evedroid.core.EIndustryGroup;
import org.dimensinfin.evedroid.core.IItemPart;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.storage.AppModelStore;
// - CLASS IMPLEMENTATION ...................................................................................

//- CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractIndustryDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -774764072513063457L;
	// - F I E L D - S E C T I O N ............................................................................
	protected AppModelStore		_store						= null;
	protected GroupPart				_output						= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractIndustryDataSource(final AppModelStore store) {
		super();
		if (null != store) {
			_store = store;
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//	public void createContentHierarchy() {
	//		// Clear the current list of elements.
	//		_root.clear();
	//
	//		// Add The classification groups with their weights.
	//		_output = (IndustryGroupPart) new IndustryGroupPart(new Separator(EIndustryGroup.OUTPUT.toString()))
	//				.setPriority(100);
	//		_root.add(_output);
	//
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.SKILL.toString())).setPriority(200));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.BLUEPRINT.toString())).setPriority(300));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.REFINEDMATERIAL.toString())).setPriority(400));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.SALVAGEDMATERIAL.toString())).setPriority(400));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.COMPONENTS.toString())).setPriority(500));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.DATACORES.toString())).setPriority(600));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.DATAINTERFACES.toString())).setPriority(600));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.DECRIPTORS.toString())).setPriority(600));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.OREMATERIALS.toString())).setPriority(400));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.MINERAL.toString())).setPriority(700));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.ITEMS.toString())).setPriority(800));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.PLANETARYMATERIALS.toString())).setPriority(900));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.REACTIONMATERIALS.toString())).setPriority(900));
	//		_root.add(new IndustryGroupPart(new Separator(EIndustryGroup.UNDEFINED.toString())).setPriority(999));
	//	}

	protected void add2Group(final IItemPart action, final EIndustryGroup igroup) {
		for (AbstractAndroidPart group : _root) {
			if (group instanceof GroupPart) {
				if (((GroupPart) group).getCastedModel().getTitle().equalsIgnoreCase(igroup.toString())) {
					group.addChild((IEditPart) action);
				}
			}
		}
	}

	protected void classifyResources(final Vector<AbstractPropertyChanger> nodes) {
		// Process the actions and set each one on the matching group.
		for (AbstractPropertyChanger node : nodes) {
			if (node instanceof IItemPart) {
				IItemPart action = (IItemPart) node;
				add2Group(action, action.getIndustryGroup());
			}
		}
	}
}
// - UNUSED CODE ............................................................................................

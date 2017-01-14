//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.factory;

//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.activity.FittingListActivity.EFittingVariants;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.model.Action;
import org.dimensinfin.eveonline.neocom.model.EveTask;
import org.dimensinfin.eveonline.neocom.model.Fitting;
import org.dimensinfin.eveonline.neocom.model.Separator;
import org.dimensinfin.eveonline.neocom.model.Separator.ESeparatorType;
import org.dimensinfin.eveonline.neocom.part.ActionPart;
import org.dimensinfin.eveonline.neocom.part.FittingListPart;
import org.dimensinfin.eveonline.neocom.part.FittingPart;
import org.dimensinfin.eveonline.neocom.part.GroupPart;
import org.dimensinfin.eveonline.neocom.part.TaskPart;

// - CLASS IMPLEMENTATION ...................................................................................
public class FittingPartFactory extends PartFactory implements IPartFactory {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("FittingPartFactory");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingPartFactory(final String variantSelected) {
		super(variantSelected);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The method should create the matching part for the model received but there is no other place where we
	 * should create the next levels of the hierarchy. So we will create the part transformations here. <br>
	 * The creation can be controlled by the variant used when the factory is created. A single factory can
	 * create different sets of parts for different pages of the same activity that can also share the same
	 * DataSource.
	 */
	@Override
	public IPart createPart(final AbstractComplexNode node) {
		FittingPartFactory.logger.info("-- [FittingPartFactory.createPart]> Node class: " + node.getClass().getName());
		// Set of Parts for the list of fittings.
		if (this.getVariant() == EFittingVariants.FITTING_LIST.name()) {
			if (node instanceof Separator) {
				// These special separators can configure an specific icon.
				IPart part = null;
				switch (((Separator) node).getType()) {
					case SHIPTYPE_BATTLECRUISER:
						part = new GroupPart((Separator) node).setIconReference(R.drawable.groupbattlecruiser).setFactory(this)
								.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
						break;
					case SHIPTYPE_BATTLESHIP:
						part = new GroupPart((Separator) node).setIconReference(R.drawable.groupbattleship).setFactory(this)
								.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
						break;
					case SHIPTYPE_CAPITAL:
						part = new GroupPart((Separator) node).setIconReference(R.drawable.groupcapital).setFactory(this)
								.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
						break;
					case SHIPTYPE_CRUISER:
						part = new GroupPart((Separator) node).setIconReference(R.drawable.groupcruiser).setFactory(this)
								.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
						break;
					case SHIPTYPE_DESTROYER:
						part = new GroupPart((Separator) node).setIconReference(R.drawable.groupdestroyer).setFactory(this)
								.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
						break;
					case SHIPTYPE_FREIGHTER:
						part = new GroupPart((Separator) node).setIconReference(R.drawable.groupfreighter).setFactory(this)
								.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
						break;
					case SHIPTYPE_FRIGATE:
						part = new GroupPart((Separator) node).setIconReference(R.drawable.groupfrigate).setFactory(this)
								.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
						break;
					case EMPTY_FITTINGLIST:
						part = new GroupPart((Separator) node).setFactory(this)
								.setRenderMode(ESeparatorType.EMPTY_FITTINGLIST.name());
						break;
					default:
						part = new GroupPart((Separator) node).setFactory(this)
								.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);

				}
				return part;
			}
			if (node instanceof Fitting) {
				IPart part = new FittingListPart((Fitting) node);
				return part;
			}
		}
		// Set of Parts for the Manufacture Page for a selected fragment.
		if (this.getVariant() == EFittingVariants.FITTING_MANUFACTURE.name()) {
			if (node instanceof Action) {
				IPart part = new ActionPart(node);
				return part;
			}
			if (node instanceof EveTask) {
				IPart part = new TaskPart(node);
				return part;
			}
			if (node instanceof Separator) {
				IPart part = new GroupPart((Separator) node);
				return part;
			}
			// This is the part element for the Fitting that going in the head.
			if (node instanceof Fitting) {
				IPart part = new FittingPart((Fitting) node).setRenderMode(AppWideConstants.rendermodes.RENDER_FITTINGHEADER);
				return part;
			}
		}

		// If no part is trapped then result a NOT FOUND mark
		return new GroupPart(new Separator("-NO data-[" + node.getClass().getName() + "]-"));
	}
}

// - UNUSED CODE ............................................................................................

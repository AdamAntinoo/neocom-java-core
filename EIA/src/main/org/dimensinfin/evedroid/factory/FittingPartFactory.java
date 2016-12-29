//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.factory;

//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.activity.FittingListActivity.EFittingVariants;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.model.Action;
import org.dimensinfin.evedroid.model.EveTask;
import org.dimensinfin.evedroid.model.Fitting;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.part.ActionPart;
import org.dimensinfin.evedroid.part.FittingListPart;
import org.dimensinfin.evedroid.part.FittingPart;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.part.TaskPart;

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
				IPart part = new GroupPart((Separator) node).setFactory(this);
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

//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.AbstractNeoComNode;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.enums.EVARIANT;
import org.dimensinfin.evedroid.model.NeoComApiKey;
import org.dimensinfin.evedroid.render.APIKeyRender;

import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class ApiKeyPart extends EveAbstractPart implements OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -7718648590261849585L;
	private static Logger			logger						= Logger.getLogger("APIKeyPart");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ApiKeyPart(final AbstractComplexNode node) {
		super(node);
		this.getCastedModel().setExpanded(true);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * This method should get the visible elements on the chain hierarchy. We depend on the children list as the
	 * source for the initial hierarchy to be run for visible pieces.<br>
	 * The method returns the visible elements and for each visible element will call its own
	 * <code>collaborate2View</code> recursively to add all the visible items under this element hierarchy.
	 */
	@Override
	public ArrayList<AbstractAndroidPart> collaborate2View() {
		ApiKeyPart.logger.info(">> [ApiKeyPart.collaborate2View]");
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		Vector<AbstractPropertyChanger> ch = this.getChildren();
		for (AbstractPropertyChanger nodePart : ch)
			// Check if the element is visible
			if (nodePart instanceof AbstractNeoComNode) {
				AbstractNeoComNode neocomModel = (AbstractNeoComNode) nodePart;
				if (neocomModel.renderWhenEmpty()) {
					ApiKeyPart.logger.info("-- [ApiKeyPart.collaborate2View]> Adding node: " + neocomModel);
					result.add((AbstractAndroidPart) nodePart);
					// If the node is added to the result then give the children the opportinity to also be added.
					result.addAll(((AbstractAndroidPart) nodePart).collaborate2View());
				}
			}
		ApiKeyPart.logger.info("<< [ApiKeyPart.collaborate2View]");
		return result;
	}

	public String get_key() {
		return EveAbstractPart.keyFormatter.format(this.getCastedModel().getKey());
	}

	public String get_type() {
		return this.getCastedModel().getType();
	}

	public NeoComApiKey getCastedModel() {
		return (NeoComApiKey) this.getModel();
	}

	@Override
	public long getModelID() {
		return this.getCastedModel().getKey();
	}

	/**
	 * Returns the list of parts that are available for this node. If the node it is expanded then the list will
	 * include the children and any other grand children of this one. If the node is collapsed then the only
	 * result will be the node itself.
	 * 
	 * @return list of parts that are accessible for this node.
	 */
	@Override
	@Deprecated
	public ArrayList<AbstractAndroidPart> getPartChildren() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		//		result.add(this);
		// Check if expanded. Then add the list of children and their collaboration to the list.
		if (this.getCastedModel().isExpanded()) {
			Vector<AbstractPropertyChanger> ch = this.getChildren();
			// Order the chatacters by alphabetical name.
			Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));

			for (AbstractPropertyChanger node : ch) {
				// Convert the node to a part.
				AbstractAndroidPart part = (AbstractAndroidPart) node;
				result.addAll(part.collaborate2View());
				//			// Check if the node is expanded. Then add its children.
				//			if (part.isExpanded()) {
				//				ArrayList<AbstractAndroidPart> grand = part.getPartChildren();
				//				result.addAll(grand);
				//			}
			}
		}
		return result;
	}

	/**
	 * Manage a click on the key visible element. A click will toggle the expand/collapse state.
	 */
	public void onClick(final View view) {
		// Toggle location to show its contents.
		this.getCastedModel().toggleExpanded();
		this.fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("APIKeyPart [");
		buffer.append(this.getCastedModel());
		buffer.append(" ]");
		return buffer.toString();
	}

	@Override
	protected AbstractHolder selectHolder() {
		// Get the proper holder set for the render mode.
		if (this.getRenderMode() == EVARIANT.CAPSULEER_LIST.hashCode()) return new APIKeyRender(this, _activity);
		// If holder not located return a default view for a sample and modeless Part.
		return new DefaultRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................

//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

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
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.model.APIKey;
import org.dimensinfin.evedroid.render.APIKeyRender;

import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class APIKeyPart extends EveAbstractPart implements OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -7718648590261849585L;
	private static Logger			logger						= Logger.getLogger("APIKeyPart");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public APIKeyPart(final AbstractComplexNode node) {
		super(node);
		getCastedModel().setExpanded(true);
	}

	/**
	 * This method should get the visible elements on the chain hierarchy. We depend on the children list as the
	 * source for the initial hierarchy to be run for visible pieces.
	 */
	@Override
	public ArrayList<AbstractAndroidPart> collaborate2View() {
		return getPartChildren();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_key() {
		return keyFormatter.format(getCastedModel().getKeyID());
	}

	public String get_type() {
		return getCastedModel().getType();
	}

	public APIKey getCastedModel() {
		return (APIKey) getModel();
	}

	@Override
	public long getModelID() {
		return getCastedModel().getKeyID();
	}

	/**
	 * Returns the list of parts that are available for this node. If the node it is expanded then the list will
	 * include the children and any other grandchildren of this one. If the node is collapsed then the only
	 * result will be the node itself.
	 * 
	 * @return list of parts that are accesible for this node.
	 */
	@Override
	public ArrayList<AbstractAndroidPart> getPartChildren() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		//		result.add(this);
		// Check if expanded. Then add the list of children and their collaboration to the list.
		if (getCastedModel().isExpanded()) {
			Vector<AbstractPropertyChanger> ch = getChildren();
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

	public void onClick(final View view) {
		// Toggle location to show its contents.
		getCastedModel().toggleExpanded();
		//		getCastedModel().setExpanded(getCastedModel().isExpanded());
		fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
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
		// Get the proper holder from the render mode.
		return new APIKeyRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................

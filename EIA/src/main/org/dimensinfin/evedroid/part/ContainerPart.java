//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.holder.ContainerHolder;
import org.dimensinfin.evedroid.model.NeoComAsset;

import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class ContainerPart extends AssetPart implements OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -2462731579059844711L;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ContainerPart(final NeoComAsset node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Container may be user named so check if the user label is defined and then return that value.
	 */
	@Override
	public String get_assetName() {
		String userName = this.getCastedModel().getUserLabel();
		if (null == userName)
			return "#" + this.getCastedModel().getAssetID();
		else
			return userName;
	}

	public String get_containerCategory() {
		return this.getCastedModel().getItem().getName();
	}

	public String get_contentCount() {
		return EveAbstractPart.qtyFormatter.format(this.getChildren().size());
	}

	@Override
	public String getName() {
		return this.get_assetName();
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartChildren() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		Vector<AbstractPropertyChanger> ch = this.getChildren();
		Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));

		for (AbstractPropertyChanger node : ch) {
			// Convert the node to a part.
			AbstractAndroidPart part = (AbstractAndroidPart) node;
			result.add(part);
			// Check if the node is expanded. Then add its children.
			if (part.isExpanded()) {
				ArrayList<AbstractAndroidPart> grand = part.getPartChildren();
				result.addAll(grand);
			}
		}
		return result;
	}

	public int getTypeID() {
		return this.getCastedModel().getTypeID();
	}

	@Override
	public void onClick(final View view) {
		// Toggle location to show its contents.
		this.toggleExpanded();
		this.fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
	}

	public boolean onLongClick(final View target) {
		return false;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("ContainerPart [");
		buffer.append(this.get_assetName());
		buffer.append(" ]");
		return buffer.toString();
	}

	@Override
	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		if (this.getRenderMode() == AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION)
			return new ContainerHolder(this, _activity);
		return new ContainerHolder(this, _activity);
	}

}

// - UNUSED CODE ............................................................................................

//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.model;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.core.AbstractNeoComNode;
import org.dimensinfin.evedroid.interfaces.IAsset;

// - CLASS IMPLEMENTATION ...................................................................................
public class Container extends AbstractNeoComNode implements IAsset {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger		= Logger.getLogger("org.dimensinfin.evedroid.model");

	// - F I E L D - S E C T I O N ............................................................................
	private IAsset				delegate	= null;
	private long					pilotID		= 0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Container(long pilot) {
		pilotID = pilot;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The collaboration of the container is different form the one of an asset. It will aggregate to the output
	 * the list of the contents. <br>
	 * The Container can access the database to get its contents
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> result = new ArrayList<AbstractComplexNode>();
		ArrayList<Asset> contents = AppConnector.getDBConnector().searchAssetContainedAt(pilotID, this.getAssetID());
		// Classify the contents
		for (Asset node : contents) {
			result.add(node);
		}
		return result;
	}

	public Container copyFrom(final IAsset asset) {
		// Install the original asset in this instance as the delegate.
		delegate = asset;
		return this;
	}

	public long getAssetID() {
		return delegate.getAssetID();
	}

	public long getLocationID() {
		return delegate.getLocationID();
	}

	public String getOrderingName() {
		return delegate.getOrderingName();
	}

	public Asset getParentContainer() {
		return delegate.getParentContainer();
	}

	public long getParentContainerId() {
		return delegate.getParentContainerId();
	}

	public boolean hasParent() {
		return delegate.hasParent();
	}

	public boolean isContainer() {
		return delegate.isContainer();
	}

	public boolean isPackaged() {
		return delegate.isPackaged();
	}

	public boolean isShip() {
		return delegate.isShip();
	}

}

// - UNUSED CODE ............................................................................................

//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractNeoComNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.core.model.INodeModel;

/**
 * This class encapsulates the core Eve Online model into the adapter for the Android MVC implementation. This
 * requires to implement the methods and the interfaces for use of the Android Parts. <br>
 * Uses a delegate to call the references real Location.
 * 
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class ShipLocation extends AbstractNeoComNode implements INodeModel {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger						= Logger.getLogger("org.dimensinfin.evedroid.model");

	// - F I E L D - S E C T I O N ............................................................................
	private EveLocation		locationDelegate	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ShipLocation(final EveLocation delegate) {
		locationDelegate = delegate;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Ship locations collaborate to the model by adding all their children because we store there the items
	 * located at the selected real location.
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		results.addAll((Collection<? extends AbstractComplexNode>) locationDelegate.getChildren());
		return results;
	}

	@Override
	public void addChild(final IGEFNode child) {
		locationDelegate.addChild(child);
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener newListener) {
		locationDelegate.addPropertyChangeListener(newListener);
	}

	public boolean equals(final EveLocation obj) {
		return locationDelegate.equals(obj);
	}

	@Override
	public void clean() {
		locationDelegate.clean();
	}

	@Override
	public IGEFNode getParent() {
		return locationDelegate.getParent();
	}

	@Override
	public boolean collapse() {
		return locationDelegate.collapse();
	}

	@Override
	public boolean equals(final Object o) {
		return locationDelegate.equals(o);
	}

	@Override
	public Vector<IGEFNode> getChildren() {
		return locationDelegate.getChildren();
	}

	@Override
	public void firePropertyChange(final PropertyChangeEvent event) {
		locationDelegate.firePropertyChange(event);
	}

	public String getConstellation() {
		return locationDelegate.getConstellation();
	}

	public long getConstellationID() {
		return locationDelegate.getConstellationID();
	}

	public String getFullLocation() {
		return locationDelegate.getFullLocation();
	}

	public long getID() {
		return locationDelegate.getID();
	}

	@Override
	public void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		locationDelegate.firePropertyChange(propertyName, oldValue, newValue);
	}

	public String getName() {
		return locationDelegate.getName();
	}

	@Override
	public void fireStructureChange(final String propertyName, final Object oldState, final Object newState) {
		locationDelegate.fireStructureChange(propertyName, oldState, newState);
	}

	public String getRegion() {
		return locationDelegate.getRegion();
	}

	public long getRegionID() {
		return locationDelegate.getRegionID();
	}

	public String getSecurity() {
		return locationDelegate.getSecurity();
	}

	public double getSecurityValue() {
		return locationDelegate.getSecurityValue();
	}

	@Override
	public void fireStructureChange(final PropertyChangeEvent event) {
		locationDelegate.fireStructureChange(event);
	}

	public String getStation() {
		return locationDelegate.getStation();
	}

	@Override
	public boolean expand() {
		return locationDelegate.expand();
	}

	public long getStationID() {
		return locationDelegate.getStationID();
	}

	public String getSystem() {
		return locationDelegate.getSystem();
	}

	public long getSystemID() {
		return locationDelegate.getSystemID();
	}

	@Override
	public int hashCode() {
		return locationDelegate.hashCode();
	}

	@Override
	public String quote(final double value) {
		return locationDelegate.quote(value);
	}

	@Override
	public String quote(final int value) {
		return locationDelegate.quote(value);
	}

	@Override
	public String quote(final String value) {
		return locationDelegate.quote(value);
	}

	@Override
	public void removeChild(final IGEFNode child) {
		locationDelegate.removeChild(child);
	}

	@Override
	public void setDirty(final boolean dirtyState) {
		locationDelegate.setDirty(dirtyState);
	}

	@Override
	public void setParent(final IGEFNode newParent) {
		locationDelegate.setParent(newParent);
	}

	public void setConstellation(final String constellation) {
		locationDelegate.setConstellation(constellation);
	}

	@Override
	public boolean hasListeners(final String propertyName) {
		return locationDelegate.hasListeners(propertyName);
	}

	public void setConstellationID(final long constellationID) {
		locationDelegate.setConstellationID(constellationID);
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		locationDelegate.removePropertyChangeListener(listener);
	}

	public void setLocationID(final long stationID) {
		locationDelegate.setLocationID(stationID);
	}

	public void setRegion(final String region) {
		locationDelegate.setRegion(region);
	}

	public void setRegionID(final long regionID) {
		locationDelegate.setRegionID(regionID);
	}

	public void setSecurity(final String security) {
		locationDelegate.setSecurity(security);
	}

	public void setStation(final String station) {
		locationDelegate.setStation(station);
	}

	@Override
	public boolean isDownloaded() {
		return locationDelegate.isDownloaded();
	}

	@Override
	public boolean isExpanded() {
		return locationDelegate.isExpanded();
	}

	public void setSystem(final String system) {
		locationDelegate.setSystem(system);
	}

	@Override
	public boolean renderWhenEmpty() {
		return locationDelegate.renderWhenEmpty();
	}

	public void setSystemID(final long systemID) {
		locationDelegate.setSystemID(systemID);
	}

	public void setTypeID(final int typeID) {
		locationDelegate.setTypeID(typeID);
	}

	@Override
	public String toString() {
		return locationDelegate.toString();
	}

	@Override
	public void setDownloaded(final boolean downloaded) {
		locationDelegate.setDownloaded(downloaded);
	}

	@Override
	public boolean setExpanded(final boolean newState) {
		return locationDelegate.setExpanded(newState);
	}

	@Override
	public void setRenderWhenEmpty(final boolean renderWhenEmpty) {
		locationDelegate.setRenderWhenEmpty(renderWhenEmpty);
	}

	@Override
	public void toggleExpanded() {
		locationDelegate.toggleExpanded();
	}
}

// - UNUSED CODE ............................................................................................

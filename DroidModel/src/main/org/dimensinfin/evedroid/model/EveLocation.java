//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.evedroid.model;

import java.util.ArrayList;
import java.util.Collection;

import org.dimensinfin.core.model.AbstractComplexNode;
// - IMPORT SECTION .........................................................................................
import org.dimensinfin.core.model.AbstractGEFNode;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveLocation extends AbstractComplexNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 1522765618286937377L;

	// - F I E L D - S E C T I O N ............................................................................
	private long							stationID					= -1;
	private String						station						= "<STATION>";
	private long							systemID					= -1;
	private String						system						= "<SYSTEM>";
	private long							constellationID		= -1;
	private String						constellation			= "<CONSTELLATION>";
	private long							regionID					= -1;
	private String						region						= "<REGION>";
	private String						security					= "0.0";
	private int								typeID						= -1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveLocation() {
	}

	public EveLocation(final long locationID) {
		stationID = locationID;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean equals(final EveLocation obj) {
		if (!getRegion().equalsIgnoreCase(obj.getRegion())) return false;
		if (!getSystem().equalsIgnoreCase(obj.getSystem())) return false;
		if (!getStation().equalsIgnoreCase(obj.getStation())) return false;
		return true;
	}

	public String getConstellation() {
		return constellation;
	}
	/**
	 * Check if the Location has children and then add all them to the model.
	 */
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
//		if (renderWhenEmpty()) {
//			results.add(this);
//		}
//		if (isExpanded()) {
			results.addAll((Collection<? extends AbstractComplexNode>) getChildren());
//		}
		return results;
	}

	public long getConstellationID() {
		return constellationID;
	}

	public String getFullLocation() {
		return "[" + security + "] " + station + " - " + region + " > " + system;
	}

	public long getID() {
		return Math.max(Math.max(Math.max(stationID, systemID), constellationID), regionID);
	}

	/**
	 * This return some understandable location name. This is not valid for most locations that are not
	 * stations.
	 * 
	 * @return
	 */
	public String getName() {
		return system + " - " + station;
	}

	public String getRegion() {
		return region;
	}

	public long getRegionID() {
		return regionID;
	}

	public String getSecurity() {
		return security;
	}

	public double getSecurityValue() {
		try {
			return Double.parseDouble(security);
		} catch (RuntimeException rtex) {
		}
		return 0.0;
	}

	public String getStation() {
		return station;
	}

	public long getStationID() {
		return stationID;
	}

	public String getSystem() {
		return system;
	}

	public long getSystemID() {
		return systemID;
	}

	public void setConstellation(final String constellation) {
		this.constellation = constellation;
	}

	public void setConstellationID(final long constellationID) {
		this.constellationID = constellationID;
	}

	public void setLocationID(final long stationID) {
		this.stationID = stationID;
	}

	public void setRegion(final String region) {
		this.region = region;
	}

	public void setRegionID(final long regionID) {
		this.regionID = regionID;
	}

	public void setSecurity(final String security) {
		this.security = security;
	}

	public void setStation(final String station) {
		this.station = station;
	}

	public void setSystem(final String system) {
		this.system = system;
	}

	public void setSystemID(final long systemID) {
		this.systemID = systemID;
	}

	public void setTypeID(final int typeID) {
		this.typeID = typeID;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Location [");
		buffer.append("#").append(getID()).append(" ");
		buffer.append("[").append(getRegion()).append("] ");
		if (null != system) buffer.append("system: ").append(system).append(" ");
		if (null != station) buffer.append("station: ").append(station).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................

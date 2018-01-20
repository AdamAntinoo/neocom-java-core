//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.model;

//- IMPORT SECTION .........................................................................................
import com.beimin.eveapi.model.eve.Station;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import net.nikr.eve.jeveasset.data.Citadel;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;

import java.sql.SQLException;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class encapsulates the concept of Eve Location. There are different types of locations and the new CCP
 * release is adding even more. The first concept is Region. This is when the constellation and system are not
 * defined. The second level is constellation, then system and inside a system we can find some more elements. <br>
 * Once we have the system id we have to check if the Location is a point in space, a NPC station (in the CCP
 * database catalog), a corporation Outpost (in the list of outposts) or it can be a Player Structure. Now we
 * have to differentiate the player structures and also this can change because POS are going to be replaced
 * by Citadels. I have a reference to get the list of Citadels until that API entry point is available on the
 * new CCP api. <br>
 * Once we know the type then we check if on the database cache and add or update as needed.
 * 
 * @author Adam Antinoo
 */
@DatabaseTable(tableName = "Locations")
public class EveLocation extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 1522765618286937377L;

	// - F I E L D - S E C T I O N ............................................................................
	@JsonIgnore
	@DatabaseField(id = true, index = true)
	protected long						id								= -2;
	@DatabaseField
	protected long						stationID					= -1;
	@DatabaseField
	private String						station						= "SPACE";
	@DatabaseField
	protected long						systemID					= -1;
	@DatabaseField
	private String						system						= "UNKNOWN";
	@DatabaseField
	protected long						constellationID		= -1;
	@DatabaseField
	private String						constellation			= "Echo Cluster";
	@DatabaseField
	protected long						regionID					= -1;
	@DatabaseField
	private String						region						= "-DEEP SPACE-";
	@DatabaseField
	private String						security					= "0.0";
	@DatabaseField
	protected String					typeID						= ELocationType.UNKNOWN.name();
	public String							urlLocationIcon		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveLocation() {
		super();
		//		this.setDownloaded(false);
		jsonClass = "EveLocation";
	}

	public EveLocation(final long locationID) {
		this();
		id = locationID;
		stationID = locationID;
	}

	public EveLocation(final long citadelid, final Citadel cit) {
		this();
		try {
			final Dao<EveLocation, String> locationDao = ModelAppConnector.getSingleton().getDBConnector().getLocationDao();
			// calculate the ocationID from the sure item and update the rest of the fields.
			this.updateFromCitadel(citadelid, cit);
			id = citadelid;
			typeID = ELocationType.CITADEL.name();
			// Try to create the pair. It fails then  it was already created.
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			this.setDirty(true);
		}
	}

	/**
	 * Create a location from an Outpost read on the current list of player outposts.
	 * 
	 * @param out
	 */
	public EveLocation(final Outpost out) {
		this();
		try {
			final Dao<EveLocation, String> locationDao = ModelAppConnector.getSingleton().getDBConnector().getLocationDao();
			// Calculate the locationID from the source item and update the rest of the fields.
			this.updateFromSystem(out.getSolarSystem());
			id = out.getFacilityID();
			typeID = ELocationType.OUTPOST.name();
			this.setStation(out.getName());
			// Try to create the pair. It fails then  it was already created.
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			this.setDirty(true);
		}
	}

	public EveLocation(final Station station) {
		this();
		try {
			final Dao<EveLocation, String> locationDao = ModelAppConnector.getSingleton().getDBConnector().getLocationDao();
			// Calculate the locationID from the source item and update the rest of the fields.
			this.updateFromSystem(station.getSolarSystemID());
			id = station.getStationID();
			typeID = ELocationType.DEEP_SPACE.name();
			this.setStation(station.getStationName());
			// Try to create the pair. It fails then  it was already created.
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			this.setDirty(true);
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//	/**
	//	 * Locations do not collaborate to models because have no contents.
	//	 */
	//	public List<ICo> collaborate2Model(final String variant) {
	//		return new ArrayList<AbstractComplexNode>();
	//	}

	public boolean equals(final EveLocation obj) {
		if (!this.getRegion().equalsIgnoreCase(obj.getRegion())) return false;
		if (!this.getSystem().equalsIgnoreCase(obj.getSystem())) return false;
		if (!this.getStation().equalsIgnoreCase(obj.getStation())) return false;
		return true;
	}

	public String getConstellation() {
		return constellation;
	}

	public long getConstellationID() {
		return constellationID;
	}

	public String getFullLocation() {
		return "[" + security + "] " + station + " - " + region + " > " + system;
	}

	@JsonInclude
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

	public long getRealId() {
		return id;
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
		} catch (final RuntimeException rtex) {
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

	public ELocationType getTypeID() {
		return ELocationType.valueOf(typeID);
	}

	/**
	 * Downloads and caches the item icon from the CCP server. The new implementation check for special cases
	 * such as locations. Stations on locations have an image that can be downloaded from the same place.
	 */
	public String getUrlLocationIcon() {
		if (null == urlLocationIcon) {
			urlLocationIcon = "http://image.eveonline.com/Render/"
					+ ModelAppConnector.getSingleton().getCCPDBConnector().searchStationType(id) + "_64.png";
		}
		return urlLocationIcon;
	}

	public final boolean isCitadel() {
		if (this.getTypeID() == ELocationType.CITADEL) return true;
		return false;
	}

	public final boolean isRegion() {
		return ((this.getStationID() == 0) && (this.getSystemID() == 0) && (this.getRegionID() != 0));
	}

	public final boolean isStation() {
		return ((this.getStationID() != 0) && (this.getSystemID() != 0) && (this.getRegionID() != 0));
	}

	public final boolean isSystem() {
		return ((this.getStationID() == 0) && (this.getSystemID() != 0) && (this.getRegionID() != 0));
	}

	@JsonIgnore
	public final boolean isUnknown() {
		return (id == -2);
	}

	public void setConstellation(final String constellation) {
		this.constellation = constellation;
	}

	public void setConstellationID(final long constellationID) {
		this.constellationID = constellationID;
	}

	public void setDirty(final boolean state) {
		if (state) {
			try {
				final Dao<EveLocation, String> locationDao = ModelAppConnector.getSingleton().getDBConnector().getLocationDao();
				locationDao.update(this);
				//		logger.finest("-- Wrote blueprint to database id [" + blueprint.getAssetID() + "]");
			} catch (final SQLException sqle) {
				//		logger.severe("E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. " + sqle.getMessage());
				sqle.printStackTrace();
			}
		}
	}

	public void setId(final long newid) {
		id = newid;
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

	public void setStationID(final long stationID) {
		this.stationID = stationID;
	}

	public void setSystem(final String system) {
		this.system = system;
	}

	public void setSystemID(final long systemID) {
		this.systemID = systemID;
	}

	public void setTypeID(final ELocationType typeID) {
		this.typeID = typeID.name();
	}

	public void setUrlLocationIcon(final String urlLocationIcon) {
		this.urlLocationIcon = urlLocationIcon;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("NeoComLocation [");
		buffer.append("#").append(this.getID()).append(" ");
		//		buffer.append("(").append(this.getContents(false).size()).append(") ");
		buffer.append("[").append(this.getRegion()).append("] ");
		if (null != system) {
			buffer.append("system: ").append(system).append(" ");
		}
		if (null != station) {
			buffer.append("station: ").append(station).append(" ");
		}
		buffer.append("]");
		return buffer.toString();
	}

	private void updateFromCitadel(final long newid, final Citadel cit) {
		this.updateFromSystem(cit.systemId);
		// Copy the data from the citadel location.
		stationID = newid;
		station = cit.name;
		systemID = cit.systemId;
		//		citadel = true;
	}

	private void updateFromSystem(final long newid) {
		// Get the system information from the CCP location tables.
		final EveLocation systemLocation = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(newid);
		systemID = systemLocation.getSystemID();
		system = systemLocation.getSystem();
		constellationID = systemLocation.getConstellationID();
		constellation = systemLocation.getConstellation();
		regionID = systemLocation.getRegionID();
		region = systemLocation.getRegion();
		security = systemLocation.getSecurity();
	}
}

// - UNUSED CODE ............................................................................................

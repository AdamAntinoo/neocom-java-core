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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.dimensinfin.android.model.AbstractViewableNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.enums.ENeoComVariants;

import com.beimin.eveapi.model.eve.Station;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import net.nikr.eve.jeveasset.data.Citadel;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class encapsulates the concept of Eve Location. There are different types of locations and the new CCP
 * release is adding even more. The first concept is Region. This is when the constellation and system are not
 * defined. The second level is constellation, then system and inside a system we can find some more elements.
 * <br>
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
public class EveLocation extends AbstractViewableNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 1522765618286937377L;

	// - F I E L D - S E C T I O N ............................................................................
	@JsonIgnore
	@DatabaseField(id = true, index = true)
	private long							id								= -2;
	@DatabaseField
	private long							stationID					= -1;
	@DatabaseField
	private String						station						= "SPACE";
	@DatabaseField
	private long							systemID					= -1;
	@DatabaseField
	private String						system						= "UNKNOWN";
	@DatabaseField
	private long							constellationID		= -1;
	@DatabaseField
	private String						constellation			= "Echo Cluster";
	@DatabaseField
	private long							regionID					= -1;
	@DatabaseField
	private String						region						= "-DEEP SPACE-";
	@DatabaseField
	private String						security					= "0.0";
	@DatabaseField
	protected String					typeID						= ELocationType.UNKNOWN.name();
	//	@DatabaseField
	//	protected String structureName="-NOT-STRUCTURE-";
	protected boolean					citadel						= false;
	public String							urlLocationIcon		= null;
	@JsonIgnore
	private List<NeoComAsset>	contents					= new Vector<NeoComAsset>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveLocation() {
		this.setDownloaded(false);
		this.setRenderWhenEmpty(false);
		jsonClass = "EveLocation";
	}

	public EveLocation(final long locationID) {
		this();
		stationID = locationID;
	}

	public EveLocation(final long citadelid, final Citadel cit) {
		this();
		try {
			Dao<EveLocation, String> locationDao = ModelAppConnector.getSingleton().getDBConnector().getLocationDAO();
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
			Dao<EveLocation, String> locationDao = ModelAppConnector.getSingleton().getDBConnector().getLocationDAO();
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
			Dao<EveLocation, String> locationDao = ModelAppConnector.getSingleton().getDBConnector().getLocationDAO();
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
	/**
	 * Intercept the use of the children and store the data on the location contents.
	 */
	@Deprecated
	@Override
	public void addChild(final IGEFNode child) {
		//	this.clean();
		if (child instanceof NeoComAsset) {
			contents.add((NeoComAsset) child);
		}
	}

	public void addContent(final NeoComAsset asset) {
		contents.add(asset);
	}

	/**
	 * Locations collaborate to the model by adding all their contents if already downloaded. If not downloaded
	 * but are being expanded then we should first download all their contents and process them into the model
	 * before generating a new collaboration hierarchy.<br>
	 * During the obtention of the contents we check the download state to download the items if not already
	 * done.
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		if (this.isExpanded()) {
			if (this.isDownloaded()) {
				results.addAll(contents);
			} else {
				//				if (download) {
				if (variant == ENeoComVariants.ASSETS_BYLOCATION.name()) {
					contents = ModelAppConnector.getSingleton().getDBConnector().queryLocationContents(id);
				}
				if (variant == ENeoComVariants.PLANETARY_BYLOCATION.name()) {
					contents = ModelAppConnector.getSingleton().getDBConnector().queryLocationPlanetaryContents(id);
				}
				this.setDownloaded(true);
				results.addAll(contents);
			}
		}
		return results;
	}

	public boolean equals(final EveLocation obj) {
		if (!this.getRegion().equalsIgnoreCase(obj.getRegion())) return false;
		if (!this.getSystem().equalsIgnoreCase(obj.getSystem())) return false;
		if (!this.getStation().equalsIgnoreCase(obj.getStation())) return false;
		return true;
	}

	/**
	 * Intercept this call and return the contents.
	 * 
	 */
	@Deprecated
	@Override
	public Vector<IGEFNode> getChildren() {
		Vector<IGEFNode> result = new Vector<IGEFNode>();
		for (NeoComAsset neoComAsset : contents) {
			result.add(neoComAsset);
		}
		return result;
	}

	public String getConstellation() {
		return constellation;
	}

	public long getConstellationID() {
		return constellationID;
	}

	public int getContentCount() {
		if (this.isExpanded()) {
			if (this.isDownloaded())
				return contents.size();
			else
				return ModelAppConnector.getSingleton().getDBConnector().totalLocationContentCount(this.getID());
		}
		return contents.size();
	}

	/**
	 * This operation should control the download state for the contents of this location. If the Location is
	 * not downloaded the result is an empty list but if the flag is set then it should fire the download code
	 * to get the list of elements stored at this Location.
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<NeoComAsset> getContents() {
		return this.getContents(false);
	}

	/**
	 * The identifier to get the contents can change depending on the Locattion type. I have found that for
	 * Citadels the resources are under the <code>parentAssetID</code> and not the <code>locationID</code>.
	 * 
	 * @param download
	 * @return
	 */
	@JsonIgnore
	public List<NeoComAsset> getContents(final boolean download) {
		if (this.isDownloaded())
			return contents;
		else {
			if (download) {
				contents = ModelAppConnector.getSingleton().getDBConnector().queryLocationContents(id);
				this.setDownloaded(true);
			}
			return contents;
		}
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

	public ELocationType getTypeID() {
		return ELocationType.valueOf(typeID);
	}

	/**
	 * Downloads and caches the item icon from the CCP server. The new implementation check for special cases
	 * such as locations. Stations on locations have an image that can be downloaded from the same place.
	 * 
	 * @param targetIcon
	 * @param typeID
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

	@Override
	public boolean isEmpty() {
		if (this.isDownloaded())
			return (contents.size() > 0) ? false : true;
		else
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

	public void setCitadel(final boolean citadel) {
		this.citadel = citadel;
	}

	public void setConstellation(final String constellation) {
		this.constellation = constellation;
	}

	public void setConstellationID(final long constellationID) {
		this.constellationID = constellationID;
	}

	@Override
	public void setDirty(final boolean state) {
		if (state) {
			try {
				Dao<EveLocation, String> locationDao = ModelAppConnector.getSingleton().getDBConnector().getLocationDAO();
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
		//		setDirty(true);
	}

	public void setRegion(final String region) {
		this.region = region;
		//		setDirty(true);
	}

	public void setRegionID(final long regionID) {
		this.regionID = regionID;
		//		setDirty(true);
	}

	public void setSecurity(final String security) {
		this.security = security;
		//		setDirty(true);
	}

	public void setStation(final String station) {
		this.station = station;
		//		setDirty(true);
	}

	public void setStationID(final long stationID) {
		this.stationID = stationID;
	}

	public void setSystem(final String system) {
		this.system = system;
		//		setDirty(true);
	}

	public void setSystemID(final long systemID) {
		this.systemID = systemID;
		//		setDirty(true);
	}

	public void setTypeID(final ELocationType typeID) {
		this.typeID = typeID.name();
		//		setDirty(true);
	}

	public void setUrlLocationIcon(final String urlLocationIcon) {
		this.urlLocationIcon = urlLocationIcon;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("NeoComLocation [");
		buffer.append("#").append(this.getID()).append(" ");
		buffer.append("(").append(this.getContents(false).size()).append(") ");
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

	private void updateFromCitadel(final long id, final Citadel cit) {
		this.updateFromSystem(cit.systemId);
		// Copy the data from the citadel location.
		stationID = id;
		station = cit.name;
		systemID = cit.systemId;
		citadel = true;
	}

	private void updateFromSystem(final long id) {
		// Get the system information from the CCP location tables.
		EveLocation systemLocation = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(id);
		systemID = systemLocation.getSystemID();
		system = systemLocation.getSystem();
		constellationID = systemLocation.getConstellationID();
		constellation = systemLocation.getConstellation();
		regionID = systemLocation.getRegionID();
		region = systemLocation.getRegion();
		security = systemLocation.getSecurity();
	}

	private void updateLocationID() {
		if (citadel) {
			id = stationID;
		} else {
			id = this.getID();
		}
	}
}

// - UNUSED CODE ............................................................................................

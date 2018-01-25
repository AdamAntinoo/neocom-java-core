//  PROJECT:     NeoCom.Android (NEOC.A)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API22.
//  DESCRIPTION: Android Application related to the Eve Online game. The purpose is to download and organize
//               the game data to help capsuleers organize and prioritize activities. The strong points are
//               help at the Industry level tracking and calculating costs and benefits. Also the market
//               information update service will help to identify best prices and locations.
//               Planetary Interaction and Ship fittings are point under development.
//               ESI authorization is a new addition that will give continuity and allow download game data
//               from the new CCP data services.
//               This is the Android application version but shares libraries and code with other application
//               designed for Spring Boot Angular 4 platform.
//               The model management is shown using a generic Model View Controller that allows make the
//               rendering of the model data similar on all the platforms used.
package org.dimensinfin.eveonline.neocom.database.entity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.database.NeoComDatabase;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok.PlanetTypeEnum;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOkPins;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComExpandableNode;
import org.dimensinfin.eveonline.neocom.planetary.ColonyCoreStructure;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the equivalent model on the NeoCom MVC for the Colony list that are the planets that have the current
 * capsuleer colonies. I ave added some mode code to transform the code to real data to complement the original
 * information from swagger OK classes.
 *
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Colony")
public class Colony extends NeoComExpandableNode /*implements IDownloadable*/ {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(Colony.class);

	private static final ModelMapper modelMapper = new ModelMapper();

	static {
		modelMapper.getConfiguration()
		           .setFieldMatchingEnabled(true)
		           .setMethodAccessLevel(AccessLevel.PRIVATE);
	}

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true, index = true)
	private Integer planetId = null;
	@DatabaseField(index = true)
	private Integer solarSystemId = null;
	@DatabaseField(dataType = DataType.ENUM_STRING)
	private PlanetTypeEnum planetType = null;
	@DatabaseField
	private Integer ownerId = null;
	@DatabaseField
	private Integer upgradeLevel = null;
	@DatabaseField
	private Integer numPins = null;
	@DatabaseField
	private DateTime lastUpdate = null;
	@DatabaseField
	private String planetName = "Planet 0";
	private transient EveLocation location = null;
	//	private transient GetUniversePlanetsPlanetIdOk planetData = null;
	private transient GetCharactersCharacterIdPlanetsPlanetIdOk structureData = null;
	private transient List<GetCharactersCharacterIdPlanetsPlanetIdOkPins> pins = new ArrayList<GetCharactersCharacterIdPlanetsPlanetIdOkPins>();
	private transient List<ColonyCoreStructure> structures = null;

	protected boolean downloaded = true;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Colony () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	@Override
	public List<ICollaboration> collaborate2Model (final String variant) {
		List<ICollaboration> results = new ArrayList<>();
		if ( this.isDownloaded() ) {
			if ( null != structures )
				results.addAll(structures);
		} else {
			if ( isExpanded() ) {
				// Download the data. The node is expanded and not downloaded.
				structures = downloadStructures();
			}
		}
		return results;
	}

	private List<ColonyCoreStructure> downloadStructures () {
		setDownloaded(true);
		return GlobalDataManager.downloadStructures4Colony(ownerId, planetId);
	}

	/**
	 * Detect that the data is downloaded if the flag is set and the list of structures is loaded.
	 *
	 * @return
	 */
	public boolean isDownloaded () {
		if ( (downloaded) && (null != structures) ) return true;
		return false;
	}

	//	@Override
	//	public boolean isDownloading () {
	//		return false;
	//	}

	public Colony setDownloaded (final boolean set) {
		downloaded = set;
		return this;
	}

	//	@Override
	//	public IDownloadable setDownloading (final boolean b) {
	//		return this;
	//	}

	//	/**
	//	 * Check that the structures are available at the node or get them from the Global if they are not.
	//	 */
	//
	//	private void downloadColonyStructures () {
	//
	//	}

	/**
	 * The empty concept on downloadable items
	 *
	 * @return
	 */
	public boolean isEmpty () {
		if ( null == structures ) return true;
		if ( structures.size() < 1 ) return true;
		return false;
	}

	public boolean isRenderWhenEmpty () {
		if ( _renderIfEmpty )
			return true;
		else {
			if ( this.isEmpty() )
				return false;
			else
				return true;
		}
	}

	public Colony create (final int planetId) {
		try {
			Dao<Colony, String> colonyDao = NeoComDatabase.getImplementer().getColonyDao();
			colonyDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			logger.info("WR [Colony.create]> Colony exists. Update values.");
			store();
		}
		return this;
	}

	public Colony store () {
		try {
			Dao<Colony, String> colonyDao = NeoComDatabase.getImplementer().getColonyDao();
			colonyDao.createOrUpdate(this);
			logger.info("-- [Colony.store]> Colony data updated successfully.");
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return this;
	}

	// --- G E T T E R S   &   S E T T E R S
	public Integer getSolarSystemId () {
		return solarSystemId;
	}

	public Integer getPlanetId () {
		return planetId;
	}

	public String getPlanetType () {
		return planetType.name();
	}

	public Integer getOwnerId () {
		return ownerId;
	}

	public Integer getUpgradeLevel () {
		return upgradeLevel;
	}

	public Integer getNumPins () {
		return numPins;
	}

	public DateTime getLastUpdate () {
		return lastUpdate;
	}

	public String getSolarSystemName () {
		// Location is transient so we have to reload the EveLocation cache if null.
		if ( null == location ) location = GlobalDataManager.searchLocationById(getSolarSystemId());
		return location.getSystem();
	}

	public double getSecurityValue () {
		// Location is transient so we have to reload the EveLocation cache if null.
		if ( null == location ) location = GlobalDataManager.searchLocationById(getSolarSystemId());
		return location.getSecurityValue();
	}

	public Colony setSolarSystemId (final Integer solarSystemId) {
		this.solarSystemId = solarSystemId;
		// Locate the solar system data on the Location database.
		location = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(solarSystemId);
		return this;
	}

	public Colony setPlanetId (final Integer planetId) {
		this.planetId = planetId;
		return this;
	}

	public Colony setPlanetData (final GetUniversePlanetsPlanetIdOk planetData) {
		//		this.planetData = planetData;
		// Copy the planet name to the persistence fields and also check if we can store the whole data.
		planetName = planetData.getName();
		return this;
	}

	public Colony setPlanetType (final PlanetTypeEnum planetType) {
		this.planetType = planetType;
		return this;
	}

	public Colony setOwnerId (final Integer ownerId) {
		this.ownerId = ownerId;
		return this;
	}

	public Colony setUpgradeLevel (final Integer upgradeLevel) {
		this.upgradeLevel = upgradeLevel;
		return this;
	}

	public Colony setNumPins (final Integer numPins) {
		this.numPins = numPins;
		return this;
	}

	public Colony setLastUpdate (final DateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
		return this;
	}

	public Colony setStructuresData (final GetCharactersCharacterIdPlanetsPlanetIdOk structures) {
		this.structureData = structures;
		this.pins = structures.getPins();
		return this;
	}

	// --- D E L E G A T E D   M E T H O D S
	public String getPlanetName () {
		return planetName;
	}

	public List<ColonyCoreStructure> getStructures () {
		if ( null == structures ) return new ArrayList<>();
		else return structures;
	}

	public void setStructures (final List<ColonyCoreStructure> results) {
		structures = results;
	}

	private List<GetCharactersCharacterIdPlanetsPlanetIdOkPins> getPins () {
		if ( null == pins ) return new ArrayList<>();
		else return pins;
	}

	public GetCharactersCharacterIdPlanetsPlanetIdOk getStructureData () {
		return structureData;
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("Colony [");
		buffer.append("name: ").append(getSolarSystemName());
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

}
// - UNUSED CODE ............................................................................................
//[01]

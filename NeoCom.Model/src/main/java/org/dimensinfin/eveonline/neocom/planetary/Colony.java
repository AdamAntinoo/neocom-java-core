//  PROJECT:     NeoCom.DataManagement(NEOC.DM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java JRE 1.5 Specification.
//  DESCRIPTION: NeoCom pure Java library to maintain and manage all the data streams and
//                 connections. It will use the Models as the building blocks for the data
//                 and will isolate to the most the code from any platform implementation.
//               It will contain the Model Generators and use the external facilities for
//                 network connections to CCP XML api, CCP ESI api and Database storage. It
//                 will also make use of Cache facilities that will be glued at compilation
//                 time depending on destination platform.
package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok.PlanetTypeEnum;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

//- INTERFACE IMPLEMENTATION ...............................................................................
public class Colony implements ICollaboration/*extends IDownloadable, IExpandable, IJsonAngular*/ {
	private Integer solarSystemId = null;
	private Integer planetId = null;
	private PlanetTypeEnum planetType=null;
	private Integer ownerId = null;
	private Integer upgradeLevel = null;
	private Integer numPins = null;
	private DateTime lastUpdate = null;

	public Integer getSolarSystemId () {
		return solarSystemId;
	}

	public void setSolarSystemId (final Integer solarSystemId) {
		this.solarSystemId = solarSystemId;
	}

	public Integer getPlanetId () {
		return planetId;
	}

	public void setPlanetId (final Integer planetId) {
		this.planetId = planetId;
	}

	public Integer getOwnerId () {
		return ownerId;
	}

	public void setOwnerId (final Integer ownerId) {
		this.ownerId = ownerId;
	}

	public Integer getUpgradeLevel () {
		return upgradeLevel;
	}

	public void setUpgradeLevel (final Integer upgradeLevel) {
		this.upgradeLevel = upgradeLevel;
	}

	public Integer getNumPins () {
		return numPins;
	}

	public void setNumPins (final Integer numPins) {
		this.numPins = numPins;
	}

	public DateTime getLastUpdate () {
		return lastUpdate;
	}

	public void setLastUpdate (final DateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public PlanetTypeEnum getPlanetType () {
		return planetType;
	}

	public void setPlanetType (final PlanetTypeEnum planetType) {
		this.planetType = planetType;
	}

	@Override
	public List<ICollaboration> collaborate2Model (final String variation) {
		return new ArrayList<>();
	}
	//	public void addStructure (ColonyStructure structure);
//
//	public long getPlanetID ();
//
//	public String getPlanetName ();
//
//	public String getSolarSystemName ();
//
//	public long getPlanetTypeID ();
//
//	public String getPlanetTypeName ();
//
//	public int getNumberOfPins ();
//
//	public List<ColonyStructure> getPins ();
//
//	public boolean isEmpty ();
//
//	public void fireStructureChange (final String propertyName, final Object oldValue, final Object newValue);
//
//	public void addPropertyChangeListener (final PropertyChangeListener newListener);
//
//	public int getStructureCount ();

}

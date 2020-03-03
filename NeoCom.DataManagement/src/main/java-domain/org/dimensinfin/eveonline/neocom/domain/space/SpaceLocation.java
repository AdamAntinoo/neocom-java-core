package org.dimensinfin.eveonline.neocom.domain.space;

import java.io.Serializable;

import org.dimensinfin.core.interfaces.ICollaboration;

public interface SpaceLocation extends Serializable, ICollaboration {
	Long getLocationId();

//	Integer getRegionId();
//
//	GetUniverseRegionsRegionIdOk getRegion();
//
//	String getRegionName();
//
//	Integer getConstellationId();
//
//	GetUniverseConstellationsConstellationIdOk getConstellation();
//
//	String getConstellationName();
//
//	Integer getSolarSystemId();
//
//	GetUniverseSystemsSystemIdOk getSolarSystem();
//
//	String getSolarSystemName();
}

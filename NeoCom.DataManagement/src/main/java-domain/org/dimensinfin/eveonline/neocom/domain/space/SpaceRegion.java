package org.dimensinfin.eveonline.neocom.domain.space;

import java.io.Serializable;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;

public interface SpaceRegion extends SpaceLocation, Serializable {
	Integer getRegionId();

	GetUniverseRegionsRegionIdOk getRegion();

	String getRegionName();
}

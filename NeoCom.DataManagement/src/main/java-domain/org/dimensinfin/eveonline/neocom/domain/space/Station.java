package org.dimensinfin.eveonline.neocom.domain.space;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;

public interface Station {
	Integer getStationId();
	GetUniverseStationsStationIdOk getStation();
	String getStationName();
}

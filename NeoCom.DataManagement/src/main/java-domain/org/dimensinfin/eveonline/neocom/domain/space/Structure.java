package org.dimensinfin.eveonline.neocom.domain.space;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;

public interface Structure extends SpaceSystem{
	Long getStructureId();

	GetUniverseStructuresStructureIdOk getStructure();

	String getStructureName();
}

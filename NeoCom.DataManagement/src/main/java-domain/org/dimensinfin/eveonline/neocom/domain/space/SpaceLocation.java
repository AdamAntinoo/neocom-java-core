package org.dimensinfin.eveonline.neocom.domain.space;

import java.io.Serializable;

import org.dimensinfin.core.interfaces.ICollaboration;

public interface SpaceLocation extends Serializable, ICollaboration {
	Long getLocationId();
}

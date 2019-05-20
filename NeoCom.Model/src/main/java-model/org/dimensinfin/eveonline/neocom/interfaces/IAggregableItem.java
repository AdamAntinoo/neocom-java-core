package org.dimensinfin.eveonline.neocom.interfaces;

import org.dimensinfin.core.interfaces.ICollaboration;

public interface IAggregableItem extends ICollaboration {
	long getQuantity();
	double getVolume();
	double getPrice();
}

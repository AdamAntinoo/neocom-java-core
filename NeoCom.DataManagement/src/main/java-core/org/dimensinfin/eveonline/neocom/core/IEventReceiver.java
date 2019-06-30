package org.dimensinfin.eveonline.neocom.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public interface IEventReceiver extends PropertyChangeListener {
	void propertyChange( PropertyChangeEvent event );
}

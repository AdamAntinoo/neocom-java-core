package org.dimensinfin.eveonline.neocom.core;

import java.beans.PropertyChangeEvent;

public class NeoComEvent extends PropertyChangeEvent {
	public NeoComEvent( final Object o, final String s, final Object o1, final Object o2 ) {
		super(o, s, o1, o2);
	}
}

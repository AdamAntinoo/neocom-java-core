package org.dimensinfin.eveonline.neocom.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Antinoo
 */

public class EventEmitter implements Serializable, IEventEmitter {
	private static final long serialVersionUID = -1694932541665379792L;
	private static Logger logger = LoggerFactory.getLogger(EventEmitter.class);

	// - F I E L D - S E C T I O N
	private transient HashMap<PropertyChangeListener, PropertyChangeListener> _listeners = null;

	// - C O N S T R U C T O R - S E C T I O N
	public EventEmitter() {
		super();
	}

	// - G E T T E R S   &   S E T T E R S
	// - M E T H O D - S E C T I O N
	protected boolean firePropertyChange( final PropertyChangeEvent event ) {
		boolean consumed = false;
		// Get all the listeners and send them this change
		for (PropertyChangeListener listener : getListeners().values()) {
			logger.info("-- [AbstractPropertyChanger.firePropertyChange]-> Property: " + event.getPropertyName()
					            + " on object: " + this.getClass().getName());
			listener.propertyChange(event);
			consumed = true;
		}
		return consumed;
	}

	private HashMap<PropertyChangeListener, PropertyChangeListener> getListeners() {
		if (null == _listeners) _listeners = new HashMap<PropertyChangeListener, PropertyChangeListener>();
		return _listeners;
	}


	// - I E V E N T E M I T T E R   I N T E R F A C E
	@Override
	public void addEventListener( final IEventReceiver listener ) {
		this.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener( final PropertyChangeListener listener ) {
		if (null != listener) getListeners().put(listener, listener);
	}

	@Override
	public void removeEventListener( final IEventReceiver listener ) {
		this.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener( final PropertyChangeListener listener ) {
		if (null != listener) getListeners().remove(listener);
	}

	@Override
	public boolean sendChangeEvent( final String eventName ) {
		return this.firePropertyChange(new PropertyChangeEvent(this, eventName, null, null));
	}

//	@Override
	public boolean sendChangeEvent( final PropertyChangeEvent event ) {
		return this.firePropertyChange(event);
	}
}

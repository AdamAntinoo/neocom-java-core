package org.dimensinfin.eveonline.neocom.core;

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
	private transient HashMap<IEventReceiver, IEventReceiver> _listeners;

	// - C O N S T R U C T O R S
	public EventEmitter() {
		super();
	}

	protected boolean fireEvent( final NeoComEvent event ) {
		boolean consumed = false;
		// Get all the listeners and send them this change
		for (IEventReceiver listener : this.getListeners().values()) {
			logger.info("-- [EventEmitter.fireEvent]-> Property: " + event.getPropertyName()
					            + " on object: " + this.getClass().getSimpleName());
			listener.eventReceived(event);
			consumed = true;
		}
		return consumed;
	}

	private HashMap<IEventReceiver, IEventReceiver> getListeners() {
		if (null == _listeners) _listeners = new HashMap<IEventReceiver, IEventReceiver>();
		return _listeners;
	}

	// - I E V E N T E M I T T E R   I N T E R F A C E
	@Override
	public void addEventListener( final IEventReceiver listener ) {
		if (null != listener) this.getListeners().put(listener, listener);
	}

	@Override
	public void removeEventListener( final IEventReceiver listener ) {
		if (null != listener) this.getListeners().remove(listener);
	}

	@Override
	public boolean sendChangeEvent( final String eventName ) {
		return this.fireEvent(new NeoComEvent(this, eventName, null, null));
	}

	@Override
	public boolean sendChangeEvent( final NeoComEvent event ) {
		return this.fireEvent(event);
	}
}

package org.dimensinfin.eveonline.neocom.core;

/**
 * Describes the api for property and structural changes event emitters. Any change to the node data will trigger the
 * emission of a typed event that can be intercepted by the listeners connected to emitters.
 */
public interface IEventEmitter {
	void addEventListener( final IEventReceiver listener );

	void removeEventListener( final IEventReceiver listener );

	boolean sendChangeEvent( final String eventName );

	boolean sendChangeEvent( final NeoComEvent eventName );
}

//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.core;

import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.enums.ERequestClass;
import org.dimensinfin.eveonline.neocom.enums.ERequestState;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.services.PendingRequestEntry;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class CoreCacheConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger									logger						= Logger.getLogger("CoreCacheConnector");

	// - F I E L D - S E C T I O N ............................................................................
	protected Vector<PendingRequestEntry>	_pendingRequests	= new Vector<PendingRequestEntry>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public CoreCacheConnector() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Add a new request to download and update the database of locations. This is an special request that it is
	 * initialized at startup and probably in some other conditions like when the information is stale and needs
	 * to be updated.
	 */
	public void addCharacterUpdateRequest(final long localizer) {
		CoreCacheConnector.logger.info(">> [MicroServicesCacheConnector.addCharacterUpdateRequest]");
		final PendingRequestEntry request = new PendingRequestEntry(localizer);
		request.setClass(ERequestClass.CHARACTERUPDATE);
		request.setPriority(20);
		if (this.addNoDuplicate(request)) {
			this.incrementTopCounter();
		}
		CoreCacheConnector.logger.info("<< [MicroServicesCacheConnector.addCharacterUpdateRequest]");
	}

	/**
	 * Queues a new request to download Market Data for an Item. We only register the ID of the item because the
	 * side will not be used. On the download phase we will download both sides. Using the ID as key will avoid
	 * requesting the same item multiple times. <br>
	 * 
	 * @param localizer
	 *          identifier of the item related to the data to download.
	 */
	public synchronized void addMarketDataRequest(final long localizer) {
		// Log.i("AndroidCacheConnector", ">>
		// AndroidCacheConnector.addMarketDataRequest");
		final EveItem item = AppConnector.getDBConnector().searchItembyID(Long.valueOf(localizer).intValue());
		CoreCacheConnector.logger
				.info("-- [AndroidCacheConnector.addMarketDataRequest] Posting market update for: " + item.getName());
		// Detect priority from the Category of the item. Download data from
		// Asteroids and Minerals first.
		final String category = item.getCategory();
		final String group = item.getGroupName();
		int priority = 1;
		if (category.equalsIgnoreCase("Asteroid")) {
			priority = 6;
		}
		if (category.equalsIgnoreCase("Material")) {
			priority = 5;
		}
		if (category.equalsIgnoreCase("Module")) {
			priority = 8;
		}
		if (group.equalsIgnoreCase("Datacores")) {
			priority = 3;
		}
		final PendingRequestEntry request = new PendingRequestEntry(localizer);
		request.setPriority(priority);
		if (this.addNoDuplicate(request)) {
			this.incrementMarketCounter();
		}
	}

	public synchronized void clearPendingRequest(final long localizer) {
		this.clearPendingRequest(Long.valueOf(localizer).toString());
	}

	public synchronized void clearPendingRequest(final String localizer) {
		for (final PendingRequestEntry entry : _pendingRequests) {
			final String entryid = entry.getIdentifier();
			if (entryid.equalsIgnoreCase(localizer)) {
				entry.state = ERequestState.COMPLETED;
				// Update the right counter depending on the priority range.
				if (entry.getPriority() < 20) {
					this.decrementMarketCounter();
				} else {
					this.decrementTopCounter();
				}
			}
		}
	}

	public abstract int decrementMarketCounter();

	public abstract int decrementTopCounter();

	public synchronized Vector<PendingRequestEntry> getPendingRequests() {
		if (null == _pendingRequests) {
			_pendingRequests = new Vector<PendingRequestEntry>();
		}
		// Clean up all completed requests.
		final Vector<PendingRequestEntry> openRequests = new Vector<PendingRequestEntry>(_pendingRequests.size());
		for (final PendingRequestEntry entry : _pendingRequests)
			if (entry.state != ERequestState.COMPLETED) {
				openRequests.add(entry);
			}
		_pendingRequests = openRequests;
		return _pendingRequests;
	}

	public abstract int incrementMarketCounter();

	public abstract int incrementTopCounter();

	/**
	 * Adds a new entry if not already found on the pending list.
	 * 
	 * @param request
	 */
	private synchronized boolean addNoDuplicate(final PendingRequestEntry request) {
		// Check for duplicates before adding the new element.
		final String requestid = request.getIdentifier();
		for (final PendingRequestEntry entry : _pendingRequests) {
			final String entryid = entry.getIdentifier();
			if (entryid.equalsIgnoreCase(requestid)) return false;
		}
		_pendingRequests.add(request);
		return true;
	}
}

// - UNUSED CODE ............................................................................................

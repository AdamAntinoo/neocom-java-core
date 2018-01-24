//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.manager;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IJsonAngular;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ExtendedLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.Region;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractManager implements ICollaboration, IJsonAngular {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -3012043551959443176L;
	protected static Logger logger = Logger.getLogger("AbstractManager");

	// - F I E L D - S E C T I O N ............................................................................
	protected String jsonClass = "AbstractManager";
	@JsonIgnore
	protected transient Credential credential;
	//	@JsonIgnore
	//	private transient NeoComCharacter pilot = null;
	protected boolean initialized = false;

	// - L O C A T I O N   M A N A G E M E N T
	protected final Hashtable<Long, Region> regions = new Hashtable<Long, Region>();
	protected final Hashtable<Long, ExtendedLocation> locations = new Hashtable<Long, ExtendedLocation>();
	protected final Hashtable<Long, NeoComAsset> containers = new Hashtable<Long, NeoComAsset>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractManager (final Credential credential) {
		super();
		this.credential = credential;
		jsonClass = "AbstractManager";
	}

	//	@Deprecated
	//	public AbstractManager (final NeoComCharacter pilot) {
	//		super();
	//		this.setPilot(pilot);
	//		jsonClass = "AbstractManager";
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public List<ICollaboration> collaborate2Model (final String variant) {
		return new ArrayList<ICollaboration>();
	}

	public int getCredentialIdentifier () {
		if ( null == credential )
			throw new RuntimeException("RT [AbstractManager]> Credential is not set on current Manager. Bad initialization.");
		return credential.getAccountId();
	}
	public String getCredentialName () {
		if ( null == credential )
			throw new RuntimeException("RT [AbstractManager]> Credential is not set on current Manager. Bad initialization.");
		return credential.getAccountName();
	}

	/**
	 * Clears the initialization state and returns the last value contained on this flag.
	 */
	public boolean clearInitialization () {
		boolean oldstate = initialized;
		initialized = false;
		return oldstate;
	}


	//@Deprecated
	//	@JsonIgnore
	//	public NeoComCharacter getPilot () {
	//		return pilot;
	//	}

	/**
	 * Returns the list of different Regions found on the list of locations.
	 */
	public Hashtable<Long, Region> getRegions () {
		this.initialize();
		return regions;
	}

	public abstract AbstractManager initialize ();

	//	/**
	//	 * Checks if the initialization method and the load of the resources has been already executed.
	//	 */
	//	//	public boolean isInitialized() {
	//	//		return initialized;
	//	//	}
	//	public void setPilot (final NeoComCharacter newPilot) {
	//		pilot = newPilot;
	//	}

	/**
	 * This adds the Asset to the target Container. If the Container is not already on the list of Containers
	 * then it is added to that list no other assets get added to the same Container. The same with the Location
	 * of the container. The Container is then added to its own Location.
	 */
	//	@SuppressWarnings("unused")
	protected void add2Container (final NeoComAsset asset, final NeoComAsset target) {
		long id = asset.getLocationId();
		IAssetContainer subtarget = (IAssetContainer) containers.get(id);
		if ( null == subtarget ) {
			if ( target instanceof IAssetContainer ) {
				((IAssetContainer) target).addAsset(asset);
			}
			containers.put(target.getAssetId(), target);
			//			this.add2Location(target);
		} else {
			subtarget.addAsset(asset);
		}
	}

	protected void add2Region (final EveLocation target) {
		long regionid = target.getRegionID();
		Region region = regions.get(regionid);
		if ( null == region ) {
			region = new Region(target.getRegion());
			regions.put(new Long(regionid), region);
		}
		region.addLocation(target);
	}

	public String getJsonClass () {
		return jsonClass;
	}
}

// - UNUSED CODE ............................................................................................

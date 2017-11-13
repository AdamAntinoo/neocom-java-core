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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.dimensinfin.android.model.AbstractViewableNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ExtendedLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.dimensinfin.eveonline.neocom.model.Region;

import com.fasterxml.jackson.annotation.JsonIgnore;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractManager extends AbstractViewableNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long													serialVersionUID	= -3012043551959443176L;
	protected static Logger														logger						= Logger.getLogger("AbstractManager");

	// - F I E L D - S E C T I O N ............................................................................
	@JsonIgnore
	private transient NeoComCharacter									pilot							= null;
	protected boolean																	initialized				= false;
	// - L O C A T I O N   M A N A G E M E N T
	protected final Hashtable<Long, Region>						regions						= new Hashtable<Long, Region>();
	protected final Hashtable<Long, ExtendedLocation>	locations					= new Hashtable<Long, ExtendedLocation>();
	protected final Hashtable<Long, NeoComAsset>			containers				= new Hashtable<Long, NeoComAsset>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractManager(final NeoComCharacter pilot) {
		super();
		this.setPilot(pilot);
		jsonClass = "AbstractManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean clearInitialization() {
		boolean oldstate = initialized;
		initialized = false;
		return oldstate;
	}

	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		return new ArrayList<AbstractComplexNode>();
	}

	//	public int getContentCount() {
	//		return 0;
	//	}

	@JsonIgnore
	public NeoComCharacter getPilot() {
		return pilot;
	}

	/**
	 * Returns the list of different Regions found on the list of locations.
	 * 
	 * @return
	 */
	public Hashtable<Long, Region> getRegions() {
		this.initialize();
		return regions;
	}

	public abstract AbstractManager initialize();

	/**
	 * Checks if the initialization method and the load of the resources has been already executed.
	 * 
	 * @return
	 */
	//	public boolean isInitialized() {
	//		return initialized;
	//	}

	public void setPilot(final NeoComCharacter newPilot) {
		pilot = newPilot;
	}

	/**
	 * This adds the Asset to the target Container. If the Container is not already on the list of Containers
	 * then it is added to that list no other assets get added to the same Container. The same with the Location
	 * of the container. The Container is then added to its own Location.
	 * 
	 * @param asset
	 * @param target
	 */
	//	@SuppressWarnings("unused")
	protected void add2Container(final NeoComAsset asset, final NeoComAsset target) {
		long id = asset.getLocationID();
		NeoComAsset subtarget = containers.get(id);
		if (null == subtarget) {
			if (target instanceof IAssetContainer) {
				((IAssetContainer) target).addContent(asset);
			}
			containers.put(target.getAssetID(), target);
			//			this.add2Location(target);
		} else {
			subtarget.addChild(asset);
		}
	}

	//	protected void add2Location(final NeoComAsset asset) {
	//		long locid = asset.getLocationID();
	//		EveLocation target = locations.get(locid);
	//		if (null == target) {
	//			target = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(locid);
	//			locations.put(new Long(locid), target);
	//			this.add2Region(target);
	//		}
	//		target.addContent(asset);
	//	}

	protected void add2Region(final EveLocation target) {
		long regionid = target.getRegionID();
		Region region = regions.get(regionid);
		if (null == region) {
			region = new Region(target.getRegion());
			regions.put(new Long(regionid), region);
		}
		region.addLocation(target);
	}

	//	/**
	//	 * Get access to the parent and its Location and add it to the Locations list it it is the top of the chain.
	//	 * THis can be performed recursively if the Parent has also another Parent.
	//	 * 
	//	 * @return
	//	 */
	//	private NeoComAsset processParent(final NeoComAsset parent) {
	//		// This is the recursive part to get the complete chain.
	//		if (parent.hasParent()) {
	//			NeoComAsset target = this.processParent(parent.getParentContainer());
	//			// Add asset to the chain.
	//			if (target instanceof IAssetContainer) {
	//				((IAssetContainer) target).addContent(parent);
	//			}
	//			return target;
	//		} else {
	//			// Get the asset (a Container or a Ship) and add it to the chain.
	//			//			NeoComAsset target = ModelAppConnector.getSingleton().getDBConnector().searchAssetByID(parent.getAssetID());
	//			if (null != parent) {
	//				this.add2Location(parent);
	//				return parent;
	//			} else
	//				return null;
	//		}
	//	}

}

// - UNUSED CODE ............................................................................................

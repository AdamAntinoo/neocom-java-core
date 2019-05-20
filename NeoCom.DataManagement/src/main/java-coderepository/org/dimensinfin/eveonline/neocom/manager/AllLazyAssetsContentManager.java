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

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IDownloadable;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.model.ExtendedLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.Ship;
import org.dimensinfin.eveonline.neocom.model.SpaceContainer;

import java.util.ArrayList;
import java.util.List;

// - CLASS IMPLEMENTATION ...................................................................................
public class AllLazyAssetsContentManager extends AbstractContentManager implements IDownloadable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -2753423369104215278L;

	// - F I E L D - S E C T I O N ............................................................................
	private boolean _downloading = false;
	private boolean _downloaded = false;
	private double totalVolume = 0.0;
	private double totalValue = 0.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AllLazyAssetsContentManager (final ExtendedLocation newparent) {
		super(newparent);
		jsonClass = "AllLazyAssetsContentManager";
	}

	@SuppressWarnings("unused")
	private AllLazyAssetsContentManager () {
		super();
		jsonClass = "AllLazyAssetsContentManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * This method will access the database to get the assets contained at that Location. It is the default
	 * behavior for a ContentManager, to get all the related items with no filtering. The download of the
	 * contents is lazily evaluated and depends on the expanded state. For not expanded satets the contents are
	 * empty but when the user expands the item we should then download the list.
	 */
	@Override
	public List<ICollaboration> collaborate2Model (final String variant) {
		List<ICollaboration> results = new ArrayList<ICollaboration>();
		// If the contents are already downloaded chain the collaboration calls.
		if ( this.isDownloaded() ) {
			results.addAll(_contents);
		} else {
			// Download the assets only if the state is expended.
			if ( parent.isExpanded() ) {
				results.addAll(this.getContents());
			}
		}
		return results;
	}

	/**
	 * This methods is public so can be called by anyone at anytime. It will force the download of all the
	 * assets for a Location being it expanded or net so any access to the method will trigger a database fetch
	 * and their use of memory. Hut there should be a way to get the contents of a Location at some point in
	 * time when we need to collaborate them to a ListView.
	 */
	@Override
	public List<NeoComAsset> getContents () {
		if ( !this.isDownloaded() ) {
			// Get the assets from the database.
			_contents.clear();
			_contents.addAll(this.processDownloadedAssets(GlobalDataManager.searchAssetsAtLocation(parent.getCredentialIdentifier(), this.getID()
			)));
			this.setDownloaded(true);
		}
		return _contents;
	}

	@Override
	public int getContentSize () {
		if ( this.isDownloaded() )
			return _contents.size();
		else
			// Go to the database and get an approximate count of the assets that are at this Location.
			return GlobalDataManager.totalLocationContentCount(this.getID());
	}

	public double getTotalValue () {
		return totalValue;
	}

	public double getTotalVolume () {
		return totalVolume;
	}

	@Override
	public boolean isEmpty () {
		if ( this.isDownloaded() )
			if ( _contents.size() < 1 )
				return true;
			else
				return false;
		else
			return false;
	}

	/**
	 * Process the assets being downloaded and convert to their new types. Warning!!. This methods need to know
	 * the id of the current pilot but has no connection to the assets manager or the Character itself so I am
	 * resorting to use the default Character stored at the AppModel.
	 */
	private List<NeoComAsset> processDownloadedAssets (final List<NeoComAsset> input) {
		ArrayList<NeoComAsset> results = new ArrayList<NeoComAsset>();
		for (NeoComAsset asset : input) {
			if ( asset.isContainer() ) {
				// Check if the asset is packaged. If so leave as asset
				if ( !asset.isPackaged() ) {
					// Transform the asset to a ship.
					SpaceContainer container = new SpaceContainer().copyFrom(asset);
					// Calculate value and volume to register on the aggregation.
					//	totalValue=+asset.getPrice()*asset.getQuantity();
					//	totalVolume=+asset.getItem().getVolume()*asset.getQuantity();
					results.add(container);
					continue;
				}
			}
			if ( asset.isShip() ) {
				// Check if the ship is packaged. If packaged leave it as a simple asset.
				if ( !asset.isPackaged() ) {
					// Transform the asset to a ship.
					Ship ship = new Ship(/*parent.getCredentialIdentifier()*/)
							.copyFrom(asset);
					// Calculate value and volume to register on the aggregation.
					totalValue = +asset.getPrice();
					//			totalVolume=+asset.getItem().getVolume()*asset.getQuantity();
					results.add(ship);
					continue;
				}
			}
			// Calculate value and volume to register on the aggregation.
			totalValue = +asset.getPrice() * asset.getQuantity();
			totalVolume = +asset.getItem().getVolume() * asset.getQuantity();
			results.add(asset);
		}
		return results;
	}

	public IDownloadable setDownloading (final boolean downloading) {
		this._downloading = downloading;
		return this;
	}

	public IDownloadable setDownloaded (final boolean downloaded) {
		this._downloaded = downloaded;
		return this;
	}

	public boolean isDownloading () {
		return _downloading;
	}

	public boolean isDownloaded () {
		return _downloaded;
	}
}

// - UNUSED CODE ............................................................................................

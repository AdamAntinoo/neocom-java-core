//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.model;

import com.annimon.stream.Stream;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.model.Container;
import org.dimensinfin.eveonline.neocom.entities.NeoComAsset;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

/**
 * This new implementation of the ship will unify the management of the contents to a single container because the
 * new location flag we get on the ESI api will classify the right aggregation for it when required. So that
 * information is not a must at the model level and only used to render the contents under different groups and that
 * can be performed dynamically during render preparation.
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class Ship extends ShipPre10 {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(Ship.class);

	// - F I E L D - S E C T I O N ............................................................................
	//	private final HashMap<GetCharactersCharacterIdAssets200Ok.LocationFlagEnum,ICollaboration> _contents = new
	//			HashMap<ICollaboration>();
	private final List<ShipContent> _contents = new ArrayList<ShipContent>();
	private boolean _expanded = false;
	//	private boolean _renderIfEmpty = true;
	//	private boolean _downloaded = false;
	//	private boolean _downloading = false;
	private double totalVolume = 0.0;
	private double totalValue = 0.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Ship() {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * During the collaboration we should convert the list of contents to the model hierarchy we should present to the
	 * renderer. So if we are showing the Ship on the AssetsByLocation we should only show the fitting as a whole group
	 * and the different cargo hols identified by their task.
	 * If the ship is shown on the PlanetaryAssetsList then we only have to show is cargo hold contents and we can
	 * ignore any other item not related to the Planetary group.
	 * On the list by category, the ship should not be expandable.
	 * And finally when showing the Fitting we should add as much information as required to the different sections and
	 * holders.
	 * @param variant the fragment variant when this collaboration is going to be used.
	 * @return
	 */
	public List<ICollaboration> collaborate2Model( final String variant ) {
		ArrayList<ICollaboration> result = new ArrayList<ICollaboration>();
		//		if ( !this.isDownloaded() ) {
		//			this.downloadShipData();
		//		}
		//		if (variant.equalsIgnoreCase(ENeoComVariants.ASSETS_BYCATEGORY.name())) return result;
		//		if (variant.equalsIgnoreCase(ENeoComVariants.PLANETARY_BYLOCATION.name())) {
		// Filter out anything that is not the planetary cargohold resources.
		// TODO test grouping experiment.
		//		 groups = Stream.of(_contents)
		//		                .collect(Collectors.groupingBy(ShipContent::getGroup))
		//		                .entrySet().
		//		                .toList();
		Stream.of(_contents)
				.filter(ShipContent::isInCargoHold)
				.filter(( node ) -> {
					if ( node.getCategory().equalsIgnoreCase("Planetary Commodities") )
						return true;
					if ( node.getCategory().equalsIgnoreCase("Planetary Resources") )
						return true;
					return false;
				})
				.forEach(( node ) -> {
					// Add the found item to the result.
					result.add(node.getContent());
				});
		//		}
		//		if (variant.equalsIgnoreCase(ENeoComVariants.ASSETS_BYLOCATION.name())) {
		// For the assets by location we should generate just two sets. The elements on the fitting and the elements on
		// cargoholds
		final List<ICollaboration> fittingContents = new ArrayList<>();
		Stream.of(_contents)
				.forEach(( node ) -> {
					if ( node.isFitted() ) fittingContents.add(node.getContent());
				});
		final ShipAssetGroup fittings = new ShipAssetGroup("FITTING", GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HIDDENMODIFIERS);
		fittings.addContentList(fittingContents);
		result.add(fittings);

		// Do the same with the cargoholds contents.
		final List<ICollaboration> cargoContents = new ArrayList<>();
		Stream.of(_contents)
				.forEach(( node ) -> {
					if ( node.isInCargoHold() ) cargoContents.add(node.getContent());
				});
		final ShipAssetGroup cargo = new ShipAssetGroup("CARGO", GetCharactersCharacterIdAssets200Ok.LocationFlagEnum
				.HIDDENMODIFIERS);
		cargo.addContentList(cargoContents);
		result.add(cargo);

		// Put the rest into a Other container
		final List<ICollaboration> otherContents = new ArrayList<>();
		Stream.of(_contents)
				.filterNot(ShipContent::isFitted)
				.filterNot(ShipContent::isInCargoHold)
				.forEach(( node ) -> otherContents.add(node.getContent()));
		final ShipAssetGroup other = new ShipAssetGroup("OTHER", GetCharactersCharacterIdAssets200Ok.LocationFlagEnum
				.HIDDENMODIFIERS);
		other.addContentList(cargoContents);
		result.add(cargo);

		//			final HashMap<GetCharactersCharacterIdAssets200Ok.LocationFlagEnum, List<ICollaboration>> groups = new HashMap<GetCharactersCharacterIdAssets200Ok.LocationFlagEnum, List<ICollaboration>>();
		//			for (ShipContent node : _contents) {
		//				final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum group = node.getGroup();
		//				final List<ICollaboration> hit = groups.get(group);
		//				if ( null == hit ) {
		//					final ArrayList<ICollaboration> newhit = new ArrayList<ICollaboration>();
		//					newhit.add(node.getContent());
		//					groups.put(group, newhit);
		//				} else {
		//					hit.add(node.getContent());
		//				}
		//			}
		//			// Create the group containers and then send them to the results.
		//			final Iterator<GetCharactersCharacterIdAssets200Ok.LocationFlagEnum> keyIterator = groups.keySet().iterator();
		//			while(keyIterator.hasNext()){
		//				final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum key = keyIterator.next();
		//				final List<ICollaboration> groupContents = groups.get(key);
		//				final ShipAssetGroup aggregator = new ShipAssetGroup(key.name(), key);
		//				aggregator.addContentList(groupContents);
		//				result.add(aggregator);
		//			}
		//		}
		return result;
	}

	public int addAsset( final NeoComAsset asset ) {
		super.addAsset(asset);
		_contents.add(new ShipContent(asset.getLocationFlag(), asset));
		return _contents.size();
	}
	//	protected void downloadShipData () {
	//		ArrayList<NeoComAsset> contents = GlobalDataManager.accessAssetsContainedAt(this.getAssetId());
	//		for(NeoComAsset asset: contents){
	//			final ShipContent newcontent = new ShipContent(asset.getFlag(), asset);
	//			_contents.add(newcontent);
	//		}
	//		this.setDownloaded(true);
	//	}

	@Override
	public Ship copyFrom( final NeoComAsset asset ) {
		return (Ship) super.copyFrom(asset);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Ship [");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class ShipContent {
		// - S T A T I C - S E C T I O N ..........................................................................

		// - F I E L D - S E C T I O N ............................................................................
		private GetCharactersCharacterIdAssets200Ok.LocationFlagEnum group = GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.CARGO;
		private ICollaboration content = null;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public ShipContent( final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum group, final ICollaboration content ) {
			super();
			this.group = group;
			this.content = content;
		}

		// - M E T H O D - S E C T I O N ..........................................................................

		private GetCharactersCharacterIdAssets200Ok.LocationFlagEnum getGroup() {
			return group;
		}

		private ICollaboration getContent() {
			return content;
		}

		private void setGroup( final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum group ) {
			this.group = group;
		}

		private void setContent( final ICollaboration content ) {
			this.content = content;
		}

		// --- G R O U P I N G   E V A L U A T I O N S
		public boolean isInCargoHold() {
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.ASSETSAFETY ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.CARGO ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.DELIVERIES ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.FLEETHANGAR ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HANGAR ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HANGARALL ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOCKED ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SHIPHANGAR ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDAMMOHOLD ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDCOMMANDCENTERHOLD ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDFUELBAY ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDGASHOLD ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDINDUSTRIALSHIPHOLD ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDLARGESHIPHOLD ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDMATERIALBAY ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDMEDIUMSHIPHOLD ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDMINERALHOLD ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDOREHOLD ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDPLANETARYCOMMODITIESHOLD )
				return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDSALVAGEHOLD ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDSHIPHOLD ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.SPECIALIZEDSMALLSHIPHOLD ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.UNLOCKED ) return true;

			return false;
		}

		public boolean isFitted() {
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.AUTOFIT ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT0 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT1 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT2 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT3 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT4 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT5 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT6 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT7 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT0 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT1 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT2 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT3 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT4 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT5 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT6 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT7 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT0 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT1 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT2 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT3 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT4 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT5 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT6 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT7 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.RIGSLOT0 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.RIGSLOT1 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.RIGSLOT2 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.RIGSLOT3 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.RIGSLOT4 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.RIGSLOT5 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.RIGSLOT6 ) return true;
			if ( group == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.RIGSLOT7 ) return true;

			return false;
		}

		// --- D E L E G A T E D
		public String getCategory() {
			if ( content instanceof NeoComAsset ) {
				return ((NeoComAsset) content).getCategoryName();
			} else return "-NO CATEGORIZED-";
		}
	}

	// - CLASS IMPLEMENTATION ...................................................................................
	public class ShipAssetGroup extends Container implements IAssetContainer {
		/** Use the type to associate it with an icon. */
		//	public enum EGroupType {
		//		DEFAULT, SHIPSECTION_HIGH, SHIPSECTION_MED, SHIPSECTION_LOW, SHIPSECTION_DRONES, SHIPSECTION_CARGO, SHIPSECTION_RIGS, SHIPTYPE_BATTLECRUISER, SHIPTYPE_BATTLESHIP, SHIPTYPE_CAPITAL, SHIPTYPE_CRUISER, SHIPTYPE_DESTROYER, SHIPTYPE_FREIGHTER, SHIPTYPE_FRIGATE, EMPTY_FITTINGLIST
		//	}

		// - S T A T I C - S E C T I O N ..........................................................................
		private static final long serialVersionUID = 8066964529677353362L;

		// - F I E L D - S E C T I O N ............................................................................
		private double totalValue = 0.0;
		private double totalVolume = 0.0;

		public GetCharactersCharacterIdAssets200Ok.LocationFlagEnum groupType =
				GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.CARGO;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public ShipAssetGroup() {
			super();
			this.expand();
			jsonClass = "AssetGroup";
		}

		public ShipAssetGroup( final String newtitle ) {
			super(newtitle);
			this.expand();
			jsonClass = "AssetGroup";
		}

		public ShipAssetGroup( final String newtitle, final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum newtype ) {
			this(newtitle);
			groupType = newtype;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		public int addAsset( final NeoComAsset asset ) {
			super.addContent(asset);
			try {
				totalValue += asset.getQuantity() * asset.getItem().getHighestBuyerPrice().getPrice();
			} catch (ExecutionException ee) {
				totalValue += asset.getQuantity() * asset.getItem().getPrice();
			} catch (InterruptedException ie) {
				totalValue += asset.getQuantity() * asset.getItem().getPrice();
			}
			totalVolume += asset.getQuantity() * asset.getItem().getVolume();
			return this.getContentSize();
		}

		public List<NeoComAsset> getAssets() {
			Vector<NeoComAsset> result = new Vector<NeoComAsset>();
			for (ICollaboration node : super.getContents()) {
				result.add((NeoComAsset) node);
			}
			return result;
		}

		@Override
		public int getContentSize() {
			return super.getContentSize();
		}

		@Override
		public double getTotalValue() {
			return this.totalValue;
		}

		@Override
		public double getTotalVolume() {
			return this.totalVolume;
		}

		public GetCharactersCharacterIdAssets200Ok.LocationFlagEnum getGroupType() {
			return groupType;
		}

		public ShipAssetGroup setGroupType( final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum newtype ) {
			groupType = newtype;
			return this;
		}

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer("AssetGroup [");
			buffer.append(this.getTitle()).append(" type: ").append(groupType.name());
			//		buffer.append(" [").append(this.getContentSize()).append("]");
			buffer.append("]");
			return buffer.toString();
		}
	}
}
// - UNUSED CODE ............................................................................................
//[01]

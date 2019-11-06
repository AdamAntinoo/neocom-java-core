package org.dimensinfin.eveonline.neocom.asset.converter;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.asset.domain.AssetTypes;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;

import retrofit2.Converter;

public class GetCharactersCharacterIdAsset2NeoAssetConverter implements Converter<GetCharactersCharacterIdAssets200Ok, NeoAsset> {
	@Override
	public NeoAsset convert( final GetCharactersCharacterIdAssets200Ok esiAsset ) throws IOException {
		final NeoAsset newAsset = new NeoAsset();
		newAsset.setAssetId( esiAsset.getItemId() );
		newAsset.setTypeId( esiAsset.getTypeId() );
		newAsset.setAssetDelegate( esiAsset );
		newAsset.setItemDelegate( new NeoItem( esiAsset.getTypeId() ) );
		if (newAsset.getCategoryName().equalsIgnoreCase( AssetTypes.SHIP.getTypeName() ))
			newAsset.setShipFlag( true );
		newAsset.setBlueprintFlag( this.checkIfBlueprint( newAsset ) );
		newAsset.setContainerFlag( this.checkIfBlueprint( newAsset ) );
		if (esiAsset.getLocationId() > 61E6) // The asset is contained into another asset. Set the parent.
			newAsset.setParentContainerId( esiAsset.getLocationId() );

		// Now calculate the public part of the location. The definitive data should be calculated outside.
		newAsset.setLocationId( new LocationIdentifier.Builder()
				.withSpaceIdentifier( esiAsset.getLocationId() )
				.withLocationFlag( esiAsset.getLocationFlag() )
				.withLocationType( esiAsset.getLocationType() )
				.build() );
		return newAsset;


//		newAsset.locationId = transformLocation( esiAsset.getLocationId(),
//				esiAsset.getLocationFlag(),
//				esiAsset.getLocationType() );
//		asset.containerFlag = checkIfContainer( asset );
//		if (esiAsset.getLocationId() > 61E6) // The asset is contained into another asset. Set the parent.
//			asset.parentContainerId = esiAsset.getLocationId();

//		private NeoComAsset convert2AssetFromESI( final GetCharactersCharacterIdAssets200Ok asset200Ok ) {
		// Create the asset from the API asset.
//		final NeoAsset newAsset = new NeoAsset.Builder()
//				.fromEsiAsset( value )
//				.build();
//		// TODO -- Location management is done outside this transformation. This is duplicated code.
//		Long locid = value.getLocationId();
//		if (null == locid) {
//			locid = (long) -2;
//		}
//		newAsset.setLocationId( locid )
//				.setLocationType( asset200Ok.getLocationType() )
//				.setQuantity( asset200Ok.getQuantity() )
//				.setLocationFlag( asset200Ok.getLocationFlag() )
//				.setSingleton( asset200Ok.getIsSingleton() );
		// Get access to the Item and update the copied fields.
//		final EveItem item = GlobalDataManager.getSingleton().searchItem4Id( newAsset.getTypeId() );
//		if (null != item) {
//			//			try {
//			newAsset.setName( item.getName() );
//			newAsset.setCategory( item.getCategoryName() );
//			newAsset.setGroupName( item.getGroupName() );
//			newAsset.setTech( item.getTech() );
//			//				if (item.isBlueprint()) {
//			//					//			newAsset.setBlueprintType(eveAsset.getRawQuantity());
//			//				}
//			//			} catch (RuntimeException rtex) {
//			//			}
//		}
//		// Add the asset value to the database.
//		newAsset.setIskValue( this.calculateAssetValue( newAsset ) );
//		return newAsset;
//		}
//
//		return null;
	}

	/**
	 * One asset is a blueprint if the category is blueprint. Then if the blueprint is original or a copy it comes from the
	 * asset delegate field 'getIsBlueprintCopy'.
	 *
	 * @param asset the asset to check.
	 * @return true if the asset is a blueprint of any type.
	 */
	private boolean checkIfBlueprint( final NeoAsset asset ) {
		return asset.getCategoryName().equalsIgnoreCase( AssetTypes.BLUEPRINT.getTypeName() );
	}

	/**
	 * There are many types of containers. Use all the identified catalog of asset types to calculate the response.
	 *
	 * @param asset the asset to check.
	 * @return true if the asset is able to contain other assets, ships included.
	 */
	private static boolean checkIfContainer( final NeoAsset asset ) {
		if (asset.isBlueprint()) return false;
		if (asset.isShip()) return true;
		// Use a list of types to set what is a container
		if (asset.getTypeId() == 11488) return true;
		if (asset.getTypeId() == 11489) return true;
		if (asset.getTypeId() == 11490) return true;
		if (asset.getTypeId() == 17363) return true;
		if (asset.getTypeId() == 17364) return true;
		if (asset.getTypeId() == 17365) return true;
		if (asset.getTypeId() == 17366) return true;
		if (asset.getTypeId() == 17367) return true;
		if (asset.getTypeId() == 17368) return true;
		if (asset.getTypeId() == 2263) return true;
		if (asset.getTypeId() == 23) return true;
		if (asset.getTypeId() == 24445) return true;
		if (asset.getTypeId() == 28570) return true;
		if (asset.getTypeId() == 3293) return true;
		if (asset.getTypeId() == 3296) return true;
		if (asset.getTypeId() == 3297) return true;
		if (asset.getTypeId() == 33003) return true;
		if (asset.getTypeId() == 33005) return true;
		if (asset.getTypeId() == 33007) return true;
		if (asset.getTypeId() == 33009) return true;
		if (asset.getTypeId() == 33011) return true;
		if (asset.getTypeId() == 3465) return true;
		if (asset.getTypeId() == 3466) return true;
		if (asset.getTypeId() == 3467) return true;
		if (asset.getTypeId() == 3468) return true;
		if (asset.getTypeId() == 41567) return true;
		if (asset.getTypeId() == 60) return true; // Asset Safety Wrap
		if (asset.getName().contains( "Container" )) return true;
		return asset.getName().contains( "Wrap" );
	}

//	private static void transform( final GetCharactersCharacterIdAssets200Ok esiAsset,
//	                               NeoAsset asset ) {
//	}


//	private GetCharactersCharacterIdAsset2NeoAssetConverter() {}
//
//	// - B U I L D E R
//	public static class Builder {
//		private GetCharactersCharacterIdAsset2NeoAssetConverter onConstruction;
//
//		public Builder() {
//			this.onConstruction = new GetCharactersCharacterIdAsset2NeoAssetConverter();
//		}
//
//		public GetCharactersCharacterIdAsset2NeoAssetConverter build() {
//			return this.onConstruction;
//		}
//	}
}
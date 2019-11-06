package org.dimensinfin.eveonline.neocom.integration.support;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;

public class GetCharactersCharacterIdAssets200OkDeserializer extends StdDeserializer<GetCharactersCharacterIdAssets200Ok> {
	public GetCharactersCharacterIdAssets200OkDeserializer( final Class<?> vc ) {
		super( vc );
	}

	@Override
	public GetCharactersCharacterIdAssets200Ok deserialize( final JsonParser p, final DeserializationContext ctxt ) throws IOException, JsonProcessingException {
		TreeNode tn = p.readValueAsTree();
		final GetCharactersCharacterIdAssets200Ok instance = new GetCharactersCharacterIdAssets200Ok();
		if (tn.get( "is_blueprint_copy" ) != null)
			instance.setIsBlueprintCopy( Boolean.parseBoolean( tn.get( "is_blueprint_copy" ).toString() ) );
		if (tn.get( "is_singleton" ) != null)
			instance.setIsSingleton( Boolean.parseBoolean( tn.get( "is_singleton" ).toString() ) );
		if (tn.get( "item_id" ) != null)
			instance.setItemId( Long.parseLong( tn.get( "item_id" ).toString() ) );
//		if (tn.get( "location_flag" ) != null)
//			instance.setLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.  ( tn.get( "location_flag" ).toString() ) );
		if (tn.get( "location_id" ) != null)
			instance.setLocationId( Long.parseLong( tn.get( "location_id" ).toString() ) );
//		if (tn.get( "location_type" ) != null)
//			instance.setLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.valueOf( tn.get( "location_type" ).toString() ) );
		if (tn.get( "quantity" ) != null)
			instance.setQuantity( Integer.parseInt( tn.get( "quantity" ).toString() ) );
		if (tn.get( "type_id" ) != null)
			instance.setTypeId( Integer.parseInt( tn.get( "type_id" ).toString() ) );

		final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum[] values = GetCharactersCharacterIdAssets200Ok.LocationFlagEnum
				.values();

		return instance;
	}
}
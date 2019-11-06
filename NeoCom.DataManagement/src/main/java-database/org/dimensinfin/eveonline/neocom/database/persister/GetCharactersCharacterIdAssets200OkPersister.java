package org.dimensinfin.eveonline.neocom.database.persister;

import java.io.IOException;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.LongStringType;
import com.j256.ormlite.support.DatabaseResults;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;

public class GetCharactersCharacterIdAssets200OkPersister extends LongStringType {
	private static final GetCharactersCharacterIdAssets200OkPersister singleTon = new GetCharactersCharacterIdAssets200OkPersister();
	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.registerModule( new JodaModule() );
	}

	public static GetCharactersCharacterIdAssets200OkPersister getSingleton() {
		return singleTon;
	}

	private GetCharactersCharacterIdAssets200OkPersister() {
		super( SqlType.LONG_STRING, new Class<?>[0] );
	}

	/**
	 * @param sqlType Type of the class as it is persisted in the databases.
	 * @param classes Associated classes for this type. These should be specified if you want this type to be always used
	 */
	private GetCharactersCharacterIdAssets200OkPersister( final SqlType sqlType, final Class<?>[] classes ) {
		super( sqlType, classes );
	}

	@Override
	public Object parseDefaultString( final FieldType fieldType, final String defaultStr ) /*throws SQLException*/ {
		try {
			return (GetCharactersCharacterIdAssets200Ok) mapper.readValue( defaultStr,
					GetCharactersCharacterIdAssets200Ok.class );
		} catch (final IOException sqle) {
//			throw new SQLException( "exception converting back GetCharactersCharacterIdAssets200Ok field: " +
//					sqle.getMessage() );
		}
		return null;
	}

	@Override
	public Object sqlArgToJava( FieldType fieldType, Object sqlArg, int columnPos ) throws SQLException {
		try {
			return (GetCharactersCharacterIdAssets200Ok) mapper.readValue( (String) sqlArg,
					GetCharactersCharacterIdAssets200Ok.class );
		} catch (final IOException sqle) {
			throw new SQLException( "Exception converting back GetCharactersCharacterIdAssets200Ok field: " +
					sqle.getMessage() );
		}
	}

	@Override
	public Object javaToSqlArg( FieldType fieldType, Object obj ) throws SQLException {
		try {
			return mapper.writeValueAsString( obj );
		} catch (final JsonProcessingException jpe) {
			throw new SQLException( "Exception parsing back GetCharactersCharacterIdAssets200Ok field: " +
					jpe.getMessage() );
		}
	}

	@Override
	public Object resultToSqlArg( final FieldType fieldType, final DatabaseResults results, final int columnPos ) throws SQLException {
		return results.getString( columnPos );
	}
}
package org.dimensinfin.eveonline.neocom.adapter;

public interface IConfigurationProvider {
	int contentCount();

	String getResourceString( final String key );

	String getResourceString( final String key, final String defaultValue );

	Integer getResourceInteger( final String key );

	Integer getResourceInteger( final String key, final Integer defaultValue );
}

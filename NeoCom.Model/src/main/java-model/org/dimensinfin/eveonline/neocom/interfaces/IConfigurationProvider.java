package org.dimensinfin.eveonline.neocom.interfaces;

public interface IConfigurationProvider {
//	IConfigurationProvider initialize();

	int contentCount();

	String getResourceString( final String key );

	String getResourceString( final String key, final String defaultValue );
}

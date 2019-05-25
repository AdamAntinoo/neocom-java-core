package org.dimensinfin.eveonline.neocom.domain;

public class EsiLocation {
	private int locationId;
	private String systemName = "Default System Name";

	public EsiLocation( final int locationId ) {
		this.locationId=locationId;
	}

	public String getSystemName() {
		return systemName;
	}
}

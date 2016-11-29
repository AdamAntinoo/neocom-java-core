package com.beimin.eveapi.character.sheet;

import java.io.Serializable;

public class ApiCorporationTitle implements Serializable {
	private static final long	serialVersionUID	= 4275205848061778398L;
	private long							titleID;
	private String						titleName;

	public long getTitleID() {
		return titleID;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleID(final long titleID) {
		this.titleID = titleID;
	}

	public void setTitleName(final String titleName) {
		this.titleName = titleName;
	}
}
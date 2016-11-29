package com.beimin.eveapi.character.sheet;

import java.io.Serializable;

public class ApiSkill implements Serializable {
	private static final long	serialVersionUID	= -5476846990164733253L;
	private int								typeID;
	private int								level							= 0;
	private int								skillpoints				= 0;
	private boolean						unpublished				= false;

	public int getLevel() {
		return level;
	}

	public int getSkillpoints() {
		return skillpoints;
	}

	public int getTypeID() {
		return typeID;
	}

	public boolean isUnpublished() {
		return unpublished;
	}

	public void setLevel(final int level) {
		this.level = level;
	}

	public void setSkillpoints(final int skillpoints) {
		this.skillpoints = skillpoints;
	}

	public void setTypeID(final int typeID) {
		this.typeID = typeID;
	}

	public void setUnpublished(final boolean unpublished) {
		this.unpublished = unpublished;
	}
}
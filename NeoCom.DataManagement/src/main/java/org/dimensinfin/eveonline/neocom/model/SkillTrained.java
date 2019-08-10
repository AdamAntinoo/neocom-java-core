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

import org.dimensinfin.eveonline.neocom.domain.EveItem;

// - CLASS IMPLEMENTATION ...................................................................................
public class SkillTrained extends ANeoComEntity {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static final long serialVersionUID = 8494419560386675752L;
	//	private static Logger logger = LoggerFactory.getLogger("SkillInTraining");

	// - F I E L D - S E C T I O N ............................................................................
	private int skillId = -1;
	private long skillpointsInSkill = 0;
	private int trainedSkillLevel = 0;
	private int activeSkillLevel = 0;

	private transient EveItem skillItem ;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- G E T T E R S   &   S E T T E R S
	public int getSkillId() {
		return skillId;
	}

	public String getSkillName() {
		if (null == this.skillItem)
			this.skillItem = accessGlobal().getSDEDBHelper().searchItem4Id(this.skillId);
		return this.skillItem.getName();
	}

	public long getSkillpointsInSkill() {
		return skillpointsInSkill;
	}

	public int getTrainedSkillLevel() {
		return trainedSkillLevel;
	}

	public int getActiveSkillLevel() {
		return activeSkillLevel;
	}

	public SkillTrained setSkillId( int skillId ) {
		this.skillId = skillId;
		return this;
	}

	public int getGroupId() {
		if (null == skillItem) this.skillItem = accessGlobal().searchItem4Id(this.skillId);
		return this.skillItem.getGroupId();
	}

	public String getGroupName() {
		if (null == skillItem) this.skillItem = accessGlobal().searchItem4Id(this.skillId);
		return this.skillItem.getGroupName();
	}

//	public GetUniverseGroupsGroupIdOk getGroupInstance() {
//		if (null == skillItem) this.skillItem = accessGlobal().searchItem4Id(this.skillId);
//		return this.skillItem.getGroup();
//	}

	public SkillTrained setSkillpointsInSkill( final long skillpointsInSkill ) {
		this.skillpointsInSkill = skillpointsInSkill;
		return this;
	}

	public SkillTrained setTrainedSkillLevel( final int trainedSkillLevel ) {
		this.trainedSkillLevel = trainedSkillLevel;
		return this;
	}

	public SkillTrained setActiveSkillLevel( final int activeSkillLevel ) {
		this.activeSkillLevel = activeSkillLevel;
		return this;
	}
}
// - UNUSED CODE ............................................................................................

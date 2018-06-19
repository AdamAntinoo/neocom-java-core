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

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// - CLASS IMPLEMENTATION ...................................................................................
public class Skill extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 8494419560386675752L;
	private static Logger logger = LoggerFactory.getLogger("Skill");

	// - F I E L D - S E C T I O N ............................................................................
	private int skillId = -1;
	private DateTime finishDate = null;
	private DateTime startDate = null;
	private int finishedLevel = 0;
	private int queuePosition = 0;
	private int trainingStartSp = 0;
	private int levelEndSp = 0;
	private int levelStartSp = 0;

	private transient EveItem skillItem = null;

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

	public DateTime getFinishDate() {
		return finishDate;
	}

	public DateTime getStartDate() {
		return startDate;
	}

	public double getPctDone() {
		if (null == startDate) return 0.0;
		final DateTime now = DateTime.now();
		if (startDate.isBeforeNow()) {
			// The skill is on training
			double secondsTotal = (this.finishDate.getMillis() - this.startDate.getMillis()) / 1000.0;
			double secondsDone = (this.finishDate.getMillis() - DateTime.now().getMillis()) / 1000.0;
			return secondsDone / secondsTotal;
		} else return 0.0;
	}

	public int getFinishedLevel() {
		return finishedLevel;
	}

	public int getQueuePosition() {
		return queuePosition;
	}

	public int getTrainingStartSp() {
		return trainingStartSp;
	}

	public int getLevelEndSp() {
		return levelEndSp;
	}

	public int getLevelStartSp() {
		return levelStartSp;
	}

	public Skill setSkillId( int skillId ) {
		this.skillId = skillId;
		return this;
	}

	public Skill setFinishDate( DateTime finishDate ) {
		this.finishDate = finishDate;
		return this;
	}

	public Skill setStartDate( DateTime startDate ) {
		this.startDate = startDate;
		return this;
	}

	public Skill setFinishedLevel( int finishedLevel ) {
		this.finishedLevel = finishedLevel;
		return this;
	}

	public Skill setQueuePosition( int queuePosition ) {
		this.queuePosition = queuePosition;
		return this;
	}

	public Skill setTrainingStartSp( int trainingStartSp ) {
		this.trainingStartSp = trainingStartSp;
		return this;
	}

	public Skill setLevelEndSp( int levelEndSp ) {
		this.levelEndSp = levelEndSp;
		return this;
	}

	public Skill setLevelStartSp( int levelStartSp ) {
		this.levelStartSp = levelStartSp;
		return this;
	}
}

// - UNUSED CODE ............................................................................................

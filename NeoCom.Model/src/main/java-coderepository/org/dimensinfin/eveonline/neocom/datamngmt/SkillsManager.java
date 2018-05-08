//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.manager;

import java.util.Set;

import org.dimensinfin.android.interfaces.INamed;
import org.dimensinfin.eveonline.neocom.model.Pilot;

import com.beimin.eveapi.model.pilot.Skill;
import com.beimin.eveapi.model.pilot.SkillQueueItem;
import com.beimin.eveapi.response.pilot.SkillInTrainingResponse;

// - CLASS IMPLEMENTATION ...................................................................................
public class SkillsManager extends AbstractManager implements INamed {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long				serialVersionUID	= -5398590391396592182L;

	// - F I E L D - S E C T I O N ............................................................................
	private Set<Skill>							skills						= null;
	private Set<SkillQueueItem>			skillQueue				= null;
	private SkillInTrainingResponse	skillInTraining		= null;
	private final String						iconName					= "skills.png";

	// - P R I V A T E   I N T E R C H A N G E   V A R I A B L E S

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public SkillsManager(final Pilot pilot) {
		super(pilot);
		// Get the skills references from the Pilot.
		skills = pilot.characterSheet.getSkills();
		skillQueue = pilot.skillQueue;
		skillInTraining = pilot.skillInTraining;
		jsonClass = "SkillsManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	@Override
	public String getOrderingName() {
		return "Skills Manager";
	}

	@Override
	public SkillsManager initialize() {
		//		this.accessAllBlueprints();
		return this;
	}
}

// - UNUSED CODE ............................................................................................

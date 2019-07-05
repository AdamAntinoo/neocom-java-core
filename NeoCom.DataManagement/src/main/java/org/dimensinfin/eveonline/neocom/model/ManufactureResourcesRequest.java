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

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.eveonline.neocom.industry.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class ManufactureResourcesRequest extends ANeoComEntity {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("ManufactureResourcesRequest");

	// - F I E L D - S E C T I O N ............................................................................
	private int jobBlueprintId = -5;
	private EveItem jobBlueprint = null;
	private int copies = 1;
	private List<Resource> lom = new ArrayList<>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ManufactureResourcesRequest( final int bpid ) {
		super();
		this.jobBlueprintId = bpid;
		jobBlueprint = accessGlobal().searchItem4Id(bpid);
//		jsonClass="ManufactureResourcesRequest";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- G E T T E R S   &   S E T T E R S
	public int getJobBlueprintId() {
		return jobBlueprintId;
	}

	public EveItem getJobBlueprint() {
		return jobBlueprint;
	}

	public int getCopies() {
		return copies;
	}

	public List<Resource> getLom() {
		return lom;
	}

	public ManufactureResourcesRequest setJobBlueprintId( final int jobBlueprintId ) {
		this.jobBlueprintId = jobBlueprintId;
		return this;
	}

	public ManufactureResourcesRequest setNumberOfCopies( final int copies ) {
		this.copies = copies;
		// Update the lom if already defined to the new number of copies.
		for(Resource res : lom){
			res.setStackSize(copies);
		}
		return this;
	}

	public ManufactureResourcesRequest setLOM( final List<Resource> lom ) {
		this.lom = lom;
		// Update the lom if already defined to the new number of copies.
		for(Resource res : lom){
			res.setStackSize(copies);
		}
		return this;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("ManufactureResourcesRequest [ ")
				.append("jobBlueprintId: ").append(jobBlueprintId).append(" ");
		if (null != jobBlueprint)
			buffer.append("name: ").append(jobBlueprint.getName()).append(" ");
		buffer.append("copies: ").append(copies).append(" ")
				.append("]")
				.append("->").append(super.toString());
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]

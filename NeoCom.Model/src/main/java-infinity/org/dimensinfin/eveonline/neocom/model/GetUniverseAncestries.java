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

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class GetUniverseAncestries {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("GetUniverseAncestries");

	// - F I E L D - S E C T I O N ............................................................................
	@SerializedName("id")
	private Integer id = null;
	@SerializedName("name")
	private String name = null;
	@SerializedName("bloodline_id")
	private Integer bloodlineId = null;
	@SerializedName("description")
	private String description = null;
	@SerializedName("short_description")
	private String shortDescription = null;
	@SerializedName("icon_id")
	private Integer iconId = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public GetUniverseAncestries() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getBloodlineId() {
		return bloodlineId;
	}

	public String getDescription() {
		return description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public Integer getIconId() {
		return iconId;
	}

	public GetUniverseAncestries setId( final Integer id ) {
		this.id = id;
		return this;
	}

	public GetUniverseAncestries setName( final String name ) {
		this.name = name;
		return this;
	}

	public GetUniverseAncestries setBloodlineId( final Integer bloodlineId ) {
		this.bloodlineId = bloodlineId;
		return this;
	}

	public GetUniverseAncestries setDescription( final String description ) {
		this.description = description;
		return this;
	}

	public GetUniverseAncestries setShortDescription( final String shortDescription ) {
		this.shortDescription = shortDescription;
		return this;
	}

	public GetUniverseAncestries setIconId( final Integer iconId ) {
		this.iconId = iconId;
		return this;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		return new StringBuffer("GetUniverseAncestries [ ")
				.append("id:").append(id).append(" ")
				.append("]")
//				.append("->").append(super.toString())
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]

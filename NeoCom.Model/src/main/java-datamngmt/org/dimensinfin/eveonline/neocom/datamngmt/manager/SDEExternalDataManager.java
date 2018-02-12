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
package org.dimensinfin.eveonline.neocom.datamngmt.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.annimon.stream.Stream;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

/**
 * This static class centralizes all the functionality to access data. It will provide a consistent api to the rest
 * of the application and will hide the internals of how that data is obtained, managed and stored.
 * All thet now are direct database access or cache access or even Model list accesses will be hidden under an api
 * that will decide at any point from where to get the information and if there are more jobs to do to keep
 * that information available and up to date.
 * <p>
 * The initial release will start transferring the ModelFactory functionality.
 *
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class SDEExternalDataManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static ObjectMapper yamlMapper = null;

	static {
		final YAMLFactory yamlFactory = new YAMLFactory();
		yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);

		yamlMapper = new ObjectMapper(yamlFactory);
		yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		yamlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private static final String inventoryFlagFileLocation = GlobalDataManager.getResourceString("R.sde.external.yaml.locationpath")
			+ GlobalDataManager.getResourceString("R.sde.external.yaml.inventoryFlag");
	private static final HashMap<Integer, InventoryFlag> flagStore = new HashMap();

	static {
		// Loading YAML data during initialization.
		try {
			final File sourceFile = new File(inventoryFlagFileLocation);
			final List<InventoryFlag> flagList = yamlMapper.readValue(sourceFile, new TypeReference<List<InventoryFlag>>() {
			});
			flagStore.clear();
			// Transform the list into map to simply the search for the elements.
			Stream.of(flagList)
					.forEach(( flag ) -> {
						flagStore.put(flag.getFlagID(), flag);
					});
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (RuntimeException rtex) {
			rtex.printStackTrace();
		}

	}
	public static InventoryFlag searchFlag4Id( final int identifier ) {
			return flagStore.get(identifier);
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
//// - CLASS IMPLEMENTATION ...................................................................................
//	public static class InventoryFlagStore {
//		// - S T A T I C - S E C T I O N ..........................................................................
//
//		// - F I E L D - S E C T I O N ............................................................................
//		@JsonProperty
//		private List<InventoryFlag> flagList = new ArrayList<>();
//
//		// - C O N S T R U C T O R - S E C T I O N ................................................................
//
//		// - M E T H O D - S E C T I O N ..........................................................................
//		protected List<InventoryFlag> getFlagList() {
//			return flagList;
//		}
//
//		protected void setFlagList( final List<InventoryFlag> flagList ) {
//			this.flagList = flagList;
//		}
//
//		public InventoryFlag searchFlag4Id( final int identifier ) {
//			for (InventoryFlag flag : flagList) {
//				if (flag.getFlagID() == identifier) return flag;
//			}
//			return null;
//		}
//	}
//
	// - CLASS IMPLEMENTATION ...................................................................................
	public static class InventoryFlag {
		// - S T A T I C - S E C T I O N ..........................................................................

		// - F I E L D - S E C T I O N ............................................................................
		private int flagID = -1;
		private String flagName = null;
		private String flagText = null;
		private int orderID = 0;

		// - C O N S T R U C T O R - S E C T I O N ................................................................

		// - M E T H O D - S E C T I O N ..........................................................................
		// --- G E T T E R S   &   S E T T E R S
		public int getFlagID() {
			return flagID;
		}

		public String getFlagName() {
			return flagName;
		}

		public String getFlagText() {
			return flagText;
		}

		public int getOrderID() {
			return orderID;
		}

		public void setFlagID( final int flagID ) {
			this.flagID = flagID;
		}

		public void setFlagName( final String flagName ) {
			this.flagName = flagName;
		}

		public void setFlagText( final String flagText ) {
			this.flagText = flagText;
		}

		public void setOrderID( final int orderID ) {
			this.orderID = orderID;
		}
	}
}

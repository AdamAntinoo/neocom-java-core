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
package org.dimensinfin.eveonline.neocom.testblock.model;

import org.dimensinfin.eveonline.neocom.database.ISDEDBHelper;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Test unit for openning and data access to the different queries supported by the SDE Eve Online game model database. The
 * test will cover all the different queries adn will also test the cases where the parameters are not expected or the use of
 * caches.
 *
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class ModelCreationTestUnit {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(ModelCreationTestUnit.class);

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Test
	public void testEveItem34( final EveItem itemPattern ) {
		Assert.assertEquals(itemPattern.getJsonClass(),"EveItem");
		Assert.assertEquals(itemPattern.getItemId(),34);
		Assert.assertEquals(itemPattern.getName(),"Tritanium");
		Assert.assertEquals(itemPattern.getGroupId(),22);
		Assert.assertEquals(itemPattern.getGroupName(),"Materials");
		Assert.assertEquals(itemPattern.getCategoryId(),22);
		Assert.assertEquals(itemPattern.getCategoryName(),"Materials");
		Assert.assertEquals(itemPattern.getIndustryGroup(), EIndustryGroup.OREMATERIALS);
		Assert.assertFalse(itemPattern.isBlueprint());

		// Calculated values.
		Assert.assertEquals(itemPattern.getBaseprice(),1.00,0.01);
		Assert.assertNotNull(itemPattern.getHighestBuyerPrice());
		Assert.assertNotNull(itemPattern.getLowestSellerPrice());
		Assert.assertEquals(itemPattern.getHighestBuyerPrice().getPrice(),itemPattern.getPrice(),0.01);
		Assert.assertEquals(itemPattern.getVolume(),0.01,0.01);

	}
}
// - UNUSED CODE ............................................................................................

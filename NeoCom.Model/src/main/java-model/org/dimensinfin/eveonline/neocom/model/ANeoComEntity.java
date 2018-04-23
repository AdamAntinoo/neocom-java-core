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

import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.eveonline.neocom.core.NeoComException;
import org.dimensinfin.eveonline.neocom.core.NeocomRuntimeException;
import org.dimensinfin.eveonline.neocom.database.ISDEDBHelper;
import org.dimensinfin.eveonline.neocom.interfaces.IDatabaseEntity;
import org.dimensinfin.eveonline.neocom.interfaces.IGlobalConnector;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * This utility class is responsible to connect the Model classes that require database access and that are
 * considered <b>Entities</b> to the <b>Global</b> data management structures but broking a direct compilation module
 * dependency that should result into a circular dependency or the complete integration of modules.
 * <p>
 * During initialization the Global structures should connect the SDE and NeoCom databases to this utility class so
 * all Entities can access the databases maintaining module isolation.
 *
 * @author Adam Antinoo
 */
public abstract class ANeoComEntity extends AbstractPropertyChanger implements IDatabaseEntity {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static ISDEDBHelper SDEHelper = null;
//	private static INeoComDBHelper neocomDBHelper = null;
	private static IGlobalConnector globalConnector=null;

	/**
	 * Required initialization step to connect the Model classes to the Global application connector source for all the
	 * external functionality implemented on the core Application.
	 *
	 * @param global connection to the Global connector.
	 */
	public static void connectGlobal( final IGlobalConnector global ) {
		globalConnector = global;
	}
	public static IGlobalConnector accessGlobal( ) throws NeocomRuntimeException {
		if (null != globalConnector) return globalConnector;
		else
			throw new NeocomRuntimeException("[ANeoComEntity.accessGlobal]> Global connector not connected to Model. Database " +
					                          "disabled as other application functionality.");
	}
	/**
	 * Required initialization step to connect the Model classes to the SDE database helper.
	 *
	 * @param helper connection to the SDE database.
	 */
	public static void connectSDEHelper( final ISDEDBHelper helper ) {
		SDEHelper = helper;
	}

//	/**
//	 * Required initialization step to connect the Model classes to the NeoCom database helper.
//	 *
//	 * @param helper connection to the NeoCom database.
//	 */
//	public static void connectNeoComHelper( final INeoComDBHelper helper ) {
//		neocomDBHelper = helper;
//	}

	public static ISDEDBHelper accessSDEDBHelper() throws NeocomRuntimeException {
		if (null != SDEHelper) return SDEHelper;
		else
			throw new NeocomRuntimeException("[ANeoComEntity.accessSDEDBHelper]> Database Helper not connected to Model. Database " +
					                          "disabled.");
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
}

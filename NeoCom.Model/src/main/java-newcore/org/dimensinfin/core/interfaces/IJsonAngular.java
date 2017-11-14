//	PROJECT:        corebase.model (CORE.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.6.
//	DESCRIPTION:		Library that defines the model classes to implement the core for a GEF based
//									Model-View-Controller. Code is as neutral as possible and made to be reused
//									on all Java development projects.
//                  Added een more generic code to develop other Model-View-Controller patterns.
package org.dimensinfin.core.interfaces;

import java.io.Serializable;

/**
 * The interface defines a method to be used when nodes of this type get transferred to Angular projects when they get
 * serialized to Json strings. This class identification helps to reconstruct the right object class at the client
 * environment.
 */
// - INTERFACE IMPLEMENTATION ...............................................................................
public interface IJsonAngular extends Serializable {
	public String getJsonClass ();
}

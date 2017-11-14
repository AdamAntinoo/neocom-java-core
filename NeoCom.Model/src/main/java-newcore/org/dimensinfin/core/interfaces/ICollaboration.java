//	PROJECT:        corebase.model (CORE.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.6.
//	DESCRIPTION:		Library that defines the model classes to implement the core for a GEF based
//									Model-View-Controller. Code is as neutral as possible and made to be reused
//									on all Java development projects.
//                  Added een more generic code to develop other Model-View-Controller patterns.
package org.dimensinfin.core.interfaces;

import java.util.ArrayList;

/**
 * This is the interface of nodes that are able to collaborate more items to a Model-View-Controller
 * pattern. This king of implementation frees model nodes from the single parent-children hierarchy
 * that was the common behavior for GEF node trees. New MVC will convert graphs to trees and then to lists for model renderization on list views.
 */
// - INTERFACE IMPLEMENTATION ...............................................................................
public interface ICollaboration {
	ArrayList<ICollaboration> collaborate2Model (String variation);
}

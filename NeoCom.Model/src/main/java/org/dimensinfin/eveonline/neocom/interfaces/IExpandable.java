//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.interfaces;

import org.dimensinfin.core.interfaces.IViewableNode;
import org.dimensinfin.core.model.AbstractComplexNode;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IExpandable extends IViewableNode {
	@Override
	public boolean collapse();

	@Override
	public boolean expand();

	@Override
	public boolean isEmpty();

	public boolean isExpandable();

	@Override
	public boolean isExpanded();

	@Override
	public boolean isRenderWhenEmpty();

	@Override
	public AbstractComplexNode setExpanded(final boolean newState);

	@Override
	public AbstractComplexNode setRenderWhenEmpty(final boolean renderWhenEmpty);

	@Override
	public boolean toggleVisible();
}

// - UNUSED CODE ............................................................................................

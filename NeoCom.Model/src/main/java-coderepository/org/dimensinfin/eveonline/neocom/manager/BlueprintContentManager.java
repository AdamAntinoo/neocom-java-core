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

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.interfaces.IAssetContainer;
import org.dimensinfin.eveonline.neocom.model.ExtendedLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This is another class of Content Manager. Instead of managing the contend on demand we should be sure that
 * all the contents are accessible before the methods are called. There is no downloaded state and contents
 * are always accessible.
 * 
 * @author Adam Antinoo
 */
public class BlueprintContentManager extends AbstractContentManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 3670100761380839436L;

	// - F I E L D - S E C T I O N ............................................................................
	private int								contentCount			= 0;
	private int								containerCount		= 0;
	protected final List<NeoComBlueprint> _blueprints = new Vector<NeoComBlueprint>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public BlueprintContentManager (final ExtendedLocation newdelegate) {
		super(newdelegate);
		jsonClass = "BlueprintContentManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * We add the new Asset to the Location contents. If the Asset is a Container then it is added to the list
	 * of Containers.
	 */
	public int addBlueprint(final NeoComBlueprint child) {
		if (null != child) {
			if (null != child) {
				_blueprints.add(child);
			}
			if (child instanceof IAssetContainer) {
				containerCount++;
			} else {
				contentCount++;
			}
		}
		return contentCount;
	}
	public List<ICollaboration> collaborate2Model(final String variant) {
		List<ICollaboration> results = new ArrayList<ICollaboration>();
		results.addAll(_blueprints);
		return results;
	}
	public List<NeoComBlueprint> getBlueprints() {
		return _blueprints;
	}

	public int getContentSize() {
		return _blueprints.size();
	}

	public boolean isEmpty() {
		return (_blueprints.size() < 1) ? true : false;
	}

	public int getContainerCount() {
		return containerCount;
	}
}

// - UNUSED CODE ............................................................................................

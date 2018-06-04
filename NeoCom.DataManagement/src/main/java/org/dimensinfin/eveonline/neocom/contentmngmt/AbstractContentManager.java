//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.contentmngmt;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.interfaces.IContentManager;
import org.dimensinfin.eveonline.neocom.model.ExtendedLocation;
import org.dimensinfin.eveonline.neocom.database.entity.NeoComAsset;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractContentManager implements IContentManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long					serialVersionUID	= 7033665841859754757L;

	// - F I E L D - S E C T I O N ............................................................................
	protected String									jsonClass					= "AbstractContentManager";
	protected ExtendedLocation				parent						= null;
	protected final List<NeoComAsset>	_contents					= new Vector<NeoComAsset>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractContentManager() {
		jsonClass = "AbstractContentManager";
	}

	public AbstractContentManager(final ExtendedLocation newparent) {
		this();
		parent = newparent;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int add(final NeoComAsset child) {
		if (null != child) {
			_contents.add(child);
		}
		return _contents.size();
	}

	public List<ICollaboration> collaborate2Model(final String variant) {
		List<ICollaboration> results = new ArrayList<ICollaboration>();
		results.addAll(_contents);
		return results;
	}

//	public long getID() {
//		return parent.getRealId();
//	}

	public List<NeoComAsset> getContents() {
		return _contents;
	}

	public int getContentSize() {
		return _contents.size();
	}

	public boolean isEmpty() {
		return (_contents.size() < 1) ? true : false;
	}

	public String getJsonClass() {
		return jsonClass;
	}
}

// - UNUSED CODE ............................................................................................

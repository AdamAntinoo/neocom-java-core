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

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.interfaces.IContentManager;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

// - CLASS IMPLEMENTATION ...................................................................................
public class DefaultAssetsContentManager extends AbstractContentManager implements IContentManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger						logger		= Logger.getLogger("DefaultAssetsContentManager");

	// - F I E L D - S E C T I O N ............................................................................
	protected boolean downloaded = false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DefaultAssetsContentManager() {
		super();
		jsonClass = "DefaultAssetsContentManager";
	}

	public DefaultAssetsContentManager(final EveLocation newparent) {
		super(newparent);
		jsonClass = "DefaultAssetsContentManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * This method will access the database to get the assets contained at that Location. It is the default
	 * behavior for a ContentManager, to get all the related items with no filtering.
	 */
	@Override
	public List<AbstractComplexNode> collaborate2Model(final String variant) {
		List<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		// If the contents are already downloaded chanin the collaboration calls.
		if (downloaded) {
			results.addAll(contents);
		} else {
			//Go to the database and get the assets. 
			contents.clear();
			contents.addAll(ModelAppConnector.getSingleton().getDBConnector().queryLocationContents(this.getID()));
			downloaded = true;
			results.addAll(contents);
		}
		return results;
	}

	@Override
	public List<NeoComAsset> getContents() {
		if (!downloaded) {
			// Get the assets from the database.
			contents.clear();
			contents.addAll(ModelAppConnector.getSingleton().getDBConnector().queryLocationContents(this.getID()));
			downloaded = true;
		}
		return contents;
	}

	@Override
	public int getContentSize() {
		if (downloaded)
			return contents.size();
		else
			// Go to the database and get an approximate count of the assets that are at this Location.
			return ModelAppConnector.getSingleton().getDBConnector().totalLocationContentCount(this.getID());
	}

	@Override
	public boolean isEmpty() {
		if (downloaded)
			if (contents.size() < 1)
				return true;
			else
				return false;
		else
			return false;
	}
}

// - UNUSED CODE ............................................................................................

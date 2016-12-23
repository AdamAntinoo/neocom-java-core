//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.datasource;

//- CLASS IMPLEMENTATION ...................................................................................
import java.util.HashMap;

import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.model.NeoComApiKey;
import org.dimensinfin.evedroid.storage.AppModelStore;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * Generates the list of keys and then the character authenticated for each key. By default the keys are
 * expanded but the user may choose to collapse them and that information will be stored inside the model. The
 * current version does not store the expand/collapse state at any other place.
 * 
 * @author Adam Antinoo
 */
public final class PilotListDataSource extends SpecialDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 4576522670385611140L;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotListDataSource(final DataSourceLocator locator, final IPartFactory partFactory) {
		super(locator, partFactory);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * This method is called to initialize the model structures. Those structures are copies or transformations
	 * from the main model stored in the <code>store</code> reference. <br>
	 * This is the method to initialize the copy of the model structures on the DataSource. Every time this
	 * method is called, the complete model is recreated. There are two ways to recreate it, comparing with the
	 * old copy and inserting/deleting different nodes or recreating completely the new model copy. Once this
	 * method is called we can create the depending part hierarchy. In the current implementation we always
	 * recreate the model from scratch.<br>
	 * The resulting model always has a RootNode and the contents are stored as children of that node. The model
	 * only deals with the first level so each on the childs will create their own set of the model on call when
	 * required by the model transformation.
	 */
	public RootNode collaborate2Model() {
		SpecialDataSource.logger.info(">> [PilotListDataSource.collaborate2Model]");
		//		AppModelStore store = AppModelStore.getSingleton();
		// The model contains the list of current registered api keys with their characters.
		HashMap<Integer, NeoComApiKey> keys = AppModelStore.getSingleton().getApiKeys();
		this.setDataModel(new RootNode());
		// Add all the characters to the new root
		for (NeoComApiKey key : keys.values()) {
			_dataModelRoot.addChild(key);
			SpecialDataSource.logger
					.info("-- [PilotListDataSource.collaborate2Model]> Adding " + key.getKey() + " to the _dataModelRoot");
		}
		SpecialDataSource.logger.info("<< [PilotListDataSource.collaborate2Model]");
		return _dataModelRoot;
	}
}
// - UNUSED CODE ............................................................................................

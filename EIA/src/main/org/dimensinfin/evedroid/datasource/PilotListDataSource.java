//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.datasource;

import java.util.HashMap;

import org.dimensinfin.android.mvc.core.IPartFactory;
import org.dimensinfin.android.mvc.core.RootNode;
import org.dimensinfin.evedroid.EVEDroidApp;
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
	 * method is called we can create the depending part hierarchy. <br>
	 * Of this instance of DataSource the method gets to the model store and searches for the selected fitting.
	 * That fitting collaborates to the view giving its contents in 6 blocks: high, med, low, rigs, drones and
	 * cargo that are the start parts for the model. <br>
	 * WARNING. This implementation is connected to the old unchanged APIKey/EveChar model. The code should be
	 * changed once the new model is implemented but the API remains witout changes.
	 */
	public RootNode collaborate2Model() {
		logger.info(">> PilotListDataSource.collaborate2Model");
		AppModelStore store = EVEDroidApp.getAppStore();
		// The model is the list of current regtistered api keys with their characters.
		HashMap<Integer, NeoComApiKey> keys = store.getApiKeys();
		// Add the keys to the model root node. If the root is already on place then the model is already loaded.
		//		if (null == _dataModelRoot) {
		setDataModel(new RootNode());
		//		}
		// Add all the nodes to the new root
		for (NeoComApiKey key : keys.values()) {
			_dataModelRoot.addChild(key);
			logger.info("-- PilotListDataSource.collaborate2Model-Adding " + key.getKeyID() + " to the _dataModelRoot");
		}
		logger.info("<< PilotListDataSource.collaborate2Model");
		return _dataModelRoot;
	}

	//	@Override
	//	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
	//		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		Collections.sort(this._root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_APIID_DESC));
	//		for (final AbstractAndroidPart node : this._root) {
	//			result.add(node);
	//			// Check if the node is expanded but test the model. Then add its
	//			// children.
	//			if (node.isExpanded()) {
	//				final ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
	//				result.addAll(grand);
	//			}
	//		}
	//		this._adapterData = result;
	//		return result;
	//	}

	@Override
	public void createPartsHierarchy() {
		createContentHierarchy();
	}
}
// - UNUSED CODE ............................................................................................

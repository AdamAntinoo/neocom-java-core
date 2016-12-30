//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.eveonline.neocom.storage;

// - IMPORT SECTION .........................................................................................
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.core.model.IModelStore;
import org.dimensinfin.core.parser.IPersistentHandler;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.model.Fitting;
import org.dimensinfin.eveonline.neocom.model.NeoComApiKey;

// - CLASS IMPLEMENTATION ...................................................................................
public class UserModelPersistenceHandler implements IPersistentHandler {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 3528207564879256211L;
	private static Logger			logger						= Logger.getLogger("UserModelPersistenceHandler");	//$NON-NLS-1$

	// - F I E L D - S E C T I O N ............................................................................
	private AppModelStore			store;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public UserModelPersistenceHandler() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//	public void clearUpdate() {
	//		// TODO Auto-generated method stub
	//
	//	}
	//
	//	public String getLocation() {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}

	//	public boolean loadContents() {
	//		// TODO Auto-generated method stub
	//		return false;
	//	}

	public AppModelStore getStore() {
		return store;
	}

	//	public ObjectInput prepareStorageInput() {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
	//
	//	public ObjectOutput prepareStorageOutput() {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}

	/**
	 * Retrieves any information of the previous state of the model from the persistent storage. This particular
	 * implementation reads the api_list.txt file (for compatibility with Aura). Because we cannot go to the net
	 * to update any of the information we only will read any stored info. Update of the structures is managed
	 * from outside the model by their clients on the UI.<br>
	 * Proper initialization needs that the access to the file be synchronized.
	 */
	public synchronized boolean restore() {
		UserModelPersistenceHandler.logger.info(">> UserModelPersistenceHandler.restore");
		File modelStoreFile = AppConnector.getStorageConnector()
				.accessAppStorage(AppConnector.getResourceString(R.string.userdatamodelfilename));
		try {
			// Read the contents of the character information.
			final BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(modelStoreFile));
			final ObjectInputStream input = new ObjectInputStream(buffer);
			try {
				this.getStore().setApiKeys((HashMap<Integer, NeoComApiKey>) input.readObject());
				this.getStore().setFittings((HashMap<String, Fitting>) input.readObject());
				UserModelPersistenceHandler.logger.info("<< UserModelPersistencehandler.restore [true]"); //$NON-NLS-1$
				return true;
			} finally {
				input.close();
				buffer.close();
			}
		} catch (final ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (final FileNotFoundException fnfe) {
			UserModelPersistenceHandler.logger
					.warning("W> " + modelStoreFile.getAbsolutePath() + " not found during restore."); //$NON-NLS-1$ //$NON-NLS-2$
			UserModelPersistenceHandler.logger.info("<< UserModelPersistencehandler.restore [FileNotFoundException]"); //$NON-NLS-1$
			return true;
		} catch (final IOException ex) {
			ex.printStackTrace();
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
		}
		UserModelPersistenceHandler.logger.info("<< UserModelPersistenceHandler.restore [false]"); //$NON-NLS-1$
		return false;
	}

	public synchronized boolean save() {
		UserModelPersistenceHandler.logger.info(">> UserModelPersistenceHandler.save"); //$NON-NLS-1$
		try {
			File modelStoreFile = AppConnector.getStorageConnector()
					.accessAppStorage(AppConnector.getResourceString(R.string.userdatamodelfilename));
			final BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(modelStoreFile));

			final ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				output.writeObject(this.getStore().getApiKeys());
				//				output.writeObject(getStore().getCharacters());
				output.writeObject(this.getStore().getFittings());
				//				output.writeObject(getStore());
				output.flush();
				UserModelPersistenceHandler.logger.info("<< UserModelPersistenceHandler.save [true]"); //$NON-NLS-1$
				return true;
			} finally {
				output.flush();
				output.close();
				buffer.close();
			}
		} catch (final FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		UserModelPersistenceHandler.logger.info("<< UserModelPersistenceHandler.save [false]"); //$NON-NLS-1$
		return false;
	}

	public void setStore(final IModelStore newStore) {
		if (null != newStore) {
			store = (AppModelStore) newStore;
		}
	}

	private void setStore(final Object readObject) {
		store = (AppModelStore) readObject;
	}
}

// - UNUSED CODE ............................................................................................

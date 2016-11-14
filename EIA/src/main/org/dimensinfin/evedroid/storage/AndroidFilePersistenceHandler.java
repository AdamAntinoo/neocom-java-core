//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.storage;

// - IMPORT SECTION .........................................................................................
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.dimensinfin.core.model.IModelStore;
import org.dimensinfin.core.parser.IPersistentHandler;
import org.dimensinfin.evedroid.connector.AppConnector;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class AndroidFilePersistenceHandler implements IPersistentHandler {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long					serialVersionUID	= -8542919445213185967L;
	//	private final int					storeResourceID		= R.string.industrystorefilename;
	private static final ObjectOutput	voidoo						= new VoidObjectOutput();
	private static final ObjectInput	voidoi						= new VoidObjectInput();

	// - F I E L D - S E C T I O N ............................................................................
	private String										fileName					= null;
	private File											modelStoreFile		= null;
	private ObjectOutput							output						= null;
	private ObjectInput								input							= null;
	private boolean										active						= true;

	private IModelStore								store;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AndroidFilePersistenceHandler(final String targetFileName) {
		fileName = targetFileName;
		modelStoreFile = AppConnector.getStorageConnector().accessAppStorage(fileName);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public IModelStore getStore() {
		return store;
	}

	public ObjectInput prepareStorageInput() {
		Log.i("AndroidFilePersistenceHandler", ">> AndroidFilePersistenceHandler.prepareStorageInput");
		try {
			// Read the contents of the character information.
			final BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(modelStoreFile));
			input = new ObjectInputStream(buffer);
			return input;
		} catch (final FileNotFoundException fnfe) {
			Log.w("IndustryPersistenceHandler", "W> " + modelStoreFile.getAbsolutePath() + " not found during restore."); //$NON-NLS-1$ //$NON-NLS-2$
			Log.i("IndustryPersistenceHandler", "<< UserModelPersistencehandler.restore [true]"); //$NON-NLS-1$
		} catch (final IOException ex) {
			ex.printStackTrace();
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
		}
		Log.i("AndroidFilePersistenceHandler", "<< AndroidFilePersistenceHandler.prepareStorageInput"); //$NON-NLS-1$
		return new VoidObjectInput();
	}

	/**
	 * Opens the storage file on the application folder at the SDCard. If something fails the method returns
	 * <code>null</code> that it is not a valid result. It should be an functionality empty
	 * <code>ObjectOutput</code> implementation that will do nothing but keeping the application consistent and
	 * behaving without errors.
	 * 
	 * @return
	 */
	public ObjectOutput prepareStorageOutput() {
		Log.i("AndroidFilePersistenceHandler", ">> AndroidFilePersistenceHandler.prepareStorageOutput");
		try {
			File modelStoreFile = AppConnector.getStorageConnector().accessAppStorage(fileName);
			final BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(modelStoreFile));
			output = new ObjectOutputStream(buffer);
			return output;
		} catch (final FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			active = false;
			Log.e("AndroidFilePersistenceHandler",
					"E> FileNotFoundException - preparing the storage receiver. " + fnfe.getMessage());
		} catch (final IOException ioe) {
			ioe.printStackTrace();
			active = false;
			Log.e("AndroidFilePersistenceHandler", "E> IOException - preparing the storage receiver. " + ioe.getMessage());
		}
		Log.i("AndroidFilePersistenceHandler", "<< AndroidFilePersistenceHandler.prepareStorageOutput");
		output = voidoo;
		return output;
	}

	public boolean restore() {
		Log.i("AndroidFilePersistenceHandler", ">> AndroidFilePersistenceHandler.restore");
		File modelStoreFile = AppConnector.getStorageConnector().accessAppStorage(fileName);
		try {
			// Read the contents of the character information.
			final BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(modelStoreFile));
			final ObjectInputStream input = new ObjectInputStream(buffer);
			try {
				//				getStore().setT2Blueprints((ArrayList<Blueprint>) input.readObject());
				//				getStore().setT1Blueprints((ArrayList<Blueprint>) input.readObject());
				Log.i("IndustryPersistenceHandler", "<< UserModelPersistencehandler.restore [true]"); //$NON-NLS-1$
				return true;
			} finally {
				input.close();
				buffer.close();
			}
			//		} catch (final ClassNotFoundException ex) {
			//			ex.printStackTrace();
		} catch (final FileNotFoundException fnfe) {
			Log.w("IndustryPersistenceHandler", "W> " + modelStoreFile.getAbsolutePath() + " not found during restore."); //$NON-NLS-1$ //$NON-NLS-2$
			Log.i("IndustryPersistenceHandler", "<< UserModelPersistencehandler.restore [true]"); //$NON-NLS-1$
			return true;
		} catch (final IOException ex) {
			ex.printStackTrace();
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
		}
		Log.i("IndustryPersistenceHandler", "<< UserModelPersistenceHandler.restore [false]"); //$NON-NLS-1$
		return false;
	}

	public boolean save() {
		//	logger.info(">> UserModelPersistenceHandler.save"); //$NON-NLS-1$
		try {
			File modelStoreFile = AppConnector.getStorageConnector().accessAppStorage(fileName);
			final BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(modelStoreFile));
			final ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				//				output.writeObject(getStore().getT2Blueprints());
				//				output.flush();
				//				output.writeObject(getStore().getT1Blueprints());
				//			logger.info("<< UserModelPersistenceHandler.save [true]"); //$NON-NLS-1$
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
		//	logger.info("<< UserModelPersistenceHandler.save [false]"); //$NON-NLS-1$
		return false;
	}

	public void setStore(final IModelStore newStore) {
		store = newStore;
	}

}

//- CLASS IMPLEMENTATION ...................................................................................
final class VoidObjectInput implements ObjectInput {

	public int available() throws IOException {
		return 0;
	}

	public void close() throws IOException {
	}

	public int read() throws IOException {
		return 0;
	}

	public int read(final byte[] arg0) throws IOException {
		return 0;
	}

	public int read(final byte[] arg0, final int arg1, final int arg2) throws IOException {
		return 0;
	}

	public boolean readBoolean() throws IOException {
		return false;
	}

	public byte readByte() throws IOException {
		return 0;
	}

	public char readChar() throws IOException {
		return 0;
	}

	public double readDouble() throws IOException {
		return 0;
	}

	public float readFloat() throws IOException {
		return 0;
	}

	public void readFully(final byte[] arg0) throws IOException {
	}

	public void readFully(final byte[] arg0, final int arg1, final int arg2) throws IOException {
	}

	public int readInt() throws IOException {
		return 0;
	}

	public String readLine() throws IOException {
		return null;
	}

	public long readLong() throws IOException {
		return 0;
	}

	public Object readObject() throws ClassNotFoundException, IOException {
		return null;
	}

	public short readShort() throws IOException {
		return 0;
	}

	public int readUnsignedByte() throws IOException {
		return 0;
	}

	public int readUnsignedShort() throws IOException {
		return 0;
	}

	public String readUTF() throws IOException {
		return null;
	}

	public long skip(final long arg0) throws IOException {
		return 0;
	}

	public int skipBytes(final int arg0) throws IOException {
		return 0;
	}
}

final class VoidObjectOutput implements ObjectOutput {

	// - M E T H O D - S E C T I O N ..........................................................................
	public void close() throws IOException {
	}

	public void flush() throws IOException {
	}

	public void write(final byte[] buffer) throws IOException {
	}

	public void write(final byte[] buffer, final int offset, final int count) throws IOException {
	}

	public void write(final int value) throws IOException {
	}

	public void writeBoolean(final boolean arg0) throws IOException {
	}

	public void writeByte(final int arg0) throws IOException {
	}

	public void writeBytes(final String arg0) throws IOException {
	}

	public void writeChar(final int arg0) throws IOException {
	}

	public void writeChars(final String arg0) throws IOException {
	}

	public void writeDouble(final double arg0) throws IOException {
	}

	public void writeFloat(final float arg0) throws IOException {
	}

	public void writeInt(final int arg0) throws IOException {
	}

	public void writeLong(final long arg0) throws IOException {
	}

	public void writeObject(final Object obj) throws IOException {
	}

	public void writeShort(final int arg0) throws IOException {
	}

	public void writeUTF(final String arg0) throws IOException {
	}
}
// - UNUSED CODE ............................................................................................

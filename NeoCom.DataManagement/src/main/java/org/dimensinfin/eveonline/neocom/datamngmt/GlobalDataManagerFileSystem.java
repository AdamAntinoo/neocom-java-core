//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.datamngmt;

import org.dimensinfin.eveonline.neocom.core.NeocomRuntimeException;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;

import java.io.InputStream;

/**
 * This class implements the isolation layer to the File System. Files and data paths are different on the platforms
 * so I need to isolate the methods so the same interface and configurations can be used on every platform. The main
 * task is to give Android access to the <b>assets</b> folder or the <b>application</b> folder where to install and
 * use cache and work files during the application run.
 * <p>
 * The File System it is a plugin that should be installed at startup because if not it should trigger runtime
 * exceptions on each file access.
 *
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalDataManagerFileSystem extends GlobalDataManagerExceptions {
	// - F I L E   S Y S T E M   S E C T I O N
	private static IFileSystem fileSystemIsolation = null;

	public static void installFileSystem (final IFileSystem newfileSystem) {
		fileSystemIsolation = newfileSystem;
	}

	public static IFileSystem getFileSystem () {
		if ( null != fileSystemIsolation ) return fileSystemIsolation;
		else throw new NeocomRuntimeException("File System isolation layer is not installed.");
	}

	public static InputStream openResource4Input (final String filePath) {
		if ( filePath.toLowerCase().contains("asset") ) return getFileSystem().openAsset4Input(filePath);
		return getFileSystem().openAsset4Input(filePath);
	}

	public static InputStream openAsset4Input (final String filePath) {
		return getFileSystem().openAsset4Input(filePath);
	}
}
// - UNUSED CODE ............................................................................................
//[01]

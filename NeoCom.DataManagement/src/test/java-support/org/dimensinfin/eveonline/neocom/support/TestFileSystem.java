package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class TestFileSystem implements IFileSystem {
	private static Logger logger = LoggerFactory.getLogger(TestFileSystem.class);
	private static ClassLoader classLoader = null;

	private String applicationFolder = "./src/test/NeoCom.UnitTest";

	// - C O N S T R U C T O R S
	public TestFileSystem() {}

	public TestFileSystem( final String applicationStoreDirectory ) {
		logger.info(">< [FileSystemSBImplementation.constructor]> applicationStoreDirectory: {}", applicationStoreDirectory);
		if (null != applicationStoreDirectory)
			this.applicationFolder = applicationStoreDirectory;
		logger.info("-- [FileSystemSBImplementation.constructor]> applicationFolder: {}", this.applicationFolder);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public InputStream openResource4Input( final String filePath ) throws IOException {
		return new FileInputStream(new File(applicationFolder + "/" + filePath));
	}

	@Override
	public OutputStream openResource4Output( final String filePath ) throws IOException {
		return new FileOutputStream(new File(applicationFolder + "/" + filePath));
	}

	@Override
	public InputStream openAsset4Input( final String filePath ) throws IOException {
		URI propertyURI = null;
		try {
			final URL resource = getClassLoader().getResource(filePath);
			if (null == resource)
				throw new IOException("[FileSystemSBImplementation.openAsset4Input]> Resource file " + filePath + "" +
						                      " not found with classloader.");
			propertyURI = new URI(resource.toString());
			logger.info("DD [FileSystemSBImplementation.openAsset4Input]> Processing file: {}", propertyURI);
		} catch (URISyntaxException use) {
		}
		return new FileInputStream(propertyURI.getPath());
	}

	@Override
	public String accessAsset4Path( final String filePath ) throws IOException {
		URI propertyURI = null;
		try {
			final URL resource = getClassLoader().getResource(filePath);
			if (null == resource)
				throw new IOException("[FileSystemSBImplementation.accessAsset4Path]> Resource file " + filePath +
						                      " not found with classloader.");
			propertyURI = new URI(classLoader.getResource(filePath).toString());
			logger.info("DD [FileSystemSBImplementation.accessAsset4Path]> Processing file: {}", propertyURI);
		} catch (URISyntaxException e) {
		}
		return propertyURI.getPath();
	}

	@Override
	public String accessResource4Path( final String filePath ) {
		return applicationFolder + "/" + filePath;
	}

	@Override
	public void copyFromAssets( final String sourceFileName, final String destinationDirectory ) {
	}

	//	@Override
	//	public String accessAppStorage4Path( final String filePath ) {
	//		return accessResource4Path(filePath);
	//	}
	//[01]

	/**
	 * Get a first access application classloader to be used to generate Resource paths.
	 *
	 * @return an application classloader to have a reference point from the application run place.
	 */
	protected ClassLoader getClassLoader() {
		if (null == classLoader) classLoader = getClass().getClassLoader();
		return classLoader;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("FileSystemSBImplementation [ ")
				                            .append("applicationFolder:").append(applicationFolder).append(" ");
		try {
			buffer.append("assetsFolder:").append(accessAsset4Path("")).append(" ");
		} catch (IOException ioe) {
		}
		return buffer.append("]")
				       //				.append("->").append(super.toString())
				       .toString();
	}
}

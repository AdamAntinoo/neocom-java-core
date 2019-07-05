package org.dimensinfin.eveonline.neocom.support.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring boot implementation for the File System isolation interface. We can get access to the application generated data
 * files stored on the private application folder or to the application assets deployed with the compiled code.
 *
 * The Assets api will access the readonly application deployed files while the Resource api will deal with the temporary
 * application storage files like cache or running stored local data.
 *
 * @author Adam Antinoo
 */
public class FileSystemSBImplementation implements IFileSystem {
	private static Logger logger = LoggerFactory.getLogger(FileSystemSBImplementation.class);
	private static ClassLoader classLoader = null;

	private String applicationFolder;

	// - C O N S T R U C T O R S
	private FileSystemSBImplementation() {
	}

	public FileSystemSBImplementation( final String applicationStoreDirectory ) {
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
			final String executionDirectory = new File(".").getCanonicalPath() + "/build/resources/main/";
			//			final URL resource = getClassLoader().getResource(filePath);
			//			if (null == resource) throw new IOException("[FileSystemSBImplementation.openAsset4Input]> Resource file " + filePath + "" +
			//					" not found with classloader.");
			propertyURI = new URI(executionDirectory + filePath);
			logger.info("DD [FileSystemSBImplementation.openAsset4Input]> Processing file: {}", propertyURI);
		} catch (URISyntaxException use) {
		}
		return new FileInputStream(propertyURI.getPath());
	}

	@Override
	public String accessAsset4Path( final String filePath ) throws IOException {
		URI propertyURI = null;
		try {
			final String executionDirectory = new File(".").getCanonicalPath() + "/build/resources/main/";
			//			final URL resource = getClassLoader().getResource(filePath);
			//			if (null == resource) throw new IOException("[FileSystemSBImplementation.openAsset4Input]> Resource file " + filePath + "" +
			//					" not found with classloader.");
			propertyURI = new URI(executionDirectory + filePath);
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

	// - B U I L D E R
	public static class Builder {
		private FileSystemSBImplementation onConstruction;

		public Builder() {
			this.onConstruction = new FileSystemSBImplementation();
		}

		public Builder withRootDirectory( final String applicationDirectory ) {
			this.onConstruction.applicationFolder = applicationDirectory;
			return this;
		}

		public IFileSystem build() {
			return this.onConstruction;
		}
	}
}

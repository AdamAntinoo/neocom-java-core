package org.dimensinfin.eveonline.neocom.support.adapters.implementers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.provider.IFileSystem;

/**
 * Spring boot implementation for the File System isolation interface. We can get access to the application generated data
 * files stored on the private application folder or to the application assets deployed with the compiled code.
 *
 * The Assets api will access the readonly application deployed files while the Resource api will deal with the temporary
 * application storage files like cache or running stored local data.
 * @author Adam Antinoo
 */
public class SBFileSystemAdapter implements IFileSystem {
	private static Logger logger = LoggerFactory.getLogger( SBFileSystemAdapter.class);
//	private static ClassLoader classLoader = null;

	protected String applicationDirectory = "./NeoCom.Infinity";

	// - C O N S T R U C T O R S
	protected SBFileSystemAdapter(){}
//	@Deprecated
//	public FileSystemSBAdapter(final String applicationStoreDirectory ) {
//		logger.info(">< [FileSystemSBImplementation.constructor]> applicationStoreDirectory: {}", applicationStoreDirectory);
//		if (null != applicationStoreDirectory)
//			this.applicationFolder = applicationStoreDirectory;
//		logger.info("-- [FileSystemSBImplementation.constructor]> applicationFolder: {}", this.applicationFolder);
//	}

	@Override
	public InputStream openResource4Input( final String filePath ) throws IOException {
		return new FileInputStream(new File( applicationDirectory + "/" + filePath));
	}

	@Override
	public OutputStream openResource4Output( final String filePath ) throws IOException {
		return new FileOutputStream(new File( applicationDirectory + "/" + filePath));
	}

	@Override
	public InputStream openAsset4Input( final String filePath ) throws IOException {
		URI propertyURI = null;
		try {
			final String executionDirectory = new java.io.File(".").getCanonicalPath() + "/build/resources/main/";
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
			final String executionDirectory = new java.io.File(".").getCanonicalPath() + "/build/resources/main/";
			propertyURI = new URI(executionDirectory + filePath);
			logger.info("DD [FileSystemSBImplementation.accessAsset4Path]> Processing file: {}", propertyURI);
		} catch (URISyntaxException e) {
		}
		return propertyURI.getPath();
	}

	@Override
	public String accessResource4Path( final String filePath ) {
		return applicationDirectory + "/" + filePath;
	}

	@Override
	public void copyFromAssets(final String sourceFileName, final String destinationDirectory) {
 // TODO - Implement the copy fom one place to another
	}

//	/**
//	 * Get a first access application classloader to be used to generate Resource paths.
//	 * @return an application classloader to have a reference point from the application run place.
//	 */
//	protected ClassLoader getClassLoader() {
//		if (null == classLoader) classLoader = getClass().getClassLoader();
//		return classLoader;
//	}

	// - C O R E

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "applicationFolder", applicationDirectory )
				.toString();
	}
	// - B U I L D E R
	public static class Builder {
		private SBFileSystemAdapter onConstruction;

		public Builder() {
			this.onConstruction = new SBFileSystemAdapter();
		}

		public SBFileSystemAdapter.Builder withRootDirectory( final String applicationDirectory ) {
			this.onConstruction.applicationDirectory = applicationDirectory;
			return this;
		}

		public IFileSystem build() {
			return this.onConstruction;
		}
	}
}

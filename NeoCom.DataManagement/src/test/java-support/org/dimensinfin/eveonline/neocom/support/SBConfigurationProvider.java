package org.dimensinfin.eveonline.neocom.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import com.annimon.stream.Stream;

import org.dimensinfin.eveonline.neocom.adapter.AConfigurationProvider;

public class SBConfigurationProvider extends AConfigurationProvider {
	public void setProperty( final String propertyName, final String value ) {
		this.configurationProperties.setProperty( propertyName, value );
	}

	protected void readAllProperties() throws IOException {
		logger.info( ">> [SBConfigurationProvider.readAllProperties]" );
		final List<String> propertyFiles = this.getResourceFiles( this.getResourceLocation() );
		Stream.of( propertyFiles )
				.sorted()
				.forEach( ( fileName ) -> {
					logger.info( "-- [SBConfigurationProvider.readAllProperties]> Processing file: {}", fileName );
					try {
						Properties properties = new Properties();
						properties.load( new FileInputStream( fileName ) );
						// Copy properties to globals.
						configurationProperties.putAll( properties );
					} catch (IOException ioe) {
						logger.error( "E [SBConfigurationProvider.readAllProperties]> Exception reading properties file {}. {}",
								fileName, ioe.getMessage() );
						ioe.printStackTrace();
					}
				} );
		logger.info( "<< [SBConfigurationProvider.readAllProperties]> Total properties number: {}", this.contentCount() );
	}

	/**
	 * Reads all the files found on the parameter directory path. Because the directory is read as a stream the method to read
	 * the directory does not use the file system isolation.
	 */
	protected List<String> getResourceFiles( final String initialPath ) throws IOException {
		final File rootFolder = new File( System.getProperty( "user.dir" ) + initialPath );
		logger.info( "-- [SBConfigurationProvider.readAllProperties]> Root directory: {}",
				rootFolder );
		return listFilesForFolder( rootFolder );
	}

	private List<String> listFilesForFolder( final File folder ) {
		List<String> filenames = new ArrayList<>();
		for (final File fileEntry : Objects.requireNonNull( folder.listFiles() )) {
			if (fileEntry.isDirectory()) listFilesForFolder( fileEntry );
			else filenames.add( fileEntry.getPath() );
		}
		return filenames;
	}

	// - B U I L D E R
	public static class Builder extends AConfigurationProvider.Builder<SBConfigurationProvider, SBConfigurationProvider.Builder> {
		private SBConfigurationProvider onConstruction;

		@Override
		protected SBConfigurationProvider getActual() {
			if (null == this.onConstruction) this.onConstruction = new SBConfigurationProvider();
			return this.onConstruction;
		}

		@Override
		protected SBConfigurationProvider.Builder getActualBuilder() {
			return this;
		}

		public SBConfigurationProvider build() throws IOException {
			return (SBConfigurationProvider) super.build();
		}
	}
}

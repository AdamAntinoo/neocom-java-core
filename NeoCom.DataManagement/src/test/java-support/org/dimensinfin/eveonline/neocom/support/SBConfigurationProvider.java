package org.dimensinfin.eveonline.neocom.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import com.annimon.stream.Stream;

import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.provider.AConfigurationService;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class SBConfigurationProvider extends AConfigurationService {
	public void readAllProperties() {
		NeoComLogger.enter();
		try {
			final List<String> propertyFiles = this.getResourceFiles( this.getResourceLocation() );
			Stream.of( propertyFiles )
					.sorted()
					.forEach( ( fileName ) -> {
						NeoComLogger.info( "Processing file: {}", fileName );
						try {
							Properties properties = new Properties();
							properties.load( new FileInputStream( fileName ) );
							// Copy properties to globals.
							configurationProperties.putAll( properties );
						} catch (IOException ioe) {
							NeoComLogger.error( "E [SBConfigurationProvider.readAllProperties]> Exception reading properties file " +
									fileName, ioe );
							ioe.printStackTrace();
						}
					} );
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
			throw new NeoComRuntimeException( ioe.getMessage() );
		}
		NeoComLogger.exit( "Total properties number: {}", this.contentCount() + "" );
	}

	/**
	 * Reads all the files found on the parameter directory path. Because the directory is read as a stream the method to read
	 * the directory does not use the file system isolation.
	 */
	protected List<String> getResourceFiles( final String initialPath ) throws IOException {
		final File rootFolder = new File( System.getProperty( "user.dir" ) + initialPath );
		NeoComLogger.info( "Root directory: {}", rootFolder.toString() );
		return listFilesForFolder( rootFolder );
	}

	public void setProperty( final String propertyName, final String value ) {
		this.configurationProperties.setProperty( propertyName, value );
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
	public static class Builder extends AConfigurationService.Builder<SBConfigurationProvider, SBConfigurationProvider.Builder> {
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

		public SBConfigurationProvider build() {
			return (SBConfigurationProvider) super.build();
		}
	}
}

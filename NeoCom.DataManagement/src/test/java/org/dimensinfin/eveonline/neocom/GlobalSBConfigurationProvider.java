//  PROJECT:     NeoCom.Microservices (NEOC.MS)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 / SpringBoot-1.3.5 / Angular 5.0
//  DESCRIPTION: This is the SpringBoot MicroServices module to run the backend services to complete the web
//               application based on Angular+SB. This is the web version for the NeoCom Android native
//               application. Most of the source code is common to both platforms and this module includes
//               the source for the specific functionality for the backend services.
package org.dimensinfin.eveonline.neocom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.annimon.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.conf.GlobalConfigurationProvider;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalSBConfigurationProvider extends GlobalConfigurationProvider {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("GlobalSBConfigurationProvider");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public GlobalSBConfigurationProvider( final String propertiesFolder ) {
		super(propertiesFolder);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	protected void readAllProperties() throws IOException {
		logger.info(">> [GlobalConfigurationProvider.readAllProperties]");
		// Read all .properties files under the predefined path on the /resources folder.
		final List<String> propertyFiles = getResourceFiles(getResourceLocation());
		final ClassLoader classLoader = getClass().getClassLoader();
		Stream.of(propertyFiles)
				.sorted()
				.forEach(( fileName ) -> {
					logger.info("-- [GlobalConfigurationProvider.readAllProperties]> Processing file: {}", fileName);
					try {
						Properties properties = new Properties();
						// Generate the proper URI to ge tot the resource file.
						final String propertyFileName = getResourceLocation() + "/" + fileName;
						final URI propertyURI = new URI(classLoader.getResource(propertyFileName).toString());
						properties.load(new FileInputStream(propertyURI.getPath()));
						// Copy properties to globals.
						globalConfigurationProperties.putAll(properties);
					} catch (IOException ioe) {
						logger.error("E [GlobalConfigurationProvider.readAllProperties]> Exception reading properties file {}. {}",
								fileName, ioe.getMessage());
						ioe.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				});
		logger.info("<< [GlobalConfigurationProvider.readAllProperties]> Total properties number: {}", contentCount());
	}

	protected List<String> getResourceFiles( String path ) throws IOException {
		List<String> filenames = new ArrayList<>();

		try (
				InputStream in = getResourceAsStream(path);
				BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String resource;

			while ((resource = br.readLine()) != null) {
				filenames.add(resource);
			}
		}

		return filenames;
	}

	private InputStream getResourceAsStream( String resource ) {
		final InputStream in
				= getContextClassLoader().getResourceAsStream(resource);

		return in == null ? getClass().getResourceAsStream(resource) : in;
	}

	private ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}
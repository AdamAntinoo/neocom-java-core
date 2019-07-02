package org.dimensinfin.neocom.support.adapters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dimensinfin.eveonline.neocom.conf.GlobalConfigurationProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Antinoo
 */
public class GlobalSBConfigurationProvider extends GlobalConfigurationProvider {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(GlobalSBConfigurationProvider.class);

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public GlobalSBConfigurationProvider( final String propertiesFolder ) {
		super(propertiesFolder);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	protected void readAllProperties() throws IOException {
		logger.info(">> [GlobalConfigurationProvider.readAllProperties]");
		// Read all .properties files under the predefined path on the /resources folder.
//		final List<String> propertyFiles = getResourceFiles(getResourceLocation());
//		final ClassLoader classLoader = getClass().getClassLoader();
//		Stream.of(propertyFiles)
		final String executionDirectory = new java.io.File(".").getCanonicalPath();
		Path start = Paths.get(executionDirectory + "/" + getResourceLocation());
		try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
			List<String> collect = stream
					.map(String::valueOf)
					.sorted()
//				.forEach(( fileName ) -> {
					.map(fileName -> {
						logger.info("-- [GlobalConfigurationProvider.readAllProperties]> Processing file: {}", fileName);
						try {
							Properties properties = new Properties();
							// Generate the proper URI to ge tot the resource file.
//						final String executionDirectory = new java.io.File(".").getCanonicalPath() + "/";
//							final String propertyFileName = executionDirectory + "/" + getResourceLocation() + "/" + fileName;
							logger.info("-- [GlobalConfigurationProvider.readAllProperties]> Resource path: {}", fileName);
//						final URI propertyURI = new URI(classLoader.getResource(propertyFileName).toString());
							final URI propertyURI = new URI(fileName);
							properties.load(new FileInputStream(propertyURI.getPath()));
							// Copy properties to globals.
							this.configurationProperties.putAll(properties);
						} catch (IOException ioe) {
							logger.error("E [GlobalConfigurationProvider.readAllProperties]> Exception reading properties file {}. {}",
									fileName, ioe.getMessage());
							ioe.printStackTrace();
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
						return fileName;
					})
					.collect(Collectors.toList());
//				});
			logger.info("<< [GlobalConfigurationProvider.readAllProperties]> Total properties number: {}", contentCount());
		}
	}

	protected List<String> getResourceFilesold( String path ) throws IOException {
		List<String> filenames = new ArrayList<>();

		try (
				InputStream in = getResourceAsStream(path);
				BufferedReader br = new BufferedReader(new InputStreamReader(in))
		) {
			String resource;

			while ((resource = br.readLine()) != null) {
				filenames.add(resource);
			}
		}

		return filenames;
	}

	protected List<String> getResourceFiles( String path ) throws IOException {
		final String executionDirectory = new java.io.File(".").getCanonicalPath();
		Path start = Paths.get(executionDirectory + "/" + path);
		try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
			List<String> collect = stream
					.map(String::valueOf)
					.sorted()
					.collect(Collectors.toList());

			collect.forEach(System.out::println);
			return collect;
		}
	}

	private InputStream getResourceAsStream( String resource ) {
		final InputStream in = getContextClassLoader().getResourceAsStream(resource);
		String executionDirectory = "/";
		try {
			executionDirectory = new java.io.File(".").getCanonicalPath();
			logger.info("-- [GlobalConfigurationProvider.getResourceAsStream]> Resource path: {}", executionDirectory +
					"/" + resource);
		} catch (IOException ioe) {
			logger.error("E [GlobalConfigurationProvider.readAllProperties]> Exception reading properties file {}. {}",
					resource, ioe.getMessage());
			ioe.printStackTrace();
		}

//		return in == null ? getClass().getResourceAsStream(resource) : in;
		try {
			return in == null ? new FileInputStream(new URI(executionDirectory + "/" + resource).toString()) : in;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	private ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}

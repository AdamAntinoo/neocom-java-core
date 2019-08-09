package org.dimensinfin.eveonline.neocom.support.adapters;

import com.annimon.stream.Stream;

import org.dimensinfin.eveonline.neocom.conf.GlobalConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Adam Antinoo
 */
public class SBConfigurationProvider extends GlobalConfigurationProvider {
	private static Logger logger = LoggerFactory.getLogger(SBConfigurationProvider.class);
	//	@Autowired
//	private SBResourceLoader resourceLoader;
//	@Value("classpath*:./acceptancetests.properties/*.property")
//	private Resource[] files;

	// - C O N S T R U C T O R S
	private SBConfigurationProvider( final String propertiesFolder ) {
		super(propertiesFolder);
//		this.resourceLoader=new SBResourceLoader().setPropertiesFolder(propertiesFolder);
	}

//	public List<Resource> loadResources() {
//		return Arrays.asList(this.files);
////		try {
////			Resource[] resources = this.resourceLoader.loadResources("file:acceptancetests.properties/*");
////			return Arrays.asList(resources);
////		} catch (IOException ex) {
////			ex.printStackTrace();
////			return new ArrayList<>();
////		}
//	}

	protected void readAllProperties() throws IOException {
		logger.info(">> [SBConfigurationProvider.readAllProperties]");
		logger.info("-- [SBConfigurationProvider.readAllProperties]> Properties directory location: {}",
		            new File(this.getResourceLocation()).getCanonicalPath());
		// Read all .properties files under the predefined path on the /resources folder.
		final List<String> propertyFiles = this.getResourceFiles(this.getResourceLocation());
//		final List<Resource> resources = this.loadResources();
//		final ClassLoader classLoader = getClass().getClassLoader();
		Stream.of(propertyFiles)
		      .sorted()
		      .forEach(( fileName ) -> {
			      logger.info("-- [SBConfigurationProvider.readAllProperties]> Processing file: {}", fileName);
			      try {
				      Properties properties = new Properties();
				      // Generate the proper URI to ge tot the resource file.
				      final String propertyFileName = this.getResourceLocation() + "/" + fileName;
				      final URI propertyURI = new URI(this.getClass().getClassLoader().getResource(propertyFileName).toString());
				      properties.load(new FileInputStream(propertyURI.getPath()));
				      logger.info("-- [SBConfigurationProvider.readAllProperties]> Processing file: {}",
				                  new File(propertyFileName).getCanonicalPath());
//				      properties.load(new FileInputStream(new File(propertyFileName)));
				      // Copy properties to globals.
				      this.configurationProperties.putAll(properties);
			      } catch (IOException ioe) {
				      logger.error("E [SBConfigurationProvider.readAllProperties]> Exception reading properties file {}. {}",
				                   fileName, ioe.getMessage());
				      ioe.printStackTrace();
			      } catch (URISyntaxException e) {
				      e.printStackTrace();
			      }
		      });
		logger.info("<< [SBConfigurationProvider.readAllProperties]> Total properties number: {}", contentCount());
	}

	protected List<String> getResourceFiles( String path ) throws IOException {
		List<String> filenames = new ArrayList<>();

		try (InputStream in = this.getResourceAsStream(path);
		     BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String resource;
			while ((resource = br.readLine()) != null)
				filenames.add(resource);
		}
		return filenames;
	}

	private InputStream getResourceAsStream( String resource ) {
		final InputStream in = this.getClass().getClassLoader().getResourceAsStream(resource);
		return in == null ? getClass().getResourceAsStream(resource) : in;
	}

	private ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	// - B U I L D E R
	public static class Builder {
		private SBConfigurationProvider onConstruction;

		public Builder( final String propertiesLocation ) {
			this.onConstruction = new SBConfigurationProvider(propertiesLocation);
		}

		public SBConfigurationProvider build() {
			this.onConstruction.initialize(); // Initialisation should be done after all fields are setup.
			return this.onConstruction;
		}
	}
}

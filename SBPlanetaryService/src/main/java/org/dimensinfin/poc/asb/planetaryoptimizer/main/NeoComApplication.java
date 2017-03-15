//	PROJECT:        POC-ASB-Planetary (POC.ASBP)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Angular-SpringBoot.
//	DESCRIPTION:	Proof of Concept. Use of Angular 2.0 and Sprint Boot to create a test service
//					to display and process the Planetary Data of a sample Eve account.
package org.dimensinfin.poc.asb.planetaryoptimizer.main;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.connector.IConnector;
import org.dimensinfin.eveonline.neocom.connector.IDatabaseConnector;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.poc.connector.SpringDatabaseConnector;
import org.joda.time.DateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * Spring Boot application that will be the local REST side for the NeoCom AngularJS application. All REST
 * calls will be forwarded to the application that will use the already developed NeoCom code to create the
 * Eve online data models to be shown in the Angular application side. <br>
 * This is a POC to check the new architecture required to integrate SB services into Angular applications
 * when they should run inside a single process. On most cases the integration is created at the Apache server
 * but for this kind of testing we integrate it into the Eclipse testing platform.
 * 
 * @author Adam Antinoo
 */

@SpringBootApplication
@RestController
@ComponentScan(basePackages = { "com.dimensinfin.poc.asb.planetaryoptimizer.config" })
public class NeoComApplication extends AppAbstractConnector {
	private static Logger							logger						= Logger.getLogger("NeoComApplication");
	private static NeoComApplication	singleton					= null;

	static DateTime										compilationDate		= new DateTime();
	static long												startTimeinMillis	= new DateTime().getMillis();
	static DateTime										startupTime				= new DateTime();
	static int												requests					= 0;
	static long												requestsTime			= 0;

	// - M A I N   E N T R Y P O I N T ........................................................................
	/**
	 * Just create the Spring application and launch it to run.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		// Instance and connect the Adaptors.
		SpringApplication.run(NeoComApplication.class, args);
	}

	// - F I E L D - S E C T I O N ............................................................................
	//	private final SpringDatabaseConnector	dbconnector	= null;
	//	private DataSourceManager							dsManager		= new DataSourceManager();
	private SpringDatabaseConnector dbCCPConnector = null;
	//	private ApplicationListenerMethodAdapter					apiKeys			= new HashMap<Long, APIKey>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComApplication() {
		logger.info(">> [NeoComApplication.<constructor>]");
		// Setup the referencing structures that will serve as proxy and global references.
		if (null == singleton) {
			singleton = this;
			AppConnector.setConnector(this);
		}
		booststrapInitialization();
		logger.info("<< [NeoComApplication.<constructor>]");
	}
	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Gets the Eve Database information for an eve item identified by it's identifier.
	 * 
	 * @param typeID
	 *          identifier of the type to search.
	 * @return json information of the type from the CCP item database.
	 */
	@RequestMapping(value = "/api/v1/eveitem/{typeID}/{debug}", method = RequestMethod.GET, produces = "application/json")
	public EveItem eveItem(@PathVariable final String typeID, @PathVariable final String debug) {
		// Connect to the eve database and generate an output for the query related to the eve item received as parameter.
		EveItem item = AppConnector.getDBConnector().searchItembyID(Integer.parseInt(typeID));
		// Add a time of 3 seconds to the response time if the debug flag is defined
		if (null != debug) {
			try {
				Thread.sleep(3000); //1000 milliseconds is one second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
		return item;
	}

	//   public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	//       final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
	//       final ObjectMapper objectMapper = new ObjectMapper();
	//       objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	//       converter.setObjectMapper(objectMapper);
	//       converters.add(converter);
	//       super.configureMessageConverters(converters);
	//   }
	//	@RequestMapping(value = "/api/v1/apikey/{apikey}:{validationcode}", method = RequestMethod.GET, produces = "application/json")
	//	public ArrayList<APIKey> apikey(@PathVariable final String apikey, @PathVariable final String validationcode) {
	//		//		public ArrayList<EveCharCore> keycharacters(@RequestBody String data, HttpServletResponse response,
	//		//				@PathVariable final String apikey, @PathVariable final String validationcode) {
	//		try {
	//			// Validate the received parameters
	//			if (null == apikey) throw new HTTPException(500, "Required parameter 'apikey' not found.");
	//			if (null == validationcode) throw new HTTPException(500, "Required parameter 'validationcode' not found.");
	//
	//			// Register the key in the global apikey list
	//			APIKey newkey = new APIKey(Integer.valueOf(apikey), validationcode);
	//			getApiKeys().put(new Long(newkey.getKeyID()), newkey);
	//
	//			// Create the datasource of ApiKeys and included are the pilots
	//			ApiKeyDatasource apikeyds = (ApiKeyDatasource) getDataSourceConector()
	//					.registerDataSource(new ApiKeyDatasource("NEOCOM", AppWideConstants.fragment.FRAGMENT_PILOTINFO_INFO));
	//			ArrayList<APIKey> keys = apikeyds.getModel();
	//			return keys;
	//			// Convert the list of ApiKeys to a json stream.
	//			//			Gson gson = new Gson();
	//			//			String json = gson.toJson(keys);
	//
	//			//			ArrayList<String> result = new ArrayList<String>();
	//			//			for (APIKey key : keys) {
	//			//				result.add(key.json());
	//			//			}
	//			//			return json;
	//		} catch (HTTPException httpe) {
	//			//			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	//			return null;
	//		}
	//	}

	//	@RequestMapping(value = "/data", method = RequestMethod.GET, produces = "application/json")
	//	@ResponseBody
	//	public AppInfo applicationInfo() {
	//		try {
	//			Thread.sleep(3000); //1000 milliseconds is one second.
	//		} catch (InterruptedException ex) {
	//			Thread.currentThread().interrupt();
	//		}
	//		NeoComApplication.requests++;
	//		NeoComApplication.requestsTime += new Long(new Double(Math.random() * 1000).intValue());
	//		return new AppInfo();
	//	}

	//	/**
	//	 * Entry point for the Assets Model. The model returned will be the Asset By Location data but instead using
	//	 * the lazy evaluation this will return the full model to be managed by the Angular Controller.
	//	 * 
	//	 * The DataSource returns a list of Regions. Regions contents will be Locations that then will have Assets.
	//	 * There can be composed assets that are Containers or Ships.
	//	 * 
	//	 * The value to be used on the demo is: 92223647
	//	 * 
	//	 * @param characterID
	//	 *          - The identifier of the character to get the assets from on the CCP call.
	//	 * @return A list of Regions.
	//	 */
	//	@RequestMapping(value = "/rest/v1/assets/{characterID}", method = RequestMethod.GET, produces = "application/json")
	//	//	@RequestMapping(value = "/rest/v1/assets/{characterID}", method = RequestMethod.GET, produces = "text/html")
	//	public List<Region> characterAssets(@PathVariable final String characterID) {
	//		// Search for a new DataSource.
	//		IDataSource<Region> ds = dsManager.registerDataSource(new AssetsByLocationDataSource(Long.parseLong(characterID),
	//				AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION));
	//		ArrayList<Region> model = ds.getModel();
	//		return model;
	//	}
	//
	//	@RequestMapping(value = "/api/v1/characters", method = RequestMethod.GET, produces = "application/json")
	//	public ArrayList<EveCharCore> characters() {
	//		// Create the datasource of Pilots and get the pilot list
	//		PilotDataSource pilotds = (PilotDataSource) getDataSourceConector()
	//				.registerDataSource(new PilotDataSource("NEOCOM", AppWideConstants.fragment.FRAGMENT_PILOTINFO_INFO));
	//		ArrayList<EveCharCore> pilots = pilotds.getModel();
	//		return pilots;
	//	}

	@Override
	public IDatabaseConnector getDBConnector() {
		if (null == dbCCPConnector) dbCCPConnector = new SpringDatabaseConnector();
		return dbCCPConnector;
	}

	//	public HashMap<Long, APIKey> getApiKeys() {
	//		return apiKeys;
	//	}

	@Override
	public IConnector getSingleton() {
		if (null == singleton) new NeoComApplication();
		return singleton;
	}

	//	@Override
	//	public IDataSourceConnector getDataSourceConector() {
	//		if (null == dsManager) dsManager = new DataSourceManager();
	//		return dsManager;
	//	}
	//
	//	@Override
	//	public IDatabaseConnector getDBConnector() {
	//		if (null == dbConnector) dbConnector = new SpringDatabaseConnector();
	//		return dbConnector;
	//	}
	//
	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
		b.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		return b;
	}

	//	@RequestMapping(value = "/rest/v1/locationitem/{locationID}", method = RequestMethod.GET, produces = "application/json")
	//	public EveLocation locationItem(@PathVariable final String locationID) {
	//		// Connect to the eve database and generate an output for the query related to the eve item received as parameter.
	//		EveLocation location = new EveLocation();
	//		try {
	//			location = AppConnector.getDBConnector().searchLocationbyID(Long.parseLong(locationID));
	//			Thread.sleep(3000); //1000 milliseconds is one second.
	//		} catch (InterruptedException ex) {
	//			Thread.currentThread().interrupt();
	//		} catch (NumberFormatException nfexc) {
	//			return new EveLocation();
	//		}
	//		return location;
	//	}
	//
	//	// Register the hystrix.stream to publish hystrix data to the dashboard
	//	@Bean
	//	public ServletRegistrationBean servletRegistrationBean() {
	//		return new ServletRegistrationBean(new HystrixMetricsStreamServlet(), "/hystrix.stream");
	//	}

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(NeoComApplication.class);
	}
	//	@RequestMapping(value = "/api/v1/apicharacters/{apikey}:{validationcode}", method = RequestMethod.GET, produces = "application/json")
	//	public ArrayList<EveCharCore> apiCharacters(@PathVariable final String apikey,
	//			@PathVariable final String validationcode) {
	//		//		public ArrayList<EveCharCore> keycharacters(@RequestBody String data, HttpServletResponse response,
	//		//				@PathVariable final String apikey, @PathVariable final String validationcode) {
	//		try {
	//			// Validate the received parameters
	//			if (null == apikey) throw new HTTPException(500, "Required parameter 'apikey' not found.");
	//			if (null == validationcode) throw new HTTPException(500, "Required parameter 'validationcode' not found.");
	//
	//			// Register the key in the global apikey list
	//			getApiKeys().add(new APIKey(Integer.valueOf(apikey), validationcode));
	//
	//			// Create the datasource of Pilots and get the pilot list
	//			PilotDataSource pilotds = (PilotDataSource) getDataSourceConector()
	//					.registerDataSource(new PilotDataSource("NEOCOM", AppWideConstants.fragment.FRAGMENT_PILOTINFO_INFO));
	//			ArrayList<EveCharCore> pilots = pilotds.getModel();
	//			return pilots;
	//		} catch (HTTPException httpe) {
	//			//			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	//			return null;
	//		}
	//	}

	private void booststrapInitialization() {
		logger.info(">> [NeoComApplication.booststrapInitialization]");
		// Initialize demo list of api keys
		//		apiKeys.put(
		//				new Long(2855881),
		//				new APIKey(2855881, "jcmdsBL4RiyzFO7mVWRz5kks1ih2fTyyFhvawr0nrRMq2Gn97AOSO3FfW6lLWgGG"));
		//		apiKeys.add(new APIKey(2956305, "u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy"));
		//		apiKeys.add(new APIKey(3106761, "gltCmvVoZl5akrM8d6DbNKZn7Jm2SaukrmqjnSOyqKbvzz5CtNfknTEwdBe6IIFf"));
		//		apiKeys.add(new APIKey(924767, "2qBKUY6I9ozYhKxYUBPnSIix0fHFCqveD1UEAv0GbYqLenLLTIfkkIWeOBejKX5P"));
		logger.info("<< [NeoComApplication.booststrapInitialization]");
	}
}

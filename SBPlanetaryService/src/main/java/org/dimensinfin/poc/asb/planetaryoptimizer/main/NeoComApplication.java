//	PROJECT:        POC-ASB-Planetary (POC.ASBP)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Angular-SpringBoot.
//	DESCRIPTION:	Proof of Concept. Use of Angular 2.0 and Sprint Boot to create a test service
//					to display and process the Planetary Data of a sample Eve account.
package org.dimensinfin.poc.asb.planetaryoptimizer.main;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.connector.IConnector;
import org.dimensinfin.eveonline.neocom.connector.IDatabaseConnector;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.poc.connector.SpringDatabaseConnector;
import org.dimensinfin.neocom.models.PlanetaryResource;
import org.joda.time.DateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
	private SpringDatabaseConnector												dbCCPConnector	= null;
	private HashMap<String, ArrayList<PlanetaryResource>>	listRepository	= new HashMap<String, ArrayList<PlanetaryResource>>();
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
	@CrossOrigin()
	@RequestMapping(value = "/api/v1/addresourcelist", method = RequestMethod.POST, produces = "application/json")
	public String addresourcelist(@RequestBody final ResourceList newlist) {
		// Map<String, List<DefaultConfigDto>>
		logger.info(">> [NeoComApplication.addresourcelist]");
		logger.info("-- [NeoComApplication.addresourcelist]> newlist: " + newlist);
		//		// Connect to the eve database and generate an output for the query related to the eve item received as parameter.
		//		EveItem item = AppConnector.getDBConnector().searchItembyID(Integer.parseInt(typeID));
		//		// Initialize the market data from start because this is a requirements on serialization.
		//		item.getHighestBuyerPrice();
		//		item.getLowestSellerPrice();
		//		logger.info("-- [NeoComApplication.eveItem]> [#" + item.getItemID() + "]" + item.getName());
		//		// Add a time of 3 seconds to the response time if the debug flag is defined
		//		//		if (null != debug) {
		//		//			try {
		//		//				Thread.sleep(3000); //1000 milliseconds is one second.
		//		//			} catch (InterruptedException ex) {
		//		//				Thread.currentThread().interrupt();
		//		//			}
		//		//		}
		logger.info("<< [NeoComApplication.addresourcelist]");
		return "OK";
	}

	@CrossOrigin()
	@RequestMapping(value = "/api/v1/deleteResource/{listname}/{resourceid}", method = RequestMethod.GET, produces = "application/json")
	public String deleteResource(@PathVariable final String listname, @PathVariable final String resourceid) {
		logger.info(">> [NeoComApplication.deleteResource]");
		logger.info("-- [NeoComApplication.deleteResource]> listname: " + listname);
		logger.info("-- [NeoComApplication.deleteResource]> resourceid: " + resourceid);
		// Transform the resource identifier to an eve type.
		int typeid = Integer.parseInt(resourceid);
		if (typeid > 0) {
			// Locate the list and the delete the resource from that list.
			ArrayList<PlanetaryResource> list = listRepository.get(listname);
			if (null != list) {
				logger.info("-- [NeoComApplication.deleteResource]> list(" + listname + ") located.");
				logger.info("-- [NeoComApplication.deleteResource]>PRE " + listname + " items: " + list.size());
				list = deleteListResource(list, typeid);
				logger.info("-- [NeoComApplication.deleteResource]>POS " + listname + " items: " + list.size());
				listRepository.put(listname, list);
			}
		}
		logger.info("<< [NeoComApplication.deleteResource]");
		return "OK";
	}

	private ArrayList<PlanetaryResource> deleteListResource(ArrayList<PlanetaryResource> list, int type) {
		// Create the new list copy.
		ArrayList<PlanetaryResource> newList = new ArrayList<PlanetaryResource>();
		for (PlanetaryResource planetaryResource : newList) {
			if (planetaryResource.getId() != type) newList.add(planetaryResource);
		}
		// Replace the list with the new list with the resource removed.
		return newList;
	}

	/**
	 * Gets the Eve Database information for an eve item identified by it's identifier.
	 * 
	 * @param typeID
	 *          identifier of the type to search.
	 * @return json information of the type from the CCP item database.
	 */
	@CrossOrigin()
	@RequestMapping(value = "/api/v1/eveitem/{typeID}", method = RequestMethod.GET, produces = "application/json")
	public EveItem eveItem(@PathVariable final String typeID/* , @PathVariable final String debug */) {
		logger.info(">> [NeoComApplication.eveItem]");
		logger.info("-- [NeoComApplication.eveItem]> typeID: " + typeID);
		// Connect to the eve database and generate an output for the query related to the eve item received as parameter.
		EveItem item = AppConnector.getDBConnector().searchItembyID(Integer.parseInt(typeID));
		// Initialize the market data from start because this is a requirements on serialization.
		item.getHighestBuyerPrice();
		item.getLowestSellerPrice();
		logger.info("-- [NeoComApplication.eveItem]> [#" + item.getItemID() + "]" + item.getName());
		// Add a time of 3 seconds to the response time if the debug flag is defined
		//		if (null != debug) {
		//			try {
		//				Thread.sleep(3000); //1000 milliseconds is one second.
		//			} catch (InterruptedException ex) {
		//				Thread.currentThread().interrupt();
		//			}
		//		}
		logger.info("<< [NeoComApplication.eveItem]");
		return item;
	}

	@Override
	public IDatabaseConnector getDBConnector() {
		if (null == dbCCPConnector) dbCCPConnector = new SpringDatabaseConnector();
		return dbCCPConnector;
	}

	// [03]

	@Override
	public IConnector getSingleton() {
		if (null == singleton) new NeoComApplication();
		return singleton;
	}

	//[04]
	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
		b.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		return b;
	}

	@CrossOrigin()
	@RequestMapping(value = "/api/v1/resourcelist/{name}", method = RequestMethod.GET, produces = "application/json")
	public ResourceList resourcelist(@PathVariable final String name) {
		logger.info(">> [NeoComApplication.resourcelist]");
		logger.info("-- [NeoComApplication.resourcelist]> name: " + name);
		// Search the list name in the repository.
		ArrayList<PlanetaryResource> hitlist = listRepository.get(name);
		if (null == hitlist) {
			// Get the demo list of the resource list and return it to the caller
			ResourceList newrl = new ResourceList();
			newrl.mockup();
			Map<String, List<PlanetaryResource>> rl = new HashMap<String, List<PlanetaryResource>>();
			List<PlanetaryResource> rllist = new ArrayList<PlanetaryResource>();
			rllist.add(new PlanetaryResource(123, 234.0));
			rllist.add(new PlanetaryResource(234, 345.0));
			rl.put("PruebaInicial", rllist);
			logger.info("<< [NeoComApplication.addresourcelist]");
			return newrl;
		} else {
			logger.info("<< [NeoComApplication.addresourcelist]");
			ResourceList newrl = new ResourceList();
			newrl.setName(name);
			newrl.setList(hitlist);
			return newrl;
		}
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(NeoComApplication.class);
	}

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
}

final class ResourceList implements Serializable {
	private static final long				serialVersionUID	= -2122653250395649670L;
	public String										name;
	public List<PlanetaryResource>	data							= new ArrayList<PlanetaryResource>();

	public String getName() {
		return name;
	}

	public void setList(ArrayList<PlanetaryResource> hitlist) {
		if (null != data) data = hitlist;
	}

	public void mockup() {
		name = "PruebaInicial";
		//	data = new PlanetaryResource[2];
		data.add(new PlanetaryResource(123, 234.0));
		data.add(new PlanetaryResource(234, 345.0));
	}

	public void setName(String newname) {
		this.name = newname;
	}
}

//[03]
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
//[04]
//	public HashMap<Long, APIKey> getApiKeys() {
//		return apiKeys;
//	}

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

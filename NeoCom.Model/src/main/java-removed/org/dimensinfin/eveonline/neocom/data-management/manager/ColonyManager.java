//  PROJECT:     NeoCom.DataManagement(NEOC.DM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java JRE 1.5 Specification.
//  DESCRIPTION: NeoCom pure Java library to maintain and manage all the data streams and
//                 connections. It will use the Models as the building blocks for the data
//                 and will isolate to the most the code from any platform implementation.
//               It will contain the Model Generators and use the external facilities for
//                 network connections to CCP XML api, CCP ESI api and Database storage. It
//                 will also make use of Cache facilities that will be glued at compilation
//                 time depending on destination platform.
package org.dimensinfin.eveonline.neocom.manager;

import com.tlabs.android.evanova.adapter.ApplicationCloudAdapter;
import com.tlabs.eve.api.character.PlanetaryColoniesRequest;
import com.tlabs.eve.api.character.PlanetaryColoniesResponse;
import com.tlabs.eve.api.character.PlanetaryColony;
import com.tlabs.eve.api.character.PlanetaryPin;
import com.tlabs.eve.api.character.PlanetaryPinsRequest;
import com.tlabs.eve.api.character.PlanetaryPinsResponse;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChonoOptions;
import org.dimensinfin.core.util.OneParameterTask;
import org.dimensinfin.eveonline.neocom.model.Colony;
import org.dimensinfin.eveonline.neocom.model.ColonyFactory;
import org.dimensinfin.eveonline.neocom.model.ColonyStructure;
import org.dimensinfin.eveonline.neocom.model.ColonyStructureFactory;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.dimensinfin.eveonline.neocom.network.XMLNetworkAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * The class responsibility is to provide of all the Planetary Interaction model data to other components. The
 * api will be able to provide the Colony data for a Character and from that point all the other dependent or
 * derived information from CCP sources.
 * <p>
 * It will be also responsible for any Planetary forecasting, optimizations or calculations on derived data to
 * help the user to make decisions or to present better usability focused information.
 *
 * @author Adam Antinoo
 */
public class ColonyManager extends AbstractManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 3794750126425122302L;
	private static Logger logger = LoggerFactory.getLogger(ColonyManager.class);

	// - F I E L D - S E C T I O N ............................................................................
	private final XMLNetworkAdapter _network = new XMLNetworkAdapter();
	public int coloniesCount = 0;
	public List<Colony> colonies = new Vector();
	public final String iconName = "planets.png";

	// - P R I V A T E   I N T E R C H A N G E   V A R I A B L E S

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ColonyManager (final NeoComCharacter pilot) {
		super(pilot);
		_network.setPilot(pilot);
		jsonClass = "ColonyManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Get all the Planetary Colonies for a selected character. After getting the list of colonies and wrapping
	 * all that data into MVC compatible classes it will launch background jobs to retrieve each colony detailed
	 * data.
	 */
	public List<Colony> accessAllColonies () {
		logger.info(">> [ColonyManager.accessAllColonies]");
		// We suppose the character is already tied to the Manager.
		final Chrono chrono = new Chrono();
		final PlanetaryColoniesRequest request = _network.PlanetaryColoniesRequest();
		final PlanetaryColoniesResponse colonies = _network.execute(request);
		logger.debug(">> [ColonyManager.accessAllColonies]> [Elapsed Time]- PlanetaryColoniesRequest ", chrono.printElapsed(ChonoOptions.SHOWMILLIS));
		// Converts data to MVC
		List<Colony> presentColonies = new Vector();
		for (PlanetaryColony col : colonies.getColonies()) {
			final Colony newcol = ColonyFactory.from(col);
			presentColonies.add(newcol);
			logger.debug(">> [ColonyManager.accessAllColonies]> newcol: ", newcol.toString());
			logger.debug(">> [ColonyManager.accessAllColonies]> Submitting another job to Executor");
			newcol.setDownloading(true);
			ApplicationCloudAdapter.getSingleton().getDownloadExecutor().submit(
					new OneParameterTask<Colony>(newcol) {
						public void run () {
							logger.info(">> [ColonyManager.accessAllColonies.OneParameterTask.run]");
							Chrono chrono = new Chrono();

							final PlanetaryPinsRequest pinrequest = _network.PlanetaryPinsRequest(getTarget().getPlanetID());
							final PlanetaryPinsResponse pins = _network.execute(pinrequest);
							logger.debug(">> [ColonyManager.accessAllColonies.OneParameterTask.run]> [Elapsed Time]- PlanetaryPinsRequest "
									, chrono.printElapsed(ChonoOptions.SHOWMILLIS));
							for (PlanetaryPin pin : pins.getPins()) {
								final ColonyStructure structure = ColonyStructureFactory.from(pin);
								getTarget().addStructure(structure);
							}
							// Mark the download complete.
							getTarget().setDownloading(false);
							getTarget().setDownloaded(true);
							// Wait a delay of 1 second to allow to watch the counter.
							try {
								Thread.sleep(TimeUnit.SECONDS.toMillis(4));
							} catch (InterruptedException ex) {
							}
							// Send message to the model to update the rendering.
							logger.debug(">> [ColonyManager.accessAllColonies]> Target: ", getTarget().toString());
							logger.debug(">> [ColonyManager.accessAllColonies.OneParameterTask.run]> Going to fire the STRUCTURE message");
							getTarget().fireStructureChange("EVENTSTRUCTURE_DOWNLOADDATA", getTarget(), null);

							logger.info("<< [ColonyManager.accessAllColonies.OneParameterTask.run]> Time Elapsed: " + chrono.printElapsed(Chrono.ChonoOptions.SHOWMILLIS));
						}
					}
			);
		}
		logger.debug("-- [ColonyManager.accessAllColonies]> [Elapsed Time]- Colony Model Generation", chrono.printElapsed(ChonoOptions.SHOWMILLIS));
		logger.info("<< [ColonyManager.accessAllColonies]> Colonies on list: ", presentColonies.size());
		return presentColonies;
	}

	@Override
	public AbstractManager initialize () {
		if ( !initialized ) {
			//			this.accessAllAssets();
			initialized = true;
		}
		return this;
	}
}

// - UNUSED CODE ............................................................................................

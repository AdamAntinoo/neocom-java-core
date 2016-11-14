//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.fragment;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.holder.SeparatorHolder;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.model.Separator;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This is an special version of a DataSource that will process a configuration external file to get the list
 * of used defined fittings.<br>
 * Then it will generate a short list of ship classes and under each of those classes a set of fittings. Then
 * the fittings will contain the list of items that form that fitting and those will be used on the data
 * source part of the fragment to show the items required for the selected fitting.
 * 
 * @author Adam Antinoo
 */
public class FittingsDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger							logger		= Logger.getLogger("FittingsDataSource");
	private static ArrayList<String>	fittings	= null;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingsDataSource() {
		// Initialize the class structures with the processed data if not already done.
		//	if (null == fittings) processFittings();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void createContentHierarchy() {
		// Clear the current list of elements.
		_root.clear();
		processFittings();
	}

	public ArrayList<String> getFittings() {
		return fittings;
	}

	public void propertyChange(final PropertyChangeEvent event) {
	}

	/**
	 * Read the fittings from the resource file to create the internal database.
	 */
	private void processFittings() {
		logger.info(">> FittingsDataSource.processFittings");
		// Read the data from the file.
		try {
			//	String fileName = ResourceStrings.getResource("");
			InputStream is = AppConnector.getStorageConnector().accessInternalStorage(
					AppConnector.getResourceString(R.string.fittingsfilename));
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			// Initialize structures.
			FittingPart fittingPart = null;
			String line = br.readLine();
			while (null != line) {
				// Check lines that start with "--" that signal a new fitting
				if (line.startsWith("--")) {
					// Extract the ship class and the fitting name
					String[] parts = line.split(",");
					String ship = parts[0];
					String fitting = parts[1];

					// Trim and adapt the names
					if (null != ship) {
						ship.replace("-", "");
						ship.replace("[", "");
					}
					if (null != fitting) {
						ship.replace("]", "");
					}
					// Add the current fitting to the list if ready.
					if (null != fittingPart) {
						_root.add(fittingPart);
					}
					fittingPart = new FittingPart(new Separator(fitting));
					// Add the ship to the resource list for this fitting.
					fittingPart.addResourceByName(ship);
				} else {
					if (null != fittingPart) {
						// Add items to the current fitting.
						if ((line == "") || (null == line) || (line.trim().equalsIgnoreCase(""))) {
							line = br.readLine();
							continue;
						}
						fittingPart.addResourceByName(line);
					}
				}
				line = br.readLine();
			}
			if (null != br) br.close();
		} catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
		logger.info("<< FittingsDataSource.processFittings");
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class FittingPart extends AbstractAndroidPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 3862966562596669788L;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingPart(final AbstractGEFNode task) {
		super(task);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addResourceByName(final String resourceName) {
		EveItem item = AppConnector.getDBConnector().searchItembyName(resourceName);
		addChild(item);
	}

	public Separator getCastedModel() {
		return (Separator) getModel();
	}

	public long getModelID() {
		return 0;
	}

	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		return new SeparatorHolder(this, _activity);
	}
}
// - UNUSED CODE ............................................................................................

//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.text.DecimalFormat;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.evedroid.model.Asteroid;
import org.dimensinfin.evedroid.model.EveItem;

import android.app.Activity;

// - CLASS IMPLEMENTATION ...................................................................................
public class AsteroidOnProgressPart extends AbstractAndroidPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long				serialVersionUID	= -7718648590261849585L;
	private static Logger						logger						= Logger.getLogger("AsteroidOnProgressPart");
	protected static DecimalFormat	quantityFormatter	= new DecimalFormat("#,##0");
	protected static DecimalFormat	volumeFormatter		= new DecimalFormat("#,##0.0");

	// - F I E L D - S E C T I O N ............................................................................
	//	private final AbstractPilotBasedActivity	activity					= null;
	//	private AbstractFragment									fragment					= null;

	//	private HashMap<Long, AbstractAndroidPart>																	containerList			= new HashMap<Long, AbstractAndroidPart>();
	//	private HashMap<AbstractAndroidPart, HashMap<Integer, AbstractAndroidPart>>	stackList					= new HashMap<AbstractAndroidPart, HashMap<Integer, AbstractAndroidPart>>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AsteroidOnProgressPart(final Asteroid node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_asteroidName() {
		return getCastedModel().getName();
	}

	public String get_asteroidQty() {
		return quantityFormatter.format(getCastedModel().getQuantity());
	}

	public String get_asteroidVolume() {
		EveItem item = getCastedModel().getItem();
		return volumeFormatter.format(getCastedModel().getQuantity() * item.getVolume());
	}

	public int get_secondsLeft() {
		EveItem item = getCastedModel().getItem();
		long qty = getCastedModel().getQuantity();
		double vol = getCastedModel().getQuantity() * item.getVolume();
		Activity act = getActivity();
		if (act instanceof MiningSessionActivity) {
			int volcycle = ((MiningSessionActivity) act).getInterfaceCycleVolume();
			int timecycle = ((MiningSessionActivity) act).getInterfaceCycleTime();
			double cycles = vol / volcycle;
			double seconds = cycles * timecycle;
			//		Interval elapsed = new Interval(getCastedModel().getStartInstant(), new Instant());
			//	return new Double(seconds - (elapsed.toDurationMillis() / 1000)).intValue();
			return new Double(seconds).intValue();
		}
		return 0;
	}

	public Asteroid getCastedModel() {
		return (Asteroid) getModel();
	}

	@Override
	public long getModelID() {
		return getCastedModel().getTypeID();
	}

	//	public void onClick(final View view) {
	//		// Toggle location to show its contents.
	//		getCastedModel().setExpanded(!getCastedModel().isExpanded());
	//		fireStructureChange(AppWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
	//	}
	//
	public String toString() {
		StringBuffer buffer = new StringBuffer("APIKeyPart [");
		buffer.append(this.getCastedModel());
		buffer.append(" ]");
		return buffer.toString();
	}

	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		return new AsteroidOnProgressHolder(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................

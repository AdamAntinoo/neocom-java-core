//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AppContext;
import org.dimensinfin.evedroid.model.EveChar;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.part.MarketDataPart;
import org.dimensinfin.evedroid.theme.DefaultTheme;
import org.dimensinfin.evedroid.theme.ITheme;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveDroidAppContext extends AppContext {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger			logger						= Logger.getLogger("EveDroidAppContext");
	private static final long	serialVersionUID	= -4682136709308278824L;

	// - F I E L D - S E C T I O N ............................................................................
	private EveChar						_pilot						= null;
	private EveItem						_item							= null;
	private ITheme						_theme						= new DefaultTheme();
	//	private int											marketCounter			= 0;
	//	private int											topCounter				= 0;
	private MarketDataPart		_blueprintpart		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	public MarketDataPart getBlueprintPart() {
		return _blueprintpart;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public EveItem getItem() {
		return _item;
	}

	//	public int getMarketCounter() {
	//		return marketCounter;
	//	}

	public EveChar getPilot() {
		return _pilot;
	}

	public ITheme getTheme() {
		return _theme;
	}

	//	public int getTopCounter() {
	//		return topCounter;
	//	}

	public void setBlueprintPart(final MarketDataPart part) {
		_blueprintpart = part;
	}

	public void setItem(final EveItem item) {
		_item = item;
	}

	//	public void setMarketCounter(final int counter) {
	//		marketCounter = counter;
	//	}

	public void setPilot(final EveChar pilot) {
		_pilot = pilot;
	}

	public void setTheme(final ITheme theme) {
		_theme = theme;
	}

	//	public void setTopCounter(final int counter) {
	//		topCounter = counter;
	//	}
}

// - UNUSED CODE ............................................................................................

//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.constant;

// - IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public class AppWideConstants {
	// - C O M P A R A T O R S
	public static final class comparators {
		public static final int	COMPARATOR_NAME							= 100;
		public static final int	COMPARATOR_ASSET_COUNT			= comparators.COMPARATOR_NAME + 1;
		public static final int	COMPARATOR_ITEM_TYPE				= comparators.COMPARATOR_NAME + 2;
		public static final int	COMPARATOR_RESOURCE_TYPE		= comparators.COMPARATOR_NAME + 3;
		public static final int	COMPARATOR_APIID_ASC				= comparators.COMPARATOR_NAME + 4;
		public static final int	COMPARATOR_APIID_DESC				= comparators.COMPARATOR_NAME + 5;
		public static final int	COMPARATOR_PRIORITY					= comparators.COMPARATOR_NAME + 6;
		public static final int	COMPARATOR_WEIGHT						= comparators.COMPARATOR_NAME + 16;
		public static final int	COMPARATOR_NEWESTDATESORT		= comparators.COMPARATOR_NAME + 7;
		public static final int	COMPARATOR_OLDESTDATESORT		= comparators.COMPARATOR_NAME + 8;
		public static final int	COMPARATOR_REQUEST_PRIORITY	= comparators.COMPARATOR_NAME + 17;
		public static final int	COMPARATOR_CARD_RATIO				= comparators.COMPARATOR_NAME + 18;
		// public static final int COMPARATOR_CREATIONDATE = COMPARATOR_NAME +
		// 9;
		public static final int	COMPARATOR_TIMEPENDING			= comparators.COMPARATOR_NAME + 10;
	}

	// - E X T R A S   M E S S A G E S
	public enum EExtras {
		CAPSULEERID, FITTINGID
	}

	// - F R A G M E N T   I D E N T I F I E R S
	public enum EFragment {
		DEFAULT_VARIANT, CAPSULEER_LIST, FITTING_LIST, FITTING_MODULES, FITTING_MANUFACTURE, FRAGMENT_SHIPSBYLOCATION, FRAGMENT_SHIPSBYCLASS
	}

	// - R E N D E R   M O D E S
	public enum ERenders {
		RENDER_FITTINGHEADER
	}

	// - M V C   V A R I A N T S
	//	public enum EVARIANT {
	//		DEFAULT_VARIANT, CAPSULEER_LIST, FITTING_LIST, FITTING_MODULES, FITTING_MANUFACTURE, SHIPS_BYLOCATION, SHIPS_BYCLASS
	//	}

	// - B A C K G R O U N D   U P D A T E   R E Q U E S T
	//	public enum ERequest {
	//		CITADEL, OUTPOST, CHARACTER, CORPORATION, MARKET
	//	}
	//	public enum ERequestClass {
	//		UNDEFINED, MARKETDATA, ASSETSDOWNLOAD, CHARACTERUPDATE, CITADELUPDATE, OUTPOSTUPDATE
	//	}
	//
	// - F I R E D   E V E N T S
	public static final class events {
		public static final String	EVENTSTRUCTURE_NEEDSREFRESH						= "EVENTSTRUCTURE_NEEDSREFRESH";
		public static final String	EVENTSTRUCTURE_RECALCULATE						= "EVENTSTRUCTURE_RECALCULATE";

		public static final String	EVENTMESSAGE_HIERARCHYCOMPLETED				= "EVENTMESSAGE_HIERARCHYCOMPLETED";

		public static final String	EVENTSTR_EVECHARBLUEPRINTS						= "EVENT_STRUCTURE.EVECHARBLUEPRINTS";
		public static final String	EVENTSTR_EVECHARLOCATIONS							= "EVENT_STRUCTURE.EVECHARLOCATIONS";
		public static final String	EVENTSTR_APIKEY												= "EVENT_STRUCTURE.APIKEY";
		public static final String	EVENTSTR_EVECHARACTER									= "EVENT_STRUCTURE.EVECHARACTER";
		// public static final String EVENTSTRUCTURE_EVECHARACTER_ASSETS =
		// "EVENT_STRUCTURE.EVECHARACTER_ASSETS";
		public static final String	EVENTSTR_EVECHART2MODULES							= "EVENT_STRUCTURE.EVECHARACTER_T2MODULES";
		public static final String	EVENTSTR_EVECHARMARKETCARDS						= "EVENT_STRUCTURE.EVECHARACTER_MARKETCARDS";
		public static final String	EVENTSTRUCTURE_EVECHARACTER_TASKADDED	= "EVENT_STRUCTURE.EVENTS_TASKADDED";
		public static final String	EVENTP_TASKMODIFIED										= "EVENT_PROPERTY.EVENTP_TASKMODIFIED";
		public static final String	EVENTSTRUCTURE_EVECHARACTER_ASSETS		= "EVENTSTRUCTURE_EVECHARACTER_ASSETS";
	}

	// - E X T R A S   M E S S A G E S
	public static final class extras {
		public static final String	EXTRA_EXCEPTIONMESSAGE				= "EXTRA_EXCEPTIONMESSAGE";
		public static final String	EXTRA_MODULEMARKETCARD_SIDE		= "EXTRA_MODULEMARKETCARD_SIDE";
		public static final String	EXTRA_MODULEMARKETCARD_ITEMID	= "EXTRA_MODULEMARKETCARD_ITEMID";
		public static final String	EXTRA_EVECHARACTERID					= "EXTRA_EVECHARACTERID";
		public static final String	EXTRA_EVEITEMID								= "EXTRA_EVEITEMID";
		public static final String	EXTRA_CHARACTER_LOCALIZER			= "EXTRA_CHARACTER_LOCALIZER";
		public static final String	EXTRA_MARKETDATA_LOCALIZER		= "EXTRA_MARKETDATA_LOCALIZER";
		public static final String	EXTRA_BLUEPRINTID							= "EXTRA_T2BLUEPRINTID";
		public static final String	EXTRA_BLUEPRINTACTIVITY				= "EXTRA_BLUEPRINTACTIVITY";
	}

	// - F R A G M E N T   I D E N T I F I E R S
	public static final class fragment {
		public static final int	FRAGMENT_DEFAULTID_EMPTY							= 0;
		public static final int	FRAGMENT_PILOTLIST										= 100;
		public static final int	FRAGMENT_MARKETORDERS									= fragment.FRAGMENT_DEFAULTID_EMPTY + 510;
		public static final int	FRAGMENT_MANUFACTUREJOBS							= fragment.FRAGMENT_DEFAULTID_EMPTY + 610;
		public static final int	FRAGMENT_INVENTIONJOBS								= fragment.FRAGMENT_DEFAULTID_EMPTY + 620;
		public static final int	FRAGMENT_INDUSTRYJOBACTIONS						= fragment.FRAGMENT_DEFAULTID_EMPTY + 304;
		public static final int	FRAGMENT_SHIPSBYLOCATION							= fragment.FRAGMENT_DEFAULTID_EMPTY + 400;
		public static final int	FRAGMENT_SHIPSBYCLASS									= fragment.FRAGMENT_DEFAULTID_EMPTY + 410;

		public static final int	FRAGMENT_PILOTINFO_INFO								= fragment.FRAGMENT_PILOTLIST + 1;
		public static final int	FRAGMENT_PILOTINFO_T24SELL						= fragment.FRAGMENT_PILOTLIST + 2;
		public static final int	FRAGMENT_PILOTINFO_SHIPS							= fragment.FRAGMENT_PILOTLIST + 3;
		public static final int	FRAGMENT_PILOTINFO_BPCT2							= fragment.FRAGMENT_PILOTLIST + 4;
		public static final int	FRAGMENT_ASSETSBYLOCATION							= fragment.FRAGMENT_DEFAULTID_EMPTY + 200;
		public static final int	FRAGMENT_ASSETSBYCATEGORY							= fragment.FRAGMENT_DEFAULTID_EMPTY + 201;
		public static final int	FRAGMENT_ASSETSBYTYPE									= fragment.FRAGMENT_DEFAULTID_EMPTY + 202;
		public static final int	FRAGMENT_ASSETSMATERIALS							= fragment.FRAGMENT_DEFAULTID_EMPTY + 203;
		public static final int	FRAGMENT_ASSETSAREASTEROIDS						= fragment.FRAGMENT_DEFAULTID_EMPTY + 204;
		public static final int	FRAGMENT_ASSETSAREPLANETARY						= fragment.FRAGMENT_DEFAULTID_EMPTY + 205;
		public static final int	FRAGMENT_ASSETSARESHIPS								= fragment.FRAGMENT_DEFAULTID_EMPTY + 206;
		public static final int	FRAGMENT_INDUSTRYT1BLUEPRINTS					= fragment.FRAGMENT_DEFAULTID_EMPTY + 300;
		public static final int	FRAGMENT_INDUSTRYT2BLUEPRINTS					= fragment.FRAGMENT_DEFAULTID_EMPTY + 301;
		public static final int	FRAGMENT_INDUSTRYT3BLUEPRINTS					= fragment.FRAGMENT_DEFAULTID_EMPTY + 302;
		public static final int	FRAGMENT_INDUSTRYT1MANUFACTURE				= fragment.FRAGMENT_DEFAULTID_EMPTY + 303;
		public static final int	FRAGMENT_INDUSTRYJOBHEADER						= fragment.FRAGMENT_DEFAULTID_EMPTY + 1304;
		public static final int	FRAGMENT_INDUSTRYLOMRESOURCES					= fragment.FRAGMENT_DEFAULTID_EMPTY + 305;
		public static final int	FRAGMENT_INDUSTRYT2MANUFACTUREHEADER	= fragment.FRAGMENT_DEFAULTID_EMPTY + 306;
		public static final int	FRAGMENT_INDUSTRYT2INVENTION					= fragment.FRAGMENT_DEFAULTID_EMPTY + 307;
		public static final int	FRAGMENT_INDUSTRYJOBACTIONSDATASOURCE	= fragment.FRAGMENT_DEFAULTID_EMPTY + 308;
		// public static final int FRAGMENT_BUYS = FRAGMENT_DEFAULTID_EMPTY +
		// 500;
		// public static final int FRAGMENT_SELLS = FRAGMENT_DEFAULTID_EMPTY +
		// 501;
		// public static final int FRAGMENT_MARKETORDERSDATASOURCE =
		// FRAGMENT_DEFAULTID_EMPTY + 520;
		// public static final int FRAGMENT_MARKETBUYS =
		// FRAGMENT_DEFAULTID_EMPTY + 503;
		// public static final int FRAGMENT_MARKETSELLS =
		// FRAGMENT_DEFAULTID_EMPTY + 504;
		public static final int	FRAGMENT_QUEUESHEADER									= fragment.FRAGMENT_DEFAULTID_EMPTY + 601;
		public static final int	FRAGMENT_JOBLISTBODY									= fragment.FRAGMENT_DEFAULTID_EMPTY + 602;

		public static final int	FRAGMENT_ITEMMODULESTACKS							= 591;
		public static final int	FRAGMENT_ITEMMODULERESOURCES					= 592;

		public static final int	FRAGMENT_FITTINGS											= 1101;

		public static final int	FRAGMENT_TASKLIST											= 440;
		public static final int	FRAGMENT_MODULEDETAIL									= 341;
		public static final int	FRAGMENT_TASKACTIONS									= 481;
		public static final int	FRAGMENT_MININGSESSIONS								= 721;
		public static final int	FRAGMENT_JOBSCHEDULED									= 921;

		// public static final String FRAGLABEL_T24SELL = "Modules4Sell";
		// public static final String FRAGLABEL_SHIPS = "Ships";
		// public static final String FRAGLABEL_BLUEPRINTS = "T2Blueprints";
		// public static final String FRAGLABEL_FITTINGS = "Fittings";
	}

	public static final class manufacturingParameters {
		public static final int	PRODUCTIONEFFICENCY_SKILL	= 5;
		public static final int	DEFAULT_T1ME							= 10;
		public static final int	DEFAULT_T2ME							= -4;
		public static final int	DEFAULT_T1PE							= 5;
		public static final int	DEFAULT_T2PE							= -4;
	}

	// - P A N E L S   I D E N T I F I E R S
	public static final class panel {
		public static final int	PANEL_EMPTY								= 20000;
		public static final int	PANEL_MARKETORDERSBODY		= panel.PANEL_EMPTY + 10;
		public static final int	PANEL_INDUSTRYJOBSHEADER	= panel.PANEL_EMPTY + 20;
		public static final int	PANEL_INDUSTRYJOBSBODY		= panel.PANEL_EMPTY + 21;
	}

	// - P R E F E R E N C E S
	public static final class preference {
		public static final String	PREF_APPTHEMES						= "prefkey_appthemes";
		public static final String	PREF_LOCATIONSLIMIT				= "prefkey_locationsLimit";
		public static final String	PREF_ALLOWMOVEREQUESTS		= "prefkey_AllowMoveRequests";
		public static final String	PREF_CALCULATEASSETVALUE	= "prefkey_AssetValueCalculation";
		public static final String	PREF_BLOCKDOWNLOAD				= "prefkey_BlockDownloads";
		public static final String	PREF_BLOCKMARKET					= "prefkey_BlockMarket";
	}

	public static final class rendermodes {
		public static final int	NORMALRENDER										= 1000;
		public static final int	RENDER_RESOURCECOMPONENTJOB			= rendermodes.NORMALRENDER + 2;
		public static final int	RENDER_RESOURCEOUTPUTJOB				= rendermodes.NORMALRENDER + 4;
		public static final int	RENDER_RESOURCEOUTPUTBLUEPRINT	= rendermodes.NORMALRENDER + 6;
		public static final int	RENDER_RESOURCESKILLJOB					= rendermodes.NORMALRENDER + 8;
		public static final int	RENDER_RESOURCEBLUEPRINTJOB			= rendermodes.NORMALRENDER + 10;
		public static final int	RENDER_RESOURCESCHEDULEDSELL		= rendermodes.NORMALRENDER + 11;
		public static final int	RENDER_BLUEPRINTINDUSTRYHEADER	= rendermodes.NORMALRENDER + 12;
		public static final int	RENDER_BLUEPRINTINVENTIONHEADER	= rendermodes.NORMALRENDER + 14;
		public static final int	RENDER_BLUEPRINTINDUSTRY				= rendermodes.NORMALRENDER + 16;
		public static final int	RENDER_BLUEPRINTT2INVENTION			= rendermodes.NORMALRENDER + 18;
		public static final int	RENDER_SKILLACTION							= rendermodes.NORMALRENDER + 20;
		public static final int	RENDER_JOB4LIST									= rendermodes.NORMALRENDER + 22;
		public static final int	RENDER_RUNNINGJOB								= rendermodes.NORMALRENDER + 24;
		public static final int	RENDER_JOBEXTENSION							= rendermodes.NORMALRENDER + 26;
		public static final int	RENDER_LOCATIONMODE							= rendermodes.NORMALRENDER + 28;
		public static final int	RENDER_LOCATIONMAKETHUB					= rendermodes.NORMALRENDER + 30;
		public static final int	RENDER_GROUPMARKETSIDE					= rendermodes.NORMALRENDER + 60;
		public static final int	RENDER_GROUPJOBSTATE						= rendermodes.NORMALRENDER + 61;
		public static final int	RENDER_GROUPSHIPFITTING					= rendermodes.NORMALRENDER + 62;
		public static final int	RENDER_GROUPMARKETANALYTICAL		= rendermodes.NORMALRENDER + 63;
		public static final int	RENDER_GROUPMARKETORDERREGION		= rendermodes.NORMALRENDER + 64;
		public static final int	RENDER_SHIP4ASSETSBYLOCATION		= rendermodes.NORMALRENDER + 70;
		public static final int	RENDER_MARKETORDER							= rendermodes.NORMALRENDER + 80;
		public static final int	RENDER_MARKETORDERSCHEDULEDSELL	= rendermodes.NORMALRENDER + 81;
		public static final int	RENDER_FITTINGHEADER						= rendermodes.NORMALRENDER + 101;
	}

	// - E X P I R A T I O N   T I M E S
	public static final class times {
		public static final long	HOURS2	= 2 * AppWideConstants.HOURS1;
		public static final long	HOURS12	= 12 * AppWideConstants.HOURS1;
	}

	// - S T A T I C - S E C T I O N
	// ..........................................................................
	public static final boolean	DEVELOPMENT									= true;

	// - C H A R A C T E R S
	public static final String	FLOW_ARROW_RIGHT						= " ► ";
	public static final String	FLOW_ARROW_LEFT							= " ◄ ";
	public static final String	FLOW_ARROW_UP								= " ▲ ";
	public static final String	FLOW_ARROW_DOWN							= " ▼ ";
	public static final String	INFINITY										= " ∞ ";
	public static final String	HOME												= " ⌂ ";
	public static final String	DELTA												= " ∆ ";

	// - B U N D L E S
	public static final String	BUNDLE_PILOTID							= "BUNDLE_PILOTID";

	// - E X C E P T I O N S
	public static final String	EXCEPTION_GENERICEXCEPTION	= "EXCEPTION_GENERICEXCEPTION";
	public static final String	EXCEPTION_APP_STOP					= "EXCEPTION_APP_STOP";

	// - E X T R A S
	public static final String	EXTRA_USERMESSAGESTOP				= "EXTRA_USERMESSAGESTOP";
	public static final String	EXTRA_EVECHARACTERID				= "EXTRA_EVECHARACTERID";
	public static final String	EXTRA_MARKETDATA_LOCALIZER	= "EXTRA_MARKETDATA_LOCALIZER";

	public static final long		SECONDS1										= 1 * 1000;
	public static final long		SECONDS5										= 5 * AppWideConstants.SECONDS1;
	public static final long		MINUTES1										= 1 * 60 * AppWideConstants.SECONDS1;
	public static final long		MINUTES15										= 15 * AppWideConstants.MINUTES1;
	public static final long		MINUTES30										= 30 * AppWideConstants.MINUTES1;
	public static final long		MINUTES45										= 45 * AppWideConstants.MINUTES1;
	public static final long		MINUTES60										= 60 * AppWideConstants.MINUTES1;
	public static final long		HOURS1											= 1 * 60 * AppWideConstants.MINUTES1;
	public static final long		HOURS3											= 3 * AppWideConstants.HOURS1;
	public static final long		HOURS24											= 24 * AppWideConstants.HOURS1;

}

// - UNUSED CODE
// ............................................................................................

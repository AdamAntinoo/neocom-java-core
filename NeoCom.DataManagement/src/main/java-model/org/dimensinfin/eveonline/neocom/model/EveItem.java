//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.market.MarketDataEntry;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveItem extends NeoComNode {
	public enum ItemTechnology {
		Tech_1("Tech I"), Tech_2("Tech II"), Tech_3("Tech III");

		// --- E N U M   I M P L E M E N T A T I O N
		private String label;

		ItemTechnology( String newlabel ) {
			this.label = newlabel;
		}

		public String getName() {
			return this.label;
		}

		/** Return the item tech from the label string by matching it to the enum label. */
		public static ItemTechnology lookupLabel( String label ) {
			return lookup.get(label);
		}

		// --- I N V E R S E   L O O K U P   T A B L E
		private static final Map<String, ItemTechnology> lookup = new HashMap<>();

		static {
			for ( ItemTechnology env : ItemTechnology.values() ) {
				lookup.put(env.getName(), env);
			}
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -2548296399305221197L;
	private static EveItem defaultItem = null;
	private static final int DEFAULT_TYPE_ID = 34;

	//	public static EveItem getDefaultItem()  {
	//		if (null == EveItem.defaultItem) {
	//			EveItem.defaultItem = accessSDEDBHelper().searchItem4Id(EveItem.DEFAULT_TYPE_ID);
	////			EveItem.defaultItem.setDefaultPrice(GlobalDataManager.searchMarketPrice(EveItem.DEFAULT_TYPE_ID));
	////			EveItem.defaultItem.futureBuyerData = new MarketDataSet(EveItem.DEFAULT_TYPE_ID, EMarketSide.BUYER);
	////			EveItem.defaultItem.futureSellerData = new MarketDataSet(EveItem.DEFAULT_TYPE_ID, EMarketSide.SELLER);
	//		}
	//		return EveItem.defaultItem;
	//	}

	// - F I E L D - S E C T I O N ............................................................................
	private int id = 34;
	private String name = "<NAME>";
	private int groupId = -1;
	private int categoryId = -1;
	private ItemGroup group = null;
	private ItemCategory category = null;
	/**
	 * This is the default price set for an item at the SDE database. This price should not be changed and there should be
	 * methods to get any other price set from the market data.
	 */
	private double baseprice = -1.0;
	/**
	 * This is the ESI returned price from the global market data service. This is the price shown on the game UI for item values
	 * and it is not tied to any specific market place.
	 */
	public double price = -1.0;
	private String tech = ModelWideConstants.eveglobal.TechI;
	private double volume = 0.0;

	// - A D D I T I O N A L   F I E L D S
	private transient EIndustryGroup industryGroup = EIndustryGroup.UNDEFINED;
	/**
	 * This represents the market data for the BUY market orders present at different selected systems. This element and the
	 * next are lazy evaluated as futures and should enqueue market requests for background threads.
	 */
	private transient Future<MarketDataSet> futureBuyerData = null;
	/**
	 * The same but for SELLER orders present at the market.
	 */
	private transient Future<MarketDataSet> futureSellerData = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveItem() {
		super();
//		jsonClass = "EveItem";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//--- G E T T E R S   &   S E T T E R S
	@Deprecated
	public int getItemId() {
		return id;
	}

	public int getTypeId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getBaseprice() {
		return baseprice;
	}

	public int getGroupId() {
		if ( null == group ) {
			try {
				group = accessSDEDBHelper().searchItemGroup4Id(groupId);
			} catch ( NeoComRuntimeException neoe ) {
				group = new ItemGroup();
			}
		}
		return group.getGroupId();
	}

	public int getCategoryId() {
		if ( null == category ) {
			try {
				category = accessSDEDBHelper().searchItemCategory4Id(categoryId);
			} catch ( NeoComRuntimeException neoe ) {
				category = new ItemCategory();
			}
		}
		return category.getCategoryId();
	}

	/**
	 * Some items have tech while others don't. Tech information has to be calculated for some items when I
	 * download them as assets or blueprints. Set it to a default value that by now I can consider valid.
	 */
	public String getTech() {
		return tech;
	}

	public double getVolume() {
		return volume;
	}

	public boolean isBlueprint() {
		if ( this.getCategoryName().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint) )
			return true;
		else
			return false;
	}

	/**
	 * Return the ESI api market set price for this item. Sometimes there is another price markets as the average price that I am
	 * not using now.
	 * @return
	 */
	public double getPrice() {
		if ( price < 0.0 ) {
			try {
				price = accessGlobal().searchMarketPrice(getTypeId()).getAdjustedPrice();
			} catch ( NeoComRuntimeException neoe ) {
				price = -1.0;
			}
			if ( price < 1.0 ) price = baseprice;
		}
		return price;
	}

	public EIndustryGroup getIndustryGroup() {
		if ( industryGroup == EIndustryGroup.UNDEFINED ) {
			this.classifyIndustryGroup();
		}
		return industryGroup;
	}

	/**
	 * This is the key method used when instantiating an EveItem to set the eve item identifier of the game objectt that is
	 * represented. During the setting for this value we instantiate the market data futures to be posted on a worker thread with
	 * the hope to be resolved before the user requirement for the market data real values.
	 * @param typeId the eve game unique type identifier.
	 */
	public void setTypeId( final int typeId ) {
		id = typeId;
		futureBuyerData = retrieveMarketData(getTypeId(), EMarketSide.BUYER);
		futureSellerData = retrieveMarketData(getTypeId(), EMarketSide.SELLER);
	}

	public void setName( final String name ) {
		this.name = name;
	}

	public void setGroupId( final int groupid ) {
		this.groupId = groupid;
		try {
			group = accessSDEDBHelper().searchItemGroup4Id(groupid);
		} catch ( NeoComRuntimeException neoe ) {
			group = new ItemGroup();
		}
	}

	public void setCategoryId( final int categoryid ) {
		this.categoryId = categoryid;
		try {
			category = accessSDEDBHelper().searchItemCategory4Id(categoryid);
		} catch ( NeoComRuntimeException neoe ) {
			category = new ItemCategory();
		}
	}

	public void setBasePrice( final double price ) {
		baseprice = price;
	}

	public void setPrice( final double price ) {
		this.price = price;
	}

	public void setTech( final String tech ) {
		this.tech = tech;
	}

	public void setVolume( final double volume ) {
		this.volume = volume;
	}

	public void setIndustryGroup( final EIndustryGroup group ) {
		this.industryGroup = group;
	}

	//--- V I R T U A L   A C C E S S O R S
	public String getHullGroup() {
		if ( getIndustryGroup() == EIndustryGroup.HULL ) {
			if ( getGroupName().equalsIgnoreCase("Assault Frigate") ) return "frigate";
			if ( getGroupName().equalsIgnoreCase("Attack Battlecruiser") ) return "battlecruiser";
			if ( getGroupName().equalsIgnoreCase("Battleship") ) return "battleship";
			if ( getGroupName().equalsIgnoreCase("Blockade Runner") ) return "battlecruiser";
			if ( getGroupName().equalsIgnoreCase("Combat Battlecruiser") ) return "battlecruiser";
			if ( getGroupName().equalsIgnoreCase("Combat Recon Ship") ) return "battleship";
			if ( getGroupName().equalsIgnoreCase("Command Destroyer") ) return "destroyer";
			if ( getGroupName().equalsIgnoreCase("Corvette") ) return "shuttle";
			if ( getGroupName().equalsIgnoreCase("Cruiser") ) return "cruiser";
			if ( getGroupName().equalsIgnoreCase("Deep Space Transport") ) return "industrial";
			if ( getGroupName().equalsIgnoreCase("Destroyer") ) return "destroyer";
			if ( getGroupName().equalsIgnoreCase("Exhumer") ) return "miningBarge";
			if ( getGroupName().equalsIgnoreCase("Frigate") ) return "frigate";
			if ( getGroupName().equalsIgnoreCase("Heavy Assault Cruiser") ) return "cruiser";
			if ( getGroupName().equalsIgnoreCase("Industrial") ) return "industrial";
			if ( getGroupName().equalsIgnoreCase("Industrial Command Ship") ) return "industrial";
			if ( getGroupName().equalsIgnoreCase("Interceptor") ) return "frigate";
			if ( getGroupName().equalsIgnoreCase("Interdictor") ) return "frigate";
			if ( getGroupName().equalsIgnoreCase("Logistics") ) return "cruiser";
			if ( getGroupName().equalsIgnoreCase("Mining Barge") ) return "miningBarge";
			if ( getGroupName().equalsIgnoreCase("Shuttle") ) return "shuttle";
			if ( getGroupName().equalsIgnoreCase("Stealth Bomber") ) return "cruiser";
			if ( getGroupName().equalsIgnoreCase("Strategic Cruiser") ) return "cruiser";
			if ( getGroupName().equalsIgnoreCase("Tactical Destroyer") ) return "destroyer";
		}
		return "not-applies";
	}

	public EveItem setHullGroup( final String dummy ) {
		return this;
	}

	public ItemGroup getGroup() {
		return this.group;
	}

	public EveItem setGroup( final ItemGroup group ) {
		this.group = group;
		return this;
	}

	public ItemCategory getCategory() {
		return this.category;
	}

	public EveItem setCategory( final ItemCategory category ) {
		this.category = category;
		return this;
	}

	/**
	 * Try to get the best price for this element. There are two sets of prices, those for selling an item
	 * (highest buyers) and those to buy the same item (lower seller). The default price that is the one from
	 * the SDE database if sometimes far from close to the real market price.
	 * The item declared price can also be obtained from the market but this will require a location to get
	 * access to t the CCP API. The item closest price will be obtained from the best buyer so that price
	 * represent the income I will obtain in case I sell that item.
	 * For simple item it is not an important point since the interface allows to get the original data to any
	 * higher level model object.
	 */
	public MarketDataEntry getLowestSellerPrice() throws ExecutionException, InterruptedException {
		return this.getSellerMarketData().getBestMarket();
	}

	public EveItem setLowestSellerPrice( final MarketDataEntry dummy ) {
		return this;
	}

	public MarketDataEntry getHighestBuyerPrice() throws ExecutionException, InterruptedException {
		return this.getBuyerMarketData().getBestMarket();
	}

	public EveItem setHighestBuyerPrice( final MarketDataEntry dummy ) {
		return this;
	}

	public String getCategoryName() {
		if ( null == category ) {
			try {
				category = accessSDEDBHelper().searchItemCategory4Id(categoryId);
			} catch ( NeoComRuntimeException neoe ) {
				category = new ItemCategory();
			}
		}
		return category.getCategoryName();
	}

	public EveItem setCategoryName( final String dummy ) {
		return this;
	}

	public String getGroupName() {
		if ( null == group ) {
			try {
				group = accessSDEDBHelper().searchItemGroup4Id(groupId);
			} catch ( NeoComRuntimeException neoe ) {
				group = new ItemGroup();
			}
		}
		return group.getGroupName();
	}

	public EveItem setGroupName( final String dummy ) {
		return this;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("EveItem [");
		buffer.append("#").append(this.getItemId()).append(" - ").append(this.getName()).append(" ");
		buffer.append(this.getGroupName()).append("/").append(this.getCategory()).append(" [").append(" ");
		buffer.append("IC:").append(industryGroup).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	//--- N O N   E X P O R T A B L E   F I E L D S

	//--- P R I V A T E   S E C T I O N
	protected void classifyIndustryGroup() {
		if ( (this.getGroupName().equalsIgnoreCase("Composite")) && (this.getCategoryName().equalsIgnoreCase("Material")) ) {
			industryGroup = EIndustryGroup.REACTIONMATERIALS;
		}
		if ( this.getCategoryName().equalsIgnoreCase("Asteroid") ) {
			industryGroup = EIndustryGroup.OREMATERIALS;
		}
		if ( (this.getGroupName().equalsIgnoreCase("Mining Crystal")) && (this.getCategoryName().equalsIgnoreCase("Charge")) ) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if ( this.getCategoryName().equalsIgnoreCase("Charge") ) {
			industryGroup = EIndustryGroup.CHARGE;
		}
		if ( this.getGroupName().equalsIgnoreCase("Tool") ) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if ( this.getCategoryName().equalsIgnoreCase("Commodity") ) {
			industryGroup = EIndustryGroup.COMMODITY;
		}
		if ( this.getCategoryName().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint) ) {
			industryGroup = EIndustryGroup.BLUEPRINT;
		}
		if ( this.getCategoryName().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill) ) {
			industryGroup = EIndustryGroup.SKILL;
		}
		if ( this.getGroupName().equalsIgnoreCase(ModelWideConstants.eveglobal.Mineral) ) {
			industryGroup = EIndustryGroup.REFINEDMATERIAL;
		}
		if ( this.getCategoryName().equalsIgnoreCase("Module") ) {
			industryGroup = EIndustryGroup.COMPONENTS;
		}
		if ( this.getCategoryName().equalsIgnoreCase("Drone") ) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if ( this.getCategoryName().equalsIgnoreCase("Planetary Commodities") ) {
			industryGroup = EIndustryGroup.PLANETARYMATERIALS;
		}
		if ( this.getGroupName().equalsIgnoreCase("Datacores") ) {
			industryGroup = EIndustryGroup.DATACORES;
		}
		if ( this.getGroupName().equalsIgnoreCase("Salvaged Materials") ) {
			industryGroup = EIndustryGroup.SALVAGEDMATERIAL;
		}
		if ( this.getCategoryName().equalsIgnoreCase("Ship") ) {
			industryGroup = EIndustryGroup.HULL;
		}
	}

	/**
	 * Submits a <code>Callable</code> request to the background threads to retrieve the data into the <code>Future</code>. In
	 * the case the market data is accessed and the Future was not completed the thread should wait until the market data access
	 * completes. Most of the calls will execute fast because the data being cached continuously py the scheduled submitted jobs.
	 * @param itemId the items id to search market data.
	 * @param side   if we should search buy orders or sell orders.
	 * @return a <code>Future</code> with the whole market data values.
	 */
	private Future<MarketDataSet> retrieveMarketData( final int itemId, final EMarketSide side ) {
		return accessGlobal().searchMarketData(itemId, side);
	}

	/**
	 * Search on the market data provider for the market registers for this particular item. This will search on
	 * the market data cache and then if not found on the market data service that will call the corresponding
	 * parsers to extract the information.<br>
	 * This method can fail by some causes. First of them because there are no connection to the sources of by
	 * errors during the parsing of the information. In such cases I should be ready to get the price
	 * information from other sources like the default price information.
	 */
	public MarketDataSet getBuyerMarketData() throws ExecutionException, InterruptedException {
		if ( null == futureBuyerData ) {
			futureBuyerData = retrieveMarketData(getTypeId(), EMarketSide.BUYER);
		}
		return futureBuyerData.get();
	}

	/**
	 * Search on the market data provider for the market registers for this particular item. This will search on
	 * the market data cache and then if not found on the market data service that will call the corresponding
	 * parsers to extract the information.<br>
	 * This method can fail by some causes. First of them because there are no connection to the sources of by
	 * errors during the parsing of the information. In such cases I should be ready to get the price
	 * information from other sources like the default price information.
	 */
	public MarketDataSet getSellerMarketData() throws ExecutionException, InterruptedException {
		if ( null == futureBuyerData ) {
			futureBuyerData = retrieveMarketData(getTypeId(), EMarketSide.SELLER);
		}
		return futureBuyerData.get();
	}
}
// - UNUSED CODE ............................................................................................
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

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.core.NeoComException;
import org.dimensinfin.eveonline.neocom.core.NeocomRuntimeException;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.market.MarketDataEntry;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveItem extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -2548296399305221197L;
	private static EveItem defaultItem = null;
	private static final int DEFAULT_TYPE_ID = 34;

//	public static EveItem getDefaultItem() {
//		if (null == EveItem.defaultItem) {
//			EveItem.defaultItem = accessSDEDBHelper().searchItem4Id(EveItem.DEFAULT_TYPE_ID);
////			EveItem.defaultItem.setDefaultPrice(GlobalDataManager.searchMarketPrice(EveItem.DEFAULT_TYPE_ID));
////			EveItem.defaultItem.buyerData = new MarketDataSet(EveItem.DEFAULT_TYPE_ID, EMarketSide.BUYER);
////			EveItem.defaultItem.sellerData = new MarketDataSet(EveItem.DEFAULT_TYPE_ID, EMarketSide.SELLER);
//		}
//		return EveItem.defaultItem;
//	}

	// - F I E L D - S E C T I O N ............................................................................
	private int id = 34;
	private String name = "<NAME>";
	private int groupid = -1;
	private int categoryid = -1;
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
	public double defaultprice = -1.0;
	private double volume = 0.0;
	private String tech = ModelWideConstants.eveglobal.TechI;

	// - A D D I T I O N A L   F I E L D S
	private transient EIndustryGroup industryGroup = EIndustryGroup.UNDEFINED;
	private transient MarketDataSet buyerData = null;
	private transient MarketDataSet sellerData = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveItem() {
		super();
		jsonClass = "EveItem";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public double getBaseprice() {
		return baseprice;
	}

	public int getCategoryId() {
		if (null == category) {
			try {
				category = accessSDEDBHelper().searchItemCategory4Id(categoryid);
			} catch (NeoComException neoe) {
				category = new ItemCategory();
			}
		}
		return category.getCategoryId();
	}

	public int getGroupId() {
		if (null == group) {
			try {
				group = accessSDEDBHelper().searchItemGroup4Id(groupid);
			} catch (NeoComException neoe) {
				group = new ItemGroup();
			}
		}
		return group.getGroupId();
	}

	public String getCategory() {
		if (null == category) {
			try {
				category = accessSDEDBHelper().searchItemCategory4Id(categoryid);
			} catch (NeoComException neoe) {
				category = new ItemCategory();
			}
		}
		return category.getCategoryName();
	}

	public String getCategoryName() {
		if (null == category) {
			try {
				category = accessSDEDBHelper().searchItemCategory4Id(categoryid);
			} catch (NeoComException neoe) {
				category = new ItemCategory();
			}
		}
		return category.getCategoryName();
	}

	public String getGroupName() {
		if (null == group) {
			try {
				group = accessSDEDBHelper().searchItemGroup4Id(groupid);
			} catch (NeoComException neoe) {
				group = new ItemGroup();
			}
		}
		return group.getGroupName();
	}

	public String getHullGroup() {
		if (getIndustryGroup() == EIndustryGroup.HULL) {
			if (getGroupName().equalsIgnoreCase("Assault Frigate")) return "frigate";
			if (getGroupName().equalsIgnoreCase("Attack Battlecruiser")) return "battlecruiser";
			if (getGroupName().equalsIgnoreCase("Battleship")) return "battleship";
			if (getGroupName().equalsIgnoreCase("Blockade Runner")) return "battlecruiser";
			if (getGroupName().equalsIgnoreCase("Combat Battlecruiser")) return "battlecruiser";
			if (getGroupName().equalsIgnoreCase("Combat Recon Ship")) return "battleship";
			if (getGroupName().equalsIgnoreCase("Command Destroyer")) return "destroyer";
			if (getGroupName().equalsIgnoreCase("Corvette")) return "shuttle";
			if (getGroupName().equalsIgnoreCase("Cruiser")) return "cruiser";
			if (getGroupName().equalsIgnoreCase("Deep Space Transport")) return "industrial";
			if (getGroupName().equalsIgnoreCase("Destroyer")) return "destroyer";
			if (getGroupName().equalsIgnoreCase("Exhumer")) return "miningBarge";
			if (getGroupName().equalsIgnoreCase("Frigate")) return "frigate";
			if (getGroupName().equalsIgnoreCase("Heavy Assault Cruiser")) return "cruiser";
			if (getGroupName().equalsIgnoreCase("Industrial")) return "industrial";
			if (getGroupName().equalsIgnoreCase("Industrial Command Ship")) return "industrial";
			if (getGroupName().equalsIgnoreCase("Interceptor")) return "frigate";
			if (getGroupName().equalsIgnoreCase("Interdictor")) return "frigate";
			if (getGroupName().equalsIgnoreCase("Logistics")) return "cruiser";
			if (getGroupName().equalsIgnoreCase("Mining Barge")) return "miningBarge";
			if (getGroupName().equalsIgnoreCase("Shuttle")) return "shuttle";
			if (getGroupName().equalsIgnoreCase("Stealth Bomber")) return "cruiser";
			if (getGroupName().equalsIgnoreCase("Strategic Cruiser")) return "cruiser";
			if (getGroupName().equalsIgnoreCase("Tactical Destroyer")) return "destroyer";
		}
		return "not-applies";
	}

	public void setGroupId( final int groupid ) {
		this.groupid = groupid;
		try {
			group = accessSDEDBHelper().searchItemGroup4Id(groupid);
		} catch (NeoComException neoe) {
			group = new ItemGroup();
		}
	}

	public void setCategoryId( final int categoryid ) {
		this.categoryid = categoryid;
		try {
			category = accessSDEDBHelper().searchItemCategory4Id(categoryid);
		} catch (NeoComException neoe) {
			category = new ItemCategory();
		}
	}

	@JsonIgnore
	public MarketDataEntry getHighestBuyerPrice() {
		return this.getBuyerMarketData().getBestMarket();
	}

	public EIndustryGroup getIndustryGroup() {
		if (industryGroup == EIndustryGroup.UNDEFINED) {
			this.classifyIndustryGroup();
		}
		return industryGroup;
	}

	@Deprecated
	public int getItemID() {
		return id;
	}

	public int getItemId() {
		return id;
	}

	/**
	 * Try to get the best price for this element. There are two sets of prices, those for selling an item
	 * (highest buyers) and those to buy the same item (lower seller). The default price that is the one from
	 * the SDE database if sometimes far from close to the real market price.<br>
	 * The item declared price can also be obtained from the market but this will require a location to get
	 * access to t the CCP API. The item closest price will be obtained from the best buyer so that price
	 * represent the income I will obtain in case I sell that item.<br>
	 * For simple item it is not an important point since the interface allows to get the original data to any
	 * higher level model object.
	 */
	public MarketDataEntry getLowestSellerPrice() {
		return this.getSellerMarketData().getBestMarket();
	}

	public String getName() {
		return name;
	}

	/**
	 * Return the ESI api market set price for this item. Sometimes there is another price markes as the average price that I am
	 * not using now.
	 *
	 * @return
	 */
	public double getPrice() {
		if (defaultprice < 0.0) {
			try {
				defaultprice = accessGlobal().searchMarketPrice(getTypeID()).getAdjustedPrice();
			} catch (NeocomRuntimeException neoe) {
				defaultprice = -1.0;
			}
			if (defaultprice < 1.0) defaultprice = baseprice;
		}
		return defaultprice;
	}

	/**
	 * Some items have tech while others don't. Tech information has to be calculated for some items when I
	 * download them as assets or blueprints. Set it to a default value that by now I can consider valid.
	 */
	public String getTech() {
		return tech;
	}

	@Deprecated
	public int getTypeID() {
		return id;
	}

	public int getTypeId() {
		return id;
	}

	public double getVolume() {
		return volume;
	}

	public boolean hasInvention() {
		// REFACTOR This has to be reimplemented
		return false;
		//		return ModelAppConnector.getSingleton().getDBConnector().checkInvention(this.getTypeId());
	}

	public boolean isBlueprint() {
		if (this.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint))
			return true;
		else
			return false;
	}

	public void setBasePrice( final double price ) {
		baseprice = price;
	}

	public void setName( final String name ) {
		this.name = name;
	}

	public void setTech( final String tech ) {
		this.tech = tech;
	}

	@Deprecated
	public void setTypeID( final int typeID ) {
		id = typeID;
	}

	public void setTypeId( final int typeId ) {
		id = typeId;
	}

	public void setVolume( final double volume ) {
		this.volume = volume;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("EveItem [");
		buffer.append("#").append(this.getItemId()).append(" - ").append(this.getName()).append(" ");
//		buffer.append(this.getGroupName()).append("/").append(this.getCategory()).append(" [").append(" ");
		buffer.append("IC:").append(industryGroup).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	private void classifyIndustryGroup() {
		if ((this.getGroupName().equalsIgnoreCase("Composite")) && (this.getCategory().equalsIgnoreCase("Material"))) {
			industryGroup = EIndustryGroup.REACTIONMATERIALS;
		}
		if (this.getCategory().equalsIgnoreCase("Asteroid")) {
			industryGroup = EIndustryGroup.OREMATERIALS;
		}
		if ((this.getGroupName().equalsIgnoreCase("Mining Crystal")) && (this.getCategory().equalsIgnoreCase("Charge"))) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if (this.getCategory().equalsIgnoreCase("Charge")) {
			industryGroup = EIndustryGroup.CHARGE;
		}
		if (this.getGroupName().equalsIgnoreCase("Tool")) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if (this.getCategory().equalsIgnoreCase("Commodity")) {
			industryGroup = EIndustryGroup.COMMODITY;
		}
		if (this.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
			industryGroup = EIndustryGroup.BLUEPRINT;
		}
		if (this.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
			industryGroup = EIndustryGroup.SKILL;
		}
		if (this.getGroupName().equalsIgnoreCase(ModelWideConstants.eveglobal.Mineral)) {
			industryGroup = EIndustryGroup.REFINEDMATERIAL;
		}
		if (this.getCategory().equalsIgnoreCase("Module")) {
			industryGroup = EIndustryGroup.COMPONENTS;
		}
		if (this.getCategory().equalsIgnoreCase("Drone")) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if (this.getCategory().equalsIgnoreCase("Planetary Commodities")) {
			industryGroup = EIndustryGroup.PLANETARYMATERIALS;
		}
		if (this.getGroupName().equalsIgnoreCase("Datacores")) {
			industryGroup = EIndustryGroup.DATACORES;
		}
		if (this.getGroupName().equalsIgnoreCase("Salvaged Materials")) {
			industryGroup = EIndustryGroup.SALVAGEDMATERIAL;
		}
		if (this.getCategory().equalsIgnoreCase("Ship")) {
			industryGroup = EIndustryGroup.HULL;
		}
	}

	/**
	 * Search on the market data provider for the market registers for this particular item. This will search on
	 * the market data cache and then if not found on the market data service that will call the corresponding
	 * parsers to extract the information.<br>
	 * This method can fail by some causes. First of them because there are no connection to the sources of by
	 * errors during the parsing of the information. In such cases I should be ready to get the price
	 * information from other sources like the default price information.
	 */
	private MarketDataSet getBuyerMarketData() {
		if (null == buyerData) {
			buyerData = accessGlobal().searchMarketData(this.getTypeID(), EMarketSide.BUYER);
			if (null == buyerData) {
				buyerData = new MarketDataSet(this.getItemId(), EMarketSide.BUYER);
			}
		}
		return buyerData;
	}

	/**
	 * Search on the market data provider for the market registers for this particular item. This will search on
	 * the market data cache and then if not found on the market data service that will call the corresponding
	 * parsers to extract the information.<br>
	 * This method can fail by some causes. First of them because there are no connection to the sources of by
	 * errors during the parsing of the information. In such cases I should be ready to get the price
	 * information from other sources like the default price information.
	 */
	private MarketDataSet getSellerMarketData() {
		if (null == sellerData) {
			sellerData = accessGlobal().searchMarketData(this.getTypeID(), EMarketSide.SELLER);
			if (null == sellerData) {
				sellerData = new MarketDataSet(this.getItemId(), EMarketSide.SELLER);
			}
		}
		return sellerData;
	}
}
// - UNUSED CODE ............................................................................................

//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download and parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.market.MarketDataEntry;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveItem extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -2548296399305221197L;
	private static EveItem defaultItem = new EveItem();
	private static final int DEFAULT_TYPE_ID = 34;

	public static EveItem getDefaultItem() {
		if (null == EveItem.defaultItem) {
			EveItem.defaultItem = GlobalDataManager.searchItem4Id(EveItem.DEFAULT_TYPE_ID);
			EveItem.defaultItem.buyerData = new MarketDataSet(EveItem.DEFAULT_TYPE_ID, EMarketSide.BUYER);
			EveItem.defaultItem.sellerData = new MarketDataSet(EveItem.DEFAULT_TYPE_ID, EMarketSide.SELLER);
		}
		return EveItem.defaultItem;
	}

	// - F I E L D - S E C T I O N ............................................................................
	private int id = 34;
	private String name = "<NAME>";
	private int groupid = -1;
	private int categoryid = -1;
	private ItemGroup group = null;
	private ItemCategory category = null;
	//	private String										getGroupName()			= "<getGroupName()>";
	//	private String										getCategory()			= "<getCategory()>";
	/**
	 * This is the default price set for an item at the SDE database. I should get other prices from market
	 * information blocks. This price will be updated from market data with the Jita lowest seller price when
	 * the market data gets updated.
	 */
	private double baseprice = -1.0;
	/**
	 * This is the highest buyers price when the market data is available or the <code>baseprice</code> is still
	 * not available. It is only used when the caller does not specify the particular market side for the
	 * requested price or any other search parameter.
	 */
	public double defaultprice = -1.0;
	private double volume = 0.0;
	private String tech = ModelWideConstants.eveglobal.TechI;
	//	private boolean										isBlueprint		= false;
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
			category = GlobalDataManager.searchItemCategory4Id(categoryid);
		}
		return category.getCategoryId();
	}

	public int getGroupId() {
		if (null == group) {
			group = GlobalDataManager.searchItemGroup4Id(groupid);
		}
		return group.getGroupId();
	}

	public String getCategory() {
		if (null == category) {
			category = GlobalDataManager.searchItemCategory4Id(categoryid);
		}
		return category.getCategoryName();
	}

	public String getCategoryName() {
		if (null == category) {
			category = GlobalDataManager.searchItemCategory4Id(categoryid);
		}
		return category.getCategoryName();
	}

	public String getGroupName() {
		if (null == group) {
			group = GlobalDataManager.searchItemGroup4Id(groupid);
		}
		return group.getGroupName();
	}

	public void setGroupId( final int groupid ) {
		this.groupid = groupid;
		group = GlobalDataManager.searchItemGroup4Id(groupid);
	}

	public void setCategoryId( final int categoryid ) {
		this.categoryid = categoryid;
		category = GlobalDataManager.searchItemCategory4Id(categoryid);
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

	@JsonIgnore
	public MarketDataEntry getLowestSellerPrice() {
		return this.getSellerMarketData().getBestMarket();
	}

	public String getName() {
		return name;
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
	@JsonIgnore
	public double getPrice() {
		if (defaultprice < 0.0) {
			defaultprice = this.getBuyerMarketData().getBestMarket().getPrice();
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

	public int getTypeID() {
		return id;
	}

	public double getVolume() {
		return volume;
	}

	public boolean hasInvention() {
		// REFACTOR This has to be reimplemented
		return false;
		//		return ModelAppConnector.getSingleton().getDBConnector().checkInvention(this.getTypeID());
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

	//	public void setBlueprint(final boolean state) {
	//		isBlueprint = state;
	//	}

	//	public void setCategory()(final String newcategory) {
	//		category = newcategory;
	//	}

	public void setName( final String name ) {
		this.name = name;
	}

	public void setTech( final String tech ) {
		this.tech = tech;
	}

	public void setTypeID( final int typeID ) {
		id = typeID;
	}

	public void setVolume( final double volume ) {
		this.volume = volume;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("EveItem [");
		buffer.append("#").append(this.getItemID()).append(" - ").append(this.getName()).append(" ");
		//		buffer.append(getgetGroupName()()).append("/").append(getgetCategory()()).append(" [").append(getPrice()).append(" ISK]")
		//		.append(" ");
		buffer.append(this.getGroupName()).append("/").append(this.getCategory()).append(" [").append(" ");
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
			buyerData = GlobalDataManager.searchMarketData(this.getTypeID(), EMarketSide.BUYER);
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
			sellerData = GlobalDataManager.searchMarketData(this.getTypeID(), EMarketSide.SELLER);
			if (null == sellerData) {
				sellerData = new MarketDataSet(this.getItemId(), EMarketSide.SELLER);
			}
		}
		return sellerData;
	}
}
// - UNUSED CODE ............................................................................................

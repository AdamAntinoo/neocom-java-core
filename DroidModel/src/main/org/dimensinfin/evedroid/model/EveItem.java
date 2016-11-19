//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.evedroid.model;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.EIndustryGroup;
import org.dimensinfin.evedroid.enums.EMarketSide;
import org.dimensinfin.evedroid.market.MarketDataEntry;
import org.dimensinfin.evedroid.market.MarketDataSet;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveItem extends AbstractComplexNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long					serialVersionUID	= -2548296399305221197L;

	// - F I E L D - S E C T I O N ............................................................................
	private int												id								= -1;
	private String										name							= "<NAME>";
	private String										groupname					= "<GROUPNAME>";
	private String										category					= "<CATEGORY>";
	/**
	 * This is the default price set for an item at the SDE database. I should get other prices from market
	 * information blocks. This price will be updated from market data with the Jita lowest seller price when
	 * the market data gets updated.
	 */
	private double										baseprice					= -1.0;
	/**
	 * This is the highest buyers price when the market data is available or the <code>baseprice</code> is still
	 * not available. It is only used when the caller does not specify the particular market side for the
	 * requested price or any other search parameter.
	 */
	private double										defaultprice			= -1.0;
	private double										volume						= 0.0;
	private String										tech							= ModelWideConstants.eveglobal.TechI;
	private boolean										isBlueprint				= false;

	// - A D D I T I O N A L   F I E L D S
	private transient EIndustryGroup	industryGroup			= EIndustryGroup.UNDEFINED;
	private transient MarketDataSet		buyerData					= null;
	private transient MarketDataSet		sellerData				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveItem() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public double getBaseprice() {
		return baseprice;
	}

	public String getCategory() {
		return category;
	}

	public String getGroupName() {
		return groupname;
	}

	public MarketDataEntry getHighestBuyerPrice() {
		return getBuyerMarketData().getBestMarket();
	}

	public EIndustryGroup getIndustryGroup() {
		if (industryGroup == EIndustryGroup.UNDEFINED) classifyIndustryGroup();
		return industryGroup;
	}

	public int getItemID() {
		return id;
	}

	public MarketDataEntry getLowestSellerPrice() {
		return getSellerMarketData().getBestMarket();
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
	 * 
	 * @param price
	 */
	public double getPrice() {
		if (defaultprice < 0.0) defaultprice = getBuyerMarketData().getBestMarket().getPrice();
		return defaultprice;
	}

	/**
	 * Some items have tech while others don't. Tech information has to be calculated for some items when I
	 * download them as assets or blueprints. Set it to a default value that by now I can consider valid.
	 * 
	 * @return
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
		return AppConnector.getDBConnector().checkInvention(getTypeID());
	}

	public boolean isBlueprint() {
		if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint))
			return true;
		else
			return false;
	}

	public void setBasePrice(final double price) {
		baseprice = price;
	}

	public void setBlueprint(final boolean state) {
		isBlueprint = state;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public void setGroupname(final String groupname) {
		this.groupname = groupname;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setTech(final String tech) {
		this.tech = tech;
	}

	public void setTypeID(final int typeID) {
		id = typeID;
	}

	public void setVolume(final double volume) {
		this.volume = volume;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("EveItem [");
		buffer.append("#").append(getItemID()).append(" - ").append(getName()).append(" ");
		buffer.append(getGroupName()).append("/").append(getCategory()).append(" [").append(getPrice()).append(" ISK]")
				.append(" ");
		buffer.append("IC:").append(industryGroup).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	private void classifyIndustryGroup() {
		if ((groupname.equalsIgnoreCase("Composite")) && (category.equalsIgnoreCase("Material"))) {
			industryGroup = EIndustryGroup.REACTIONMATERIALS;
		}
		if (category.equalsIgnoreCase("Asteroid")) {
			industryGroup = EIndustryGroup.OREMATERIALS;
		}
		if ((groupname.equalsIgnoreCase("Mining Crystal")) && (category.equalsIgnoreCase("Charge"))) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if (category.equalsIgnoreCase("Charge")) {
			industryGroup = EIndustryGroup.CHARGE;
		}
		if (groupname.equalsIgnoreCase("Tool")) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if (category.equalsIgnoreCase("Commodity")) {
			industryGroup = EIndustryGroup.COMMODITY;
		}
		if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
			industryGroup = EIndustryGroup.BLUEPRINT;
		}
		if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
			industryGroup = EIndustryGroup.SKILL;
		}
		if (groupname.equalsIgnoreCase(ModelWideConstants.eveglobal.Mineral)) {
			industryGroup = EIndustryGroup.REFINEDMATERIAL;
		}
		if (category.equalsIgnoreCase("Module")) {
			industryGroup = EIndustryGroup.COMPONENTS;
		}
		if (category.equalsIgnoreCase("Drone")) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if (category.equalsIgnoreCase("Planetary Commodities")) {
			industryGroup = EIndustryGroup.PLANETARYMATERIALS;
		}
		if (groupname.equalsIgnoreCase("Datacores")) {
			industryGroup = EIndustryGroup.DATACORES;
		}
		if (groupname.equalsIgnoreCase("Salvaged Materials")) industryGroup = EIndustryGroup.SALVAGEDMATERIAL;
		if (category.equalsIgnoreCase("Ship")) {
			industryGroup = EIndustryGroup.HULL;
		}
	}

	private MarketDataSet getBuyerMarketData() {
		if (null == buyerData) buyerData = AppConnector.getDBConnector().searchMarketData(getTypeID(), EMarketSide.BUYER);
		return buyerData;
	}

	private MarketDataSet getSellerMarketData() {
		if (null == sellerData)
			sellerData = AppConnector.getDBConnector().searchMarketData(getTypeID(), EMarketSide.SELLER);
		return sellerData;
	}
}

// - UNUSED CODE ............................................................................................

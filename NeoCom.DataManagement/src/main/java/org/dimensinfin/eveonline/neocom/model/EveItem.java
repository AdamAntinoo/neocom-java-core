package org.dimensinfin.eveonline.neocom.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.domain.IItemFacet;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOkDogmaAttributes;
import org.dimensinfin.eveonline.neocom.market.MarketDataEntry;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class EveItem extends NeoComNode implements IItemFacet {
	public enum ItemTechnology {
		Tech_1("Tech I"), Tech_2("Tech II"), Tech_3("Tech III");

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

		// - I N V E R S E   L O O K U P   T A B L E
		private static final Map<String, ItemTechnology> lookup = new HashMap<>();

		static {
			for (ItemTechnology env : ItemTechnology.values()) {
				lookup.put(env.getName(), env);
			}
		}
	}

	private static final long serialVersionUID = -2548296399305221197L;
	private static ESIDataAdapter esiDataAdapter;

	public static void injectEsiDataAdapter( final ESIDataAdapter newEsiDataAdapter ) {
		esiDataAdapter = newEsiDataAdapter;
	}

	protected int id = -1;
	private transient GetUniverseTypesTypeIdOk item;
	private transient GetUniverseGroupsGroupIdOk group;
	private transient GetUniverseCategoriesCategoryIdOk category;
	//	/**
	//	 * This is the default price set for an item at the SDE database. This price should not be changed and there should be
	//	 * methods to get any other price set from the market data.
	//	 */
	//	private double baseprice = -1.0;
	/**
	 * This is the ESI returned price from the global market data service. This is the price shown on the game UI for item values
	 * and it is not tied to any specific market place.
	 */
	private double price = -1.0;
	private String tech = ModelWideConstants.eveglobal.TechI;

	// - A D D I T I O N A L   F I E L D S
	private transient EIndustryGroup industryGroup = EIndustryGroup.UNDEFINED;
	/**
	 * This represents the market data for the BUY market orders present at different selected systems. This element and the
	 * next are lazy evaluated as futures and should enqueue market requests for background threads.
	 */
	private transient Future<MarketDataSet> futureBuyerData;
	/**
	 * The same but for SELLER orders present at the market.
	 */
	private transient Future<MarketDataSet> futureSellerData;

	// - C O N S T R U C T O R S
	@Deprecated
	public EveItem() {
		super();
	}

	@Deprecated
	public EveItem( final GetUniverseTypesTypeIdOk sdeItem ) {
		super();
		this.item = sdeItem;
	}

	public EveItem( final int typeId ) {
		this.id = typeId;
		this.loadup();
	}

	private void loadup() {
		try {
			this.item = esiDataAdapter.searchEsiItem4Id(this.id);
			Objects.requireNonNull(this.item);
			this.group = esiDataAdapter.searchItemGroup4Id(this.item.getGroupId());
			Objects.requireNonNull(this.group);
			this.category = esiDataAdapter.searchItemCategory4Id(this.group.getCategoryId());
			Objects.requireNonNull(this.category);
		} catch (RuntimeException rtex) {
			logger.info("RT [Eveitem.loadup]> Error downloading the EveItem data. not able to complete the instantiation.");
			logger.info("RT [Eveitem.loadup]> Message: {}", rtex.getMessage());
		}
	}

	// - G E T T E R S   &   S E T T E R S
	@Deprecated
	public int getItemId() {
		return id;
	}

	public int getTypeId() {
		return id;
	}

	public String getName() {
		if (null == this.item) this.loadup();
		return this.item.getName();
	}

	/**
	 * Return the ESI api market set price for this item. Sometimes there is another price markets as the average price that I am
	 * not using now.
	 */
	public double getPrice() {
		if (price < 0.0) {
			price = esiDataAdapter.searchSDEMarketPrice(this.getTypeId());
		}
		return price;
	}

	public int getGroupId() {
		if (null == this.group) this.loadup();
		return this.group.getGroupId();
	}

	public int getCategoryId() {
		if (null == this.category) this.loadup();
		return this.category.getCategoryId();
	}

	/**
	 * Some items have tech while others don't. Tech information has to be calculated for some items when I
	 * download them as assets or blueprints. Set it to a default value that by now I can consider valid.
	 */
	public String getTech() {
		return tech;
	}

	public double getVolume() {
		if (null == this.item) this.loadup();
		return this.item.getVolume();
	}

	public boolean isBlueprint() {
		if (this.getCategoryName().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint))
			return true;
		else
			return false;
	}

	public EIndustryGroup getIndustryGroup() {
		if (industryGroup == EIndustryGroup.UNDEFINED) {
			this.classifyIndustryGroup();
		}
		return industryGroup;
	}

	public Float getCapacity() {
		return this.item.getCapacity();
	}

	public List<GetUniverseTypesTypeIdOkDogmaAttributes> getDogmaAttributes() {
		return this.item.getDogmaAttributes();
	}

	/**
	 * This is the key method used when instantiating an EveItem to set the eve item identifier for the esi underlying object. After setting this
	 * value we can post the download or caches access to the delegated esi data.
	 * Also we later will used this entry point to locate the extended market information to be used when calculating prices. This price
	 * data will be located inside a new delegete instance.
	 *
	 * @param typeId the eve game unique type identifier.
	 */
	public EveItem setTypeId( final int typeId ) {
		this.id = typeId;
		this.loadup();
		futureBuyerData = this.retrieveMarketData(getTypeId(), EMarketSide.BUYER);
		futureSellerData = this.retrieveMarketData(getTypeId(), EMarketSide.SELLER);
		return this;
	}

	public void setPrice( final double price ) {
		this.price = price;
	}

	public void setTech( final String tech ) {
		this.tech = tech;
	}

	// - V I R T U A L   A C C E S S O R S
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

	public EveItem setHullGroup( final String dummy ) {
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
		if (null == this.category) this.loadup();
		return this.category.getName();
	}

	public EveItem setCategoryName( final String dummy ) {
		return this;
	}

	public String getGroupName() {
		if (null == this.group) this.loadup();
		return this.group.getName();
	}

	public EveItem setGroupName( final String dummy ) {
		return this;
	}

	// - I I T E M F A C E T
	public String getURLForItem() {
		return "http://image.eveonline.com/Type/" + this.getTypeId() + "_64.png";
	}

	// - C O R E
//	@Override
//	public String toString() {
//		final StringBuffer buffer = new StringBuffer("EveItem [");
//		buffer.append("#").append(this.getItemId()).append(" - ").append(this.getName()).append(" ");
//		buffer.append(this.getGroupName()).append("/").append(this.getCategoryName()).append(" [").append(" ");
//		buffer.append("IC:").append(industryGroup).append(" ");
//		buffer.append("]");
//		return buffer.toString();
//	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("id", id)
				.append("item", item)
				.append("group", group)
				.append("category", category)
				.append("price", price)
				.append("tech", tech)
				.append("industryGroup", industryGroup)
				.toString();
	}

	// - P R I V A T E   S E C T I O N
	protected void classifyIndustryGroup() {
		if ((this.getGroupName().equalsIgnoreCase("Composite")) && (this.getCategoryName().equalsIgnoreCase("Material"))) {
			industryGroup = EIndustryGroup.REACTIONMATERIALS;
		}
		if (this.getCategoryName().equalsIgnoreCase("Asteroid")) {
			industryGroup = EIndustryGroup.OREMATERIALS;
		}
		if ((this.getGroupName().equalsIgnoreCase("Mining Crystal")) && (this.getCategoryName().equalsIgnoreCase("Charge"))) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if (this.getCategoryName().equalsIgnoreCase("Charge")) {
			industryGroup = EIndustryGroup.CHARGE;
		}
		if (this.getGroupName().equalsIgnoreCase("Tool")) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if (this.getCategoryName().equalsIgnoreCase("Commodity")) {
			industryGroup = EIndustryGroup.COMMODITY;
		}
		if (this.getCategoryName().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
			industryGroup = EIndustryGroup.BLUEPRINT;
		}
		if (this.getCategoryName().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
			industryGroup = EIndustryGroup.SKILL;
		}
		if (this.getGroupName().equalsIgnoreCase(ModelWideConstants.eveglobal.Mineral)) {
			industryGroup = EIndustryGroup.REFINEDMATERIAL;
		}
		if (this.getCategoryName().equalsIgnoreCase("Module")) {
			industryGroup = EIndustryGroup.COMPONENTS;
		}
		if (this.getCategoryName().equalsIgnoreCase("Drone")) {
			industryGroup = EIndustryGroup.ITEMS;
		}
		if (this.getCategoryName().equalsIgnoreCase("Planetary Commodities")) {
			industryGroup = EIndustryGroup.PLANETARYMATERIALS;
		}
		if (this.getGroupName().equalsIgnoreCase("Datacores")) {
			industryGroup = EIndustryGroup.DATACORES;
		}
		if (this.getGroupName().equalsIgnoreCase("Salvaged Materials")) {
			industryGroup = EIndustryGroup.SALVAGEDMATERIAL;
		}
		if (this.getCategoryName().equalsIgnoreCase("Ship")) {
			industryGroup = EIndustryGroup.HULL;
		}
	}

	/**
	 * Submits a <code>Callable</code> request to the background threads to retrieve the data into the <code>Future</code>. In
	 * the case the market data is accessed and the Future was not completed the thread should wait until the market data access
	 * completes. Most of the calls will execute fast because the data being cached continuously py the scheduled submitted jobs.
	 *
	 * @param itemId the items id to search market data.
	 * @param side   if we should search buy orders or sell orders.
	 * @return a <code>Future</code> with the whole market data values.
	 */
	private Future<MarketDataSet> retrieveMarketData( final int itemId, final EMarketSide side ) {
		return esiDataAdapter.searchMarketData(itemId, side);
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
		if (null == futureBuyerData) {
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
		if (null == futureBuyerData) {
			futureBuyerData = retrieveMarketData(getTypeId(), EMarketSide.SELLER);
		}
		return futureBuyerData.get();
	}
}

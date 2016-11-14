//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.industry.EJobClasses;
import org.dimensinfin.evedroid.industry.IJobProcess;
import org.dimensinfin.evedroid.industry.JobManager;
import org.dimensinfin.evedroid.model.Blueprint;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.render.ItemHeaderRender;

// - CLASS IMPLEMENTATION ...................................................................................
public class ItemHeader4IndustryPart extends MarketDataPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -8424319135676691254L;
	// - F I E L D - S E C T I O N ............................................................................
	//	private AppModelStore	_store					= null;
	private int								bpid							= -1;
	private boolean						manufacturable		= false;
	private IJobProcess				process						= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * Check on creation the type of item. Manufacturable items may get their Manufacture Processor from the
	 * Blueprint. Manufacturable is a Part property.<br>
	 * Then manufacturable parts may use a Processor that get created at this point.
	 * 
	 * @param item
	 */
	public ItemHeader4IndustryPart(final AbstractGEFNode node) {
		super(node);
		bpid = AppConnector.getDBConnector().searchBlueprint4Module(getCastedModel().getTypeID());
		// TODO Check this validation. There are more items that are manufacturable and also item made with reactions.
		if (-1 != bpid) manufacturable = true;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getGroup() {
		return getCastedModel().getGroupName();
	}
	public String getCategory() {
		return  getCastedModel().getCategory();
	}

	public String getName() {
		return getCastedModel().getName();
	}

	/**
	 * Shows the icon for manufacture and the manufacture calculated cost for this item if can be calculated.
	 * Not all item types can have this value so the display has to reflect that. If the cost of manufacture is
	 * less that the best sell price then the price is shown in green and the sell multiplier is added to the
	 * price. If the manufacture cost is greater than the sell price it is shown in red. If the cost is less
	 * than the sell price but not greater than the 110% of that price then the price is shown in white.
	 * 
	 * @return
	 */
	public String get_manufactureCost() {
		double cost = getJobProcess().getJobCost();
		//		double sellprice = getSellData().getPrice();
		// Start with the white color
		String secColor = "#FFFFFF";
		if ((cost * 1.1) < getSellerPrice()) secColor = "#6CC417";
		if (cost >= getSellerPrice()) secColor = "#F62217";
		StringBuffer htmlPrice = new StringBuffer();
		htmlPrice.append("<font color='").append(secColor).append("'>").append(generatePriceString(cost, true, true))
				.append("</font>");
		return htmlPrice.toString();
	}

	public int getProfitIndex() {
		return getJobProcess().getProfitIndex();
	}

	public double getMultiplier() {
		return getJobProcess().getMultiplier();
	}

	public EveItem getCastedModel() {
		return (EveItem) getModel();
	}

	public double getManufactureCost() {
		return getJobProcess().getJobCost();
	}

	public long getModelID() {
		return getCastedModel().getItemID();
	}

	public boolean isManufacturable() {
		return manufacturable;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("ItemHeader4IndustryPart [");
		buffer.append(getCastedModel()).append(" ");
		buffer.append("[").append(bpid).append("]").append(" ");
		buffer.append(" ]");
		return buffer.toString();
	}

	//	public EveAbstractPart setStore(final AppModelStore store) {
	//		_store = store;
	//		return this;
	//	}

	protected void initialize() {
		item = getCastedModel();
	}

	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		return new ItemHeaderRender(this, _activity);
	}

	private IJobProcess getJobProcess() {
		if (null == process)
			process = JobManager.generateJobProcess(EVEDroidApp.getAppStore().getPilot(), new Blueprint(bpid),
					EJobClasses.MANUFACTURE);
		return process;
	}
}

// - UNUSED CODE ............................................................................................

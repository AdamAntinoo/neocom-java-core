//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.datasource;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.CEventModel.ECoreModelEvents;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.NeoComApp;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.enums.EVARIANT;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.MarketOrderAnalyticalGroup;
import org.dimensinfin.eveonline.neocom.model.NeoComMarketOrder;
import org.dimensinfin.eveonline.neocom.model.Separator;
import org.dimensinfin.eveonline.neocom.part.GroupPart;
import org.dimensinfin.eveonline.neocom.part.MarketOrderAnalyticalGroupPart;
import org.dimensinfin.eveonline.neocom.part.MarketOrderPart;
import org.dimensinfin.eveonline.neocom.part.ResourcePart;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This datasource will generate the list of market orders. They will be grouped into headers by their class
 * like the scheduled buys or sells, pending or completed. After the class grouping they are ordered by
 * location so all operations related to a location are set together. This is not used for completed that
 * instead are ordered by date from most recent to most old.
 * 
 * @author Adam Antinoo
 */
public class MarketOrdersDataSource extends AbstractNewDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long											serialVersionUID	= -8071069909661621102L;

	// - F I E L D - S E C T I O N ............................................................................
	private ArrayList<MarketOrderAnalyticalGroup>	analyticalGroups	= new ArrayList<MarketOrderAnalyticalGroup>();
	private final ArrayList<AbstractGEFNode>			modelList					= new ArrayList<AbstractGEFNode>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketOrdersDataSource(final AppModelStore store) {
		super(store);
	}

	public RootNode collaborate2Model() {
		// TODO Auto-generated method stub
		return null;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The hierarchy contains two levels of elements. The first level are the market hubs where the user has to
	 * launch the market actions. The second level are the actions pending ordered by registration date.<br>
	 * During the processing of the orders there is an aggregation of the scheduled buys so all the schedule4d
	 * orders are presented as a single order with all the quantities summed up.
	 */
	@Override
	public void createContentHierarchy() {
		Log.i("EVEI", ">> MarketOrdersDataSource.createHierarchy");
		super.createContentHierarchy();

		// Get the full model from the character. The model already arrives with all the hierarchy developed.
		analyticalGroups = _store.getPilot().accessMarketOrders();
		// Get the modules ready for sell and add them to the new group.
		final MarketOrderAnalyticalGroup scheduledSellGroup = _store.getPilot().accessModules4Sell();
		analyticalGroups.add(scheduledSellGroup);
		Log.i("EVEI", "<< MarketOrdersDataSource.createHierarchy");
	}

	@Override
	public ArrayList<AbstractAndroidPart> getBodyParts() {
		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		_adapterData = result;
		return result;
	}

	/**
	 * Generate the part list for the body. This list maybe or not generated before this request. Timing will be
	 * checked when the implementation is running.
	 * 
	 * @param panelMarketordersbody
	 * @return
	 */
	@Override
	public ArrayList<AbstractAndroidPart> getBodyPartsHierarchy(final int panelMarketordersbody) {
		final ArrayList<AbstractAndroidPart> hierarchy = new ArrayList<AbstractAndroidPart>();
		// Order the groups on the defined weight order.
		Collections.sort(analyticalGroups, NeoComApp.createComparator(AppWideConstants.comparators.COMPARATOR_WEIGHT));
		// Add all the collaborations to the output list
		modelList.clear();
		for (final MarketOrderAnalyticalGroup group : analyticalGroups)
			modelList.addAll(group.collaborate2Model(EVARIANT.DEFAULT_VARIANT.name()));

		// Create the hierarchy from the model list.
		for (final IGEFNode node : modelList) {
			if (node instanceof MarketOrderAnalyticalGroup) {
				MarketOrderAnalyticalGroupPart mopart = new MarketOrderAnalyticalGroupPart((MarketOrderAnalyticalGroup) node);
				// Depending on the coded name of the group we can use different renders.
				//				if(node.getTitleresourceId()==R.string.Finished)
				//					hierarchy.add(mopart
				//							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPMARKETANALYTICAL));
				//				else	
				hierarchy
						.add((AbstractAndroidPart) mopart.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPMARKETANALYTICAL));
			}
			if (node instanceof NeoComMarketOrder)
				hierarchy.add((AbstractAndroidPart) new MarketOrderPart((NeoComMarketOrder) node)
						.setRenderMode(AppWideConstants.rendermodes.RENDER_MARKETORDER));
			if (node instanceof Resource) hierarchy.add((AbstractAndroidPart) new ResourcePart((Resource) node)
					.setRenderMode(AppWideConstants.rendermodes.RENDER_MARKETORDERSCHEDULEDSELL));
			if (node instanceof Separator) hierarchy.add((AbstractAndroidPart) new GroupPart((Separator) node)
					.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPMARKETSIDE));
		}
		_adapterData = hierarchy;
		return hierarchy;
	}

	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<AbstractAndroidPart> getHeaderPartsHierarchy(final int panelMarketordersbody) {
		return new ArrayList<AbstractAndroidPart>();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_NEEDSREFRESH))
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		if (event.getPropertyName().equalsIgnoreCase(ECoreModelEvents.EVENT_CHILD_REMOVEDPROP.name()))
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
	}

}
// - UNUSED CODE ............................................................................................

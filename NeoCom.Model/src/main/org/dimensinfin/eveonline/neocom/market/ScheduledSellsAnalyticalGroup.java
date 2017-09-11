//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.
package org.dimensinfin.eveonline.neocom.market;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.eveonline.neocom.core.ComparatorFactory;
import org.dimensinfin.eveonline.neocom.enums.EComparatorField;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.Separator;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * Specialized group to deal with the special constraints that affect the scheduled sells, this is the modules
 * that are ready to be sold at the market.
 * 
 * @author Adam Antinoo
 */
public class ScheduledSellsAnalyticalGroup extends MarketOrderAnalyticalGroup {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID	= 7060625181454041097L;

	// - F I E L D - S E C T I O N ............................................................................
	private final HashMap<String, Separator>	regions						= new HashMap<String, Separator>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	public ScheduledSellsAnalyticalGroup(final int newWeight, final String newTitle) {
		super(newWeight, newTitle);
	}

	/**
	 * Adds a new element to the list of aggregated items and increments and recalculates the analytical data.
	 * 
	 * @param newOrder
	 *          the order to be added to the group.
	 */
	public void addChild(final Resource newOrder) {
		super.addChild(newOrder);

		// Recalculate analytical data from the order api methods.
		budget += newOrder.getItem().getHighestBuyerPrice().getPrice() * newOrder.getQuantity();
		volume += newOrder.getItem().getVolume() * newOrder.getQuantity();
		quantity += newOrder.getQuantity();
		Vector<AbstractGEFNode> hit = locations.get(newOrder.getItem().getHighestBuyerPrice().getLocation().getID());
		if (null == hit) {
			hit = new Vector<AbstractGEFNode>();
			hit.add(newOrder);
			locations.put(newOrder.getItem().getHighestBuyerPrice().getLocation().getID(), hit);
		} else {
			hit.add(newOrder);
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public ArrayList<AbstractGEFNode> collaborate2Model() {
		final ArrayList<AbstractGEFNode> results = new ArrayList<AbstractGEFNode>();
		results.add(this);
		for (final IGEFNode node : this.getChildren())
			if (node instanceof Resource) {
				final Resource order = (Resource) node;
				// Get the type and the location to classify.
				final EveLocation location = order.getItem().getHighestBuyerPrice().getLocation();
				final int type = order.getTypeID();
				this.classifyOrder(order, type, location);
			}
		// Now get the regions and move the parts to the result in the right order.
		final ArrayList<AbstractGEFNode> regionNames = new ArrayList<AbstractGEFNode>(regions.values());
		Collections.sort(regionNames, ComparatorFactory.createComparator(EComparatorField.NAME));
		for (final AbstractGEFNode region : regionNames) {
			results.add(region);
			// Now add the depending item in the order but with their own rules.
			Vector<AbstractPropertyChanger> orders = new Vector<AbstractPropertyChanger>();
			Vector<IGEFNode> v = region.getChildren();
			for (IGEFNode node : v) {
				orders.add((AbstractPropertyChanger) node);
			}
			Collections.sort(orders, ComparatorFactory.createComparator(EComparatorField.NAME));
			for (final AbstractPropertyChanger node : orders)
				if (node instanceof Resource) {
					results.addAll(((Resource) node).collaborate2Model());
				}
		}
		return results;
	}

	/**
	 * Given an order and a set of parameters put it into the corresponding place on the Part hierarchy.
	 * 
	 * @param order
	 * @param type
	 * @param location
	 */
	private void classifyOrder(final Resource order, final int type, final EveLocation location) {
		final String locRegion = location.getRegion();
		Separator hitRegion = regions.get(locRegion);
		if (null == hitRegion) {
			hitRegion = new Separator(locRegion);
			regions.put(locRegion, hitRegion);
		}
		hitRegion.addChild(order);
	}
}
//- UNUSED CODE ............................................................................................

//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.
package org.dimensinfin.evedroid.model;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import org.dimensinfin.core.interfaces.INeoComNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.ModelWideConstants;

import com.beimin.eveapi.model.shared.MarketOrder;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * The grouping classes allow to aggregate multiple instances of the same kind (at least this is the goal)
 * into a set where some other meta information and analytical data can be calculated and extracted. The best
 * example are market orders where the group will calculate values like the number of orders, the number of
 * elements, the budget, the median, profit, etc.
 * 
 * Each type of grouping and analytical set of values will be implemented with a different
 * <code>Aggregator</code> class.
 * 
 * @author Adam Antinoo
 */
public class MarketOrderAnalyticalGroup extends AnalyticalGroup implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long																serialVersionUID	= -6476601202625023850L;

	// - F I E L D - S E C T I O N ............................................................................
	// TODO Clean up this because the elements accepted by a group should be of the same class. I have to convert resources into orderes
	protected int																						quantity					= 0;
	protected double																				budget						= 0.0;
	protected double																				volume						= 0.0;
	protected final HashMap<Long, Vector<AbstractGEFNode>>	locations					= new HashMap<Long, Vector<AbstractGEFNode>>();
	protected boolean																				renderIfEmpty			= false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketOrderAnalyticalGroup() {
	}

	public MarketOrderAnalyticalGroup(final int newWeight, final String newTitle) {
		weight = newWeight;
		title = newTitle;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Adds a new element to the list of aggregated items and increments and recalculates the analytical data.
	 * 
	 * @param newOrder
	 *          the order to be added to the group.
	 */
	public void addChild(final NeoComMarketOrder newOrder) {
		super.addChild(newOrder);

		// Recalculate analytical data from the order api methods.
		budget += newOrder.getPrice() * newOrder.getQuantity();
		volume += newOrder.getItem().getVolume() * newOrder.getQuantity();
		quantity += newOrder.getQuantity();
		Vector<AbstractGEFNode> hit = locations.get(newOrder.getOrderLocationID());
		if (null == hit) {
			hit = new Vector<AbstractGEFNode>();
			hit.add(newOrder);
			locations.put(newOrder.getOrderLocationID(), hit);
		} else
			hit.add(newOrder);
	}

	/**
	 * Generates the list of model elements in the right order depending on the environment and the data. This
	 * way to generate the content leaves the knowledge in the right place of the model and not on the
	 * implementing part. For a group the results are the children parts but ordered in a predeterminate way.
	 * There are exceptions to that ordering or other grouping parameters that may change the default hierarchy
	 * order or structure to an specific one for a single instance.
	 * 
	 * @return
	 */
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		// If the groups has no elements then check the flag to determinate if it is shown or not.
		if (this.isRenderWhenEmpty()) results.add(this);

		// Add the children that are inside these group in the right date order. Aggregate items of the same type.
		Vector<AbstractPropertyChanger> orders = this.aggregate(this.getChildren());
		Collections.sort(orders, AppConnector.createComparator(ModelWideConstants.comparators.COMPARATOR_NAME));
		for (final AbstractPropertyChanger node : orders)
			if (node instanceof NeoComMarketOrder) results.addAll(((NeoComMarketOrder) node).collaborate2Model("DEFAULT"));
		return results;
	}

	//	@Override
	//	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
	//		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
	//		results.addAll((Collection<? extends AbstractComplexNode>) getChildren());
	//		return results;
	//	}

	public double getBudget() {
		return budget;
	}

	public Vector<IGEFNode> getOrders() {
		return this.getChildren();
	}

	public int getQuantity() {
		return quantity;
	}

	public double getVolume() {
		return volume;
	}

	/**
	 * Checks of the groups should be rendered depending of some configuration values. By default all groups are
	 * rendered only when they have contents. If they are ampty they may not be rendered. Only they can be
	 * forced to be rendered if the right flag value is set to true.
	 * 
	 * @return
	 */
	@Override
	public boolean isRenderWhenEmpty() {
		// Is not empty the render.
		if (this.getChildren().size() > 0) return true;
		return renderIfEmpty;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("MarketOrderAnalyticalGroup [");
		buffer.append(this.getWeight()).append(" ");
		buffer.append(this.getTitle()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	private Vector<AbstractPropertyChanger> aggregate(final Vector<IGEFNode> children) {
		final HashMap<Integer, NeoComMarketOrder> datamap = new HashMap<Integer, NeoComMarketOrder>();
		for (final IGEFNode node : children)
			if (node instanceof MarketOrder) {
				final NeoComMarketOrder order = (NeoComMarketOrder) node;
				final NeoComMarketOrder hit = datamap.get(new Integer(order.getItemTypeID()));
				if (null == hit)
					datamap.put(new Integer(order.getItemTypeID()), order);
				else
					hit.setVolEntered(hit.getVolEntered() + order.getVolEntered());
			}
		// Unpack the data map into a new list with the quantities aggregated
		return new Vector<AbstractPropertyChanger>(datamap.values());
	}

}
//- UNUSED CODE ............................................................................................

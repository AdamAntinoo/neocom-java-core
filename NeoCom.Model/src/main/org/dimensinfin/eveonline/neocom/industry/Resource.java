//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.industry;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * The class defines the basic stack of some type of item. It will allow the aggregation of more of the same
 * type items and differentiated from the asset in that it has no specified Location not owner. Includes the
 * methods for resource calculation on dependence of the character skills but that information has to be given
 * from outside if you desire to use it on the calculations.<br>
 * By default we consider a 5 on the Industry skills and that T2 blueprints have a ME=7 ME and TE=14 and that
 * other T1 blueprints have a 10 of ME and a 20 on TE.<br>
 * We also consider that T2 BPC have 10 runs while T1 BPC have 300.<br>
 * In short the blueprints will have the correct ME/TE/RUNS available so this values will be set from the
 * blueprint from where the resource was extracted.
 * 
 * @author Adam Antinoo
 */
public class Resource extends AbstractComplexNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID					= 921961484632479376L;
	private static Logger			logger										= Logger.getLogger("Resource");
	private static final int	INDUSTRY_SKILL						= 5;
	private static final int	PRODUCTIONEFFICENCY_SKILL	= 5;
	private static final int	DEFAULT_T1ME							= 10;
	private static final int	DEFAULT_T2ME							= 7;
	private static final int	DEFAULT_T1TE							= 20;
	private static final int	DEFAULT_T2TE							= 14;

	// - F I E L D - S E C T I O N ............................................................................
	public EveItem						item											= new EveItem();
	private int								resourceID								= -1;
	public int								baseQty										= 0;
	public int								stackSize									= 1;
	private double						damage										= 1.0;
	private DateTime					registrationDate					= new DateTime(DateTimeZone.UTC);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Resource(final int typeID, final int newQty) {
		this.resourceID = typeID;
		this.item = AppConnector.getDBConnector().searchItembyID(typeID);
		this.baseQty = newQty;
	}
/**
 * Builds a new resource of quantity 1.
 * @param hullTypeId
 */
	public Resource(int typeID) {
		this.resourceID = typeID;
		this.item = AppConnector.getDBConnector().searchItembyID(typeID);
		this.baseQty = 1;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void add(final int count) {
		this.baseQty += count;
	}

	/**
	 * Adds the quantities of two resources of the same type. On this moment the original resource losses the
	 * stack values and the equivalent quantity is calculated before adding the new quantity calculated exactly
	 * on the same way. The final result is the total quantity but with a stack size of one.
	 * 
	 * @param newResource
	 */
	public void addition(final Resource newResource) {
		int newqty = getBaseQuantity() * getStackSize();
		newqty += newResource.getBaseQuantity() * newResource.getStackSize();
		this.baseQty = newqty;
		this.stackSize = 1;
		//	wasteQty = 0;
	}

	/**
	 * Generate the model elements that want to be represented at the UI.
	 * 
	 * @return
	 */
	public ArrayList<AbstractGEFNode> collaborate2Model() {
		final ArrayList<AbstractGEFNode> result = new ArrayList<AbstractGEFNode>();
		result.add(this);
		return result;
	}

	public int getBaseQuantity() {
		return this.baseQty;
	}

	public String getCategory() {
		return this.item.getCategory();
	}

	public double getDamage() {
		return this.damage;
	}

	public String getGroupName() {
		return this.item.getGroupName();
	}

	public EveItem getItem() {
		return this.item;
	}

	public String getName() {
		return this.item.getName();
	}

	/**
	 * Apply the manufacture formulas to get the correct value of the quantity for this user.
	 * 
	 * @return my manufacture quantity
	 */
	public int getQuantity() {
		return getBaseQuantity() * this.stackSize;
	}

	public DateTime getRegistrationDate() {
		if (null == this.registrationDate) this.registrationDate = new DateTime(DateTimeZone.UTC);
		return this.registrationDate;
	}

	public int getStackSize() {
		return this.stackSize;
	}

	public int getTypeID() {
		return this.item.getItemID();
	}

	public void setAdaptiveStackSize(final int size) {
		setStackSize(size);
		if (this.item.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
			if (this.item.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
				final double stack = Math.ceil(size / 10d);
				setStackSize(Math.max(new Double(stack).intValue(), 1));
			}
			if (this.item.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
				final double stack = Math.ceil(size / 300d);
				setStackSize(Math.max(new Double(stack).intValue(), 1));
			}
		}
		if (this.item.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) setStackSize(1);
	}

	public void setDamage(final double damage) {
		this.damage = damage;
	}

	public void setQuantity(final int newQuantity) {
		this.baseQty = newQuantity;
	}

	public void setRegistrationDate(final DateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	public void setStackSize(final int stackSize) {
		this.stackSize = stackSize;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("Resource [");
		buffer.append("[").append(getCategory()).append("] ");
		buffer.append(this.item.getName()).append(" x").append(this.baseQty).append(" ");
		buffer.append("stack: ").append(this.stackSize).append(" ");
		buffer.append("total: ").append(getQuantity()).append(" ");
		buffer.append("#").append(getTypeID()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................

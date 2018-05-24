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
package org.dimensinfin.eveonline.neocom.industry;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

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
public class Resource extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 921961484632479376L;

	// - F I E L D - S E C T I O N ............................................................................
	public int typeId = -1;
	protected int baseQty = 0;
	protected int stackSize = 1;
	protected double damage = 1.0;

	private transient EveItem item = new EveItem();
	private DateTime registrationDate = new DateTime(DateTimeZone.UTC);

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	/**
	 * Builds a new resource of quantity 1.
	 *
	 * @param typeId
	 */
	public Resource( final int typeId ) {
		super();
		this.typeId = typeId;
		item = accessGlobal().searchItem4Id(typeId);
		this.baseQty = 0;
		jsonClass = "Resource";
	}

	public Resource( final int typeId, final int newQty ) {
		this(typeId);
		this.baseQty = newQty;
	}
	public Resource( final int typeId, final int newQty , final int stackSize) {
		this(typeId,newQty);
		this.stackSize = stackSize;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void add( final int count ) {
		baseQty += count;
	}

	/**
	 * Adds the quantities of two resources of the same type. On this moment the original resource losses the
	 * stack values and the equivalent quantity is calculated before adding the new quantity calculated exactly
	 * on the same way. The final result is the total quantity but with a stack size of one.
	 *
	 * @param newResource
	 */
	public void addition( final Resource newResource ) {
		int newqty = this.getBaseQuantity() * this.getStackSize();
		newqty += newResource.getBaseQuantity() * newResource.getStackSize();
		baseQty = newqty;
		stackSize = 1;
	}

	/**
	 * Generate the model elements that want to be represented at the UI.
	 *
	 * @return
	 */
	public List<ICollaboration> collaborate2Model() {
		final ArrayList<ICollaboration> result = new ArrayList<ICollaboration>();
		result.add(this);
		return result;
	}

	public int getBaseQuantity() {
		return baseQty;
	}

	public String getCategory() {
		return getItem().getCategoryName();
	}

	public double getDamage() {
		return damage;
	}

	public String getGroupName() {
		return getItem().getGroupName();
	}

	public EveItem getItem() {
		if(null==item)item=accessGlobal().searchItem4Id(typeId);
		return item;
	}

	public String getName() {
		return getItem().getName();
	}

	/**
	 * Apply the manufacture formulas to get the correct value of the quantity for this user.
	 *
	 * @return my manufacture quantity
	 */
	public int getQuantity() {
		return this.getBaseQuantity() * stackSize;
	}

	public DateTime getRegistrationDate() {
		if (null == registrationDate) {
			registrationDate = new DateTime(DateTimeZone.UTC);
		}
		return registrationDate;
	}

	public int getStackSize() {
		return stackSize;
	}

	public int getTypeId() {
		return getItem().getTypeId();
	}

	public void setAdaptiveStackSize( final int size ) {
		this.setStackSize(size);
		getItem();
		if (item.getCategoryName().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
			if (item.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
				final double stack = Math.ceil(size / 10d);
				this.setStackSize(Math.max(new Double(stack).intValue(), 1));
			}
			if (item.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
				final double stack = Math.ceil(size / 300d);
				this.setStackSize(Math.max(new Double(stack).intValue(), 1));
			}
		}
		if (item.getCategoryName().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
			this.setStackSize(1);
		}
	}

	public Resource setDamage( final double damage ) {
		this.damage = damage;
		return this;
	}

	public Resource setQuantity( final int newQuantity ) {
		baseQty = newQuantity;
		return this;
	}

	public void setRegistrationDate( final DateTime registrationDate ) {
		this.registrationDate = registrationDate;
	}

	public Resource setStackSize( final int stackSize ) {
		this.stackSize = stackSize;
		return this;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("Resource [");
		buffer.append("[").append(this.getCategory()).append("] ");
		buffer.append(getItem().getName()).append(" x").append(baseQty).append(" ");
		buffer.append("stack: ").append(stackSize).append(" ");
		buffer.append("total: ").append(this.getQuantity()).append(" ");
		buffer.append("#").append(this.getTypeId()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................

package org.dimensinfin.eveonline.neocom.industry;

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.domain.IItemFacet;
import org.dimensinfin.eveonline.neocom.interfaces.IAggregableItem;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

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
public class Resource extends NeoComNode implements IAggregableItem, IItemFacet {
	private static final long serialVersionUID = 921961484632479376L;

	public int typeId = -1;
	protected int baseQty = 0;
	protected int stackSize = 1;
	protected double damage = 1.0;

	protected transient EveItem esiItem;
	//	private DateTime registrationDate = new DateTime(DateTimeZone.UTC);

	// - C O N S T R U C T O R S
	public Resource( final int typeId ) {
		super();
		this.typeId = typeId;
		this.esiItem = new EveItem(typeId);
		this.baseQty = 0;
//		jsonClass = "Resource";
	}

	public Resource( final int typeId, final int newQty ) {
		this(typeId);
		this.baseQty = newQty;
	}

	public Resource( final int typeId, final int newQty, final int stackSize ) {
		this(typeId, newQty);
		this.stackSize = stackSize;
	}

	public int add( final int count ) {
		this.baseQty += count;
		return this.baseQty;
	}

	/**
	 * Adds the quantities of two resources of the same type. On this moment the original resource losses the
	 * stack values and the equivalent quantity is calculated before adding the new quantity calculated exactly
	 * on the same way. The final result is the total quantity but with a stack size of one.
	 */
	public int addition( final Resource newResource ) {
		int newqty = this.getBaseQuantity() * this.getStackSize();
		newqty += newResource.getBaseQuantity() * newResource.getStackSize();
		this.baseQty = newqty;
		this.stackSize = 1;
		return this.baseQty;
	}

	public int getBaseQuantity() {
		return baseQty;
	}

	public String getCategory() {
		return this.getItem().getCategoryName();
	}

	public double getDamage() {
		return damage;
	}

	public Resource setDamage( final double damage ) {
		this.damage = damage;
		return this;
	}

	public String getGroupName() {
		return getItem().getGroupName();
	}

	public EveItem getItem() {
		if (null == this.esiItem) this.esiItem = new EveItem(this.typeId);
		return this.esiItem;
	}

	public String getName() {
		return this.getItem().getName();
	}

	@Override
	public String getURLForItem() {
		return this.getItem().getURLForItem();
	}

	public int getStackSize() {
		return this.stackSize;
	}

	public Resource setStackSize( final int stackSize ) {
		this.stackSize = stackSize;
		return this;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public void setAdaptiveStackSize( final int size ) {
		this.setStackSize(size);
		if (this.getItem().getCategoryName().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
			if (this.getItem().getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
				final double stack = Math.ceil(size / 10d);
				this.setStackSize(Math.max(new Double(stack).intValue(), 1));
			}
			if (this.getItem().getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
				final double stack = Math.ceil(size / 300d);
				this.setStackSize(Math.max(new Double(stack).intValue(), 1));
			}
		}
		if (this.getItem().getCategoryName().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
			this.setStackSize(1);
		}
	}

	/**
	 * Apply the manufacture formulas to get the correct value of the quantity for this user.
	 *
	 * @return my manufacture quantity
	 */
	public int getQuantity() {
		return this.getBaseQuantity() * stackSize;
	}

	// - I A G G R E G A B L E I T E M

	public Resource setQuantity( final int newQuantity ) {
		baseQty = newQuantity;
		return this;
	}

	@Override
	public double getVolume() {
		return this.getItem().getVolume();
	}

	@Override
	public double getPrice() {
		return this.getItem().getPrice();
	}

	// - C O R E
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

//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.sql.SQLException;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.interfaces.IDateTimeComparator;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.NeoComMarketOrder;
import org.dimensinfin.evedroid.render.MarketOrderRender;
import org.joda.time.DateTime;

import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

// - CLASS IMPLEMENTATION ...................................................................................
public class MarketOrderPart extends EveAbstractPart
		implements IMenuActionTarget, OnClickListener, IDateTimeComparator {
	private static final long serialVersionUID = 5816353740185144480L;

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketOrderPart(final AbstractComplexNode node) {
		super(node);
	}

	//	public MarketOrderPart(final AbstractGEFNode node) {
	//		super(node);
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public NeoComMarketOrder getCastedModel() {
		return (NeoComMarketOrder) this.getModel();
	}

	public DateTime getComparableDate() {
		return new DateTime(this.getCastedModel().getIssuedDate());
	}

	public int getEntered() {
		return this.getCastedModel().getVolEntered();
	}

	@Override
	public long getModelID() {
		return this.getCastedModel().getOrderID();
	}

	public String getName() {
		return this.getCastedModel().getItem().getName();
	}

	public EveLocation getOrderLocation() {
		return this.getCastedModel().getOrderLocation();
	}

	public int getOrderState() {
		return this.getCastedModel().getOrderState();
	}

	public double getPrice() {
		return this.getCastedModel().getPrice();
	}

	public int getRemaining() {
		return this.getCastedModel().getVolRemaining();
	}

	public int getTypeID() {
		return this.getCastedModel().getItemTypeID();
	}

	public void onClick(final View target) {
		Log.i("EVEI", ">> StackPart.onClick");
		// REFACTOR Access to ItemDetail Activity removed until that actiity is rewritten
		//		Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
		//		intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID, getPilot().getCharacterID());
		//		intent.putExtra(AppWideConstants.extras.EXTRA_EVEITEMID, getCastedModel().getItemTypeID());
		//		getActivity().startActivity(intent);
		Log.i("EVEI", "<< StackPart.onClick");
	}

	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		// Process the command depending on the menu and the item selected
		switch (menuItemIndex) {
			case R.id.deletemarketOrderActionMenuID:
				try {
					AppConnector.getDBConnector().getMarketOrderDAO().delete(this.getCastedModel());
					// Clear the cache in memory
					this.getPilot().cleanOrders();
					// Remove from parent and clear the view to force a redraw.
					// TODO Check if this is required. We should not manipulate the
					// structures directly but send update signals.
					// ((AbstractGEFNode) getParent()).removeChild(this);
					this.invalidate();
					// TODO Added the firing of the signal to force the update.
					this.fireStructureChange(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE, this, this);
				} catch (SQLException sqle) {
					sqle.printStackTrace();
				}
				break;
			default:
				return false;
		}
		return true;
	}

	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		// Activate the menu only of the order is scheduled.
		if (this.getOrderState() == ModelWideConstants.orderstates.SCHEDULED)
			this.getActivity().getMenuInflater().inflate(R.menu.marketorder_menu, menu);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("MarketOrderPart [");
		buffer.append(this.getName()).append(" ");
		buffer.append("Qtys:").append(this.getEntered()).append("/").append(this.getRemaining()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected AbstractHolder selectHolder() {
		return new MarketOrderRender(this, _activity);
	}
}

// - UNUSED CODE
// ............................................................................................

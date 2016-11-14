//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.sql.SQLException;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.core.IMenuActionTarget;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.ItemDetailsActivity;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.core.IDateTimeComparator;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.MarketOrder;
import org.dimensinfin.evedroid.render.MarketOrderRender;
import org.joda.time.DateTime;

import android.content.Intent;
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

	// - S T A T I C - S E C T I O N
	// ..........................................................................

	// - F I E L D - S E C T I O N
	// ............................................................................

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public MarketOrderPart(final AbstractGEFNode node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	public MarketOrder getCastedModel() {
		return (MarketOrder) getModel();
	}

	public DateTime getComparableDate() {
		return new DateTime(getCastedModel().getIssuedDate());
	}

	public int getEntered() {
		return getCastedModel().getVolEntered();
	}

	public void onClick(final View target) {
		Log.i("EVEI", ">> StackPart.onClick");
		Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
		intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID, getPilot().getCharacterID());
		intent.putExtra(AppWideConstants.extras.EXTRA_EVEITEMID, getCastedModel().getItemTypeID());
		getActivity().startActivity(intent);
		Log.i("EVEI", "<< StackPart.onClick");
	}

	@Override
	public long getModelID() {
		return getCastedModel().getOrderID();
	}

	public String getName() {
		return getCastedModel().getItem().getName();
	}

	public EveLocation getOrderLocation() {
		return getCastedModel().getOrderLocation();
	}

	public int getOrderState() {
		return getCastedModel().getOrderState();
	}

	public double getPrice() {
		return getCastedModel().getPrice();
	}

	public int getRemaining() {
		return getCastedModel().getVolRemaining();
	}

	public int getTypeID() {
		return getCastedModel().getItemTypeID();
	}

	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		// Process the command depending on the menu and the item selected
		switch (menuItemIndex) {
		case R.id.deletemarketOrderActionMenuID:
			try {
				AppConnector.getDBConnector().getMarketOrderDAO().delete(getCastedModel());
				// Clear the cache in memory
				getPilot().cleanOrders();
				// Remove from parent and clear the view to force a redraw.
				// TODO Check if this is required. We should not manipulate the
				// structures directly but send update signals.
				// ((AbstractGEFNode) getParent()).removeChild(this);
				this.invalidate();
				// TODO Added the firing of the signal to force the update.
				fireStructureChange(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE, this, this);
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
		if (getOrderState() == ModelWideConstants.orderstates.SCHEDULED) {
			getActivity().getMenuInflater().inflate(R.menu.marketorder_menu, menu);
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("MarketOrderPart [");
		buffer.append(getName()).append(" ");
		buffer.append("Qtys:").append(getEntered()).append("/").append(getRemaining()).append(" ");
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

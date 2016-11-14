//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.sql.SQLException;
import java.text.DecimalFormat;

import org.dimensinfin.android.mvc.activity.ADialogCallback;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.core.IMenuActionTarget;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.dialog.BuyQtyDialog;
import org.dimensinfin.evedroid.enums.ETaskType;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.EveTask;
import org.dimensinfin.evedroid.model.MarketOrder;
import org.dimensinfin.evedroid.render.TaskRender;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.j256.ormlite.dao.Dao;

import android.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;

// - CLASS IMPLEMENTATION ...................................................................................
public class TaskPart extends MarketDataPart implements IMenuActionTarget {
	// - S T A T I C - S E C T I O N
	// ..........................................................................
	private static final long serialVersionUID = 2556476507087279363L;

	// - F I E L D - S E C T I O N
	// ............................................................................

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public TaskPart(final AbstractGEFNode node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	public MarketOrder generateOrder(final int quantity) {
		final DateTime now = new DateTime(DateTimeZone.UTC);
		this.item = getCastedModel().getItem();
		final MarketOrder newMarketOrder = new MarketOrder(now.getMillis());
		try {
			newMarketOrder.setOwnerID(getPilot().getCharacterID());
			newMarketOrder.setStationID(this.item.getLowestSellerPrice().getLocation().getID());
			newMarketOrder.setVolEntered(quantity);
			newMarketOrder.setVolRemaining(0);
			newMarketOrder.setMinVolume(1);
			newMarketOrder.setOrderState(10);
			newMarketOrder.setTypeID(this.item.getTypeID());
			newMarketOrder.setRange(1);
			newMarketOrder.setAccountKey(1000);
			newMarketOrder.setDuration(14);
			newMarketOrder.setEscrow(0);
			newMarketOrder.setPrice(this.item.getLowestSellerPrice().getPrice());
			newMarketOrder.setBid(0);
			newMarketOrder.setIssuedDate(now.toDate());
			try {
				final Dao<MarketOrder, String> marketOrderDao = AppConnector.getDBConnector().getMarketOrderDAO();
				marketOrderDao.createOrUpdate(newMarketOrder);
				Log.i("EVEI", "-- TaskPart.generateOrder.Wrote MarketOrder to database id ["
						+ newMarketOrder.getOrderID() + "]");
			} catch (final SQLException sqle) {
				Log.i("EVEI",
						"E> Unable to create the new Job [" + newMarketOrder.getOrderID() + "]. " + sqle.getMessage());
				sqle.printStackTrace();
			}
		} catch (final RuntimeException rtex) {
			rtex.printStackTrace();
		}
		return newMarketOrder;
	}

	public String get_action() {
		final ETaskType action = getCastedModel().getTaskType();
		return action.toString();
	}

	public Spanned get_assetLocation() {
		final StringBuffer htmlLocation = new StringBuffer();
		final String security = getCastedModel().getLocation().getSecurity();
		String secColor = securityLevels.get(security);
		if (null == secColor) {
			secColor = "#2FEFEF";
		}
		htmlLocation.append("<font color='").append(secColor).append("'>")
				.append(getCastedModel().getLocation().getSecurity()).append("</font>");
		htmlLocation.append(" ").append(getCastedModel().getLocation().getStation());
		return Html.fromHtml(htmlLocation.toString());
	}

	public String get_balance() {
		final double price = getCastedModel().getPrice();
		final double bal = (price * getCastedModel().getQty()) / 1000.0;
		if (bal > 1000000.0) {
			final DecimalFormat formatter = new DecimalFormat("###,###M ISK");
			final String balanceString = formatter.format(bal / 1000.0);
			return balanceString;
		}
		final DecimalFormat formatter = new DecimalFormat("###,###K ISK");
		final String balanceString = formatter.format(bal);
		return "- " + balanceString;
	}

	/**
	 * Presents on the UI with the cost to obtain of the resources missing to
	 * manufacture the schedule count.
	 */
	public String get_budget() {
		return generatePriceString(getSellerPrice() * getCastedModel().getQty(), true, true);
	}

	public String get_cost() {
		final double price = getCastedModel().getPrice();
		final DecimalFormat formatter = new DecimalFormat("###,###.0# ISK");
		final String costString = formatter.format(price);
		return costString;
	}

	/**
	 * Searches for a text representation of the destination station or place
	 * where to move some asset.
	 */
	public String get_destination() {
		// If the location comes from the Market then there is no station
		return getCastedModel().getDestination().getStation();
	}

	public Spanned get_fromtoLocation() {
		final StringBuffer htmlLocation = new StringBuffer();
		// Compose the FROM
		EveLocation loc = getCastedModel().getLocation();
		String security = loc.getSecurity();
		String secColor = securityLevels.get(security);
		if (null == secColor) {
			secColor = "#2FEFEF";
		}
		htmlLocation.append("<font color='").append(secColor).append("'>")
				.append(securityFormatter.format(loc.getSecurityValue())).append("</font>");
		htmlLocation.append(" ").append(loc.getSystem());
		htmlLocation.append(AppWideConstants.FLOW_ARROW_RIGHT);
		// Compose the TO
		loc = getCastedModel().getDestination();
		security = loc.getSecurity();
		secColor = securityLevels.get(security);
		if (null == secColor) {
			secColor = "#2FEFEF";
		}
		htmlLocation.append("<font color='").append(secColor).append("'>")
				.append(securityFormatter.format(loc.getSecurityValue())).append("</font>");
		htmlLocation.append(" ").append(loc.getSystem());
		return Html.fromHtml(htmlLocation.toString());
	}

	public String get_itemName() {
		return getCastedModel().getItemName();
	}

	public Spanned get_manufacturelocation() {
		final StringBuffer htmlLocation = new StringBuffer();
		// Compose the FROM
		final EveLocation loc = getCastedModel().getLocation();
		final String security = loc.getSecurity();
		String secColor = securityLevels.get(security);
		if (null == secColor) {
			secColor = "#2FEFEF";
		}
		htmlLocation.append("<font color='").append(secColor).append("'>").append(loc.getSecurity()).append("</font>");
		htmlLocation.append(" ").append(loc.getSystem());
		return Html.fromHtml(htmlLocation.toString());
	}

	public String get_qtyAvailable() {
		return get_qtyRequired();
	}

	public String get_qtyRequired() {
		final long quantity = getCastedModel().getQty();
		final DecimalFormat formatter = new DecimalFormat("x###,##0");
		final String qtyString = formatter.format(quantity);
		return qtyString;
	}

	public String get_volume() {
		return volumeFormatter.format(getCastedModel().getItem().getVolume() * getCastedModel().getQty()) + " m3";
	}

	public ETaskType getActionCode() {
		return getCastedModel().getTaskType();
	}

	public EveTask getCastedModel() {
		return (EveTask) getModel();
	}

	@Override
	public long getModelID() {
		return getCastedModel().getTypeID();
	}

	public int getQuantity() {
		return getCastedModel().getQty();
	}

	public EveLocation getSourceLocation() {
		return getCastedModel().getLocation();
	}

	public boolean onContextItemSelected(final MenuItem item) {
		return false;
	}

	/**
	 * Creates the contextual menu for the selected task if this is a BUY task
	 * only. The action should trigger the display of the quantity request
	 * dialog to get the number to be bought.
	 */
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		Log.i("EVEI", ">> TaskPart.onCreateContextMenu");
		// For blueprints the menu depends on the renderer selected.
		if ((getActionCode() == ETaskType.BUY) || (getActionCode() == ETaskType.MOVE)) {
			final BuyQtyDialog dialog = new BuyQtyDialog();
			dialog.setPart(this);
			dialog.setDialogCallback(new ADialogCallback() {
				@Override
				public void onDialogNegativeClick(final DialogFragment dialog) {
					Log.i("EVEI", "<< TaskPart.onCreateContextMenu - Cancelled BUY request.");
				}

				@Override
				public void onDialogPositiveClick(final DialogFragment dialog) {
					// Get the number of runs selected by the user.
					final int quantity = ((BuyQtyDialog) dialog).getQuantity();
					generateOrder(quantity);
					Log.i("EVEI", "<< TaskPart.onCreateContextMenu - BUY request for [" + quantity + "]");
					// Recalculate the current view.
					invalidate();
					fireStructureChange(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE, this, this);
				}
			});
			dialog.show(getActivity().getFragmentManager(), "BuyQtyDialog");
		}
		Log.i("EVEI", "<< TaskPart.onCreateContextMenu");
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("TaskPart [");
		buffer.append(getCastedModel().toString()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected void initialize() {
		this.item = getCastedModel().getItem();
		if (null == this.item)
			throw new RuntimeException(
					"RT> TaskPart - The task item is not defined. " + getCastedModel().getItemName());
	}

	@Override
	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		return new TaskRender(this, this._activity);
	}
}
// - UNUSED CODE
// ............................................................................................

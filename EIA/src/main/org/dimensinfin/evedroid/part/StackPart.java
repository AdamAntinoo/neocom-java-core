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
import org.dimensinfin.evedroid.activity.ItemDetailsActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.INamedPart;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.render.MarketOrderResourceRender;

import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class StackPart extends MarketDataPart implements INamedPart, OnLongClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 8910031270706432316L;

	// private static Logger logger = Logger.getLogger("StackPart");

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public StackPart(final AbstractGEFNode node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public Spanned get_balance() {
		final double qty = getCastedModel().getQuantity();
		return Html.fromHtml(generatePriceString(qty * getBuyerprice(), true, true));
	}

	public String get_count() {
		return qtyFormatter.format(getCastedModel().getQuantity());
	}

	public String get_itemName() {
		return getCastedModel().getName();
	}

	public int get_typeID() {
		return getCastedModel().item.getItemID();
	}

	public Resource getCastedModel() {
		return (Resource) getModel();
	}

	@Override
	public long getModelID() {
		return getCastedModel().item.getItemID();
	}

	@Override
	public String getName() {
		return getCastedModel().getName();
	}

	@Override
	public boolean onLongClick(final View target) {
		Log.i("EVEI", ">> T2Mod4SellPart.onClick");
		// Set the pilot selected on the context and then go to the Director board.
		final Object part = target.getTag();
		if (part instanceof StackPart) {
			final EveItem item = ((StackPart) part).getCastedModel().item;
			EVEDroidApp.getAppContext().setItem(item);
			final Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
			intent.putExtra(AppWideConstants.extras.EXTRA_EVEITEMID, ((StackPart) part).getCastedModel().item.getItemID());
			EVEDroidApp.getAppContext().getActivity().startActivity(intent);
		}
		Log.i("EVEI", "<< T2Mod4SellPart.onClick");
		return false;
	}

	@Override
	protected void initialize() {
		this.item = getCastedModel().getItem();
	}

	@Override
	protected AbstractHolder selectHolder() {
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_MARKETORDERSCHEDULEDSELL)
			return new MarketOrderResourceRender(this, this._activity);
		return new MarketOrderResourceRender(this, this._activity);
	}

}

// - UNUSED CODE ............................................................................................

//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.activity.ItemDetailsActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.holder.Asset4CategoryHolder;
import org.dimensinfin.evedroid.holder.AssetHolder;
import org.dimensinfin.evedroid.interfaces.INamedPart;
import org.dimensinfin.evedroid.model.Asset;
import org.dimensinfin.evedroid.render.AssetLineRender;

import android.content.Intent;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class AssetPart extends MarketDataPart implements INamedPart, OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long																	serialVersionUID	= 8910031270706432316L;
	private static Logger																			logger						= Logger.getLogger("AssetPart");

	// - F I E L D - S E C T I O N ............................................................................
	@SuppressWarnings("rawtypes")
	private final HashMap<Long, HashMap<Integer, AssetPart>>	stackList					= new HashMap();

	//	private MarketDataSet																			mdata							= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetPart(final AbstractGEFNode node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_assetCategory() {
		final String assetIdentifier = getCastedModel().getAssetID() + "-" + getCastedModel().getCategory();
		return assetIdentifier;
	}

	public Spanned get_assetLocation() {
		return colorFormatLocation(getCastedModel().getLocation());
	}

	public String get_assetName() {
		return getCastedModel().getName();
	}

	public int get_assetTypeID() {
		return getCastedModel().getTypeID();
	}

	public String get_count() {
		long quantity = getCastedModel().getQuantity();
		DecimalFormat formatter = new DecimalFormat("###,###");
		String qtyString = formatter.format(quantity);
		return qtyString;
	}

	/**
	 * This method does two things. It will get the current price (even obsolete or inexact) from the Item
	 * database and at the same time fire an update of the item information (mainly on prices and locations) to
	 * the background manager.
	 * 
	 * @return
	 */
	public String get_itemPrice() {
		return generatePriceString(getBuyerPrice(), false, true);
	}

	public String get_stackValue() {
		long quantity = getCastedModel().getQuantity();
		//		double price = searchMarketData(getCastedModel().getTypeID()).getBestMarket().getPrice();
		return generatePriceString(getBuyerPrice() * quantity, true, true);
	}

	public long getAssetID() {
		return getCastedModel().getAssetID();
	}

	public Asset getCastedModel() {
		return (Asset) getModel();
	}

	public long getModelID() {
		return getCastedModel().getDAOID();
	}

	public String getName() {
		return getCastedModel().getName();
	}

	public void onClick(final View target) {
		Log.i("EVEI", ">> AssetPart.onClick");
		Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
		intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID, getPilot().getCharacterID());
		intent.putExtra(AppWideConstants.extras.EXTRA_EVEITEMID, getCastedModel().getTypeID());
		getActivity().startActivity(intent);
		Log.i("EVEI", "<< AssetPart.onClick");
	}

	protected void checkAssetStacking(final AssetPart apart) {
		// Locate the stack if exists.
		HashMap<Integer, AssetPart> container = stackList.get(getCastedModel().getDAOID());
		int type = apart.getCastedModel().getTypeID();
		if (null != container) {
			AssetPart stack = container.get(type);
			if (null != stack) {
				//	if (stack instanceof AssetPart) {
				int count = stack.getCastedModel().getQuantity();
				stack.getCastedModel().setQuantity(count + 1);
				return;
				//	}
				// Do nothing because we do not know how to add to Assets.
			} else {
				// Add a new stack for this type to the current container.
				container.put(type, apart);
				addChild(apart);
			}
		} else {
			// There is no container also with this stack.
			container = new HashMap<Integer, AssetPart>();
			container.put(type, apart);
			addChild(apart);
			stackList.put(getCastedModel().getDAOID(), container);
		}
	}

	protected void initialize() {
		item = getCastedModel().getItem();
	}

	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		if (getRenderMode() == AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION)
			return new AssetHolder(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_LOCATIONMODE)
			return new AssetLineRender(this, _activity);
		if (getRenderMode() == AppWideConstants.fragment.FRAGMENT_ITEMMODULESTACKS)
			return new Asset4CategoryHolder(this, _activity);
		return new AssetHolder(this, _activity);
	}

	//	private MarketDataSet searchMarketData(final int itemid) {
	//		if (null == mdata)
	//			mdata = AppConnector.getDBConnector().searchMarketData(getCastedModel().getItem().getItemID(),
	//					ModelWideConstants.marketSide.BUYER);
	//		return mdata;
	//	}
}

// - UNUSED CODE ............................................................................................

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
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.holder.Asset4CategoryHolder;
import org.dimensinfin.evedroid.holder.AssetHolder;
import org.dimensinfin.evedroid.interfaces.INamedPart;
import org.dimensinfin.evedroid.render.AssetLineRender;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

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
	public AssetPart(final NeoComAsset node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_assetCategory() {
		final String assetIdentifier = this.getCastedModel().getAssetID() + "-" + this.getCastedModel().getCategory();
		return assetIdentifier;
	}

	public Spanned get_assetLocation() {
		return this.colorFormatLocation(this.getCastedModel().getLocation());
	}

	public String get_assetName() {
		return this.getCastedModel().getName();
	}

	public int get_assetTypeID() {
		return this.getCastedModel().getTypeID();
	}

	public String get_count() {
		long quantity = this.getCastedModel().getQuantity();
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
		return this.generatePriceString(this.getBuyerPrice(), false, true);
	}

	public String get_stackValue() {
		long quantity = this.getCastedModel().getQuantity();
		//		double price = searchMarketData(getCastedModel().getTypeID()).getBestMarket().getPrice();
		return this.generatePriceString(this.getBuyerPrice() * quantity, true, true);
	}

	public long getAssetID() {
		return this.getCastedModel().getAssetID();
	}

	public NeoComAsset getCastedModel() {
		return (NeoComAsset) this.getModel();
	}

	@Override
	public long getModelID() {
		return this.getCastedModel().getDAOID();
	}

	public String getName() {
		return this.getCastedModel().getName();
	}

	public void onClick(final View target) {
		Log.i("EVEI", ">> AssetPart.onClick");
		// REFACTOR Call to ItemDetails Activity disabled because that activity was not supported.
		//		Intent intent = new Intent(this.getActivity(), ItemDetailsActivity.class);
		//		intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID, this.getPilot().getCharacterID());
		//		intent.putExtra(AppWideConstants.extras.EXTRA_EVEITEMID, this.getCastedModel().getTypeID());
		//		this.getActivity().startActivity(intent);
		Log.i("EVEI", "<< AssetPart.onClick");
	}

	protected void checkAssetStacking(final AssetPart apart) {
		// Locate the stack if exists.
		HashMap<Integer, AssetPart> container = stackList.get(this.getCastedModel().getDAOID());
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
				this.addChild(apart);
			}
		} else {
			// There is no container also with this stack.
			container = new HashMap<Integer, AssetPart>();
			container.put(type, apart);
			this.addChild(apart);
			stackList.put(this.getCastedModel().getDAOID(), container);
		}
	}

	@Override
	protected void initialize() {
		item = this.getCastedModel().getItem();
	}

	@Override
	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		if (this.getRenderMode() == AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION)
			return new AssetHolder(this, _activity);
		if (this.getRenderMode() == AppWideConstants.rendermodes.RENDER_LOCATIONMODE)
			return new AssetLineRender(this, _activity);
		if (this.getRenderMode() == AppWideConstants.fragment.FRAGMENT_ITEMMODULESTACKS)
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

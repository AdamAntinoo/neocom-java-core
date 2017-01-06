//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.core.EveAbstractHolder;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.part.MarketOrderPart;
import org.joda.time.Instant;

import android.app.Activity;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class MarketOrderRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger		logger					= Logger.getLogger("ResourceHolder");

	// - F I E L D - S E C T I O N ............................................................................
	// - L A Y O U T   F I E L D S
	private TextView	itemName			= null;
	private TextView		orderLocation	= null;
	private TextView		itemPrice			= null;
	private TextView		volumes				= null;
	private TextView		orderDuration	= null;

	// - L A Y O U T   L A B E L S
	private TextView		expiresLabel	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketOrderRender(final MarketOrderPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public MarketOrderPart getPart() {
		return (MarketOrderPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		itemName = (TextView) _convertView.findViewById(R.id.itemName);
		orderLocation = (TextView) _convertView.findViewById(R.id.orderLocation);
		itemPrice = (TextView) _convertView.findViewById(R.id.itemPrice);
		volumes = (TextView) _convertView.findViewById(R.id.volumes);
		orderDuration = (TextView) _convertView.findViewById(R.id.orderDuration);

		expiresLabel = (TextView) _convertView.findViewById(R.id.expiresLabel);
	}

	/**
	 * Renders a MarketOrder that is available at the corresponding part. There are some variants on the
	 * presentation of this data even the layout is the same.<br>
	 * The first variant is for open orders. They are shadowed in blue and have an active timer on the lower
	 * right corner. Then there a re the completed orders that have a white tint. There are also the scheduled
	 * orders that have a red tint and have to time set.
	 */
	@SuppressWarnings("deprecation")
	public void updateContent() {
		super.updateContent();

		// Draw the common parts to all types of market orders.
		itemName.setText(getPart().getName());
		if (AppWideConstants.DEVELOPMENT) itemName.setText(getPart().getName() + " [#" + getPart().getTypeID() + "]");
		int qty = getPart().getEntered();
		int remaining = getPart().getRemaining();
		volumes.setText(qtyFormatter.format(qty - remaining) + " / " + qtyFormatter.format(qty));
		orderLocation.setText(regionSystemLocation(getPart().getOrderLocation()));
		itemPrice.setText(generatePriceString(getPart().getPrice(), true, true));

		// Starts the variations. The first one is for active orders.
		int state = getPart().getOrderState();
		if (state == ModelWideConstants.orderstates.OPEN) {
			Instant now = new Instant();
			final Instant issued = new Instant(getPart().getCastedModel().getIssuedDate());
			final Instant expires = issued.plus(getPart().getCastedModel().getDuration() * AppWideConstants.HOURS24);
			long millis = expires.getMillis() - now.getMillis();
			CountDownTimer timer = new CountDownTimer(millis, 1000) {
				public void onFinish() {
					orderDuration.setText("Expired! - " + timePointFormatter.print(expires));
				}

				public void onTick(final long millisUntilFinished) {
					orderDuration.setText(generateDurationString(millisUntilFinished, true));
					orderDuration.invalidate();
					_convertView.invalidate();
				}
			}.start();
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bluetraslucent60));
		}
		if (state == ModelWideConstants.orderstates.EXPIRED) {
			orderDuration.setText(generateDateString(getPart().getCastedModel().getIssuedDate().getTime()));
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.whitetraslucent40));
		}
		if ((state == ModelWideConstants.orderstates.CLOSED) || (state == ModelWideConstants.orderstates.CANCELLED)) {
			orderDuration.setText(generateDateString(getPart().getCastedModel().getIssuedDate().getTime()));
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.redtraslucent60));
		}
		if (state == ModelWideConstants.orderstates.SCHEDULED) {
			// Instead a duration set the total budget.
			double budget = qty*getPart().getPrice();
			orderDuration.setText(generatePriceString(budget, true, true));
			expiresLabel.setText("BUDGET");
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.redtraslucent40));
		}

		// Draw the specific variations for each class.
		if (state == ModelWideConstants.orderstates.SCHEDULED) {
			volumes.setText(qtyFormatter.format(qty));
		}

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getTypeID());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.resource4marketorders, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

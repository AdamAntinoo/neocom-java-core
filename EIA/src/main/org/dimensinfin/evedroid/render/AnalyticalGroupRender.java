//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.MarketOrderAnalyticalGroupPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class AnalyticalGroupRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private ImageView	groupIcon	= null;
	private TextView	title			= null;
	private TextView	quantity	= null;
	private TextView	budget		= null;
	private TextView	modules		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AnalyticalGroupRender(final AbstractAndroidPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public MarketOrderAnalyticalGroupPart getPart() {
		return (MarketOrderAnalyticalGroupPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		this.title = (TextView) this._convertView.findViewById(R.id.title);
		this.quantity = (TextView) this._convertView.findViewById(R.id.quantity);
		this.budget = (TextView) this._convertView.findViewById(R.id.budget);
		this.modules = (TextView) this._convertView.findViewById(R.id.totalItemCount);
		this.groupIcon = (ImageView) this._convertView.findViewById(R.id.groupIcon);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		this.title.setText(getPart().getGroupTitle());
		this.quantity.setText(qtyFormatter.format(getPart().getGroupCount()));
		this.budget.setText(generatePriceString(getPart().getGroupBudget(), true, true));
		this.modules.setText(qtyFormatter.format((getPart().getGroupQuantity())));
		this.groupIcon.setImageResource(R.drawable.markethub);
		this._convertView.setBackgroundResource(R.drawable.whitetraslucent40);
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this._convertView = mInflater.inflate(R.layout.analyticalgroup4marketorders, null);
		this._convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

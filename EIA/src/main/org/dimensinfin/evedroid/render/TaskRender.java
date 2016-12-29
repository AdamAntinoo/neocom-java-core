//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.TaskPart;
import org.dimensinfin.eveonline.neocom.enums.ETaskType;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class TaskRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger				= Logger.getLogger("BPOHolder");

	// - F I E L D - S E C T I O N ............................................................................
	private TextView			action				= null;
	private TextView			itemName			= null;
	private TextView			qtyRequired		= null;
	private TextView			volume				= null;
	private TextView			location			= null;
	private TextView			bestBuyPrice	= null;
	private TextView			budget				= null;

	private ViewGroup			bestBuyGroup	= null;
	private TextView			fromLabel			= null;
	private TextView			budgetLabel		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public TaskRender(final TaskPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public TaskPart getPart() {
		return (TaskPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		this.action = (TextView) this._convertView.findViewById(R.id.action);
		this.itemName = (TextView) this._convertView.findViewById(R.id.itemName);
		this.qtyRequired = (TextView) this._convertView.findViewById(R.id.qtyRequired);
		this.volume = (TextView) this._convertView.findViewById(R.id.volume);
		this.location = (TextView) this._convertView.findViewById(R.id.location);
		this.bestBuyPrice = (TextView) this._convertView.findViewById(R.id.bestBuyPrice);
		this.budget = (TextView) this._convertView.findViewById(R.id.budget);

		this.bestBuyGroup = (ViewGroup) this._convertView.findViewById(R.id.bestBuy);
		this.fromLabel = (TextView) this._convertView.findViewById(R.id.fromLabel);
		this.budgetLabel = (TextView) this._convertView.findViewById(R.id.budgetLabel);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void updateContent() {
		final ETaskType type = getPart().getActionCode();
		this.action.setText(getPart().get_action());
		this.qtyRequired.setText(getPart().get_qtyRequired());
		this.volume.setVisibility(View.GONE);

		// Paint a different colour for specials.
		//		if (type == ETaskType.GET)
		//			action.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.redroundedbox));
		//		if (type == ETaskType.SELL)
		//			action.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.blueroundedbox));

		// Pack the changes for each task type
		if (type == ETaskType.AVAILABLE) {
			this.action.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.greenroundedbox));
			this.qtyRequired.setTextColor(getContext().getResources().getColor(R.color.whitePrice));

			this.bestBuyPrice.setText(getPart().get_lowestSellerPrice());
			this.budget.setText(getPart().get_budget());

			this.itemName.setVisibility(View.GONE);
			this.location.setVisibility(View.GONE);
			this.fromLabel.setVisibility(View.GONE);
			// Check for blueprints. They have to remove the price information.
			if (getPart().getCastedModel().getItem().isBlueprint()) {
				this.bestBuyPrice.setVisibility(View.GONE);
				this.budget.setVisibility(View.GONE);
				this.fromLabel.setVisibility(View.GONE);
				this.budgetLabel.setVisibility(View.GONE);
			}
		}
		if (type == ETaskType.BUY) {
			this.action.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.orangeroundedbox));
			this._convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.redtraslucent80));

			this.bestBuyPrice.setText(getPart().get_lowestSellerPrice());
			this.location.setText(getPart().display_SellerLocation());
			this.budget.setText(getPart().get_budget());
			this.fromLabel.setText("BUY AT");

			this.itemName.setVisibility(View.GONE);
			if (getPart().getCastedModel().getItem().isBlueprint()) {
				this.bestBuyGroup.setVisibility(View.GONE);
				this.budget.setVisibility(View.GONE);
				this.fromLabel.setVisibility(View.GONE);
				this.budgetLabel.setVisibility(View.GONE);
			}
		}
		if (type == ETaskType.BUYCOVERED) {
			this.action.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.blueroundedbox));
			this._convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bluetraslucent40));

			this.bestBuyPrice.setText(getPart().get_lowestSellerPrice());
			this.location.setText(getPart().display_SellerLocation());
			this.budget.setText(getPart().get_budget());
			this.fromLabel.setText("BUY AT");

			this.itemName.setVisibility(View.GONE);
			if (getPart().getCastedModel().getItem().isBlueprint()) {
				this.bestBuyGroup.setVisibility(View.GONE);
				this.budget.setVisibility(View.GONE);
				this.fromLabel.setVisibility(View.GONE);
				this.budgetLabel.setVisibility(View.GONE);
			}
		}
		if (type == ETaskType.EXTRACT) {
			this.action.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.yellowroundedbox));
			this.qtyRequired.setTextColor(getContext().getResources().getColor(R.color.orangeText));
			this.bestBuyPrice.setText(getPart().get_lowestSellerPrice());
			this.budget.setText(getPart().get_budget());

			this.bestBuyGroup.setVisibility(View.VISIBLE);
			this.location.setText(getPart().get_assetLocation());
			this.fromLabel.setText("REFINE AT");

			this.itemName.setVisibility(View.GONE);
			this.location.setVisibility(View.GONE);
			this.fromLabel.setVisibility(View.GONE);
		}
		if (type == ETaskType.PRODUCE) {
			this.action.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.greenroundedbox));
			this.qtyRequired.setTextColor(getContext().getResources().getColor(R.color.whitePrice));
			this.itemName.setText(getPart().get_itemName());
			this.itemName.setTextColor(getContext().getResources().getColor(R.color.whitePrice));

			this.itemName.setVisibility(View.VISIBLE);
			this.bestBuyGroup.setVisibility(View.GONE);
			this.budget.setVisibility(View.GONE);
			this.budgetLabel.setVisibility(View.GONE);
			this.fromLabel.setVisibility(View.GONE);
		}
		if (type == ETaskType.MOVE) {
			this.action.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.yellowroundedbox));
			this.qtyRequired.setTextColor(getContext().getResources().getColor(R.color.orangeName));
			this.location.setText(getPart().get_fromtoLocation());
			this.volume.setText(getPart().get_volume());
			//			budgetLabel.setText("VALUE");

			this.itemName.setVisibility(View.GONE);
			this.bestBuyPrice.setVisibility(View.GONE);
			this.location.setVisibility(View.VISIBLE);
			this.volume.setVisibility(View.VISIBLE);
			this.fromLabel.setVisibility(View.VISIBLE);
			this.budget.setVisibility(View.INVISIBLE);
			this.budgetLabel.setVisibility(View.INVISIBLE);
			this._convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bluetraslucent40));
		}
		if (type == ETaskType.BUILD) {
			this.action.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.yellowroundedbox));
			this._convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bluetraslucent40));
			this.qtyRequired.setTextColor(getContext().getResources().getColor(R.color.orangeName));

			this.location.setText(colorFormatLocation(getPart().getSourceLocation()));
			this.fromLabel.setText("MANUFACTURE AT");
			//			budget.setText(generatePriceString(getPart().getComponentManufacturingCosrt(), true, true));
			this.budget.setVisibility(View.GONE);
			this.budgetLabel.setVisibility(View.GONE);

			this.itemName.setVisibility(View.GONE);
			this.location.setVisibility(View.VISIBLE);
			this.fromLabel.setVisibility(View.VISIBLE);
			this.bestBuyPrice.setVisibility(View.GONE);
			//			budget.setVisibility(View.VISIBLE);
			//			budgetLabel.setVisibility(View.VISIBLE);
		}
		if (type == ETaskType.INVENTION) {
			this.location.setText(getPart().get_manufacturelocation());
			this.fromLabel.setText("INVENT AT");
			this.qtyRequired.setTextColor(getContext().getResources().getColor(R.color.orangeName));
			this.bestBuyPrice.setVisibility(View.GONE);
			this.budget.setVisibility(View.GONE);
			this.fromLabel.setVisibility(View.GONE);
			this.budgetLabel.setVisibility(View.GONE);
		}
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this._convertView = mInflater.inflate(R.layout.task4industry, null);
		this._convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

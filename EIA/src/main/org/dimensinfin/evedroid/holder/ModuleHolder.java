//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.holder;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.ModulePart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class ModuleHolder extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger		logger								= Logger.getLogger("ModuleHolder");

	// - F I E L D - S E C T I O N ............................................................................
	// - L A Y O U T   F I E L D S
	private TextView				moduleName						= null;
	private TextView				moduleIndex						= null;
	private TextView				moduleMultiplier			= null;
	private TextView				moduleProfitHour			= null;
	//	private ImageView				itemIcon							= null;

	// - L A Y O U T   L A B E L S
	private final TextView	moduleProfitHourLabel	= null;
	private final TextView	indexLabel						= null;
	private final TextView	multiplierLabel				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ModuleHolder(final ModulePart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public ModulePart getPart() {
		return (ModulePart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		moduleName = (TextView) _convertView.findViewById(R.id.moduleName);
		moduleIndex = (TextView) _convertView.findViewById(R.id.moduleIndex);
		moduleMultiplier = (TextView) _convertView.findViewById(R.id.moduleMultiplier);
		moduleProfitHour = (TextView) _convertView.findViewById(R.id.moduleProfitHour);
		//		itemIcon = (ImageView) _convertView.findViewById(R.id.itemIcon);

		moduleName.setTypeface(daysFace);
		moduleIndex.setTypeface(daysFace);
		moduleMultiplier.setTypeface(daysFace);
	}

	@Override
	public void updateContent() {
		// Detect the aspect to show and then go to the right method
		super.updateContent();
		if (getPart().getRenderMode() == SystemWideConstants.layoutmodel.NORMAL) updateContentNormal();
		if (getPart().getRenderMode() == SystemWideConstants.layoutmodel.REDUCED) updateContentReduced();

		// Set the background color depending on the selection state.
		if (getPart().isScheduled4Job())
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.greentraslucent40));
		else
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bluetraslucent40));
		_convertView.invalidate();
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.module_4list, null);
		_convertView.setTag(this);
	}

	private void updateContentNormal() {
		moduleName.setText(getPart().get_moduleName());
		moduleIndex.setText(getPart().get_index());
		moduleMultiplier.setText(getPart().get_multiplier());
		double income = getPart().getCastedModel().getPotentialIncome();
		moduleProfitHour.setText(getPart().get_profit());
		if (income > 0)
			moduleProfitHour.setTextColor(getContext().getResources().getColor(R.color.whitePrice));
		else
			moduleProfitHour.setTextColor(getContext().getResources().getColor(R.color.redPrice));
		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getItemTypeID());
	}

	private void updateContentReduced() {
		moduleName.setText(getPart().get_moduleName());
		moduleIndex.setText(getPart().get_index());
		moduleMultiplier.setText(getPart().get_multiplier());

		// Change the size of the icon.
		//		LinearLayout.LayoutParams sizeParams = new LinearLayout.LayoutParams(32, 32);
		//		itemIcon.setLayoutParams(sizeParams);
		moduleProfitHour.setVisibility(View.GONE);
	}
}

// - UNUSED CODE ............................................................................................

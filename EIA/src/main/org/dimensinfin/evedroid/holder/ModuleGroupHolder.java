//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.holder;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.JobModuleGroupPart;
import org.dimensinfin.evedroid.part.ModulePart;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class ModuleGroupHolder extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger		logger							= Logger.getLogger("ModuleGroupHolder");

	// - F I E L D - S E C T I O N ............................................................................
	// - L A Y O U T   F I E L D S
	private TextView				moduleName					= null;
	private TextView				moduleIndex					= null;
	private TextView				moduleMultiplier		= null;
	private TextView				moduleSellLocation	= null;
	//	private ImageView				itemIcon							= null;

	//	private final TextView	moduleMarketSecurity	= null;
	//	private final TextView	moduleMarket					= null;

	// - L A Y O U T   L A B E L S
	//	private final TextView	moduleProfitHourLabel	= null;
	private final TextView	indexLabel					= null;
	private final TextView	multiplierLabel			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ModuleGroupHolder(final ModulePart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public JobModuleGroupPart getPart() {
		return (JobModuleGroupPart) super.getPart();
	}

	public void initializeViews() {
		super.initializeViews();
		moduleName = (TextView) _convertView.findViewById(R.id.moduleName);
		moduleIndex = (TextView) _convertView.findViewById(R.id.moduleIndex);
		moduleMultiplier = (TextView) _convertView.findViewById(R.id.moduleMultiplier);
		moduleSellLocation = (TextView) _convertView.findViewById(R.id.moduleSellLocation);

		moduleName.setTypeface(daysFace);
		moduleIndex.setTypeface(daysFace);
		moduleMultiplier.setTypeface(daysFace);
	}

	public void updateContent() {
		// Detect the aspect to show and then go to the right method
		super.updateContent();
		moduleName.setText(getPart().get_moduleName());
		moduleIndex.setText(getPart().get_index());
		moduleMultiplier.setText(getPart().get_multiplier());
		moduleSellLocation.setText(Html.fromHtml(getPart().get_sellLocation()));

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getItemTypeID());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.module_4group, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

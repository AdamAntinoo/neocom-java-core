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
import org.dimensinfin.evedroid.part.ModulePart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class SmallModuleHolder extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger						= Logger.getLogger("SmallModuleHolder");

	// - F I E L D - S E C T I O N ............................................................................
	private TextView			moduleName				= null;
	private TextView			moduleIndex				= null;
	private TextView			moduleMultiplier	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public SmallModuleHolder(final ModulePart target, final Activity context) {
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

		moduleName.setTypeface(daysFace);
		moduleMultiplier.setTypeface(daysFace);
		moduleIndex.setTypeface(daysFace);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		moduleName.setText(getPart().get_moduleName());
		moduleIndex.setText(getPart().get_index());
		moduleMultiplier.setText(getPart().get_multiplier());

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getItemTypeID());
		//		_convertView.invalidate();
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.module_4reduced, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

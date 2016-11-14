//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.holder;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.part.AssetPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class BlueprintHolder extends AssetHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger				= Logger.getLogger("BlueprintHolder");

	// - F I E L D - S E C T I O N ............................................................................
	public TextView				locationHtml	= null;
	public TextView				bpcme					= null;
	public TextView				bpcpe					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public BlueprintHolder(final AssetPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public AssetPart getPart() {
		return super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		locationHtml = (TextView) _convertView.findViewById(R.id.assetLocation);
		bpcme = (TextView) _convertView.findViewById(R.id.bpcme);
		bpcpe = (TextView) _convertView.findViewById(R.id.bpcpe);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		locationHtml.setText(getPart().get_assetLocation());
		bpcme.setText("-4");
		bpcpe.setText("-4");
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.blueprint_4expandable, null);
		_convertView.setTag(this);
	}
}
// - UNUSED CODE ............................................................................................

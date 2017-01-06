//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.core.EveAbstractHolder;
import org.dimensinfin.eveonline.neocom.part.FittingListPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class FittingListRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	public TextView	fittingName		= null;
	public TextView	fittingClass	= null;
	public TextView	fittingCost		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingListRender(final FittingListPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public FittingListPart getPart() {
		return (FittingListPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		fittingName = (TextView) _convertView.findViewById(R.id.fittingName);
		fittingClass = (TextView) _convertView.findViewById(R.id.fittingClass);
		fittingCost = (TextView) _convertView.findViewById(R.id.fittingCost);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		fittingName.setText(this.getPart().getName());
		fittingClass.setText(this.getPart().getHullName() + " >> " + this.getPart().getHullGroup());
		fittingCost.setText(this.getPart().getTransformedFittingCost());

		this.loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), this.getPart().getHullTypeID());
		_convertView.setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.blacktraslucent40));
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) this.getContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.fitting4list, null);
		_convertView.setTag(this);
	}
}
// - UNUSED CODE ............................................................................................

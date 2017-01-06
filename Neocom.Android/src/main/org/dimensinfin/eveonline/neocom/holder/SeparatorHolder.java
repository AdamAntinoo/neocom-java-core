//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.holder;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.core.EveAbstractHolder;
import org.dimensinfin.eveonline.neocom.part.SeparatorPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class SeparatorHolder extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger	= Logger.getLogger("SeparatorHolder");

	// - F I E L D - S E C T I O N ............................................................................
	private TextView			title		= null;
	private TextView			content	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public SeparatorHolder(final AbstractAndroidPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public SeparatorPart getPart() {
		return (SeparatorPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		title = (TextView) _convertView.findViewById(R.id.title);
		content = (TextView) _convertView.findViewById(R.id.content);
	}

	@Override
	public void updateContent() {
		String tt = this.getPart().get_title();
		if (null != tt) {
			title.setText(tt);
			title.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) this.getContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.separator_4list, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

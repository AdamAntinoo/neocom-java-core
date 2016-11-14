//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.holder;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.SeparatorPart;

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
		String tt = getPart().get_title();
		if (null != tt) {
			title.setText(tt);
			title.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.separator_4list, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.core.EveAbstractHolder;
import org.dimensinfin.eveonline.neocom.part.GroupPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class JobStateRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private ImageView	groupIcon	= null;
	private TextView	title			= null;
	private TextView	count			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public JobStateRender(final AbstractAndroidPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public GroupPart getPart() {
		return (GroupPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		title = (TextView) _convertView.findViewById(R.id.title);
		count = (TextView) _convertView.findViewById(R.id.count);
		groupIcon = (ImageView) _convertView.findViewById(R.id.groupIcon);
		count.setVisibility(View.INVISIBLE);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		title.setText(getPart().getTitle());
		count.setVisibility(View.GONE);
		groupIcon.setImageResource(R.drawable.jobdirector);
		_convertView.setBackgroundResource(R.drawable.whitetraslucent40);
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.group4manufacture, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

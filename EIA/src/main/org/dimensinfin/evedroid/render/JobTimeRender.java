//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.JobTimePart;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class JobTimeRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private ImageView	groupIcon	= null;
	private TextView	runTime		= null;
	private TextView	totalTime	= null;
	private TextView	count			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public JobTimeRender(final AbstractAndroidPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public JobTimePart getPart() {
		return (JobTimePart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		groupIcon = (ImageView) _convertView.findViewById(R.id.groupIcon);
		runTime = (TextView) _convertView.findViewById(R.id.runTime);
		totalTime = (TextView) _convertView.findViewById(R.id.totalTime);
		count = (TextView) _convertView.findViewById(R.id.count);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		runTime.setText(generateTimeString(getPart().getRunTime() * ModelWideConstants.SECONDS1));
		totalTime.setText(generateTimeString(getPart().getRunTime() * getPart().getRuns() * ModelWideConstants.SECONDS1));
		count.setText(qtyFormatter.format(getPart().getRuns()));
		if (getPart().getJobActivity() == ModelWideConstants.activities.MANUFACTURING)
			groupIcon.setImageResource(R.drawable.manufacturing);
		if (getPart().getJobActivity() == ModelWideConstants.activities.INVENTION)
			groupIcon.setImageResource(R.drawable.invention);
		_convertView.setBackgroundResource(R.drawable.whitetraslucent40);
		totalTime.setVisibility(View.VISIBLE);
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.time4job, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

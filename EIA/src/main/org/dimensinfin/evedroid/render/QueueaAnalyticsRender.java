//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.QueueAnalyticsPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class QueueaAnalyticsRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private TextView	manufactureQueues	= null;
	private TextView	inventionQueues		= null;
	private ViewGroup	manufactureBlock	= null;
	private ViewGroup	inventionBlock		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public QueueaAnalyticsRender(final QueueAnalyticsPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public QueueAnalyticsPart getPart() {
		return (QueueAnalyticsPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		manufactureQueues = (TextView) _convertView.findViewById(R.id.manufactureQueues);
		inventionQueues = (TextView) _convertView.findViewById(R.id.inventionQueues);
		manufactureBlock = (ViewGroup) _convertView.findViewById(R.id.manufactureBlock);
		inventionBlock = (ViewGroup) _convertView.findViewById(R.id.inventionBlock);
		manufactureBlock.setVisibility(View.GONE);
		inventionBlock.setVisibility(View.GONE);
	}

	public void updateContent() {
		super.updateContent();
		// Get pilot reference values.
		int maxManufacture = getPart().getMaxManufacture();
		int maxInvention = getPart().getMaxInvention();
		// Select the block to show depending on the page activity (Manufacture/Invention)
		if (getPart().getJobActivity() == ModelWideConstants.activities.MANUFACTURING) {
			manufactureBlock.setVisibility(View.VISIBLE);
			manufactureQueues.setText(getPart().getManufacture() + " / " + maxManufacture);
		}
		if (getPart().getJobActivity() == ModelWideConstants.activities.INVENTION) {
			inventionBlock.setVisibility(View.VISIBLE);
			inventionQueues.setText(getPart().getInvention() + " / " + maxInvention);
		}
		if ((maxManufacture == getPart().getManufacture()) && (maxInvention == getPart().getInvention()))
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.redtraslucent40));
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.group4queueanalytics, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

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
import org.dimensinfin.evedroid.part.JobPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class JobHolder extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger					= Logger.getLogger("JobHolder");

	// - F I E L D - S E C T I O N ............................................................................
	private TextView			runCount				= null;
	private TextView			slotsCount			= null;
	private TextView			jobModule				= null;
	private TextView			jobStartTime		= null;
	private TextView			jobDuration			= null;
	private TextView			jobEndTime			= null;
	private TextView			jobProfit				= null;

	private TextView			jobLabel				= null;
	private TextView			runCountLabel		= null;
	private TextView			slotsCountLabel	= null;
	private TextView			startTimeLabel	= null;
	private TextView			durationLabel		= null;
	private TextView			endTimeLabel		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public JobHolder(final JobPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public JobPart getPart() {
		return (JobPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		//		super.initializeViews();
		//		_itemIcon = (ImageView) _convertView.findViewById(R.id.itemIcon);
		runCount = (TextView) _convertView.findViewById(R.id.runCount);
		slotsCount = (TextView) _convertView.findViewById(R.id.slotsCount);
		jobModule = (TextView) _convertView.findViewById(R.id.jobModule);
		jobStartTime = (TextView) _convertView.findViewById(R.id.jobStartTime);
		jobDuration = (TextView) _convertView.findViewById(R.id.jobDuration);
		jobEndTime = (TextView) _convertView.findViewById(R.id.jobEndTime);
		jobProfit = (TextView) _convertView.findViewById(R.id.jobProfit);

		jobLabel = (TextView) _convertView.findViewById(R.id.jobLabel);
		runCountLabel = (TextView) _convertView.findViewById(R.id.runCountLabel);
		slotsCountLabel = (TextView) _convertView.findViewById(R.id.slotsCountLabel);
		startTimeLabel = (TextView) _convertView.findViewById(R.id.startTimeLabel);
		durationLabel = (TextView) _convertView.findViewById(R.id.durationLabel);
		endTimeLabel = (TextView) _convertView.findViewById(R.id.endTimeLabel);

		jobModule.setTypeface(daysFace);
		runCount.setTypeface(daysFace);
		slotsCount.setTypeface(daysFace);
	}

	public void updateContent() {
		runCount.setText(getPart().get_runCount());
		slotsCount.setText(getPart().get_slotsCount());
		jobModule.setText(getPart().get_jobModule());
		jobStartTime.setText(getPart().get_jobStartTime());
		jobDuration.setText(getPart().get_jobDuration());
		jobEndTime.setText(getPart().get_jobEndTime());
		jobProfit.setText(getPart().get_jobProfit());

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getItemID());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.job_4list, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

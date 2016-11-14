//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.JobPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class JobExtendedRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger	logger					= Logger.getLogger("JobHolder");

	// - F I E L D - S E C T I O N ............................................................................
	private ImageView	stateIcon			= null;
	private ImageView	activityIcon	= null;
	private TextView	blueprintName	= null;
	private TextView	jobLocation		= null;
	private TextView	jobQtys				= null;
	private TextView	jobCost				= null;
	private TextView	jobDuration		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public JobExtendedRender(final JobPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public JobPart getPart() {
		return (JobPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		stateIcon = (ImageView) _convertView.findViewById(R.id.stateIcon);
		activityIcon = (ImageView) _convertView.findViewById(R.id.activityIcon);
		blueprintName = (TextView) _convertView.findViewById(R.id.blueprintName);
		jobLocation = (TextView) _convertView.findViewById(R.id.jobLocation);
		jobQtys = (TextView) _convertView.findViewById(R.id.jobQtys);
		jobCost = (TextView) _convertView.findViewById(R.id.jobCost);
		jobDuration = (TextView) _convertView.findViewById(R.id.jobDuration);
	}

	public void updateContent() {
		super.updateContent();
		blueprintName.setText(getPart().getName());
		jobLocation.setText(getPart().get_jobLocation());
		jobQtys.setText(getPart().getRuns());
		//		jobCost.setText(getPart().getJobCost());
		jobDuration.setText(getPart().get_jobDuration());

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getBlueprintTypeID());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.job4list, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

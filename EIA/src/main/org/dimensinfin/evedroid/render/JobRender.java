//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.JobPart;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class JobRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private TextView	stateIcon					= null;
	private ImageView	activityIcon			= null;
	private TextView	blueprintName			= null;
	private TextView	jobLocation				= null;
	private TextView	jobQtys						= null;
	private TextView	jobCost						= null;
	private TextView	jobStart					= null;
	private TextView	jobEnd						= null;
	private TextView	jobDuration				= null;
	private TextView	jobDurationLabel	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public JobRender(final JobPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public JobPart getPart() {
		return (JobPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		stateIcon = (TextView) _convertView.findViewById(R.id.stateIcon);
		activityIcon = (ImageView) _convertView.findViewById(R.id.activityIcon);
		blueprintName = (TextView) _convertView.findViewById(R.id.blueprintName);
		jobLocation = (TextView) _convertView.findViewById(R.id.jobLocation);
		jobQtys = (TextView) _convertView.findViewById(R.id.jobQtys);
		jobCost = (TextView) _convertView.findViewById(R.id.jobCost);
		jobStart = (TextView) _convertView.findViewById(R.id.jobStart);
		jobEnd = (TextView) _convertView.findViewById(R.id.jobEnd);
		jobDuration = (TextView) _convertView.findViewById(R.id.jobDuration);
		jobDurationLabel = (TextView) _convertView.findViewById(R.id.jobDurationLabel);
	}

	@SuppressLint("ResourceAsColor")
	public void updateContent() {
		super.updateContent();
		int activity = getPart().getJobActivity();
		// Set the activity icon.
		if (activity == ModelWideConstants.activities.MANUFACTURING)
			activityIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.manufacturing));
		if (activity == ModelWideConstants.activities.INVENTION)
			activityIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.invention));
		if (activity == ModelWideConstants.activities.RESEARCH_EFI)
			activityIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.researchmaterial));
		if (activity == ModelWideConstants.activities.RESEARCH_TIME)
			activityIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.researchtime));
		if (activity == ModelWideConstants.activities.COPYING)
			activityIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.copying));

		// Draw the common part to all states.
		blueprintName.setText(getPart().getName());
		EveLocation location = getPart().getJobLocation();
		StringBuffer htmlLocation = new StringBuffer();
		double security = location.getSecurityValue();
		// Append the Region -> Constellation
		htmlLocation.append(location.getRegion()).append(AppWideConstants.FLOW_ARROW_RIGHT)
				.append(location.getConstellation()).append(AppWideConstants.FLOW_ARROW_RIGHT);
		htmlLocation.append(generateSecurityColor(security, securityFormatter.format(location.getSecurityValue())));
		htmlLocation.append(" ").append(location.getStation());
		jobLocation.setText(Html.fromHtml(htmlLocation.toString()));
		jobQtys.setText(getPart().getRuns() + " runs");
		double jCost = getPart().getJobCost();
		if (jCost < 1.0)
			jobCost.setText("- ISK");
		else
			jobCost.setText(generatePriceString(jCost, false, true));
		jobStart.setText(generateDateString(getPart().getStartDate().getTime()));
		jobEnd.setText(generateDateString(getPart().getEndDate().getTime()));
		_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bluetraslucent80));

		int status = getPart().getJobStatus();
		// Run different presentation depending on the status of the jobs.
		// RUNNING JOBS
		if (status == ModelWideConstants.jobstatus.ACTIVE) {
			stateIcon.setTextColor(getContext().getResources().getColor(R.color.blueBrightLine));
			DateTime now = new DateTime(DateTimeZone.UTC);
			final Instant endinstant = new Instant(getPart().getEndDate());
			long togomillis = endinstant.getMillis() - now.getMillis();
			if (togomillis > 0) {
				CountDownTimer timer = new CountDownTimer(togomillis, ModelWideConstants.MINUTES1) {
					public void onFinish() {
						jobDuration.setText(generateTimeString(getPart().getTimeInSeconds() * 1000));
						jobDurationLabel.setText("DURATION");
					}

					public void onTick(final long millisUntilFinished) {
						jobDuration.setText(generateDurationString(millisUntilFinished, true));
						jobDuration.invalidate();
						_convertView.invalidate();
					}
				}.start();
				jobDurationLabel.setText("TIME LEFT");
				_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.greentraslucent40));
			} else {
				jobDuration.setText(generateTimeString(getPart().getTimeInSeconds() * 1000));
				jobDurationLabel.setText("DURATION");
				_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bluetraslucent60));
			}
		}
		if (status == ModelWideConstants.jobstatus.SCHEDULED) {
			stateIcon.setTextColor(getContext().getResources().getColor(R.color.redPrice));
			if (getPart().canBeLaunched()) {
				_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.greentraslucent20));
				jobStart.setText("CAN BE STARTED");
				jobStart.setTextColor(getContext().getResources().getColor(R.color.greenPrice));
				// Make the text blink.
				CountDownTimer timer = new CountDownTimer(Integer.MAX_VALUE, ModelWideConstants.SECONDS1 / 5) {
					int	counter	= 0;

					public void onFinish() {
						jobStart.setTextColor(getContext().getResources().getColor(R.color.greenPrice));
					}

					public void onTick(final long millisUntilFinished) {
						jobStart.setTextColor(Color.rgb(0, 50 + (20 * counter), 0));
						counter++;
						if (counter > 10) counter = 0;
						jobStart.invalidate();
						_convertView.invalidate();
					}
				}.start();
			} else {
				jobStart.setText(generateDateString(getPart().getFirstStartTime().getMillis()));
				jobStart.setTextColor(getContext().getResources().getColor(R.color.redPrice));
				_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.redtraslucent40));
			}
		}
		if (status == ModelWideConstants.jobstatus.READY) {
			stateIcon.setTextColor(getContext().getResources().getColor(R.color.greenPrice));
			jobDuration.setText(generateTimeString(getPart().getTimeInSeconds() * 1000));
			jobDurationLabel.setText("DURATION");
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bluetraslucent60));
		}
		if (status == ModelWideConstants.jobstatus.DELIVERED) {
			stateIcon.setTextColor(getContext().getResources().getColor(R.color.whitePrice));
			jobDuration.setVisibility(View.INVISIBLE);
			jobDurationLabel.setVisibility(View.INVISIBLE);
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.graytraslucent80));
		}
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

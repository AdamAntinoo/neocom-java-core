//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.QueuePart;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class QueueRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private ImageView		activityIcon			= null;
	private TextView		queueLabel				= null;
	private ProgressBar	queueProgressBar	= null;
	private TextView		finishTime				= null;
	private TextView		time2Go						= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public QueueRender(final QueuePart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public QueuePart getPart() {
		return (QueuePart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		activityIcon = (ImageView) _convertView.findViewById(R.id.activityIcon);
		queueLabel = (TextView) _convertView.findViewById(R.id.queueLabel);
		queueProgressBar = (ProgressBar) _convertView.findViewById(R.id.queueProgressBar);
		queueProgressBar.setMax(48);
		finishTime = (TextView) _convertView.findViewById(R.id.finishTime);
		time2Go = (TextView) _convertView.findViewById(R.id.time2Go);
	}

	public void updateContent() {
		super.updateContent();
		queueLabel.setText(queueIndexFormatter.format(getPart().getNumber()));
		DateTime now = new DateTime(DateTimeZone.UTC);
		//		final Instant startinstant = new Instant(getPart().getStartDate());
		final Instant endinstant = new Instant(getPart().getEndDate());
		long togomillis = endinstant.getMillis() - now.getMillis();
		if (togomillis < 1) {
			finishTime.setVisibility(View.GONE);
			time2Go.setText(timePointFormatter.print(endinstant));
			queueProgressBar.setProgress(0);
		} else {
			int hoursPending = Double.valueOf(togomillis / ModelWideConstants.HOURS1).intValue();
			finishTime.setText(jobTimeFormatter.print(endinstant));
			CountDownTimer timer = new CountDownTimer(togomillis, ModelWideConstants.MINUTES1) {
				public void onFinish() {
					finishTime.setVisibility(View.GONE);
					time2Go.setText(timePointFormatter.print(endinstant));
				}

				public void onTick(final long millisUntilFinished) {
					time2Go.setText(generateDurationString(millisUntilFinished, false));
					time2Go.invalidate();
					_convertView.invalidate();
				}
			}.start();
			queueProgressBar.setProgress(hoursPending);
		}
		int activity = getPart().getJobActivity();
		if (activity == ModelWideConstants.activities.MANUFACTURING)
			activityIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.manufacturing));
		if (activity == ModelWideConstants.activities.INVENTION)
			activityIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.invention));
		if (activity == ModelWideConstants.activities.RESEARCH_EFI)
			activityIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.researchmaterial));
		if (activity == ModelWideConstants.activities.RESEARCH_TIME)
			activityIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.researchtime));
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.queue4header, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

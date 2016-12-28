//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.FittingPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class FittingListRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	public TextView	fittingName			= null;
	public TextView	fittingHull			= null;
	public TextView	fittingRuns			= null;
	public TextView	hullSlotsCount	= null;
	//	public TextView	blueprintMETE			= null;
	//	public TextView	stackRuns					= null;
	//	public TextView	jobs							= null;
	//	public TextView	budget						= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingListRender(final FittingPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public FittingPart getPart() {
		return (FittingPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		fittingName = (TextView) _convertView.findViewById(R.id.fittingName);
		fittingHull = (TextView) _convertView.findViewById(R.id.fittingHull);
		fittingRuns = (TextView) _convertView.findViewById(R.id.fittingRuns);
		hullSlotsCount = (TextView) _convertView.findViewById(R.id.hullSlotsCount);
		//		blueprintMETE = (TextView) _convertView.findViewById(R.id.blueprintMETE);
		//		stackRuns = (TextView) _convertView.findViewById(R.id.stackRuns);
		//		jobs = (TextView) _convertView.findViewById(R.id.jobs);
		//		budget = (TextView) _convertView.findViewById(R.id.budget);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		fittingName.setText(this.getPart().getName());
		//		if (AppWideConstants.DEVELOPMENT)
		//			fittingName.setText(getPart().getName() + " [#" + getPart().getTypeID() + "]");
		fittingHull.setText(this.getPart().getHullName() + "[" + this.getPart().getHullGroup() + "]");
		fittingRuns.setText(this.getPart().get_fittingRunsCount());
		hullSlotsCount.setText("[" + this.getPart().getSlotsInfo() + "]");
		//		blueprintMETE.setText(getPart().get_blueprintMETE());
		//		// Start to show calculated information.
		//		stackRuns.setText(getPart().get_stackRuns()); // The number of available runs / manufacturable runs.
		//		jobs.setText(qtyFormatter.format(getPart().getJobs()) + " jobs");
		//		// Check the budget value to set the right presentation.
		//		double budgetValue = getPart().getBudget();
		//		if (budgetValue > 0.0) {
		//			budget.setTextColor(getContext().getResources().getColor(R.color.redPrice));
		//			budget.setText(generatePriceString(budgetValue, true, true));
		//		} else
		//			budget.setText("- ISK");

		this.loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), this.getPart().getHullTypeID());
		_convertView.setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.blacktraslucent40));
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) this.getContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.fitting4header, null);
		_convertView.setTag(this);
	}
}
// - UNUSED CODE ............................................................................................

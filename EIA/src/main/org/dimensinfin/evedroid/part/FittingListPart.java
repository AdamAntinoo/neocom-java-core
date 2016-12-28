//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.part;

import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.evedroid.activity.FittingActivity;
import org.dimensinfin.evedroid.activity.FittingListActivity.EFittingVariants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.interfaces.INamedPart;
import org.dimensinfin.evedroid.model.Fitting;
import org.dimensinfin.evedroid.render.FittingListRender;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class FittingListPart extends EveAbstractPart implements INamedPart, OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 1956908168853667475L;
	private static Logger			logger						= Logger.getLogger("FittingPart");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingListPart(final Fitting model) {
		super(model);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//	public CharSequence get_fittingRunsCount() {
	//		return Integer.valueOf(this.getCastedModel().getRuns()).toString();
	//	}

	public String getHullGroup() {
		return this.getCastedModel().getHull().getItem().getGroupName();
	}

	public String getHullName() {
		return this.getCastedModel().getHull().getItem().getName();
	}

	public int getHullTypeID() {
		return this.getCastedModel().getHull().getItem().getTypeID();
	}

	@Override
	public long getModelID() {
		return this.getCastedModel().getName().hashCode();
	}

	public String getName() {
		return this.getCastedModel().getName();
	}

	//	public int getRuns() {
	//		return this.getCastedModel().getRuns();
	//	}

	//	public String getSlotsInfo() {
	//		return "8 / 3 / 6";
	//	}

	//	public boolean onContextItemSelected(final MenuItem item) {
	//		return false;
	//	}

	//	/**
	//	 * Creates the contextual menu for the selected blueprint. The menu depends on multiple factors like if the
	//	 * blueprint is rendered on the header or on other listings like the assets or the industry listings.
	//	 */
	//	// REFACTOR Removed during the DataSource integration
	//	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
	//		FittingListPart.logger.info(">> [FittingPart.onCreateContextMenu]");
	//		// Check the renderer to see if I have to show the dialog or not. Only valid if in a header.
	//		if (this.getRenderMode() == AppWideConstants.rendermodes.RENDER_FITTINGHEADER) {
	//			final FittingRunsDialog dialog = new FittingRunsDialog();
	//			dialog.setFittingPart(this);
	//			//			final BlueprintPart self = this;
	//			// PagerFragment frag = (PagerFragment) getFragment();
	//			// dialog.setFragment(frag);
	//			dialog.setDialogCallback(new ADialogCallback() {
	//				@Override
	//				public void onDialogNegativeClick(final DialogFragment dialog) {
	//				}
	//
	//				@Override
	//				public void onDialogPositiveClick(final DialogFragment dialog) {
	//					// Get the number of runs selected by the user.
	//					final int runs = ((FittingRunsDialog) dialog).getRuns();
	//					// Update the model with the new runs value
	//					FittingListPart.this.getCastedModel().setRuns(runs);
	//					FittingListPart.this.invalidate();
	//					FittingListPart.this.firePropertyChange(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE, this, this);
	//				}
	//			});
	//			//
	//			this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	//			dialog.show(this.getActivity().getFragmentManager(), "JobRunsDialog");
	//		}
	//		FittingListPart.logger.info("<< [FittingPart.onCreateContextMenu]");
	//	}

	/**
	 * Returns the ISK formatted string that represents the full fit cost. This is calculated by the sum of all
	 * the fitting contents using the market sell prices.
	 * 
	 * @return string with the price formatted and with the ISK suffix added.
	 */
	public String getTransformedFittingCost() {
		return this.generatePriceString(this.getCastedModel().getFittingCost(), true, true);
	}

	public void onClick(final View arg0) {
		FittingListPart.logger.info(">> [FittingListPart.onClick]");
		Intent intent = new Intent(this.getActivity(), FittingActivity.class);
		intent.putExtra(EFittingVariants.FITTING_MANUFACTURE.name(), this.getCastedModel().getName());
		this.getActivity().startActivity(intent);
		FittingListPart.logger.info("<< [FittingListPart.onClick]");
	}

	@Override
	protected AbstractHolder selectHolder() {
		return new FittingListRender(this, _activity);
	}

	private Fitting getCastedModel() {
		return (Fitting) this.getModel();
	}

}

// - UNUSED CODE ............................................................................................

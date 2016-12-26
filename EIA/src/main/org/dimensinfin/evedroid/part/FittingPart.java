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

import org.dimensinfin.android.mvc.activity.ADialogCallback;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.dialog.FittingRunsDialog;
import org.dimensinfin.evedroid.interfaces.INamedPart;
import org.dimensinfin.evedroid.model.Fitting;
import org.dimensinfin.evedroid.render.FittingHeaderRender;

import android.app.DialogFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

// - CLASS IMPLEMENTATION ...................................................................................
public class FittingPart extends EveAbstractPart implements INamedPart, IMenuActionTarget {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 1956908168853667475L;
	private static Logger			logger						= Logger.getLogger("FittingPart");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingPart(final Fitting model) {
		super(model);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public CharSequence get_fittingRunsCount() {
		return Integer.valueOf(this.getCastedModel().getRuns()).toString();
	}

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

	public int getRuns() {
		return this.getCastedModel().getRuns();
	}

	public String getSlotsInfo() {
		return "8 / 3 / 6";
	}

	public boolean onContextItemSelected(final MenuItem item) {
		return false;
	}

	/**
	 * Creates the contextual menu for the selected blueprint. The menu depends on multiple factors like if the
	 * blueprint is rendered on the header or on other listings like the assets or the industry listings.
	 */
	// REFACTOR Removed during the DataSource integration
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		FittingPart.logger.info(">> [FittingPart.onCreateContextMenu]");
		// Check the renderer to see if I have to show the dialog or not. Only valid if in a header.
		if (this.getRenderMode() == AppWideConstants.rendermodes.RENDER_FITTINGHEADER) {
			final FittingRunsDialog dialog = new FittingRunsDialog();
			dialog.setFittingPart(this);
			//			final BlueprintPart self = this;
			// PagerFragment frag = (PagerFragment) getFragment();
			// dialog.setFragment(frag);
			dialog.setDialogCallback(new ADialogCallback() {
				@Override
				public void onDialogNegativeClick(final DialogFragment dialog) {
				}

				@Override
				public void onDialogPositiveClick(final DialogFragment dialog) {
					// Get the number of runs selected by the user.
					final int runs = ((FittingRunsDialog) dialog).getRuns();
					// Update the model with the new runs value
					FittingPart.this.getCastedModel().setRuns(runs);
					FittingPart.this.invalidate();
					FittingPart.this.firePropertyChange(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE, this, this);
				}
			});
			//
			this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			dialog.show(this.getActivity().getFragmentManager(), "JobRunsDialog");
		}
		FittingPart.logger.info("<< [FittingPart.onCreateContextMenu]");
	}

	@Override
	protected AbstractHolder selectHolder() {
		return new FittingHeaderRender(this, _activity);
	}

	private Fitting getCastedModel() {
		return (Fitting) this.getModel();
	}

}

// - UNUSED CODE ............................................................................................

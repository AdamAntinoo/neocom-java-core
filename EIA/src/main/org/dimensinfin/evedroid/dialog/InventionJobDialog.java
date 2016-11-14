//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.dialog;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.activity.ADialogCallback;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.industry.EJobClasses;
import org.dimensinfin.evedroid.industry.IJobProcess;
import org.dimensinfin.evedroid.industry.JobManager;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.model.Blueprint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Dialog has access to the blueprint part with all the UI information. On
 * creation of the Dialog UI I will fill up the fields with that part data.<br>
 * The entry point to make the Dialog UI is the method
 * <code>onCreateDialog</code> that composes the dialog before launching it for
 * user interaction.
 */
// - CLASS IMPLEMENTATION
// ...................................................................................
public class InventionJobDialog extends DialogFragment {
	// - S T A T I C - S E C T I O N
	// ..........................................................................

	// - F I E L D - S E C T I O N
	// ............................................................................
	private ADialogCallback _dialogCallback = null;
	private View _dialogContainer = null;
	private Blueprint _blueprint = null;

	// - U I F I E L D S
	private ImageView jobTypeIcon = null;
	/**
	 * The number of runs selected by the user. There is a top limit that is the
	 * less of the manufacturable count or the number of runs available on the
	 * blueprints.
	 */
	private EditText _runsCount = null;
	/**
	 * The resulting number of jobs. Calculated from the number of runs selected
	 * by the user and the number of runs available on each blueprint.
	 */
	private TextView _jobCount = null;
	private TextView _itemName = null;
	private TextView _blueprintCount = null;
	private TextView _blueprintRuns = null;
	private TextView _blueprintMETE = null;
	private TextView _jobDuration = null;
	private TextView _errorMessage = null;

	// - W O R K V A R I A B L E S
	private int _runs = 0;
	private int _maxruncount = 0;
	private int _jobs = 0;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	public int getRuns() {
		return this._runs;
	}

	/**
	 * On dialog creation we identify the graphical UI elements and then set
	 * their initial values. The number of jobs is set by default to 1 because
	 * we are not going to allow unlimited invention jobs. So the initial is 6
	 * and the maximun depends on the runs left on the blueprint. Because the
	 * blueprint is a prototype and maybe not a real one, this max number is the
	 * default of 100 copies.
	 */
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		// Create the dialog and all its elements.
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Inflate and set the layout for the dialog
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		this._dialogContainer = inflater.inflate(R.layout.dialog_jobruns, null);

		// Check that the blueprint is initialized. If not then exit.
		if (null == this._blueprint)
			return null;
		initializeViews();
		setupContents();
		// Calculate the initial values for calculated data.
		final IJobProcess process = JobManager.generateJobProcess(EVEDroidApp.getAppStore().getPilot(), this._blueprint,
				EJobClasses.INVENTION);
		final int jobDuration = process.getCycleDuration();
		final int bpccount = 1;
		final int runs = 6;
		// final double intermediate = (1.0 * this._blueprint.getMaxRuns()) /
		// (1.0 * runs);
		// Set up interface internal values.
		this._maxruncount = 100;
		this._runs = 6;
		this._jobs = 1;
		// Update the interface.
		this._runsCount.setText(Integer.valueOf(this._runs).toString());
		this._jobCount.setText(Integer.valueOf(this._jobs).toString());
		this._jobDuration.setText(EveAbstractPart.generateTimeString(jobDuration * this._runs));

		// Add event hoot to the editable text field to check validity.
		this._runsCount.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(final Editable newValue) {
				int currentruns = 0;
				try {
					currentruns = Integer.parseInt(InventionJobDialog.this._runsCount.getEditableText().toString());
				} catch (final RuntimeException rtex) {
					currentruns = 0;
				}
				if (currentruns > InventionJobDialog.this._maxruncount) {
					InventionJobDialog.this._runsCount
							.setText(Integer.valueOf(InventionJobDialog.this._maxruncount).toString());
				} else {
					// final double intermediate = (1.0 * currentruns) / (1.0 *
					// runs);
					// InventionJobDialog.this._jobs =
					// Math.min(Double.valueOf(Math.ceil(intermediate)).intValue(),
					// bpccount);
					InventionJobDialog.this._runs = currentruns;
					// InventionJobDialog.this._jobCount.setText(InventionJobDialog.this._blueprint
					// .get_jobsParameter(InventionJobDialog.this._jobs));
					InventionJobDialog.this._jobDuration
							.setText(EveAbstractPart.generateTimeString(jobDuration * Math.min(currentruns, runs)));
				}
			}

			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
			}
		});
		final InventionJobDialog self = this;
		// Add action buttons
		if (null != this._dialogCallback) {
			builder.setView(this._dialogContainer)
					.setPositiveButton(R.string.setJobRuns, new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, final int id) {
							try {
								if (null != InventionJobDialog.this._runsCount) {
									InventionJobDialog.this._runs = Integer
											.parseInt(InventionJobDialog.this._runsCount.getEditableText().toString());
									Toast.makeText(getActivity(), "Selected Runs: " + InventionJobDialog.this._runs,
											Toast.LENGTH_LONG);
									InventionJobDialog.this._dialogCallback.onDialogPositiveClick(self);
								}
							} catch (final RuntimeException rtex) {
							}
						}
					}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, final int id) {
							InventionJobDialog.this.getDialog().cancel();
						}
					});
		}
		return builder.create();
	}

	public void setBlueprint(final Resource bp) {
		// REFACTOR Implemente the assignment of the resource as a blueprint.
		// What is the received resource?.
		this._blueprint = new Blueprint(bp.getTypeID());
	}

	public void setDialogCallback(final ADialogCallback callback) {
		if (null != callback) {
			this._dialogCallback = callback;
		}
	}

	// public void setTaskPart(final TaskPart part) {
	// _taskPart = part;
	// int typeID = _taskPart.getItem().getTypeID();
	// int bpid = AppConnector.getDBConnector().searchBlueprint4Module(typeID);
	// _blueprintPart = new BlueprintPart(new Blueprint(bpid));
	// }

	public void setupContents() {
		// Set the text of the informative fields from the blueprint part.
		this._itemName.setText(this._blueprint.getName());
		this._blueprintCount.setText("1");
		this._blueprintRuns.setText("[" + this._blueprint.getRuns() + "]");
		this._blueprintMETE
				.setText(this._blueprint.getMaterialEfficiency() + " / " + this._blueprint.getTimeEfficiency());
	}

	private void initializeViews() {
		// Get access to the dialog UI components.
		this.jobTypeIcon = (ImageView) this._dialogContainer.findViewById(R.id.jobTypeIcon);
		this.jobTypeIcon.setImageResource(R.drawable.invention);
		this._runsCount = (EditText) this._dialogContainer.findViewById(R.id.runsCount);
		this._itemName = (TextView) this._dialogContainer.findViewById(R.id.itemName);
		this._blueprintCount = (TextView) this._dialogContainer.findViewById(R.id.blueprintCount);
		this._blueprintRuns = (TextView) this._dialogContainer.findViewById(R.id.blueprintRuns);
		this._blueprintMETE = (TextView) this._dialogContainer.findViewById(R.id.blueprintMETE);
		this._jobCount = (TextView) this._dialogContainer.findViewById(R.id.jobCount);
		this._jobDuration = (TextView) this._dialogContainer.findViewById(R.id.jobDuration);
		this._errorMessage = (TextView) this._dialogContainer.findViewById(R.id.errorMessage);
	}
}

// - UNUSED CODE
// ............................................................................................

//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.dialog;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.activity.ADialogCallback;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.evedroid.part.BlueprintPart;

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
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Dialog has access to the blueprint part with all the UI information. On creation of the Dialog UI I
 * will fill up the fields with that part data.<br>
 * The entry point to make the Dialog UI is the method <code>onCreateDialog</code> that composes the dialog
 * before launching it for user interaction.
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class JobRunsDialog extends DialogFragment {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private ADialogCallback	_dialogCallback		= null;
	private View						_dialogContainer	= null;
	private BlueprintPart		_blueprintPart		= null;

	/**
	 * The number of runs selected by the user. There is a top limit that is the less of the manufacturable
	 * count or the number of runs available on the blueprints.
	 */
	private EditText				_runsCount				= null;
	/**
	 * The resulting number of jobs. Calculated from the number of runs selected by the user and the number of
	 * runs available on each blueprint.
	 */
	private TextView				_jobCount					= null;
	private TextView				_itemName					= null;
	private TextView				_blueprintCount		= null;
	private TextView				_blueprintRuns		= null;
	private TextView				_blueprintMETE		= null;
	private TextView				_jobDuration			= null;
	private TextView				_errorMessage			= null;

	// - W O R K   V A R I A B L E S
	private int							_runs							= 0;
	private int							_maxruncount			= 0;
	private int							_jobs							= 0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getRuns() {
		return _runs;
	}

	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		// Create the dialog and all its elements.
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Inflate and set the layout for the dialog
		LayoutInflater inflater = getActivity().getLayoutInflater();
		_dialogContainer = inflater.inflate(R.layout.dialog_jobruns, null);

		initializeViews();
		setupContents();

		// Calculate some intermediate and variable data.
		final int bpccount = _blueprintPart.getBlueprintCount();
		final int runs = _blueprintPart.getRuns();
		double intermediate = (1.0 * _blueprintPart.getMaxRuns()) / (1.0 * runs);
		// Set up interface internal values.
		_maxruncount = _blueprintPart.getMaxRuns();
		_runs = _maxruncount;
		_jobs = Math.min(Double.valueOf(Math.ceil(intermediate)).intValue(), bpccount);
		// Update the interface.
		_runsCount.setText(Integer.valueOf(_runs).toString());
		_jobCount.setText(_blueprintPart.get_jobsParameter(_jobs));
		_jobDuration.setText(_blueprintPart.get_totalJobDuration(Math.min(_maxruncount, runs)));

		// Add event hoot to the editable text field to check validity.
		_runsCount.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(final Editable newValue) {
				int currentruns = 0;
				try {
					currentruns = Integer.parseInt(_runsCount.getEditableText().toString());
				} catch (RuntimeException rtex) {
					currentruns = 0;
				}
				if (currentruns > _maxruncount)
					_runsCount.setText(Integer.valueOf(_maxruncount).toString());
				else {
					double intermediate = (1.0 * currentruns) / (1.0 * runs);
					_jobs = Math.min(Double.valueOf(Math.ceil(intermediate)).intValue(), bpccount);
					_runs = currentruns;
					_jobCount.setText(_blueprintPart.get_jobsParameter(_jobs));
					_jobDuration.setText(_blueprintPart.get_totalJobDuration(Math.min(currentruns, runs)));
				}
			}

			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
			}
		});
		final JobRunsDialog self = this;
		// Add action buttons
		if (null != _dialogCallback) {
			builder.setView(_dialogContainer).setPositiveButton(R.string.setJobRuns, new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					try {
						if (null != _runsCount) {
							_runs = Integer.parseInt(_runsCount.getEditableText().toString());
							Toast.makeText(getActivity(), "Selected Runs: " + _runs, Toast.LENGTH_LONG);
							_dialogCallback.onDialogPositiveClick(self);
						}
					} catch (RuntimeException rtex) {
					}
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					JobRunsDialog.this.getDialog().cancel();
				}
			});
		}
		return builder.create();
	}

	public void setBlueprintPart(final BlueprintPart part) {
		_blueprintPart = part;
	}

	public void setDialogCallback(final ADialogCallback callback) {
		if (null != callback) _dialogCallback = callback;
	}

	//	public void setTaskPart(final TaskPart part) {
	//		_taskPart = part;
	//		int typeID = _taskPart.getItem().getTypeID();
	//		int bpid = AppConnector.getDBConnector().searchBlueprint4Module(typeID);
	//		_blueprintPart = new BlueprintPart(new Blueprint(bpid));
	//	}

	public void setupContents() {
		// Set the text of the informative fields from the blueprint part.
		_itemName.setText(_blueprintPart.getName());
		_blueprintCount.setText(_blueprintPart.get_blueprintCount());
		_blueprintRuns.setText("[" + _blueprintPart.getRuns() + "]");
		_blueprintMETE.setText(_blueprintPart.get_blueprintMETE());
	}

	private void initializeViews() {
		// Get access to the dialog UI components.
		_runsCount = (EditText) _dialogContainer.findViewById(R.id.runsCount);
		_itemName = (TextView) _dialogContainer.findViewById(R.id.itemName);
		_blueprintCount = (TextView) _dialogContainer.findViewById(R.id.blueprintCount);
		_blueprintRuns = (TextView) _dialogContainer.findViewById(R.id.blueprintRuns);
		_blueprintMETE = (TextView) _dialogContainer.findViewById(R.id.blueprintMETE);
		_jobCount = (TextView) _dialogContainer.findViewById(R.id.jobCount);
		_jobDuration = (TextView) _dialogContainer.findViewById(R.id.jobDuration);
		_errorMessage = (TextView) _dialogContainer.findViewById(R.id.errorMessage);
	}
}

// - UNUSED CODE ............................................................................................

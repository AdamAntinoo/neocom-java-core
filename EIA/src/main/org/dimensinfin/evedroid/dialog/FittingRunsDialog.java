//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.dialog;

import java.util.logging.Logger;

import org.dimensinfin.android.mvc.activity.ADialogCallback;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.part.FittingPart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// - CLASS IMPLEMENTATION ...................................................................................
public class FittingRunsDialog extends DialogFragment {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger		logger						= Logger.getLogger("FittingRunsDialog");

	// - F I E L D - S E C T I O N ............................................................................
	private View						_dialogContainer	= null;
	private ADialogCallback	_dialogCallback		= null;
	private FittingPart			_fittingPart			= null;
	/**
	 * The number of runs selected by the user. There is a top limit that is the less of the manufacturable
	 * count or the number of runs available on the blueprints.
	 */
	private EditText				_runsCount				= null;
	private TextView				_fittingName			= null;
	private TextView				_errorMessage			= null;

	// - W O R K   V A R I A B L E S
	private int							_runs							= 0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingRunsDialog() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getRuns() {
		return _runs;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		// Create the dialog and all its elements.
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Inflate and set the layout for the dialog
		LayoutInflater inflater = getActivity().getLayoutInflater();
		_dialogContainer = inflater.inflate(R.layout.dialog_fittingruns, null);

		initializeViews();
		setupContents();

		final FittingRunsDialog self = this;
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
					FittingRunsDialog.this.getDialog().cancel();
				}
			});
		}
		return builder.create();
	}

	public void setDialogCallback(final ADialogCallback callback) {
		if (null != callback) {
			_dialogCallback = callback;
		}
	}

	public void setFittingPart(final FittingPart part) {
		_fittingPart = part;
	}

	private void initializeViews() {
		// Get access to the dialog UI components.
		_fittingName = (TextView) _dialogContainer.findViewById(R.id.fittingName);
		_runsCount = (EditText) _dialogContainer.findViewById(R.id.runsCount);
		//		_blueprintCount = (TextView) _dialogContainer.findViewById(R.id.blueprintCount);
		//		_blueprintRuns = (TextView) _dialogContainer.findViewById(R.id.blueprintRuns);
		//		_blueprintMETE = (TextView) _dialogContainer.findViewById(R.id.blueprintMETE);
		//		_jobCount = (TextView) _dialogContainer.findViewById(R.id.jobCount);
		//		_jobDuration = (TextView) _dialogContainer.findViewById(R.id.jobDuration);
		_errorMessage = (TextView) _dialogContainer.findViewById(R.id.errorMessage);
	}

	private void setupContents() {
		// Set the text of the informative fields from the blueprint part.
		_fittingName.setText(_fittingPart.getName());
		_runs = _fittingPart.getRuns();
		_runsCount.setText(Integer.valueOf(_runs).toString());
		//		_blueprintCount.setText(_blueprintPart.get_blueprintCount());
		//		_blueprintRuns.setText("[" + _blueprintPart.getRuns() + "]");
		//		_blueprintMETE.setText(_blueprintPart.get_blueprintMETE());
	}
}

// - UNUSED CODE ............................................................................................

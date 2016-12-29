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
import org.dimensinfin.evedroid.industry.JobManager;
import org.dimensinfin.evedroid.part.BlueprintPart;
import org.dimensinfin.evedroid.part.TaskPart;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.industry.EJobClasses;
import org.dimensinfin.eveonline.neocom.industry.IJobProcess;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The Dialog has access to the blueprint part with all the UI information. On creation of the Dialog UI I
 * will fill up the fields with that part data.<br>
 * The entry point to make the Dialog UI is the method <code>onCreateDialog</code> that composes the dialog
 * before launching it for user interaction.
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class BuildDialog extends DialogFragment {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private TaskPart				_taskPart						= null;
	private BlueprintPart		_blueprintPart			= null;
	private ADialogCallback	_dialogCallback			= null;
	private View						_dialogContainer		= null;
	/**
	 * This dialog has no elements configurable by the user.
	 */
	private EditText				_runsCount					= null;
	private TextView				_runsBuild					= null;
	/**
	 * The resulting number of jobs. Calculated from the number of runs selected by the user and the number of
	 * runs available on each blueprint.
	 */
	private TextView				_jobCount						= null;
	private TextView				_itemName						= null;
	private TextView				_blueprintCount			= null;
	private TextView				_blueprintRuns			= null;
	private TextView				_blueprintMETE			= null;
	private TextView				_jobDuration				= null;
	private TextView				_errorMessage				= null;
	private TextView				_dialogTitle				= null;
	private TextView				_blueprintRunsLabel	= null;
	private TextView				_blueprintMETELabel	= null;

	// - W O R K   V A R I A B L E S
	private int							_runs								= 0;
	private NeoComBlueprint				_blueprint					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public BlueprintPart getBlueprint() {
		return _blueprintPart;
	}

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

		// Get the martching blueprint and obtain some key information from it.
		int typeID = _taskPart.getItem().getTypeID();
		int bpid = AppConnector.getDBConnector().searchBlueprint4Module(typeID);
		_blueprint = new NeoComBlueprint(bpid);
		IJobProcess buildProcess = JobManager.generateJobProcess(EVEDroidApp.getAppStore().getPilot(), _blueprint,
				EJobClasses.MANUFACTURE);
		_runsBuild.setText(_taskPart.getQuantity());
		_runs = _taskPart.getQuantity();
		_jobCount.setText("1");
		_jobDuration.setText(EveAbstractPart.generateTimeString(buildProcess.getCycleDuration() * _taskPart.getQuantity()
				* 1000));
		final BuildDialog self = this;
		// Add action buttons
		if (null != _dialogCallback) {
			builder.setView(_dialogContainer).setPositiveButton(R.string.setJobRuns, new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					try {
						if (null != _runsCount) {
							_runs = Integer.parseInt(_runsCount.getEditableText().toString());
							//							Toast.makeText(getActivity(), "Selected Runs: " + _runs, Toast.LENGTH_LONG);
							_dialogCallback.onDialogPositiveClick(self);
						}
					} catch (RuntimeException rtex) {
					}
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					BuildDialog.this.getDialog().cancel();
				}
			});
		}
		return builder.create();
	}

	public void setDialogCallback(final ADialogCallback callback) {
		if (null != callback) _dialogCallback = callback;
	}

	/**
	 * We receive a TaskPart and from it we create the required BlueprintPart that the dialog used to calculate
	 * and display the data
	 * 
	 * @param part
	 */
	public void setTaskPart(final TaskPart part) {
		_taskPart = part;
		int typeID = _taskPart.getItem().getTypeID();
		int bpid = AppConnector.getDBConnector().searchBlueprint4Module(typeID);
		_blueprintPart = new BlueprintPart(new NeoComBlueprint(bpid));
		// TODO We have to set the location that should be set to create the job
		// TODO Set the runs that the user has selected or that are set to the build job
	}

	public void setupContents() {
		// Set the text of the informative fields from the blueprint part.
		_itemName.setText(_taskPart.getItem().getName());
		_blueprintCount.setText(_taskPart.getQuantity());
		_dialogTitle.setText("BUILD LAUNCH ACTION");
	}

	private void initializeViews() {
		// Get access to the dialog UI components.
		_runsCount = (EditText) _dialogContainer.findViewById(R.id.runsCount);
		_runsCount.setVisibility(View.GONE);
		_runsBuild = (EditText) _dialogContainer.findViewById(R.id.runsBuild);
		_runsBuild.setVisibility(View.VISIBLE);

		_dialogTitle = (TextView) _dialogContainer.findViewById(R.id.dialogTitle);
		_itemName = (TextView) _dialogContainer.findViewById(R.id.itemName);
		_blueprintCount = (TextView) _dialogContainer.findViewById(R.id.blueprintCount);
		_blueprintRuns = (TextView) _dialogContainer.findViewById(R.id.blueprintRuns);
		_blueprintRuns.setVisibility(View.GONE);
		_blueprintMETE = (TextView) _dialogContainer.findViewById(R.id.blueprintMETE);
		_blueprintMETE.setVisibility(View.GONE);
		_blueprintRunsLabel = (TextView) _dialogContainer.findViewById(R.id.blueprintRunsLabel);
		_blueprintRunsLabel.setVisibility(View.GONE);
		_blueprintMETELabel = (TextView) _dialogContainer.findViewById(R.id.blueprintMETELabel);
		_blueprintMETELabel.setVisibility(View.GONE);
		_jobCount = (TextView) _dialogContainer.findViewById(R.id.jobCount);
		_jobDuration = (TextView) _dialogContainer.findViewById(R.id.jobDuration);
		_errorMessage = (TextView) _dialogContainer.findViewById(R.id.errorMessage);
	}
}

// - UNUSED CODE ............................................................................................

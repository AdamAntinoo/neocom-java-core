//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.dialog;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.activity.ADialogCallback;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.core.EveAbstractPart;
import org.dimensinfin.eveonline.neocom.industry.EJobClasses;
import org.dimensinfin.eveonline.neocom.industry.IJobProcess;
import org.dimensinfin.eveonline.neocom.industry.JobManager;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

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
 * The Dialog has access to the blueprint part with all the UI information. On creation of the Dialog UI I
 * will fill up the fields with that part data.<br>
 * The entry point to make the Dialog UI is the method <code>onCreateDialog</code> that composes the dialog
 * before launching it for user interaction.
 */
// - CLASS IMPLEMENTATION
// ...................................................................................
public class InventionJobDialog extends DialogFragment {
	// - S T A T I C - S E C T I O N
	// ..........................................................................

	// - F I E L D - S E C T I O N
	// ............................................................................
	private ADialogCallback	_dialogCallback		= null;
	private View						_dialogContainer	= null;
	private NeoComBlueprint	_blueprint				= null;

	// - U I F I E L D S
	private ImageView				jobTypeIcon				= null;
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

	// - W O R K V A R I A B L E S
	private int							_runs							= 0;
	private int							_maxruncount			= 0;
	private int							_jobs							= 0;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	public int getRuns() {
		return _runs;
	}

	/**
	 * On dialog creation we identify the graphical UI elements and then set their initial values. The number of
	 * jobs is set by default to 1 because we are not going to allow unlimited invention jobs. So the initial is
	 * 6 and the maximun depends on the runs left on the blueprint. Because the blueprint is a prototype and
	 * maybe not a real one, this max number is the default of 100 copies.
	 */
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		// Create the dialog and all its elements.
		final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		// Inflate and set the layout for the dialog
		final LayoutInflater inflater = this.getActivity().getLayoutInflater();
		_dialogContainer = inflater.inflate(R.layout.dialog_jobruns, null);

		// Check that the blueprint is initialized. If not then exit.
		if (null == _blueprint) return null;
		this.initializeViews();
		this.setupContents();
		// Calculate the initial values for calculated data.
		final IJobProcess process = JobManager.generateJobProcess(AppModelStore.getSingleton().getPilot(), _blueprint,
				EJobClasses.INVENTION);
		final int jobDuration = process.getCycleDuration();
		final int bpccount = 1;
		final int runs = 6;
		// final double intermediate = (1.0 * this._blueprint.getMaxRuns()) /
		// (1.0 * runs);
		// Set up interface internal values.
		_maxruncount = 100;
		_runs = 6;
		_jobs = 1;
		// Update the interface.
		_runsCount.setText(Integer.valueOf(_runs).toString());
		_jobCount.setText(Integer.valueOf(_jobs).toString());
		_jobDuration.setText(EveAbstractPart.generateTimeString(jobDuration * _runs));

		// Add event hoot to the editable text field to check validity.
		_runsCount.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(final Editable newValue) {
				int currentruns = 0;
				try {
					currentruns = Integer.parseInt(_runsCount.getEditableText().toString());
				} catch (final RuntimeException rtex) {
					currentruns = 0;
				}
				if (currentruns > _maxruncount) {
					_runsCount.setText(Integer.valueOf(_maxruncount).toString());
				} else {
					// final double intermediate = (1.0 * currentruns) / (1.0 *
					// runs);
					// InventionJobDialog.this._jobs =
					// Math.min(Double.valueOf(Math.ceil(intermediate)).intValue(),
					// bpccount);
					_runs = currentruns;
					// InventionJobDialog.this._jobCount.setText(InventionJobDialog.this._blueprint
					// .get_jobsParameter(InventionJobDialog.this._jobs));
					_jobDuration.setText(EveAbstractPart.generateTimeString(jobDuration * Math.min(currentruns, runs)));
				}
			}

			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
			}
		});
		final InventionJobDialog self = this;
		// Add action buttons
		if (null != _dialogCallback) {
			builder.setView(_dialogContainer).setPositiveButton(R.string.setJobRuns, new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					try {
						if (null != _runsCount) {
							_runs = Integer.parseInt(_runsCount.getEditableText().toString());
							Toast.makeText(InventionJobDialog.this.getActivity(), "Selected Runs: " + _runs, Toast.LENGTH_LONG);
							_dialogCallback.onDialogPositiveClick(self);
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
		_blueprint = new NeoComBlueprint(bp.getTypeID());
	}

	public void setDialogCallback(final ADialogCallback callback) {
		if (null != callback) {
			_dialogCallback = callback;
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
		_itemName.setText(_blueprint.getName());
		_blueprintCount.setText("1");
		_blueprintRuns.setText("[" + _blueprint.getRuns() + "]");
		_blueprintMETE.setText(_blueprint.getMaterialEfficiency() + " / " + _blueprint.getTimeEfficiency());
	}

	private void initializeViews() {
		// Get access to the dialog UI components.
		jobTypeIcon = (ImageView) _dialogContainer.findViewById(R.id.jobTypeIcon);
		jobTypeIcon.setImageResource(R.drawable.invention);
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

// - UNUSED CODE
// ............................................................................................

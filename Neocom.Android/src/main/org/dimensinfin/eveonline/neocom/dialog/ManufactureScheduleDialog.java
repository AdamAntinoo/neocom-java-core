//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.eveonline.neocom.dialog;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.activity.IDialogListener;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.eveonline.neocom.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * The activity that creates an instance of this dialog fragment must implement this interface in order to
 * receive event callbacks. Each method passes the DialogFragment in case the host needs to query it.
 */
public class ManufactureScheduleDialog extends DialogFragment {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger				logger						= Logger.getLogger("ManufactureScheduleDialog");

	// - F I E L D - S E C T I O N ............................................................................
	private IDialogListener			mListener					= null;
	private AbstractAndroidPart	_part							= null;
	private View								_dialogContainer	= null;
	private EditText						_containerName		= null;
	private String							_name							= "NO NAME";

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getName() {
		return _name;
	}

	public AbstractAndroidPart getPart() {
		return _part;
	}

	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (IDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement IDialogListener");
		}
	}

	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater and instantiate the layout.
		LayoutInflater inflater = getActivity().getLayoutInflater();
		_dialogContainer = inflater.inflate(R.layout.dialog_manufactureschedule, null);
		_containerName = (EditText) _dialogContainer.findViewById(R.id.containerName);

		final ManufactureScheduleDialog self = this;
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(_dialogContainer)
		// Add action buttons
				.setPositiveButton(R.string.setContainerName, new DialogInterface.OnClickListener() {

					public void onClick(final DialogInterface dialog, final int id) {
						if (null != mListener) {
							if (null != _containerName) {
								_name = _containerName.getEditableText().toString();
								mListener.onDialogPositiveClick(self);
							}
						}
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						ManufactureScheduleDialog.this.getDialog().cancel();
					}
				});
		return builder.create();
	}

	public void setPart(final AbstractAndroidPart part) {
		_part = part;
	}
}

// - UNUSED CODE ............................................................................................

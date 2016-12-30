//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.dialog;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.activity.ADialogCallback;
import org.dimensinfin.android.mvc.activity.PagerFragment;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.part.TaskPart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
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
public class BuyQtyDialog extends DialogFragment {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	// - D I A L O G   V A R I A B L E S
	private ADialogCallback	_dialogCallback		= null;
	private View						_dialogContainer	= null;
	private int							_quantity					= 0;

	/** The core element selected by the user and base of the information to be displayed on the dialog. */
	private TaskPart				_part							= null;
//	private Fragment				_fragment					= null;
//

	// - D I A L O G   F I E L D S
	private TextView				_itemName					= null;
	private EditText				_qtyRequested			= null;
	private TextView				_price						= null;
	private TextView				_budget						= null;
	private final TextView	_errorMessage			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public TaskPart getPart() {
		return _part;
	}

	public int getQuantity() {
		return _quantity;
	}

	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		// Create the dialog and all its elements.
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Inflate and set the layout for the dialog
		LayoutInflater inflater = getActivity().getLayoutInflater();
		_dialogContainer = inflater.inflate(R.layout.dialog_buyqty, null);

		// Get access to the dialog UI components.
		_itemName = (TextView) _dialogContainer.findViewById(R.id.itemName);
		_qtyRequested = (EditText) _dialogContainer.findViewById(R.id.qtyRequested);
		_price = (TextView) _dialogContainer.findViewById(R.id.price);
		_budget = (TextView) _dialogContainer.findViewById(R.id.budget);
		_quantity = _part.getQuantity();
		_itemName.setText(_part.get_itemName());
		_qtyRequested.setText(new Integer(_quantity).toString());
		_price.setText(_part.get_cost());
		_budget.setText(_part.get_budget());
		final BuyQtyDialog self = this;
		// Add action buttons
		builder.setView(_dialogContainer).setPositiveButton(R.string.setQuantity, new DialogInterface.OnClickListener() {

			public void onClick(final DialogInterface dialog, final int id) {
				try {
					if (null != _dialogCallback) {
						if (null != _qtyRequested) {
							_quantity = Integer.parseInt(_qtyRequested.getEditableText().toString());
							Toast.makeText(getActivity(), "Scheduled buy qty: " + _quantity, Toast.LENGTH_LONG);
							_dialogCallback.onDialogPositiveClick(self);
						}
					} else
						Toast.makeText(getActivity(), "Fragment invalid.", Toast.LENGTH_LONG);
				} catch (RuntimeException rtex) {
				}
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				BuyQtyDialog.this.getDialog().cancel();
			}
		});
		return builder.create();
	}

	public void setDialogCallback(final ADialogCallback callback) {
		if (null != callback) _dialogCallback = callback;
	}

	public void setPart(final TaskPart taskPart) {
		_part = taskPart;
	}
}

// - UNUSED CODE ............................................................................................

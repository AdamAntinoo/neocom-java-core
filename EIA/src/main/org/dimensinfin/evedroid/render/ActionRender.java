//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.ActionPart;
import org.dimensinfin.eveonline.neocom.enums.ETaskCompletion;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class ActionRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger	logger							= Logger.getLogger("ActionHolder");

	// - F I E L D - S E C T I O N ............................................................................
	private ImageView		itemIcon						= null;
	protected TextView	itemName						= null;
	private ProgressBar	qtyRequiredProgress	= null;
	protected TextView	qtyRequired					= null;
	private TextView		moduleAction				= null;

	private ViewGroup		itemIconBlock				= null;
	private TextView		functionLabel				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ActionRender(final ActionPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public ActionPart getPart() {
		return (ActionPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		itemIcon = (ImageView) _convertView.findViewById(R.id.itemIcon);
		qtyRequired = (TextView) _convertView.findViewById(R.id.qtyRequired);
		itemName = (TextView) _convertView.findViewById(R.id.itemName);
		moduleAction = (TextView) _convertView.findViewById(R.id.moduleAction);
		qtyRequiredProgress = (ProgressBar) _convertView.findViewById(R.id.qtyRequiredProgress);

		itemIconBlock = (ViewGroup) _convertView.findViewById(R.id.itemIconBlock);
		functionLabel = (TextView) _convertView.findViewById(R.id.functionLabel);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("ActionRender [");
		buffer.append(getPart().toString());
		buffer.append(" ]");
		return buffer.toString();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void updateContent() {
		super.updateContent();
		itemName.setText(getPart().get_itemName());
		qtyRequired.setText(getPart().get_qtyRequired());

		// If the item has preferred action show it.
		moduleAction.setVisibility(View.GONE);
		functionLabel.setVisibility(View.GONE);
		int itemid = getPart().getCastedModel().getTypeID();
		String action = getPart().getCastedModel().getUserAction();
		if (null != action) {
			moduleAction.setText(action);
			moduleAction.setVisibility(View.VISIBLE);
		}

		ETaskCompletion completed = getPart().isCompleted();
		if (completed == ETaskCompletion.COMPLETED) {
			moduleAction.setVisibility(View.GONE);
			qtyRequiredProgress = (ProgressBar) _convertView.findViewById(R.id.qtyRequiredProgress);
			qtyRequiredProgress.setMax(100);
			qtyRequiredProgress.setProgress(100);
			itemIcon.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.completeresourceframe));
		}
		if (completed == ETaskCompletion.PENDING) {
			qtyRequiredProgress.setMax(getPart().getRequestQty());
			qtyRequiredProgress.setProgress(getPart().getCompletedQty());
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bluetraslucent60));
		}
		if (completed == ETaskCompletion.MARKET) {
			qtyRequiredProgress.setMax(getPart().getRequestQty());
			qtyRequiredProgress.setProgress(getPart().getCompletedQty());
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.redtraslucent60));
		}

		View icon = _convertView.findViewById(R.id.itemIcon);
		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getTypeID());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.action4industry, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

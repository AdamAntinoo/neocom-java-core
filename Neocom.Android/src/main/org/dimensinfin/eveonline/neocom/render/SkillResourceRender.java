//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.eveonline.neocom.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.core.EveAbstractHolder;
import org.dimensinfin.eveonline.neocom.part.ResourcePart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class SkillResourceRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private ImageView	itemIcon		= null;
	private TextView	itemName		= null;
	private TextView	qtyRequired	= null;
	private ImageView	skillLevel1	= null;
	private ImageView	skillLevel2	= null;
	private ImageView	skillLevel3	= null;
	private ImageView	skillLevel4	= null;
	private ImageView	skillLevel5	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public SkillResourceRender(final ResourcePart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public ResourcePart getPart() {
		return (ResourcePart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		itemIcon = (ImageView) _convertView.findViewById(R.id.itemIcon);
		itemName = (TextView) _convertView.findViewById(R.id.itemName);
		qtyRequired = (TextView) _convertView.findViewById(R.id.qtyRequired);
		skillLevel1 = (ImageView) _convertView.findViewById(R.id.skillLevel1);
		skillLevel2 = (ImageView) _convertView.findViewById(R.id.skillLevel2);
		skillLevel3 = (ImageView) _convertView.findViewById(R.id.skillLevel3);
		skillLevel4 = (ImageView) _convertView.findViewById(R.id.skillLevel4);
		skillLevel5 = (ImageView) _convertView.findViewById(R.id.skillLevel5);
	}

	public void updateContent() {
		super.updateContent();
		itemName.setText(getPart().getCastedModel().getName());
		// Get the skill level requested and the current skill level.
		int requested = getPart().getQuantity();
		int current = getPart().getSkillLevel();
		// Set color for levels.
		skillLevel1.setBackgroundResource(calculateColor4(1, requested, current));
		skillLevel2.setBackgroundResource(calculateColor4(2, requested, current));
		skillLevel3.setBackgroundResource(calculateColor4(3, requested, current));
		skillLevel4.setBackgroundResource(calculateColor4(4, requested, current));
		skillLevel5.setBackgroundResource(calculateColor4(5, requested, current));

		qtyRequired.setText("Level " + requested);
		itemIcon.setBackgroundResource(R.drawable.completeresourceframe);
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.skill4industry, null);
		_convertView.setTag(this);
	}

	private int calculateColor4(final int i, final int requested, final int current) {
		int color = R.drawable.skillempty;
		if (requested >= i) color = R.drawable.skillpending;
		if (current >= i) color = R.drawable.skillcompleted;
		return color;
	}
}

// - UNUSED CODE ............................................................................................

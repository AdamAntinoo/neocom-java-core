//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.part.LocationAssetsPart;
import org.dimensinfin.eveonline.neocom.model.Property;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class Location4AssetsRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - L A Y O U T   F I E L D S
	public TextView	locationSystem						= null;
	public TextView	locationRegion						= null;
	public TextView	locationStation						= null;
	public TextView	childCount								= null;
	public TextView	function									= null;
	public TextView	locationItemsValue				= null;
	public TextView	locationItemsVolume				= null;

	// - L A Y O U T   L A B E L S
	public TextView	locationItemsValueLabel		= null;
	public TextView	locationItemsVolumeLabel	= null;
	public TextView	functionLabel							= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Location4AssetsRender(final EveAbstractPart target, final Activity context) {
		super(target, context);
	}

	public String generatePriceSuffix(final double price) {
		if (Math.abs(price) > 1200000000.0) return "B ISK";
		if (Math.abs(price) > 12000000.0) return "M ISK";
		return "ISK";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public LocationAssetsPart getPart() {
		return (LocationAssetsPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		locationSystem = (TextView) _convertView.findViewById(R.id.locationSystem);
		locationRegion = (TextView) _convertView.findViewById(R.id.locationRegion);
		locationStation = (TextView) _convertView.findViewById(R.id.locationStation);
		childCount = (TextView) _convertView.findViewById(R.id.childCount);
		function = (TextView) _convertView.findViewById(R.id.role);
		locationItemsValue = (TextView) _convertView.findViewById(R.id.locationItemsValue);
		locationItemsVolume = (TextView) _convertView.findViewById(R.id.locationItemsVolume);

		locationItemsValueLabel = (TextView) _convertView.findViewById(R.id.locationItemsValueLabel);
		locationItemsVolumeLabel = (TextView) _convertView.findViewById(R.id.locationItemsVolumeLabel);
		functionLabel = (TextView) _convertView.findViewById(R.id.functionLabel);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		locationSystem.setText(getPart().get_locationSystem());
		locationRegion.setText(getPart().get_locationRegion());
		locationStation.setText(getPart().get_locationStation());
		childCount.setText(getPart().get_locationContentCount());

		// The value and volume depends on a setting.
		double itemsValueISK = getPart().getItemsValoration();
		if (itemsValueISK > 0.0) {
			locationItemsValue.setText(generatePriceString(itemsValueISK, true, false));
			locationItemsValueLabel.setText(generatePriceSuffix(itemsValueISK));
		} else {
			locationItemsValue.setText("-");
		}
		double itemsVolume = getPart().getItemsVolume();
		if (itemsVolume > 0.0) {
			locationItemsVolume.setText(qtyFormatter.format(itemsVolume));
		} else {
			locationItemsVolume.setText("-");
		}

		// Control the ROLE block
		ArrayList<Property> roles = getPart().accessLocationFunction();
		function.setVisibility(View.GONE);
		functionLabel.setVisibility(View.GONE);
		if ((null != roles) && (roles.size() > 0)) {
			String functionName = "";
			for (Property role : roles) {
				functionName = functionName + " " + role.getStringValue();
			}
			function.setVisibility(View.VISIBLE);
			functionLabel.setVisibility(View.VISIBLE);
			function.setText(functionName);
		}
		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().searchStationType(), true);
		_convertView.setBackgroundResource(R.drawable.topwhiteline);
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		// The view is a new view. We have to fill all the items
		_convertView = mInflater.inflate(R.layout.location4assets, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.LocationShipsPart;
import org.dimensinfin.eveonline.neocom.model.Property;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class Location4ShipsRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - L A Y O U T   F I E L D S
	private TextView	locationSystem	= null;
	private TextView	locationRegion	= null;
	private TextView	locationStation	= null;
	private TextView	childCount			= null;
	public TextView		roles						= null;
	public TextView		rolesLabel			= null;

	private ViewGroup	containerBlock	= null;
	private ImageView	containerIcon		= null;
	private TextView	containerName		= null;

	// - L A Y O U T   L A B E L S

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Location4ShipsRender(final LocationShipsPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public LocationShipsPart getPart() {
		return (LocationShipsPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		locationSystem = (TextView) _convertView.findViewById(R.id.locationSystem);
		locationStation = (TextView) _convertView.findViewById(R.id.locationStation);
		locationRegion = (TextView) _convertView.findViewById(R.id.locationRegion);
		childCount = (TextView) _convertView.findViewById(R.id.childCount);
		containerBlock = (ViewGroup) _convertView.findViewById(R.id.containerBlock);
		containerIcon = (ImageView) _convertView.findViewById(R.id.containerIcon);
		containerName = (TextView) _convertView.findViewById(R.id.containerName);
		roles = (TextView) _convertView.findViewById(R.id.roles);
		rolesLabel = (TextView) _convertView.findViewById(R.id.rolesLabel);
		rolesLabel.setVisibility(View.GONE);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		locationSystem.setText(this.getPart().get_locationSystem());
		locationRegion.setText(this.getPart().get_locationRegion());
		locationStation.setText(this.getPart().get_locationStation());
		childCount.setText(this.getPart().get_locationContentCount());

		// Control the ROLE block
		ArrayList<Property> roleList = this.getPart().accessLocationFunction();
		roles.setVisibility(View.GONE);
		rolesLabel.setVisibility(View.GONE);
		if ((null != roleList) && (roleList.size() > 0)) {
			String functionName = "";
			for (Property role : roleList)
				functionName = functionName + " " + role.getStringValue();
			roles.setVisibility(View.VISIBLE);
			rolesLabel.setVisibility(View.VISIBLE);
			roles.setText(functionName);
		}

		// If the location has a container then show the Container info.
		containerBlock.setVisibility(View.GONE);
		if (this.getPart().hasContainer()) {
			containerBlock.setVisibility(View.VISIBLE);
			containerName.setText(this.getPart().getContainerName());
			this.loadEveIcon(containerIcon, 17366);
		}
		this.loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), this.getPart().searchStationType(), true);
		_convertView.setBackgroundResource(R.drawable.topwhiteline);
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) this.getContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		// The view is a new view. We have to fill all the items
		_convertView = mInflater.inflate(R.layout.location4industry, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

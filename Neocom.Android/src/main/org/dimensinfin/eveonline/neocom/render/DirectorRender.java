//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.render;

import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.core.EveAbstractHolder;
import org.dimensinfin.eveonline.neocom.part.DirectorPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class DirectorRender extends AbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger		= Logger.getLogger("org.dimensinfin.evedroid.render");

	// - F I E L D - S E C T I O N ............................................................................
	private ImageView			menuIcon	= null;
	private TextView			menuLabel	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DirectorRender(final DirectorPart target, final Activity context) {
		super(target, context);
	}

	@Override
	public DirectorPart getPart() {
		return (DirectorPart) super.getPart();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void initializeViews() {
		super.initializeViews();
		menuIcon = (ImageView) _convertView.findViewById(R.id.neocomEntryIcon);
		menuLabel = (TextView) _convertView.findViewById(R.id.neocomEntryLabel);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		// Check if the neocom item is active
		if (this.getPart().checkActivation()) {
			// Get the icon depending on the state.
			menuIcon.setImageDrawable(this.getContext().getResources().getDrawable(this.getPart().getActiveIcon()));
			// Add the click action to the icon only when the icon is active.
			//			activator = (ImageView) findViewById(R.id.assetsDirectorIcon);
			menuIcon.setClickable(true);
			// TODO Chanck if this listener connection works.
			menuIcon.setOnClickListener(this.getPart());
		} else {
			menuIcon.setImageDrawable(this.getContext().getResources().getDrawable(this.getPart().getDimmedIcon()));
			menuIcon.setClickable(false);
		}
		menuLabel.setText(this.getPart().getName());
		menuLabel.setTypeface(EveAbstractHolder.daysFace);
		menuIcon.invalidate();
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) this.getContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.neocomitem4menu, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

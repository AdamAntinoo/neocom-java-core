//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.render;

import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.AssetsDirectorActivity;
import org.dimensinfin.evedroid.interfaces.IDirector;
import org.dimensinfin.evedroid.part.DirectorPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class DirectorHolder extends AbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger		= Logger.getLogger("org.dimensinfin.evedroid.render");

	// - F I E L D - S E C T I O N ............................................................................
	private ImageView			menuIcon	= null;
	private TextView			menuLabel	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DirectorHolder(final DirectorPart target, final Activity context) {
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
		if (getPart().checkActivation()) {
			// Get the icon depending on the state.
			menuIcon.setImageDrawable(getContext().getResources().getDrawable(getPart().getActiveIcon()));
			// Add the click action to the icon only when the icon is active.
			//			activator = (ImageView) findViewById(R.id.assetsDirectorIcon);
			menuIcon.setClickable(true);
		} else {
			menuIcon.setImageDrawable(getContext().getResources().getDrawable(getPart().getDimmedIcon()));
			menuIcon.setClickable(false);
		}
		menuLabel.setText(getPart().getName());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.menu_neocomitem, null);
		_convertView.setTag(this);
	}

	private void n() {
		final IDirector adirector = new AssetsDirectorActivity();
		if (adirector.checkActivation(_store.getPilot())) {
			logger.info("-- DirectorsBoardActivity.onResume - activated " + directorCode);
			activator = (ImageView) findViewById(R.id.assetsDirectorIcon);
			activator.setImageDrawable(getDrawable(R.drawable.assetsdirector));
			final TextView label = (TextView) findViewById(R.id.assetsDirectorLabel);
			label.setTypeface(daysFace);
			activator.invalidate();
		}

	}
}

// - UNUSED CODE ............................................................................................

//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.eveonline.neocom.holder;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.core.EveAbstractHolder;
import org.dimensinfin.eveonline.neocom.part.RegionPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class RegionHolder extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger			= Logger.getLogger("RegionHolder");

	// - F I E L D - S E C T I O N ............................................................................

	// - L A Y O U T   F I E L D S
	public TextView				region			= null;
	public TextView				childCount	= null;

	// - L A Y O U T   L A B E L S
	public TextView				titleLabel	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public RegionHolder(final RegionPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public RegionPart getPart() {
		return (RegionPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		region = (TextView) _convertView.findViewById(R.id.region);
		childCount = (TextView) _convertView.findViewById(R.id.childCount);

		titleLabel = (TextView) _convertView.findViewById(R.id.titleLabel);

//		region.setTypeface(_theme.getThemeTextFont());
//		childCount.setTypeface(_theme.getThemeTextFont());
	}

	//	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	//	@SuppressWarnings("deprecation")
	@Override
	public void updateContent() {
		super.updateContent();
		region.setText(getPart().get_region());
		childCount.setText(getPart().get_contentCount());

		// Set the background form the Theme.
		//		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
		//			_convertView.setBackgroundDrawable(_theme.getThemeTransparent(EThemeTransparency.VERYLOW));
		//		else
		//			_convertView.setBackground(_theme.getThemeTransparent(EThemeTransparency.VERYLOW));
		_convertView.invalidate();
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		// The view is a new view. We have to fill all the items
		_convertView = mInflater.inflate(R.layout.region4assets, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

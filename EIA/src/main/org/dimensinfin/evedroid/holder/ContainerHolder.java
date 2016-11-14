//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.holder;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.ContainerPart;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class ContainerHolder extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger						= Logger.getLogger("ContainerHolder");

	// - F I E L D - S E C T I O N ............................................................................
	public TextView				name							= null;
	public TextView				containerCategory	= null;
	public TextView				count							= null;

	public TextView				titleLabel				= null;
	public TextView				countLabel				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ContainerHolder(final ContainerPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public ContainerPart getPart() {
		return (ContainerPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		this.name = (TextView) this._convertView.findViewById(R.id.assetName);
		this.count = (TextView) this._convertView.findViewById(R.id.count);
		this.containerCategory = (TextView) this._convertView.findViewById(R.id.containerCategory);

		this.name.setTypeface(getThemeTextFont());
		this.count.setTypeface(getThemeTextFont());
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void updateContent() {
		super.updateContent();
		this.name.setText(getPart().get_assetName());
		this.count.setText(getPart().get_contentCount());
		this.containerCategory.setText(getPart().get_containerCategory());
		loadEveIcon((ImageView) this._convertView.findViewById(R.id.assetIcon), getPart().getTypeID());
		setBackgroundTransparency(R.drawable.bluetraslucent40);
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this._convertView = mInflater.inflate(R.layout.container4asset, null);
		this._convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................

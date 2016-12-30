//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.part;

import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.eveonline.neocom.activity.FittingActivity;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.core.EveAbstractPart;
import org.dimensinfin.eveonline.neocom.interfaces.INamedPart;
import org.dimensinfin.eveonline.neocom.model.Fitting;
import org.dimensinfin.eveonline.neocom.render.FittingListRender;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class FittingListPart extends EveAbstractPart implements INamedPart, OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 1956908168853667475L;
	private static Logger			logger						= Logger.getLogger("FittingPart");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingListPart(final Fitting model) {
		super(model);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getHullGroup() {
		return this.getCastedModel().getHull().getItem().getGroupName();
	}

	public String getHullName() {
		return this.getCastedModel().getHull().getItem().getName();
	}

	public int getHullTypeID() {
		return this.getCastedModel().getHull().getItem().getTypeID();
	}

	@Override
	public long getModelID() {
		return this.getCastedModel().getName().hashCode();
	}

	public String getName() {
		return this.getCastedModel().getName();
	}

	/**
	 * Returns the ISK formatted string that represents the full fit cost. This is calculated by the sum of all
	 * the fitting contents using the market sell prices.
	 * 
	 * @return string with the price formatted and with the ISK suffix added.
	 */
	public String getTransformedFittingCost() {
		return this.generatePriceString(this.getCastedModel().getFittingCost(), true, true);
	}

	public void onClick(final View arg0) {
		FittingListPart.logger.info(">> [FittingListPart.onClick]");
		Intent intent = new Intent(this.getActivity(), FittingActivity.class);
		intent.putExtra(AppWideConstants.EExtras.EXTRA_FITTINGID.name(), this.getCastedModel().getName());
		this.getActivity().startActivity(intent);
		FittingListPart.logger.info("<< [FittingListPart.onClick]");
	}

	@Override
	protected AbstractHolder selectHolder() {
		return new FittingListRender(this, _activity);
	}

	private Fitting getCastedModel() {
		return (Fitting) this.getModel();
	}

}

// - UNUSED CODE ............................................................................................

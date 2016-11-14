//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.fragment.core;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeListener;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.core.DataSourceAdapter;
import org.dimensinfin.evedroid.constant.AppWideConstants;

import android.util.Log;
import android.view.View;

// - CLASS IMPLEMENTATION ...................................................................................
public class PagerFullFragment extends NewPagerFragment implements PropertyChangeListener {
	//- CLASS IMPLEMENTATION ...................................................................................
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The reception of this message signals the termination of the data source processing. We can lift the
	 * spinning progress indicator and start to render the views.<br>
	 * But when the phone is tilted than the structures are still valid, no do not reinitialize them but signal
	 * them for refresh.
	 */
	@Override
	public void signalHierarchyCreationCompleted() {
		if (null == this._adapter) {
			this._adapter = new DataSourceAdapter(this, new HeaderDataSourceProxy(this._messageBus));
			this._listContainer.setAdapter(this._adapter);
		}
		this._adapter.notifyDataSetChanged();
		this._progressLayout.setVisibility(View.GONE);
		this._listContainer.setVisibility(View.VISIBLE);
		this._container.invalidate();
		this._listContainer.invalidate();

		// Get the header contents and add them to the header layout.
		this._headerContents = this._messageBus.getHeaderPartsHierarchy(AppWideConstants.panel.PANEL_INDUSTRYJOBSHEADER);
		// Add the header parts once the display is initialized.
		if (this._headerContents.size() > 0) {
			this._headerContainer.removeAllViews();
			for (final AbstractAndroidPart part : this._headerContents)
				addViewtoHeader(part);
		}
	}

	private void addViewtoHeader(final AbstractAndroidPart target) {
		Log.i("PageFragment", ">> PageFragment.addViewtoHeader");
		try {
			final AbstractHolder holder = target.getHolder(getActivity());
			holder.initializeViews();
			holder.updateContent();
			final View hv = holder.getView();
			this._headerContainer.addView(hv);
			this._headerContainer.setVisibility(View.VISIBLE);
		} catch (final RuntimeException rtex) {
			Log.e("PageFragment", "R> PageFragment.addViewtoHeader RuntimeException. " + rtex.getMessage());
			rtex.printStackTrace();
		}
		Log.i("PageFragment", "<< PageFragment.addViewtoHeader");
	}
}
// - UNUSED CODE ............................................................................................

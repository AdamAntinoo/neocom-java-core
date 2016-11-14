//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.android.mvc.core;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractFragment extends Fragment {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger				logger				= Logger.getLogger("AbstractFragment");

	// - F I E L D - S E C T I O N ............................................................................
	protected IDataSource				_datasource		= null;
	protected DataSourceAdapter	_adapter			= null;

	// - U I    F I E L D S
	protected ViewGroup					_container		= null;
	protected ListView					_fragmentList	= null;
	protected ProgressBar				_progress			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public abstract int getIdentifier();

	/**
	 * Creates the structures when the fragment is about to be shown. We have to check that the parent Activity
	 * is compatible with this kind of fragment. So the fragment has to check of it has access to a valid pilot
	 * before returning any UI element.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		logger.info(">> AbstractFragment.onCreateView");
		super.onCreateView(inflater, container, savedInstanceState);

		try {
			_container = (ViewGroup) inflater.inflate(R.layout.fragment_base, container, false);
			_fragmentList = (ListView) _container.findViewById(R.id.listContainer);
			_progress = (ProgressBar) _container.findViewById(R.id.progress);
		} catch (RuntimeException rtex) {
			logger.info("E> AbstractFragment.onCreateView RuntimeException. " + rtex.getMessage());
			return null;
		}
		logger.info("<< AbstractFragment.onCreateView");
		return _container;
	}

	/**
	 * At this level only accesses the data elements and stores them on the instance. Startup of the data
	 * collecting and the adapter connections is done on higher classes.
	 */
	public void onStart() {
		logger.info(">> AbstractFragment.onStart");
		super.onStart();
		// Check the validity of the parent activity.
		if (getActivity() instanceof IActivityCallback) {
			_datasource = ((IActivityCallback) getActivity()).getDataSource(this.getIdentifier());
		}
		logger.info("<< AbstractFragment.onStart");
	}
}

// - UNUSED CODE ............................................................................................

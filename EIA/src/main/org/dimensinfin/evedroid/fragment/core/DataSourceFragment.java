//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.fragment.core;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.IDataSource;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.evedroid.activity.core.EVEPagerActivity;
import org.dimensinfin.evedroid.activity.core.SplashActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.datasource.AbstractNewDataSource;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A DataSource Fragment is a non UI persistent fragment that contains the data
 * model for an specific activity. During the creation of the Activity this
 * fragment is configured and afterwards started on the <code>onStart</code>
 * message. Once the data source has created its model will signal their friend
 * fragments (all other fragments defined on the same Activity) that it has
 * finished and that can start to serve the Part hierarchies.
 * 
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION
// ...................................................................................
public class DataSourceFragment extends Fragment implements PropertyChangeListener {
	private class ActivityNotificator extends AbstractPropertyChanger {
		private DataSourceFragment source = null;

		public ActivityNotificator(final DataSourceFragment dataSourceFragment) {
			this.source = dataSourceFragment;
		}

	}

	// - CLASS IMPLEMENTATION
	// ...................................................................................
	private class InitializeDataSourceTask extends AsyncTask<Fragment, Void, Boolean> {

		// - F I E L D - S E C T I O N
		// ............................................................................
		private final Fragment fragment;

		// - C O N S T R U C T O R - S E C T I O N
		// ................................................................
		public InitializeDataSourceTask(final Fragment fragment) {
			this.fragment = fragment;
		}

		// - M E T H O D - S E C T I O N
		// ..........................................................................
		/**
		 * Initializes and creates the part hierarchy. This method detects any
		 * previous initialization to skip this process if already performed.
		 * <br>
		 * Initialization means that all the Fragment bundles are passed to the
		 * DataSource for extraction of the expected and valid parameters. This
		 * helps to isolate all data structures from App and global data
		 * dependencies.
		 */
		@Override
		protected Boolean doInBackground(final Fragment... arg0) {
			try {
				// Create the hierarchy structure to be used on the Adapter.
				if (null != DataSourceFragment.this._datasource) {
					if (!DataSourceFragment.this._alreadyInitialized) {
						DataSourceFragment.this._datasource.createContentHierarchy();
					}
					DataSourceFragment.this._alreadyInitialized = true;
				}
			} catch (final RuntimeException rtex) {
				rtex.printStackTrace();
				DataSourceFragment.this._alreadyInitialized = false;
			}
			return Boolean.valueOf(DataSourceFragment.this._alreadyInitialized).booleanValue();
		}

		/**
		 * When the data model initialization completes then we should signal
		 * other fragments that they can get their list contents to render the
		 * different contents on the right places. The communication is done
		 * through the Activity.
		 */
		@Override
		protected void onPostExecute(final Boolean result) {
			if (result.booleanValue()) {
				DataSourceFragment.this.changer
						.firePropertyChange(AppWideConstants.events.EVENTMESSAGE_HIERARCHYCOMPLETED, null, this);
			}
			super.onPostExecute(result);
		}
	}

	// - S T A T I C - S E C T I O N
	// ..........................................................................

	// - F I E L D - S E C T I O N
	// ............................................................................
	// private String _title = "<TITLE>";
	// private String _subtitle = "";
	protected int _fragmentID = -1;
	public AbstractNewDataSource _datasource = null;
	// protected DataSourceAdapter _adapter = null;
	protected boolean _alreadyInitialized = false;
	private final ActivityNotificator changer = new ActivityNotificator(this);

	// protected final Vector<AbstractAndroidPart> _headerContents = new
	// Vector<AbstractAndroidPart>();

	// - U I F I E L D S
	// protected ViewGroup _container = null;
	// /** The view that handles the non scrolling header. */
	// protected ViewGroup _headerContainer = null;
	// /** The view that represent the list view and the space managed though
	// the adapter. */
	// protected ListView _modelContainer = null;
	// protected ViewGroup _progressLayout = null;
	// private IMenuActionTarget _listCallback = null;
	// private ADialogCallback _dialogCallback = null;
	// private int _fragmentLayout = R.layout.fragment_limited;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................

	public void addMessageBus(final EVEPagerActivity newMarketActivity) {
		this.changer.addPropertyChangeListener(newMarketActivity);
	}

	// public void notifyDataSetChanged() {
	// if (null != _adapter) _adapter.notifyDataSetChanged();
	// }

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	/**
	 * Generate the part list for the body. This list maybe or not generated
	 * before this request. Timing will be checked when the implementation is
	 * running.
	 * 
	 * @param panelMarketordersbody
	 * @return
	 */
	public ArrayList<AbstractAndroidPart> getBodyPartsHierarchy(final int panelMarketordersbody) {
		return this._datasource.getBodyPartsHierarchy(panelMarketordersbody);
	}

	public ArrayList<AbstractAndroidPart> getHeaderPartsHierarchy(final int panelMarketordersbody) {
		return this._datasource.getHeaderPartsHierarchy(panelMarketordersbody);
	}

	/**
	 * If the user has set the identifier return the identifier set (This allows
	 * to use the Generic by code in multifragment activities). Otherwise return
	 * the Id of the fragment that would be generated on the layout XML.
	 */
	public int getIdentifier() {
		if (this._fragmentID > 0)
			return this._fragmentID;
		else
			return getId();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	/**
	 * Non UI fragments should return null so they are not allocated to any
	 * view.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		Log.i("EVEI", ">> DataSourceFragment.onCreateView");
		Log.i("EVEI", "<< DataSourceFragment.onCreateView");
		return null;
	}

	/**
	 * When the execution reaches this point to activate the fragment we start
	 * to generate the data model and their corresponding Parts on a background
	 * task. As this fragment does not have a visible UI interface the method
	 * will return and go to the same message of other fragments that can then
	 * start their respective interfaces.
	 */
	@Override
	public void onStart() {
		Log.i("EVEI", ">> DataSourceFragment.onStart");
		super.onStart();
		try {
			// Check the validity of the data source.
			if (null == this._datasource)
				throw new RuntimeException(">RT DataSourceFragment.onStart - Datasource not defined.");
			// Do the initialization on another thread or if completed signal
			// the termination.
			new InitializeDataSourceTask(this).execute();
		} catch (final Exception rtex) {
			Log.e("EVEI", "R> Runtime Exception on DataSourceFragment.onStart." + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
		}
		Log.i("EVEI", "<< DataSourceFragment.onStart");
	}

	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTMESSAGE_HIERARCHYCOMPLETED)) {
			this.changer.firePropertyChange(event);
		} else {
			this._datasource.propertyChange(event);
		}
	}

	public void setDataSource(final IDataSource dataSource) {
		if (null != dataSource) {
			this._datasource = (AbstractNewDataSource) dataSource;
			// Connect to the datasource.
			((AbstractPropertyChanger) dataSource).addPropertyChangeListener(this);
		}
	}

	public void setIdentifier(final int id) {
		this._fragmentID = id;
	}

	/**
	 * For really unrecoverable or undefined exceptions the application should
	 * go to a safe spot. That spot is defined by the application so this is
	 * another abstract method.
	 * 
	 * @param exception
	 */
	protected void stopActivity(final Exception exception) {
		final Intent intent = new Intent(getActivity(), SplashActivity.class);
		// Pass the user message to the activity for display.
		intent.putExtra(SystemWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
		startActivity(intent);
	}

}

// - UNUSED CODE
// ............................................................................................

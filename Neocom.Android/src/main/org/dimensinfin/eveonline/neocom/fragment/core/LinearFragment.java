//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.eveonline.neocom.fragment.core;

// - IMPORT SECTION .........................................................................................
import java.util.Vector;

import org.dimensinfin.android.mvc.activity.ADialogCallback;
import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.DataSourceAdapter;
import org.dimensinfin.android.mvc.interfaces.IDataSource;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.activity.core.SplashActivity;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

// - CLASS IMPLEMENTATION ...................................................................................
public class LinearFragment extends Fragment {
	//- CLASS IMPLEMENTATION ...................................................................................
	private class InitializeDataSourceTask extends AsyncTask<Fragment, Void, Void> {

		// - F I E L D - S E C T I O N ............................................................................
		private final Fragment fragment;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public InitializeDataSourceTask(final Fragment fragment) {
			this.fragment = fragment;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		/**
		 * Initializes and creates the part hierarchy. This method detects any previous initialization to skip
		 * this process if already performed.<br>
		 * Initialization means that all the Fragment bundles are passed to the DataSource for extraction of the
		 * expected and valid parameters. This helps to isolate all data structures from App and global data
		 * dependencies.
		 */
		@Override
		protected Void doInBackground(final Fragment... arg0) {
			try {
				// Create the hierarchy structure to be used on the Adapter.
				if (null != _datasource) {
					if (!_alreadyInitialized) {
						//						_datasource.processArguments(fragment.getArguments());
						_datasource.createContentHierarchy();
					}
					_alreadyInitialized = true;
				}
			} catch (RuntimeException rtex) {
				rtex.printStackTrace();
				_alreadyInitialized = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			if (null != _datasource) {
				_adapter = new DataSourceAdapter(fragment, _datasource);
				_modelContainer.setAdapter(_adapter);

				_progressLayout.setVisibility(View.GONE);
				_modelContainer.setVisibility(View.VISIBLE);
				_container.invalidate();
			}
			super.onPostExecute(result);
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private String															_title							= "<TITLE>";
	private String															_subtitle						= "";
	protected int																_fragmentID					= -1;
	public IDataSource													_datasource					= null;
	protected DataSourceAdapter									_adapter						= null;
	protected boolean														_alreadyInitialized	= false;
	protected final Vector<AbstractAndroidPart>	_headerContents			= new Vector<AbstractAndroidPart>();

	// - U I    F I E L D S
	protected ViewGroup													_container					= null;
	/** The view that handles the non scrolling header. */
	protected ViewGroup													_headerContainer		= null;
	/** The view that represent the list view and the space managed though the adapter. */
	protected ListView													_modelContainer			= null;
	protected ViewGroup													_progressLayout			= null;
	private IMenuActionTarget										_listCallback				= null;
	private ADialogCallback											_dialogCallback			= null;
	private int																	_fragmentLayout			= R.layout.fragment_limited;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	//	public void addtoHeader(final AbstractAndroidPart target) {
	//		Log.i("PageFragment", ">> PageFragment.addtoHeader");
	//		_headerContents.add(target);
	//		Log.i("PageFragment", "<< PageFragment.addtoHeader");
	//	}
	//
	//	public void clearHeader() {
	//		_headerContents.clear();
	//	}

	/**
	 * If the user has set the identifier return the identifier set (This allows to use the Generic by code in
	 * multifragment activities) . Otherwise return the Id of the fragment that would be generated on the layout
	 * XML.
	 */
	public int getIdentifier() {
		if (_fragmentID > 0)
			return _fragmentID;
		else
			return getId();
	}

	public String getSubtitle() {
		return _subtitle;
	}

	public String getTitle() {
		return _title;
	}

	public void notifyDataSetChanged() {
		if (null != _adapter) {
			_adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		//		logger.info(">> ManufactureContextFragment.onContextItemSelected"); //$NON-NLS-1$
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		AbstractAndroidPart part = (AbstractAndroidPart) info.targetView.getTag();
		if (part instanceof IMenuActionTarget)
			return ((IMenuActionTarget) part).onContextItemSelected(item);
		else
			return true;
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		Log.i("EVEI", ">> PageFragment.onCreateContextMenu"); //$NON-NLS-1$
		// REFACTOR If we call the super then the fragment's parent activity gets called. So the listcallback and the Activity
		// have not to be the same
		super.onCreateContextMenu(menu, view, menuInfo);
		// Check parameters to detect the item selected for menu target.
		if (view == _headerContainer) {
			//			 Check if this fragment has the callback configured
			AbstractAndroidPart part = _headerContents.firstElement();
			if (part instanceof IMenuActionTarget) {
				((IMenuActionTarget) part).onCreateContextMenu(menu, view, menuInfo);
			}
		}
		if (view == _modelContainer) {
			// Check if this fragment has the callback configured
			if (null != _listCallback) {
				_listCallback.onCreateContextMenu(menu, view, menuInfo);
			}
		}
		Log.i("EVEI", "<< PageFragment.onCreateContextMenu"); //$NON-NLS-1$
	}

	/**
	 * Creates the structures when the fragment is about to be shown. We have only a defined container that will
	 * be the destination of the datasource parts so create the connections through the adapter.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("EVEI", ">> LinearFragment.onCreateView");
		View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			if (!_alreadyInitialized) {
				_container = (ViewGroup) inflater.inflate(_fragmentLayout, container, false);
			}
			//			_headerContainer = (ViewGroup) _container.findViewById(R.id.headerContainer);
			_modelContainer = (ListView) _container.findViewById(R.id.modelContainer);
			_progressLayout = (ViewGroup) _container.findViewById(R.id.progressLayout);
			// Prepare the structures for the context menu.
			//			registerForContextMenu(_headerContainer);
			registerForContextMenu(_modelContainer);
		} catch (RuntimeException rtex) {
			Log.e("PageFragment", "R> PageFragment.onCreateView RuntimeException. " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("R> PageFragment.onCreateView RuntimeException. " + rtex.getMessage()));
		}
		Log.i("PageFragment", "<< PageFragment.onCreateView");
		return _container;
	}

	public void onDialogNegativeClick(final DialogFragment dialog) {
		if (null != _dialogCallback) {
			_dialogCallback.onDialogNegativeClick(dialog);
		}
	}

	public void onDialogPositiveClick(final DialogFragment dialog) {
		if (null != _dialogCallback) {
			_dialogCallback.onDialogPositiveClick(dialog);
		}
	}

	/**
	 * When the execution reaches this point to activate the fragment we have to check that all the elements
	 * required are defined, mainly the Data Source. If the DS is ready and valid then we launch the DS data
	 * loading code. If that was performed on a previous start and the DS is already loaded we can skip this
	 * step. This last approach will reduce CPU usage and give a better user feeling when activating and
	 * deactivating activities and fragments.
	 */
	@Override
	public void onStart() {
		Log.i("PageFragment", ">> PageFragment.onStart");
		super.onStart();
		try {
			// Check the validity of the data source.
			if (null == _datasource) throw new RuntimeException("Datasource not defined.");
			if (!_alreadyInitialized) {
				new InitializeDataSourceTask(this).execute();
			}
		} catch (Exception rtex) {
			Log.e("PageFragment", "R> Runtime Exception on PageFragment.onStart." + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(rtex);
		}
		//		// Update the spinner counter on the actionbar.
		//		getActivity().invalidateOptionsMenu();
		// Add the header parts once the display is initialized.
		//		if (_headerContents.size() > 0) {
		//			_headerContainer.removeAllViews();
		//			for (AbstractAndroidPart part : _headerContents)
		//				addViewtoHeader(part);
		//		}
		Log.i("PageFragment", "<< PageFragment.onStart");
	}

	public void setDataSource(final IDataSource dataSource) {
		Log.i("RESTART", "-- PagerFragment.setDataSource. Validation checkpoint [" + dataSource + "]");
		if (null != dataSource) {
			_datasource = dataSource;
		}
	}

	public void setDialogCallback(final ADialogCallback callback) {
		if (null != callback) {
			_dialogCallback = callback;
		}
	}

	public void setIdentifier(final int id) {
		_fragmentID = id;
	}

	public void setLayout(final int newlayout) {
		_fragmentLayout = newlayout;
	}

	public void setListCallback(final IMenuActionTarget callback) {
		if (null != callback) {
			_listCallback = callback;
		}
	}

	public void setSubtitle(final String subtitle) {
		_subtitle = subtitle;
	}

	public void setTitle(final String title) {
		_title = title;
	}

	/**
	 * For really unrecoverable or undefined exceptions the application should go to a safe spot. That spot is
	 * defined by the application so this is another abstract method.
	 * 
	 * @param exception
	 */
	protected void stopActivity(final Exception exception) {
		final Intent intent = new Intent(getActivity(), SplashActivity.class);
		// Pass the user message to the activity for display.
		intent.putExtra(SystemWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
		//		EVEDroidApp.getSingletonApp().init();
		startActivity(intent);
	}

	//	private void addViewtoHeader(final AbstractAndroidPart target) {
	//		Log.i("PageFragment", ">> PageFragment.addViewtoHeader");
	//		try {
	//			AbstractHolder holder = target.getHolder(this);
	//			holder.initializeViews();
	//			holder.updateContent();
	//			View hv = holder.getView();
	//			//	_headerContainer.removeAllViews();
	//			_headerContainer.addView(hv);
	//			_headerContainer.setVisibility(View.VISIBLE);
	//		} catch (RuntimeException rtex) {
	//			Log.e("PageFragment", "R> PageFragment.addViewtoHeader RuntimeException. " + rtex.getMessage());
	//			rtex.printStackTrace();
	//		}
	//		Log.i("PageFragment", "<< PageFragment.addViewtoHeader");
	//	}
}

// - UNUSED CODE ............................................................................................

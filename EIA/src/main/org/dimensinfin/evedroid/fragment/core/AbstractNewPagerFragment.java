//	PROJECT:        NeoCom (NEOC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to character data from Eve Online. Specialized on
//									industrial management.
package org.dimensinfin.evedroid.fragment.core;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Vector;

import org.dimensinfin.android.mvc.activity.SafeStopActivity;
import org.dimensinfin.android.mvc.activity.TitledFragment;
import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.core.DataSourceAdapter;
import org.dimensinfin.android.mvc.core.IMenuActionTarget;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.AppWideConstants.EFragment;
import org.dimensinfin.evedroid.datasource.IExtendedDataSource;
import org.dimensinfin.evedroid.model.EveCharCore;

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
// REFACTOR Used this dependency just to maintain more code compatible with the new model.
public abstract class AbstractNewPagerFragment extends TitledFragment {
	//- CLASS IMPLEMENTATION ...................................................................................
	protected class CreatePartsTask extends AsyncTask<AbstractNewPagerFragment, Void, Void> {

		// - F I E L D - S E C T I O N ............................................................................
		private final AbstractNewPagerFragment fragment;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public CreatePartsTask(final AbstractNewPagerFragment fragment) {
			this.fragment = fragment;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		/**
		 * The datasource is ready and the new hierarchy should be created from the current model. All the stages
		 * are executed at this time both the model contents update and the list of parts to be used on the
		 * ListView. First, the model is checked to be initialized and if not then it is created. Then the model
		 * is run from start to end to create all the visible elements and from this list then we create the full
		 * list of the parts with their right renders.<br>
		 * This is the task executed every time a datasource gets its model modified and hides all the update time
		 * from the main thread as it is recommended by Google.
		 */
		@Override
		protected Void doInBackground(final AbstractNewPagerFragment... arg0) {
			Log.i("NEOCOM", ">> CreatePartsTask.doInBackground");
			try {
				// Create the hierarchy structure to be used on the Adapter.
				_datasource.collaborate2Model();
				_datasource.createContentHierarchy();
			} catch (final RuntimeException rtex) {
				rtex.printStackTrace();
			}
			Log.i("NEOCOM", "<< CreatePartsTask.doInBackground");
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			Log.i("NEOCOM", ">> CreatePartsTask.onPostExecute");
			// Activate the display of the list to force a redraw. Stop user UI waiting.
			AbstractNewPagerFragment.this._adapter = new DataSourceAdapter(this.fragment,
					AbstractNewPagerFragment.this._datasource);
			AbstractNewPagerFragment.this._modelContainer.setAdapter(AbstractNewPagerFragment.this._adapter);

			AbstractNewPagerFragment.this._progressLayout.setVisibility(View.GONE);
			AbstractNewPagerFragment.this._modelContainer.setVisibility(View.VISIBLE);
			AbstractNewPagerFragment.this._container.invalidate();

			// Add the header parts once the display is initialized.
			ArrayList<AbstractAndroidPart> headerContents = _datasource.getHeaderParts();
			if (headerContents.size() > 0) {
				_headerContainer.removeAllViews();
				_headerContainer.invalidate();
				for (final AbstractAndroidPart part : headerContents) {
					fragment.addViewtoHeader(part);
				}
			}
			super.onPostExecute(result);
			Log.i("NEOCOM", "<< CreatePartsTask.onPostExecute");
		}
	}

	//- CLASS IMPLEMENTATION ...................................................................................
	protected class StructureChangeTask extends AsyncTask<AbstractNewPagerFragment, Void, Void> {

		// - F I E L D - S E C T I O N ............................................................................
		private final AbstractNewPagerFragment fragment;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public StructureChangeTask(final AbstractNewPagerFragment fragment) {
			this.fragment = fragment;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		/**
		 * The datasource is ready and the new hierarchy should be created from the current model. All the stages
		 * are executed at this time both the model contents update and the list of parts to be used on the
		 * ListView. First, the model is checked to be initialized and if not then it is created. Then the model
		 * is run from start to end to create all the visible elements and from this list then we create the full
		 * list of the parts with their right renders.<br>
		 * This is the task executed every time a datasource gets its model modified and hides all the update time
		 * from the main thread as it is recommended by Google.
		 */
		@Override
		protected Void doInBackground(final AbstractNewPagerFragment... arg0) {
			Log.i("NEOCOM", ">> StructureChangeTask.doInBackground");
			try {
				// Create the hierarchy structure to be used on the Adapter.
				_datasource.createContentHierarchy();
			} catch (final RuntimeException rtex) {
				rtex.printStackTrace();
			}
			Log.i("NEOCOM", "<< StructureChangeTask.doInBackground");
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			Log.i("NEOCOM", ">> StructureChangeTask.onPostExecute");
			//			
			//			AbstractPagerFragment.this._adapter = new DataSourceAdapter(this.fragment, AbstractPagerFragment.this._datasource);
			//			AbstractPagerFragment.this._modelContainer.setAdapter(AbstractPagerFragment.this._adapter);

			AbstractNewPagerFragment.this._progressLayout.setVisibility(View.GONE);
			AbstractNewPagerFragment.this._modelContainer.setVisibility(View.VISIBLE);
			AbstractNewPagerFragment.this._container.invalidate();
			// Tell the adapter to refresh the contents.
			_adapter.notifyDataSetChanged();

			// Add the header parts once the display is initialized.
			ArrayList<AbstractAndroidPart> headerContents = _datasource.getHeaderParts();
			if (headerContents.size() > 0) {
				_headerContainer.removeAllViews();
				_headerContainer.invalidate();
				for (final AbstractAndroidPart part : headerContents) {
					fragment.addViewtoHeader(part);
				}
			}
			super.onPostExecute(result);
			Log.i("NEOCOM", "<< StructureChangeTask.onPostExecute");
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	protected int																_fragmentID				= -1;
	protected IExtendedDataSource								_datasource				= null;
	protected DataSourceAdapter									_adapter					= null;
	//	protected boolean													_alreadyInitialized	= false;
	// REFACTOR Set back to private after the PagerFragment is removed
	protected final Vector<AbstractAndroidPart>	_headerContents		= new Vector<AbstractAndroidPart>();

	// - U I    F I E L D S
	protected ViewGroup													_container				= null;
	/** The view that handles the non scrolling header. */
	protected ViewGroup													_headerContainer	= null;
	/** The view that represent the list view and the space managed though the adapter. */
	protected ListView													_modelContainer		= null;
	protected ViewGroup													_progressLayout		= null;
	protected IMenuActionTarget									_listCallback			= null;
	private Bundle															_extras						= new Bundle();
	protected EFragment													_variant					= AppWideConstants.EFragment.UNDEFINED_FRAGMENT;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addtoHeader(final AbstractAndroidPart target) {
		Log.i("NEOCOM", ">> PageFragment.addtoHeader");
		this._headerContents.add(target);
		Log.i("NEOCOM", "<< PageFragment.addtoHeader");
	}

	public void clearHeader() {
		this._headerContents.clear();
	}

	public Bundle getExtras() {
		return _extras;
	}

	/**
	 * If the user has set the identifier return the identifier set (This allows to use the Generic by code in
	 * multifragment activities) . Otherwise return the Id of the fragment that would be generated on the layout
	 * XML.
	 */
	public int getIdentifier() {
		if (this._fragmentID > 0)
			return this._fragmentID;
		else
			return getId();
	}

	public EveCharCore getPilot() {
		return EVEDroidApp.getAppStore().getPilot();
	}

	protected EFragment getVariant() {
		return _variant;
	}

	public String getPilotName() {
		return EVEDroidApp.getAppStore().getPilot().getName();
	}

	@Override
	public abstract String getSubtitle();

	//	@Override
	//	public void propertyChange(final PropertyChangeEvent arg0) {
	//		// TODO Auto-generated method stub
	//
	//	}
	@Override
	public abstract String getTitle();

	public void notifyDataSetChanged() {
		if (null != this._adapter) {
			this._adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		//		logger.info(">> ManufactureContextFragment.onContextItemSelected"); //$NON-NLS-1$
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		final AbstractAndroidPart part = (AbstractAndroidPart) info.targetView.getTag();
		if (part instanceof IMenuActionTarget)
			return ((IMenuActionTarget) part).onContextItemSelected(item);
		else
			return true;
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		Log.i("NEOCOM", ">> PageFragment.onCreateContextMenu"); //$NON-NLS-1$
		// REFACTOR If we call the super then the fragment's parent activity gets called. So the listcallback and the Activity
		// have not to be the same
		super.onCreateContextMenu(menu, view, menuInfo);
		// Check parameters to detect the item selected for menu target.
		if (view == this._headerContainer) {
			//			 Check if this fragment has the callback configured
			final AbstractAndroidPart part = this._headerContents.firstElement();
			if (part instanceof IMenuActionTarget) {
				((IMenuActionTarget) part).onCreateContextMenu(menu, view, menuInfo);
			}
		}
		if (view == this._modelContainer) {
			// Get the tag assigned to the selected view and if implements the callback interface send it the message.
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			// Check if the se4lected item is suitable for menu and select it depending on item part class.
			AbstractAndroidPart part = (AbstractAndroidPart) info.targetView.getTag();
			if (part instanceof IMenuActionTarget) {
				((IMenuActionTarget) part).onCreateContextMenu(menu, view, menuInfo);
			}
		}
		Log.i("NEOCOM", "<< PageFragment.onCreateContextMenu"); //$NON-NLS-1$
	}

	/**
	 * Creates the structures when the fragment is about to be shown. We have to check that the parent Activity
	 * is compatible with this kind of fragment. So the fragment has to check of it has access to a valid pilot
	 * before returning any UI element.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> AbstractPageFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			//			if (!this._alreadyInitialized)
			this._container = (ViewGroup) inflater.inflate(R.layout.fragment_base, container, false);
			this._headerContainer = (ViewGroup) this._container.findViewById(R.id.headerContainer);
			this._modelContainer = (ListView) this._container.findViewById(R.id.listContainer);
			this._progressLayout = (ViewGroup) this._container.findViewById(R.id.progressLayout);
			// Prepare the structures for the context menu.
			registerForContextMenu(this._headerContainer);
			registerForContextMenu(this._modelContainer);
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> AbstractPageFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> AbstractPageFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< AbstractPageFragment.onCreateView");
		return this._container;
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
		Log.i("NEOCOM", ">> AbstractPageFragment.onStart");
		super.onStart();
		try {
			createParts();
			// Update the spinner counter on the actionbar.
			getActivity().invalidateOptionsMenu();
			// Add the header parts once the display is initialized.
			if (this._headerContents.size() > 0) {
				this._headerContainer.removeAllViews();
				for (final AbstractAndroidPart part : this._headerContents) {
					addViewtoHeader(part);
				}
			}
		} catch (final Exception rtex) {
			Log.e("NEOCOM", "RTEX> AbstractPageFragment.onStart - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> AbstractPageFragment.onStart - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< AbstractPageFragment.onStart");
	}

	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(AbstractComplexNode.EVENT_EXPANDCOLLAPSENODE)) {
			new StructureChangeTask(this).execute();
		}
	}

	public void setDataSource(final IExtendedDataSource dataSource) {
		if (null != dataSource) {
			this._datasource = dataSource;
		}
	}

	public AbstractNewPagerFragment setExtras(final Bundle extras) {
		_extras = extras;
		return this;
	}

	/**
	 * Stores the identifier used to register this fragment as a unique identifier for later retrieval. <br>
	 * Warning: I think I am not using this method or this identifier to locate back the fragments.
	 * 
	 * @param id
	 */
	public void setIdentifier(final int id) {
		this._fragmentID = id;
	}

	public void setListCallback(final IMenuActionTarget callback) {
		if (null != callback) {
			this._listCallback = callback;
		}
	}

	protected boolean checkDSState() {
		if (null == _datasource)
			return true;
		else
			return false;
	}

	protected void createParts() {
		try {
			// Check the validity of the data source.
			if (null == this._datasource) throw new RuntimeException("Datasource not defined.");
			Log.i("NEOCOM", "-- AbstractPageFragment.createParts - Launching CreatePartsTask");
			new CreatePartsTask(this).execute();
		} catch (final Exception rtex) {
			Log.e("NEOCOM", "RTEX> AbstractPageFragment.onStart - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> AbstractPageFragment.onStart - " + rtex.getMessage()));
		}
	}

	/**
	 * For really unrecoverable or undefined exceptions the application should go to a safe spot. That spot is
	 * defined by the application so this is another abstract method.
	 * 
	 * @param exception
	 */
	protected void stopActivity(final Exception exception) {
		final Intent intent = new Intent(getActivity(), SafeStopActivity.class);
		// Pass the user message to the activity for display.
		intent.putExtra(SystemWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
		//		EVEDroidApp.getSingletonApp().init();
		startActivity(intent);
	}

	private void addViewtoHeader(final AbstractAndroidPart target) {
		Log.i("NEOCOM", ">> AbstractPagerFragment.addViewtoHeader");
		try {
			final AbstractHolder holder = target.getHolder(this);
			holder.initializeViews();
			holder.updateContent();
			final View hv = holder.getView();
			//	_headerContainer.removeAllViews();
			this._headerContainer.addView(hv);
			this._headerContainer.setVisibility(View.VISIBLE);
		} catch (final RuntimeException rtex) {
			Log.e("PageFragment", "R> PageFragment.addViewtoHeader RuntimeException. " + rtex.getMessage());
			rtex.printStackTrace();
		}
		Log.i("NEOCOM", "<< AbstractPagerFragment.addViewtoHeader");
	}

	public AbstractNewPagerFragment setVariant(final EFragment filter) {
		_variant = filter;
		return this;
	}
}

// - UNUSED CODE ............................................................................................

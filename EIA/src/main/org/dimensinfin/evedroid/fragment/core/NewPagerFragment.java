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

import org.dimensinfin.android.mvc.activity.TitledFragment;
import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.core.DataSourceAdapter;
import org.dimensinfin.android.mvc.core.IMenuActionTarget;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.SplashActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.IActivityMessageBus;

import android.content.Intent;
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
public class NewPagerFragment extends TitledFragment implements PropertyChangeListener {
	// - S T A T I C - S E C T I O N
	// ..........................................................................

	// - F I E L D - S E C T I O N
	// ............................................................................
	protected int _fragmentID = -1;
	protected DataSourceAdapter _adapter = null;
	protected ArrayList<AbstractAndroidPart> _headerContents = new ArrayList<AbstractAndroidPart>();
	/**
	 * This is the object to connect to the inter fragment messaging. This
	 * matches the Activity but is ever available.
	 */
	protected IActivityMessageBus _messageBus = null;

	// - U I F I E L D S
	protected ViewGroup _container = null;
	/** The view that handles the non scrolling header. */
	protected ViewGroup _headerContainer = null;
	/**
	 * The view that represent the list view and the space managed though the
	 * adapter.
	 */
	protected ListView _listContainer = null;
	protected ViewGroup _progressLayout = null;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	/**
	 * If the user has set the identifier return the identifier set (This allows
	 * to use the Generic by code in multifragment activities) . Otherwise
	 * return the Id of the fragment that would be generated on the layout XML.
	 */
	public int getIdentifier() {
		if (this._fragmentID > 0)
			return this._fragmentID;
		else
			return getId();
	}

	public IActivityMessageBus getMessageBus() {
		return this._messageBus;
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		// logger.info(">> ManufactureContextFragment.onContextItemSelected");
		// //$NON-NLS-1$
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
		Log.i("EVEI", ">> PageFragment.onCreateContextMenu"); //$NON-NLS-1$
		// REFACTOR If we call the super then the fragment's parent activity
		// gets called. So the listcallback and the Activity
		// have not to be the same
		super.onCreateContextMenu(menu, view, menuInfo);
		// Check parameters to detect the item selected for menu target.
		if (view == this._headerContainer) {
			// Check if this fragment has the callback configured
			final AbstractAndroidPart part = this._headerContents.get(0);
			if (part instanceof IMenuActionTarget) {
				((IMenuActionTarget) part).onCreateContextMenu(menu, view, menuInfo);
			}
		}
		// if (view == this._listContainer) // Check if this fragment has the
		// callback configured
		// if (null != _listCallback) _listCallback.onCreateContextMenu(menu,
		// view, menuInfo);
		Log.i("EVEI", "<< PageFragment.onCreateContextMenu"); //$NON-NLS-1$
	}

	/**
	 * Creates the structures when the fragment is about to be shown. We have to
	 * check that the parent Activity is compatible with this kind of fragment.
	 * So the fragment has to check of it has access to a valid pilot before
	 * returning any UI element.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		Log.i("EVEI", ">> PageFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			// if (!_alreadyInitialized)
			this._container = (ViewGroup) inflater.inflate(R.layout.fragment_pager, container, false);
			this._headerContainer = (ViewGroup) this._container.findViewById(R.id.headerContainer);
			this._listContainer = (ListView) this._container.findViewById(R.id.bodyContainer);
			this._progressLayout = (ViewGroup) this._container.findViewById(R.id.progressLayout);
			// Prepare the structures for the context menu.
			registerForContextMenu(this._headerContainer);
			registerForContextMenu(this._listContainer);
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "R> PageFragment.onCreateView RuntimeException. " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("R> PageFragment.onCreateView RuntimeException. " + rtex.getMessage()));
		}
		Log.i("EVEI", "<< PageFragment.onCreateView");
		return this._container;
	}

	public void propertyChange(final PropertyChangeEvent event) {
		// TODO Auto-generated method stub

	}

	// public void onDialogNegativeClick(final DialogFragment dialog) {
	// if (null != _dialogCallback)
	// _dialogCallback.onDialogNegativeClick(dialog);
	// }
	//
	// public void onDialogPositiveClick(final DialogFragment dialog) {
	// if (null != _dialogCallback)
	// _dialogCallback.onDialogPositiveClick(dialog);
	// }

	// /**
	// * When the execution reaches this point to activate the fragment we have
	// to check that all the elements
	// * required are defined, mainly the Data Source. If the DS is ready and
	// valid then we launch the DS data
	// * loading code. If that was performed on a previous start and the DS is
	// already loaded we can skip this
	// * step. This last approach will reduce CPU usage and give a better user
	// feeling when activating and
	// * deactivating activities and fragments.
	// */
	// @Override
	// public void onStart() {
	// Log.i("PageFragment", ">> PageFragment.onStart");
	// super.onStart();
	// try {
	// // Check the validity of the data source.
	// if (null == _datasource) throw new RuntimeException("Datasource not
	// defined.");
	// if (!_alreadyInitialized) new InitializeDataSourceTask(this).execute();
	// } catch (Exception rtex) {
	// Log.e("PageFragment", "R> Runtime Exception on PageFragment.onStart." +
	// rtex.getMessage());
	// rtex.printStackTrace();
	// stopActivity(rtex);
	// }
	// // // Update the spinner counter on the actionbar.
	// // getActivity().invalidateOptionsMenu();
	// // Add the header parts once the display is initialized.
	//// if (_headerContents.size() > 0) {
	//// _headerContainer.removeAllViews();
	//// for (AbstractAndroidPart part : _headerContents)
	//// addViewtoHeader(part);
	//// }
	// Log.i("PageFragment", "<< PageFragment.onStart");
	// }

	// public void setDataSource(final IDataSource dataSource) {
	// Log.i("RESTART", "-- PagerFragment.setDataSource. Validation checkpoint
	// [" + dataSource + "]");
	// if (null != dataSource) _datasource = dataSource;
	// }

	// public void setDialogCallback(final ADialogCallback callback) {
	// if (null != callback) _dialogCallback = callback;
	// }

	public void setIdentifier(final int id) {
		this._fragmentID = id;
	}

	// public void setListCallback(final IMenuActionTarget callback) {
	// if (null != callback) _listCallback = callback;
	// }

	public void setMessageBus(final IActivityMessageBus messageBus) {
		this._messageBus = messageBus;
	}

	/**
	 * The reception of this message signals the termination of the data source
	 * processing. We can lift the spinning progress indicator and start to
	 * render the views.<br>
	 * But when the phone is tilted than the structures are still valid, no do
	 * not reinitialize them but signal them for refresh.
	 */
	public void signalHierarchyCreationCompleted() {
		if (null == this._adapter) {
			this._adapter = new DataSourceAdapter(this, new HeaderDataSourceProxy(this._messageBus));
			this._listContainer.setAdapter(this._adapter);
		} else {
			this._adapter.notifyDataSetChanged();
		}
		this._progressLayout.setVisibility(View.GONE);
		this._listContainer.setVisibility(View.VISIBLE);
		this._container.invalidate();
		this._listContainer.invalidate();
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

final class HeaderDataSourceProxy extends AbstractDataSource {

	private final IActivityMessageBus source;

	public HeaderDataSourceProxy(final IActivityMessageBus activity) {
		this.source = activity;
	}

	@Override
	public void createContentHierarchy() {
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
		return this.source.getBodyPartsHierarchy(AppWideConstants.panel.PANEL_MARKETORDERSBODY);
	}

	/**
	 * Events received should be passed to the parent datasource. This is done
	 * through the message bus connection that will pass the event messages to
	 * the data source fragment for their relay to the real datasource.
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		// this.source.propertyChange(event);
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
			fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_NEEDSREFRESH)) {
			fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE)) {
			this.source.propertyChange(event);
		}
	}
}
// - UNUSED CODE
// ............................................................................................

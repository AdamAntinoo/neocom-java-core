//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.android.mvc.core;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.R;
import org.dimensinfin.android.mvc.constants.SystemWideConstants;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class DataSourceAdapter extends BaseAdapter implements PropertyChangeListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger														logger					= Logger.getLogger("DataSourceAdapter");

	// - F I E L D - S E C T I O N ............................................................................
	protected Activity															_context				= null;
	protected IDataSource														_datasource			= null;
	protected final ArrayList<AbstractAndroidPart>	_hierarchy			= new ArrayList<AbstractAndroidPart>();
	private Fragment																_fragment				= null;
	private final HashMap<Integer, View>						_hierarchyViews	= new HashMap<Integer, View>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * The real separation of data sources requires that it is not tied to an Activity. So the base adapter has
	 * to receive both parameters on construction to be able to get Pilot based information and connect to the
	 * data source. At the same time there are two versions, one for Fragments and another for Activities.
	 * 
	 * @param activity
	 *          reference to the activity where this Adapter is tied for UI presentation.
	 * @param datasource
	 *          the source for the data to be represented on the view structures.
	 */
	public DataSourceAdapter(final AbstractContextActivity activity, final IDataSource datasource) {
		super();
		_context = activity;
		_datasource = datasource;
		if (_datasource instanceof AbstractDataSource) {
			((AbstractDataSource) _datasource).addPropertyChangeListener(this);
		}
		setModel(_datasource.getPartHierarchy());
	}

	/**
	 * The real separation of data sources requires that it is not tied to an Activity. So the base adapter has
	 * to receive both parameters on construction to be able to get Pilot based information and connect to the
	 * data source. At the same time there are two versions, one for Fragments and another for Activities.
	 * 
	 * @param fragment
	 *          reference to the fragment to where this Adapter is tied.
	 * @param datasource
	 *          the source for the data to be represented on the view structures.
	 */
	public DataSourceAdapter(final Fragment fragment, final IDataSource datasource) {
		super();
		_fragment = fragment;
		_context = _fragment.getActivity();
		_datasource = datasource;
		if (_datasource instanceof AbstractDataSource) {
			((AbstractDataSource) _datasource).addPropertyChangeListener(this);
		}
		setModel(_datasource.getPartHierarchy());
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public AbstractAndroidPart getCastedItem(final int position) {
		return _hierarchy.get(position);
	}

	public int getCount() {
		return _hierarchy.size();
	}

	public Object getItem(final int position) {
		return _hierarchy.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return _hierarchy.get(position).getModelID();
	}

	/**
	 * This method is called so many times that represent the most consuming tasks on the Activity. The
	 * optimization to not create more views than the needed ones and the reduction of code line is s must that
	 * will improve user response times.
	 */
	public View getView(final int position, View convertView, final ViewGroup parent) {
		//		logger.info("-- Getting view [" + position + "]");
		try {
			// If the request is new we are sure this has to be created.
			AbstractAndroidPart item = getCastedItem(position);
			if (null == convertView) {
				Log.i("DataSourceAdapter", "-- Getting view [" + position + "]");
				AbstractHolder holder = getCastedItem(position).getHolder(getContext());
				holder.initializeViews();
				convertView = holder.getView();
				convertView.setTag(item);
				holder.updateContent();
				// Store view on the Part.
				if (SystemWideConstants.ENABLECACHE) item.setView(convertView);
			} else {
				View cachedView = item.getView();
				if (null == cachedView) {
					Log.i("DataSourceAdapter", "-- Getting view [" + position + "]");
					// Recreate the view.
					AbstractHolder holder = getCastedItem(position).getHolder(getContext());
					holder.initializeViews();
					convertView = holder.getView();
					convertView.setTag(item);
					holder.updateContent();
					// Store view on the Part.
					if (SystemWideConstants.ENABLECACHE) item.setView(convertView);
				} else {
					// Cached view found. Return new view.
					convertView = cachedView;
					Log.i("DataSourceAdapter", "-- Getting view [" + position + "] CACHED");
				}
			}
			// Activate listeners if the Part supports that feature.
			convertView.setClickable(false);
			convertView.setLongClickable(true);
			if (item instanceof OnClickListener) {
				convertView.setClickable(true);
				convertView.setOnClickListener((OnClickListener) item);
			}
			if (item instanceof OnLongClickListener) {
				convertView.setClickable(true);
				convertView.setOnLongClickListener((OnLongClickListener) item);
			}
			// REFACTOR Add the DataSource as an event listener because that feature does not depend on the interfaces.
			item.addPropertyChangeListener(_datasource);
			return convertView;
		} catch (RuntimeException rtex) {
			String message = rtex.getMessage();
			if (null == message) message = "NullPointerException detected.";
			logger.severe("R> Runtime Exception on DataSourceAdapter.getView." + message);
			rtex.printStackTrace();
			//DEBUG Add exception registration to the exception page.
			final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			// The view is a new view. We have to fill all the items
			convertView = mInflater.inflate(R.layout.exception_4list, null);
			TextView exceptionMessage = (TextView) convertView.findViewById(R.id.exceptionMessage);
			exceptionMessage.setText("X> EveBaseAdapter.getView] " + message);
			return convertView;
		}
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void notifyDataSetChanged() {
		setModel(_datasource.getPartHierarchy());
		super.notifyDataSetChanged();
	}

	/**
	 * Send messages to the parent activity that is the one that has code implemented for every different case.
	 * This class is a generic class that must not be upgraded because we start then to replicate most of the
	 * code.
	 */
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES)) {
			notifyDataSetChanged();
		}
	}

	protected Activity getContext() {
		return _context;
	}

	protected void setModel(final ArrayList<AbstractAndroidPart> partData) {
		_hierarchy.clear();
		_hierarchyViews.clear();
		_hierarchy.addAll(partData);
	}

	/**
	 * This block optimizes the use of the views. Any structure update will clear the cache but any request that
	 * matches the id of the content will be returned from this resource list instead always creating a new
	 * resource.
	 * 
	 * @param convertView
	 * @param position
	 * @return
	 */
	private View searchCachedView(final int position, final View convertView) {
		AbstractAndroidPart item = getCastedItem(position);
		long modelid = item.getModelID();
		View hit = _hierarchyViews.get(position);
		if (null != hit) {
			// Check that the view belongs to the same item.
			Object tag = convertView.getTag();
			if (tag instanceof AbstractAndroidPart) {
				long viewModelid = ((AbstractAndroidPart) tag).getModelID();
				if (modelid == viewModelid)
					return hit;
				else {
					// Clear this element on the cache because does not match.
					_hierarchyViews.remove(position);
				}
			}
		}
		return null;
	}
}
// - UNUSED CODE ............................................................................................

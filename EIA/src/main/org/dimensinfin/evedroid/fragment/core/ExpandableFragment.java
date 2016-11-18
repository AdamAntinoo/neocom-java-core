//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.fragment.core;

// - IMPORT SECTION .........................................................................................
import java.text.DecimalFormat;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.DataSourceAdapter;
import org.dimensinfin.android.mvc.interfaces.IDataSource;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.SplashActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class ExpandableFragment extends Fragment {
	//- CLASS IMPLEMENTATION ...................................................................................
	private class InitializeDataSource extends AsyncTask<Fragment, Void, Void> {

		// - F I E L D - S E C T I O N ............................................................................
		private final Fragment fragment;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public InitializeDataSource(final Fragment fragment) {
			this.fragment = fragment;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		@Override
		protected Void doInBackground(final Fragment... arg0) {
			// Create the hierarchy structure to be used on the Adapter.
			if (null != _datasource) {
				_datasource.createContentHierarchy();
				_alreadyInitialized = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			if (null != _datasource) {
				_adapter = new DataSourceAdapter(fragment, _datasource);
				_fragmentList.setAdapter(_adapter);

				_progress.setVisibility(View.GONE);
				_progressCounter.setVisibility(View.VISIBLE);
				_progressCounter.setText(contentCounter.format(_datasource.getItemsCount()));
				_container.invalidate();
			}
			super.onPostExecute(result);
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	public static Logger					logger							= Logger.getLogger("ExpandableFragment");
	protected static Typeface			daysFace						= Typeface
			.createFromAsset(EVEDroidApp.getSingletonApp().getApplicationContext().getAssets(), "fonts/Days.otf");
	private static DecimalFormat	contentCounter			= new DecimalFormat("0");

	// - F I E L D - S E C T I O N ............................................................................
	private int										_fragmentID					= AppWideConstants.fragment.FRAGMENT_DEFAULTID_EMPTY;
	private IDataSource						_datasource					= null;
	private DataSourceAdapter			_adapter						= null;
	private String								_labelContent				= "<label>";
	private boolean								_alreadyInitialized	= false;

	// - U I    F I E L D S
	private ViewGroup							_container					= null;
	private ListView							_fragmentList				= null;
	private ProgressBar						_progress						= null;
	private TextView							_fragmentLabel			= null;
	private TextView							_progressCounter		= null;
	private boolean								_expand							= false;
	private ImageView							_rightArrow					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * If the user has set the identifier return the identifier set (This allows to use the Generic by code in
	 * multifragment activities) . Otherwise return the Id of the fragment that would be generated on the XML
	 * layout.
	 */
	public int getIdentifier() {
		if (_fragmentID > 0)
			return _fragmentID;
		else
			return getId();
	}

	/**
	 * Creates the fragment layout and stores references to the key elements. The fragment layout is a generic
	 * layout that is able to be expanded or collapsed. The key elements are the fragment list container
	 * <code>_fragmentList</code>, the fragment title to be shown when collapsed (<code>_fragmentLabel</code>),
	 * the progress elements that show the number of elements on the fragment or the spinning image and the
	 * right arrow that shows the expanded/collapsed state.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		logger.info(">> ExpandableFragment.onCreateView");
		View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			if (!_alreadyInitialized) {
				_container = (ViewGroup) inflater.inflate(R.layout.fragment_expandable, container, false);
			}
			_fragmentList = (ListView) _container.findViewById(R.id.fragmentList);
			_progress = (ProgressBar) _container.findViewById(R.id.progress);

			_fragmentLabel = (TextView) _container.findViewById(R.id.fragmentLabel);
			_progressCounter = (TextView) _container.findViewById(R.id.progressCounter);
			_rightArrow = (ImageView) _container.findViewById(R.id.rightArrow);

			_container.setOnClickListener(new View.OnClickListener() {
				public void onClick(final View v) {
					if (_expand) {
						_fragmentList.setVisibility(View.GONE);
						_expand = !_expand;
						_rightArrow.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.arrowright));
					} else {
						_fragmentList.setVisibility(View.VISIBLE);
						_expand = !_expand;
						_rightArrow.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.arrowleft));
					}
				}
			});
			_progress.setVisibility(View.VISIBLE);
			_progressCounter.setVisibility(View.GONE);
			_fragmentLabel.setText(_labelContent);

			_fragmentLabel.setTypeface(daysFace);
			_progressCounter.setTypeface(daysFace);
		} catch (RuntimeException rtex) {
			logger.info("E> PilotShipListFragment.onCreateView RuntimeException. " + rtex.getMessage());
			return null;
		}
		logger.info("<< ExpandableFragment.onCreateView");
		return _container;
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
		logger.info(">> ExpandableFragment.onStart");
		super.onStart();
		try {
			// Check the validity of the data source.
			if (null == _datasource) throw new RuntimeException("Datasource not defined.");
			if (!_alreadyInitialized) {
				new InitializeDataSource(this).execute();
			}
		} catch (Exception rtex) {
			logger.severe("R> ExpandableFragment.onStart RuntimeException. " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("R> ExpandableFragment.onStart RuntimeException. " + rtex.getMessage()));
		}
		logger.info("<< ExpandableFragment.onStart");
	}

	public void setDataSource(final IDataSource targetds) {
		_datasource = targetds;
	}

	public void setIdentifier(final int id) {
		_fragmentID = id;
	}

	public void setLabel(final String label) {
		_labelContent = label;
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
		intent.putExtra(AppWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
		startActivity(intent);
	}
}
// - UNUSED CODE ............................................................................................

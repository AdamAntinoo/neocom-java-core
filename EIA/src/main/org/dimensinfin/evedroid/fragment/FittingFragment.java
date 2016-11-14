//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.fragment;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.FittingActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.fragment.core.PagerFragment;
import org.dimensinfin.evedroid.model.EveItem;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Fragment implementation that will get some input form the user to select a fitting and a count of copies to
 * calculate the item requirements to cover that request. By default fittings are matched against the GARAGE
 * function Location. The GARAGE function may not be unique. If that case the matching should be against each
 * of the GARAGE locations.
 * 
 * @author Adam Antinoo
 */
public class FittingFragment extends PagerFragment {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger	= Logger.getLogger("FittingFragment");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	//	public ViewGroup getPageLayout() {
	//		return _container;
	//	}

	/**
	 * Creates the structures when the fragment is about to be shown. We have to check that the parent Activity
	 * is compatible with this kind of fragment. So the fragment has to check of it has access to a valid pilot
	 * before returning any UI element.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		logger.info(">> FittingFragment.onCreateView");
		// Create the standard structure for a page fragment.
		View currentView = super.onCreateView(inflater, container, savedInstanceState);

		// Add a header part to the header container.
		FittingHeaderPart header = new FittingHeaderPart(null);
		AbstractHolder holder = header.getHolder(this);
		if (holder instanceof FittingHeaderHolder) {
			//			((FittingHeaderHolder) holder).setView(currentView);
			holder.initializeViews();
			holder.updateContent();
			_headerContainer.removeAllViews();
			_headerContainer.addView(holder.getView());
			_headerContainer.invalidate();
		}
		return currentView;
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class FittingHeaderHolder extends AbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	// - F I E L D - S E C T I O N ............................................................................
	//	private ITheme	_theme					= null;

	private Spinner	fittingsSpinner	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingHeaderHolder(final FittingHeaderPart target, final Activity context) {
		super(target, context);
		//		_theme = new RubiconRedTheme(context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public FittingHeaderPart getPart() {
		return (FittingHeaderPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		// Create spinner contents from part.
		fittingsSpinner = (Spinner) _convertView.findViewById(R.id.fittingspinner);
		if (null != fittingsSpinner) {
			fittingsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position,
						final long id) {
					Object item = fittingsSpinner.getSelectedItem();
					if (item instanceof String) {
						String itemName = (String) item;
						Toast toast = Toast.makeText(getPart().getActivity(), "Item selected: " + itemName, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
						toast.show();
					}
				}

				public void onNothingSelected(final AdapterView<?> parentView) {
					// your code here
				}

			});
			List<String> ships = getPart().getFittings();
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, ships);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			fittingsSpinner.setAdapter(dataAdapter);
		}
	}

	public void setView(final View newView) {
		_convertView = newView;
	}

	@Override
	public void updateContent() {
		super.updateContent();
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.fitting_4header, null);
		_convertView.setTag(this);
	}

	protected void loadEveIcon(final ImageView targetIcon, final int typeID) {
		if (null != targetIcon) {
			final String link = EVEDroidApp.getTheCacheConnector().getURLForItem(typeID);
			final Drawable draw = EVEDroidApp.getTheCacheConnector().getCacheDrawable(link, targetIcon);
			targetIcon.setImageDrawable(draw);
		}
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class FittingHeaderPart extends EveAbstractPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -4642153502498052929L;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingHeaderPart(final AbstractGEFNode item) {
		super(item);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public EveItem getCastedModel() {
		return (EveItem) getModel();
	}

	public List<String> getFittings() {
		List<String> fittings = new ArrayList<String>();

		// Get a reference to the special DataSource.
		if (getActivity() instanceof FittingActivity) {
			FittingActivity act = (FittingActivity) getActivity();
			FittingsDataSource ds = (FittingsDataSource) act.getDataSource(AppWideConstants.fragment.FRAGMENT_FITTINGS);
			fittings.addAll(ds.getFittings());
		}
		return fittings;
	}

	public long getModelID() {
		return 0;
	}

	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		return new FittingHeaderHolder(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................

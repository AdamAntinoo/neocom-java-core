//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.fragment;

// - IMPORT SECTION .........................................................................................
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.core.DataSourceAdapter;
import org.dimensinfin.android.mvc.core.IActivityCallback;
import org.dimensinfin.android.mvc.core.IDataSource;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.core.SafeStopActivity;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.BundlesAndMessages;
import org.dimensinfin.evedroid.market.MarketDataSet;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.theme.ITheme;
import org.dimensinfin.evedroid.theme.RubiconRedTheme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class MiningSessionFragment extends Fragment {
	private class InitiateModelUItask extends AsyncTask<Fragment, Void, Void> {

		private final Fragment	fragment;

		public InitiateModelUItask(final Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		protected Void doInBackground(final Fragment... arg0) {
			// Create the hierarchy structure to be used on the Adapter.
			if (null != _datasource) _datasource.createContentHierarchy();
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			if (null != _datasource) {
				_adapter = new DataSourceAdapter(fragment, _datasource);
				_listContainer.setAdapter(_adapter);

				_progress.setVisibility(View.GONE);
				_listContainer.setVisibility(View.VISIBLE);
				_container.invalidate();
			}
			super.onPostExecute(result);
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger				logger					= Logger.getLogger("MiningSessionFragment");

	// - F I E L D - S E C T I O N ............................................................................
	private int									_fragmentID			= -1;
	private IDataSource					_datasource			= null;
	protected DataSourceAdapter	_adapter				= null;

	// - U I    F I E L D S
	protected ViewGroup					_container			= null;
	protected TextView					_label					= null;
	protected ListView					_listContainer	= null;
	protected ProgressBar				_progress				= null;
	private EditText						volumeData			= null;
	private EditText						cycleTimeData		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getIdentifier() {
		return AppWideConstants.fragment.FRAGMENT_MININGSESSIONS;
	}

	public ViewGroup getPageLayout() {
		return _container;
	}

	/**
	 * Creates the structures when the fragment is about to be shown. We have to check that the parent Activity
	 * is compatible with this kind of fragment. So the fragment has to check of it has access to a valid pilot
	 * before returning any UI element.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		logger.info(">> MiningSessionFragment.onCreateView");
		super.onCreateView(inflater, container, savedInstanceState);
		try {
			_container = (ViewGroup) inflater.inflate(R.layout.activity_miningsession, container, false);
			_label = (TextView) _container.findViewById(R.id.labelAsteroids);
			_listContainer = (ListView) _container.findViewById(R.id.fragmentList);
			_progress = (ProgressBar) _container.findViewById(R.id.progress);

			if (null == _container)
				stopActivity("E> MiningSessionFragment.onCreateView RuntimeException. " + "Container not found.");
			if (null == _label)
				stopActivity("E> MiningSessionFragment.onCreateView RuntimeException. " + "LabelStacks not found.");
			if (null == _listContainer)
				stopActivity("E> MiningSessionFragment.onCreateView RuntimeException. " + "ListContainer not found.");
		} catch (RuntimeException rtex) {
			logger.info("E> MiningSessionFragment.onCreateView RuntimeException. " + rtex.getMessage());
			stopActivity("E> MiningSessionFragment.onCreateView RuntimeException. " + rtex.getMessage());
		}
		logger.info("<< MiningSessionFragment.onCreateView");
		return _container;
	}

	/**
	 * At this level only accesses the data elements and stores them on the instance. Startup of the data
	 * collecting and the adapter connections is done on higher classes.
	 */
	@Override
	public void onStart() {
		logger.info(">> MiningSessionFragment.onStart");
		super.onStart();

		// Identify and prepare the page interface.
		volumeData = (EditText) _container.findViewById(R.id.volumeData);
		cycleTimeData = (EditText) _container.findViewById(R.id.cycleTimeData);
		try {
			// Check the validity of the parent activity.
			if (getActivity() instanceof IActivityCallback) {
				_datasource = ((IActivityCallback) getActivity()).getDataSource(this.getIdentifier());
			}
			// Launch a asynch task to update the UI, get the data records and the replace the UI with the list of views.
			new InitiateModelUItask(this).execute();
		} catch (Exception rtex) {
			logger.severe("R> Runtime Exception on PageFragment.onStart." + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity("R> Runtime Exception on PageFragment.onStart." + rtex.getMessage());
		}
		logger.info("<< MiningSessionFragment.onStart");
	}

	public void setIdentifier(final int id) {
		_fragmentID = id;
	}

	/**
	 * For really unrecoverable or undefined exceptions the application should go to a safe spot. That spot is
	 * defined by the application so this is another abstract method.
	 * 
	 * @param exception
	 */
	protected void stopActivity(final String message) {
		final Intent intent = new Intent(getActivity(), SafeStopActivity.class);
		// Pass the user message to the activity for display.
		intent.putExtra(AppWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, message);
		startActivity(intent);
	}

	private EveItem getItem() {
		return EVEDroidApp.getAppContext().getItem();
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class MiningHeaderHolder extends AbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	//		private static Logger	logger						= Logger.getLogger("ItemHeaderHolder");

	// - F I E L D - S E C T I O N ............................................................................
	private ITheme	_theme						= null;

	public TextView	itemName					= null;
	public TextView	bestSellPrice			= null;
	public TextView	bestSellLocation	= null;
	public TextView	bestBuyPrice			= null;
	public TextView	bestBuyLocation		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MiningHeaderHolder(final MiningHeaderPart target, final Activity context) {
		super(target, context);
		setTheme(new RubiconRedTheme(context));
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public MiningHeaderPart getPart() {
		return (MiningHeaderPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		itemName = (TextView) _convertView.findViewById(R.id.itemName);
		bestSellPrice = (TextView) _convertView.findViewById(R.id.bestSellPrice);
		bestSellLocation = (TextView) _convertView.findViewById(R.id.bestSellLocation);
		bestBuyPrice = (TextView) _convertView.findViewById(R.id.bestBuyPrice);
		bestBuyLocation = (TextView) _convertView.findViewById(R.id.bestBuyLocation);

		itemName.setTypeface(_theme.getThemeTextFont());
		bestSellPrice.setTypeface(_theme.getThemeTextFont());
		bestBuyPrice.setTypeface(_theme.getThemeTextFont());
	}

	public void setView(final View newView) {
		_convertView = newView;
	}

	@Override
	public void updateContent() {
		super.updateContent();
		itemName.setText(getPart().get_itemName());
		bestSellPrice.setText(getPart().get_bestSellPrice());
		bestSellLocation.setText(Html.fromHtml(getPart().get_bestSellLocation()));
		bestBuyPrice.setText(getPart().get_bestBuyPrice());
		bestBuyLocation.setText(Html.fromHtml(getPart().get_bestBuyLocation()));

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getItemID());
	}

	@Override
	protected void createView() {
	}

	protected void loadEveIcon(final ImageView targetIcon, final int typeID) {
		if (null != targetIcon) {
			final String link = EVEDroidApp.getTheCacheConnector().getURLForItem(typeID);
			final Drawable draw = EVEDroidApp.getTheCacheConnector().getCacheDrawable(link, targetIcon);
			targetIcon.setImageDrawable(draw);
		}
	}

	private void setTheme(final ITheme theme) {
		_theme = theme;
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class MiningHeaderPart extends AbstractAndroidPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long								serialVersionUID	= 8910031270706432316L;
	private static Logger										logger						= Logger.getLogger("ItemHeaderPart");
	protected static DecimalFormat					priceFormatter		= new DecimalFormat("###,###.00 ISK");
	private static HashMap<String, String>	securityLevels		= new HashMap<String, String>();
	static {
		securityLevels.put("1.0", "#2FEFEF");
		securityLevels.put("0.9", "#48F0C0");
		securityLevels.put("0.8", "#00EF47");
		securityLevels.put("0.7", "#00F000");
		securityLevels.put("0.6", "#8FEF2F");
		securityLevels.put("0.5", "#EFEF00");
		securityLevels.put("0.4", "#D77700");
		securityLevels.put("0.3", "#F06000");
		securityLevels.put("0.2", "#F04800");
		securityLevels.put("0.1", "#D73000");
		securityLevels.put("0.0", "#F00000");
	}

	// - F I E L D - S E C T I O N ............................................................................
	private MarketDataSet										selldata					= null;
	private MarketDataSet										buydata						= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MiningHeaderPart(final AbstractGEFNode item) {
		super(item);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//	public int get_assetItemID() {
	//		return getCastedModel().getItemID();
	//	}

	public String get_bestBuyLocation() {
		StringBuffer htmlLocation = new StringBuffer();
		buydata = getBuyData();
		String security = buydata.getBestMarket().getHubSecurity();
		String secColor = securityLevels.get(security);
		if (null == secColor) secColor = "#2FEFEF";
		htmlLocation.append("<font color='").append(secColor).append("'>").append(security).append("</font>");
		htmlLocation.append(" ").append(buydata.getBestMarket().getHubRegion()).append("-")
				.append(buydata.getBestMarket().getHubName());
		return htmlLocation.toString();
	}

	public String get_bestBuyPrice() {
		buydata = getBuyData();
		double price = buydata.getBestMarket().getPrice();
		String qtyString = priceFormatter.format(price);
		return qtyString;
	}

	public String get_bestSellLocation() {
		StringBuffer htmlLocation = new StringBuffer();
		selldata = getSellData();
		String security = selldata.getBestMarket().getHubSecurity();
		String secColor = securityLevels.get(security);
		if (null == secColor) secColor = "#2FEFEF";
		htmlLocation.append("<font color='").append(secColor).append("'>").append(security).append("</font>");
		htmlLocation.append(" ").append(selldata.getBestMarket().getHubRegion()).append("-")
				.append(selldata.getBestMarket().getHubName());
		return htmlLocation.toString();
	}

	public String get_bestSellPrice() {
		selldata = getBuyData();
		double price = selldata.getBestMarket().getPrice();
		String qtyString = priceFormatter.format(price);
		return qtyString;
	}

	//	public String get_count() {
	//		long quantity = getCastedModel().getCount();
	//		DecimalFormat formatter = new DecimalFormat("###,###");
	//		String qtyString = formatter.format(quantity);
	//		return qtyString;
	//	}

	public String get_itemName() {
		return getCastedModel().getName();
	}

	//	public long getAssetID() {
	//		return getCastedModel().getAssetID();
	//	}

	public EveItem getCastedModel() {
		return (EveItem) getModel();
	}

	@Override
	public AbstractHolder getHolder(final Activity activity) {
		_activity = activity;
		return new MiningHeaderHolder(this, _activity);
	}

	public AbstractHolder getHolder(final Fragment fragment) {
		_fragment = fragment;
		_activity = fragment.getActivity();
		return new MiningHeaderHolder(this, _activity);
	}

	public long getModelID() {
		return getCastedModel().getItemID();
	}

	private MarketDataSet getBuyData() {
		if (null == buydata)
			buydata = AppConnector.getStorageConnector().searchMarketDataByID(getCastedModel().getItemID(),
					BundlesAndMessages.marketSide.BUYER);
		return buydata;
	}

	private MarketDataSet getSellData() {
		if (null == selldata)
			selldata = AppConnector.getStorageConnector().searchMarketDataByID(getCastedModel().getItemID(),
					BundlesAndMessages.marketSide.SELLER);
		return selldata;
	}
}

// - UNUSED CODE ............................................................................................

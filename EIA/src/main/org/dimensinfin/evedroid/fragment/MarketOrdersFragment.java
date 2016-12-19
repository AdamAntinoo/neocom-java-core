//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.fragment;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.enums.EVARIANT;
import org.dimensinfin.evedroid.fragment.core.AbstractPagerFragment;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.model.MarketOrderAnalyticalGroup;
import org.dimensinfin.evedroid.model.NeoComMarketOrder;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.part.MarketOrderAnalyticalGroupPart;
import org.dimensinfin.evedroid.part.MarketOrderPart;
import org.dimensinfin.evedroid.part.ResourcePart;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class MarketOrdersFragment extends AbstractPagerFragment {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> MarketOrdersFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			this.setIdentifier(AppWideConstants.fragment.FRAGMENT_MARKETORDERS);
			this.setTitle(this.getPilotName());
			this.setSubtitle("Market Orders");
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> MarketOrdersFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> MarketOrdersFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< MarketOrdersFragment.onCreateView");
		return theView;
	}

	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> MarketOrdersFragment.onStart");
		try {
			if (!_alreadyInitialized) this.setDataSource(new MarketOrdersDataSource(EVEDroidApp.getAppStore()));
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> MarketOrdersFragment.onStart - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> MarketOrdersFragment.onStart - " + rtex.getMessage()));
		}
		super.onStart();
		Log.i("NEOCOM", "<< MarketOrdersFragment.onStart");
	}
}

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This datasource will generate the list of market orders. They will be grouped into headers by their class
 * like the scheduled buys or sells, pending or completed. After the class grouping they are ordered by
 * location so all operations related to a location are set together. This is not used for completed that
 * instead are ordered by date from most recent to most old.
 * 
 * @author Adam Antinoo
 */
final class MarketOrdersDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long											serialVersionUID	= -8071069909661621102L;

	// - F I E L D - S E C T I O N ............................................................................
	private AppModelStore													_store						= null;
	private ArrayList<MarketOrderAnalyticalGroup>	analyticalGroups	= new ArrayList<MarketOrderAnalyticalGroup>();
	private final ArrayList<AbstractGEFNode>			modelList					= new ArrayList<AbstractGEFNode>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketOrdersDataSource(final AppModelStore store) {
		super();
		if (null != store) _store = store;
	}

	public RootNode collaborate2Model() {
		// TODO Auto-generated method stub
		return null;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The hierarchy contains two levels of elements. The first level are the market hubs where the user has to
	 * launch the market actions. The second level are the actions pending ordered by registration date.<br>
	 * During the processing of the orders there is an aggregation of the scheduled buys so all the schedule4d
	 * orders are presented as a single order with all the quantities summed up.
	 */
	@Override
	public void createContentHierarchy() {
		Log.i("EVEI", ">> MarketOrdersDataSource.createHierarchy");
		super.createContentHierarchy();

		// Get the full model from the character. The model already arrives with all the hierarchy developed.
		analyticalGroups = _store.getPilot().accessMarketOrders();
		// Get the modules ready for sell and add them to the new group.
		final MarketOrderAnalyticalGroup scheduledSellGroup = _store.getPilot().accessModules4Sell();
		analyticalGroups.add(scheduledSellGroup);
		Log.i("EVEI", "<< MarketOrdersDataSource.createHierarchy");
	}

	/**
	 * Generate the part hierarchy to be displayed on the ListView. This comes from the current model where all
	 * the objects have generated their corresponding model elements that are the current model structures. Any
	 * change to the model will be updated and then generates a new model that is rendered again to the Part
	 * list.
	 */
	@Override
	public ArrayList<AbstractAndroidPart> getBodyParts() {
		// Update the model structures and hierarchy before creating the Part hierarchy.
		this.updateModel();

		// Create the hierarchy from the model list.
		final ArrayList<AbstractAndroidPart> hierarchy = new ArrayList<AbstractAndroidPart>();
		for (final IGEFNode node : modelList) {
			if (node instanceof MarketOrderAnalyticalGroup) {
				MarketOrderAnalyticalGroupPart mopart = new MarketOrderAnalyticalGroupPart((MarketOrderAnalyticalGroup) node);
				hierarchy
						.add((AbstractAndroidPart) mopart.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPMARKETANALYTICAL));
			}
			if (node instanceof NeoComMarketOrder)
				hierarchy.add((AbstractAndroidPart) new MarketOrderPart((NeoComMarketOrder) node)
						.setRenderMode(AppWideConstants.rendermodes.RENDER_MARKETORDER));
			if (node instanceof Resource) hierarchy.add((AbstractAndroidPart) new ResourcePart((Resource) node)
					.setRenderMode(AppWideConstants.rendermodes.RENDER_MARKETORDERSCHEDULEDSELL));
			if (node instanceof Separator) hierarchy.add((AbstractAndroidPart) new GroupPart((Separator) node)
					.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPMARKETSIDE));
		}
		_adapterData = hierarchy;
		return hierarchy;
	}

	/**
	 * Generate the part list for the body. This list maybe or not generated before this request. Timing will be
	 * checked when the implementation is running.
	 * 
	 * @param panelMarketordersbody
	 * @return
	 */
	public ArrayList<AbstractAndroidPart> getBodyPartsHierarchy(final int panelMarketordersbody) {
		final ArrayList<AbstractAndroidPart> hierarchy = new ArrayList<AbstractAndroidPart>();
		// Order the groups on the defined weight order.
		Collections.sort(analyticalGroups, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_WEIGHT));
		// Add all the collaborations to the output list
		modelList.clear();
		for (final MarketOrderAnalyticalGroup group : analyticalGroups)
			modelList.addAll(group.collaborate2Model(EVARIANT.DEFAULT_VARIANT.name()));

		// Create the hierarchy from the model list.
		for (final IGEFNode node : modelList) {
			if (node instanceof MarketOrderAnalyticalGroup) {
				MarketOrderAnalyticalGroupPart mopart = new MarketOrderAnalyticalGroupPart((MarketOrderAnalyticalGroup) node);
				// Depending on the coded name of the group we can use different renders.
				//			if(node.getTitleresourceId()==R.string.Finished)
				//				hierarchy.add(mopart
				//						.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPMARKETANALYTICAL));
				//			else	
				hierarchy
						.add((AbstractAndroidPart) mopart.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPMARKETANALYTICAL));
			}
			if (node instanceof NeoComMarketOrder)
				hierarchy.add((AbstractAndroidPart) new MarketOrderPart((AbstractGEFNode) node)
						.setRenderMode(AppWideConstants.rendermodes.RENDER_MARKETORDER));
			if (node instanceof Resource) hierarchy.add((AbstractAndroidPart) new ResourcePart((Resource) node)
					.setRenderMode(AppWideConstants.rendermodes.RENDER_MARKETORDERSCHEDULEDSELL));
			if (node instanceof Separator) hierarchy.add((AbstractAndroidPart) new GroupPart((Separator) node)
					.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPMARKETSIDE));
		}
		_adapterData = hierarchy;
		return hierarchy;
	}

	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE)) {
			this.createContentHierarchy();
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_NEEDSREFRESH))
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		//		if (event.getPropertyName().equalsIgnoreCase(AbstractGEFNode.CHILD_REMOVED_PROP))
		//			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
		//					event.getNewValue());
	}

	protected void updateModel() {
		// Order the groups on the defined weight order.
		Collections.sort(analyticalGroups, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_WEIGHT));
		// Add all the collaborations to the output list
		modelList.clear();
		for (final MarketOrderAnalyticalGroup group : analyticalGroups)
			modelList.addAll(group.collaborate2Model(EVARIANT.DEFAULT_VARIANT.name()));
	}

	//@Override
	//public ArrayList<AbstractAndroidPart> getHeaderPartsHierarchy(int panelMarketordersbody) {
	//	return new ArrayList<AbstractAndroidPart>();
	//}

}

// - UNUSED CODE ............................................................................................

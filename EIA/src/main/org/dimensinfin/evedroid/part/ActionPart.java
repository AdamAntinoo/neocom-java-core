//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.IndustryT2Activity;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.EIndustryGroup;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.enums.ETaskCompletion;
import org.dimensinfin.evedroid.interfaces.IItemPart;
import org.dimensinfin.evedroid.model.Action;
import org.dimensinfin.evedroid.model.Asset;
import org.dimensinfin.evedroid.model.EveTask;
import org.dimensinfin.evedroid.render.ActionRender;
import org.dimensinfin.evedroid.render.SkillRender;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

// - CLASS IMPLEMENTATION ...................................................................................
public class ActionPart extends EveAbstractPart implements IItemPart, OnClickListener, IMenuActionTarget {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 6148259479329269362L;

	// - F I E L D - S E C T I O N ............................................................................
	private long							blueprintID				= -1;
	private boolean						clickOverride			= false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ActionPart(final AbstractComplexNode node) {
		super(node);
		// Set the expanded state by default
		getCastedModel().setExpanded(false);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void createHierarchy() {
		clean();
		for (final EveTask t : getCastedModel().getTasks()) {
			addChild(new TaskPart(t));
		}
	}

	public String get_balance() {
		final double price = getCastedModel().getPrice();
		final double bal = (price * getCastedModel().getRequestQty()) / 1000.0;
		if (bal > 1000000.0) {
			final DecimalFormat formatter = new DecimalFormat("###,###M ISK");
			final String balanceString = formatter.format(bal / 1000.0);
			return balanceString;
		}
		final DecimalFormat formatter = new DecimalFormat("###,###K ISK");
		final String balanceString = formatter.format(bal);
		return "- " + balanceString;
	}

	public String get_category() {
		return getCastedModel().getCategory();
	}

	public String get_cost() {
		final double price = getCastedModel().getPrice();
		final DecimalFormat formatter = new DecimalFormat("###,###.0# ISK");
		final String costString = formatter.format(price);
		return costString;
	}

	public String get_group() {
		return getCastedModel().getGroupName();
	}

	public String get_itemName() {
		if (AppWideConstants.DEVELOPMENT)
			return getCastedModel().getItemName() + " #[" + getCastedModel().getTypeID() + "]";
		else
			return getCastedModel().getItemName();
	}

	public String get_qtyRequired() {
		return qtyFormatter.format(getCastedModel().getCompletedQty()) + "/"
				+ qtyFormatter.format(getCastedModel().getRequestQty());
	}

	public long getBlueprintID() {
		return blueprintID;
	}

	public Action getCastedModel() {
		try {
			return (Action) getModel();
		} catch (final RuntimeException rtex) {
			rtex.printStackTrace();
		}
		return (Action) getModel();
	}

	public String getCategory() {
		return getCastedModel().getCategory();
	}

	public int getCompletedQty() {
		return getCastedModel().getCompletedQty();
	}

	public String getGroup() {
		return getCastedModel().getGroupName();
	}

	public EIndustryGroup getIndustryGroup() {
		return getCastedModel().getItemIndustryGroup();
	}

	@Override
	public long getModelID() {
		return getCastedModel().getTypeID();
	}

	public String getName() {
		return getCastedModel().getItemName();
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartChildren() {
		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		//		result.add(this);
		// Add the children only if the model is expanded.
		if (isExpanded()) {
			Vector<AbstractPropertyChanger> ch = getChildren();
			for (final AbstractPropertyChanger node : ch) {
				// Convert the node to a part.
				final AbstractAndroidPart part = (AbstractAndroidPart) node;
				result.add(part);
				// Check if the node is expanded. Then add its children.
				if (part.isExpanded()) {
					final ArrayList<AbstractAndroidPart> grand = part.getPartChildren();
					result.addAll(grand);
				}
			}
		}
		return result;
	}

	public int getRequestQty() {
		return getCastedModel().getRequestQty();
	}

	public int getSkillLevel() {
		return getPilot().getSkillLevel(getCastedModel().getTypeID());
	}

	/**
	 * Check if all the actions inside are completed (AVAILABLE).
	 * 
	 * @return
	 */
	public ETaskCompletion isCompleted() {
		return getCastedModel().isCompleted();
	}

	public void onClick(final View view) {
		if (!clickOverride) {
			// Clean the view to force an update.
			invalidate();
			toggleExpanded();
			fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
			clickOverride = false;
		}
	}

	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		// Process the command depending on the menu and the item selected
		switch (menuItemIndex) {
			case R.id.reactionmaterialreaction:
				getPilot().putAction4Item(getCastedModel().getTypeID(), "REACTION");
				break;
			case R.id.refinedmaterialrefine:
				getPilot().putAction4Item(getCastedModel().getTypeID(), item.getTitle().toString());
				break;
			case R.id.reactionmaterialbuy:
				getPilot().putAction4Item(getCastedModel().getTypeID(), "BUY");
				break;
			case R.id.refinedmaterialbuy:
				getPilot().putAction4Item(getCastedModel().getTypeID(), "BUY");
				break;
			case R.id.componentbuild:
				getPilot().putAction4Item(getCastedModel().getTypeID(), "BUILD");
				break;
			case R.id.componentbuy:
				getPilot().putAction4Item(getCastedModel().getTypeID(), "BUY");
				break;

			default:
				break;
		}
		invalidate();
		// REFACTOR The event fires a EVENTSTRUCTURE_NEEDSREFRESH that is not
		// processed by the different event managers.
		fireStructureChange(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE, this, this);
		return true;
	}

	/**
	 * This method is activated when the user makes a long click on any list element. There are two solutions,
	 * or to use a contextual menu or to activate a long click listener (performed by implementing the
	 * corresponding interface). The contextual menu allows for a better control os the interaction because
	 * allows to create a selection menu or a dialog.
	 */
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		// Clear click detection.
		clickOverride = false;
		// get the industry group to determine the right actions.
		final EIndustryGroup industryGroup = getCastedModel().getItemIndustryGroup();
		switch (industryGroup) {
			case COMPONENTS:
			case ITEMS:
				getActivity().getMenuInflater().inflate(R.menu.actioncomponent_menu, menu);
				break;
			case REFINEDMATERIAL:
				getActivity().getMenuInflater().inflate(R.menu.actionrefinedmaterial_menu, menu);
				break;
			case REACTIONMATERIALS:
				getActivity().getMenuInflater().inflate(R.menu.actionreactionmaterial_menu, menu);
				break;
			case PLANETARYMATERIALS:
				getActivity().getMenuInflater().inflate(R.menu.actionplanetarymaterial_menu, menu);
				break;
			case BLUEPRINT:
				// Identify a blueprint of the correct type and open the Invention
				// activity.
				final ArrayList<EveTask> tasks = getCastedModel().getTasks();
				final Asset asset = tasks.get(0).getReferencedAsset();
				// Check this is a T2 blueprint and then get its T1 version.
				if (asset.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
					final ArrayList<Integer> ids = AppConnector.getDBConnector()
							.searchInventionableBlueprints(Integer.valueOf(asset.getTypeID()).toString());
					// The first element is the blueprint that invents the target.
					// Now search for an asset of that type.
					final Integer t1bpid = ids.get(0);
					final ArrayList<Asset> targetbpassetid = AppConnector.getDBConnector()
							.searchAsset4Type(getPilot().getCharacterID(), t1bpid);
					if (targetbpassetid.size() > 0) {
						final Asset targetAsset = targetbpassetid.get(0);
						final Intent intent = new Intent(getActivity(), IndustryT2Activity.class);
						intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID, getPilot().getCharacterID());
						intent.putExtra(AppWideConstants.extras.EXTRA_BLUEPRINTID,
								Long.valueOf(targetAsset.getAssetID()).longValue());
						intent.putExtra(AppWideConstants.extras.EXTRA_BLUEPRINTACTIVITY, ModelWideConstants.activities.INVENTION);
						getActivity().startActivity(intent);

						// Event consumed. Override the click.
						clickOverride = true;
					}
				}
				// final InventionJobDialog dialog = new InventionJobDialog();
				// // REFACTOR I have to search for a real blueprint with this type
				// at the current location to fill this hole.
				// dialog.setBlueprint(getCastedModel().getResource());
				// // final BlueprintPart self = this;
				// // PagerFragment frag = (PagerFragment) getFragment();
				// // dialog.setFragment(frag);
				// dialog.setDialogCallback(new ADialogCallback() {
				//
				// @Override
				// public void onDialogNegativeClick(final DialogFragment dialog) {
				// }
				//
				// @Override
				// public void onDialogPositiveClick(final DialogFragment dialog) {
				// // Get the number of runs selected by the user.
				// final int runs = ((JobRunsDialog) dialog).getRuns();
				// // Verify with the number of runs the number of blueprints used.
				// Toast.makeText(getActivity(), "Selected Runs: " + runs,
				// Toast.LENGTH_LONG).show();
				// // JobManager.launchJob(getPilot(), self, runs,
				// getJobActivity());
				// // final Intent intent = new Intent(getActivity(),
				// JobDirectorActivity.class);
				// // intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
				// getPilot().getCharacterID());
				// // getActivity().startActivity(intent);
				// }
				// });
				// //
				// getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				// dialog.show(getActivity().getFragmentManager(), "JobRunsDialog");
				break;

			default:
				break;
		}
	}

	public void setBlueprintID(final long assetID) {
		blueprintID = assetID;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("ActionPart [");
		final String action = getCastedModel().getUserAction();
		buffer.append(action).append(" ");
		buffer.append("State: ").append(getCastedModel().isCompleted()).append(" ");
		buffer.append("Item: ").append(getCastedModel().getItemName()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected AbstractHolder selectHolder() {
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_SKILLACTION) return new SkillRender(this, _activity);
		return new ActionRender(this, _activity);
	}
}

// - UNUSED CODE
// ............................................................................................

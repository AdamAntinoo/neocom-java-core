//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.IndustryT2Activity;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.enums.EIndustryGroup;
import org.dimensinfin.evedroid.enums.ETaskCompletion;
import org.dimensinfin.evedroid.interfaces.IItemPart;
import org.dimensinfin.evedroid.model.Action;
import org.dimensinfin.evedroid.model.EveTask;
import org.dimensinfin.evedroid.model.NeoComAsset;
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
		this.getCastedModel().setExpanded(false);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Deprecated
	public void createHierarchy() {
		this.clean();
		for (final EveTask t : this.getCastedModel().getTasks()) {
			this.addChild(new TaskPart(t));
		}
	}

	public String get_balance() {
		final double price = this.getCastedModel().getPrice();
		final double bal = (price * this.getCastedModel().getRequestQty()) / 1000.0;
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
		return this.getCastedModel().getCategory();
	}

	public String get_cost() {
		final double price = this.getCastedModel().getPrice();
		final DecimalFormat formatter = new DecimalFormat("###,###.0# ISK");
		final String costString = formatter.format(price);
		return costString;
	}

	public String get_group() {
		return this.getCastedModel().getGroupName();
	}

	public String get_itemName() {
		if (AppWideConstants.DEVELOPMENT)
			return this.getCastedModel().getItemName() + " #[" + this.getCastedModel().getTypeID() + "]";
		else
			return this.getCastedModel().getItemName();
	}

	public String get_qtyRequired() {
		return EveAbstractPart.qtyFormatter.format(this.getCastedModel().getCompletedQty()) + "/"
				+ EveAbstractPart.qtyFormatter.format(this.getCastedModel().getRequestQty());
	}

	public long getBlueprintID() {
		return blueprintID;
	}

	public Action getCastedModel() {
		try {
			return (Action) this.getModel();
		} catch (final RuntimeException rtex) {
			rtex.printStackTrace();
		}
		return (Action) this.getModel();
	}

	public String getCategory() {
		return this.getCastedModel().getCategory();
	}

	public int getCompletedQty() {
		return this.getCastedModel().getCompletedQty();
	}

	public String getGroup() {
		return this.getCastedModel().getGroupName();
	}

	public EIndustryGroup getIndustryGroup() {
		return this.getCastedModel().getItemIndustryGroup();
	}

	@Override
	public long getModelID() {
		return this.getCastedModel().getTypeID();
	}

	public String getName() {
		return this.getCastedModel().getItemName();
	}

	public int getRequestQty() {
		return this.getCastedModel().getRequestQty();
	}

	// FIXME This was removed from the implementation while the migration to new eveapi. reimplement
	public int getSkillLevel() {
		return 5;
		//		return getPilot().getSkillLevel(getCastedModel().getTypeID());
	}

	/**
	 * Check if all the actions inside are completed (AVAILABLE).
	 * 
	 * @return
	 */
	public ETaskCompletion isCompleted() {
		return this.getCastedModel().isCompleted();
	}

	public void onClick(final View view) {
		if (!clickOverride) {
			// Clean the view to force an update.
			this.invalidate();
			this.toggleExpanded();
			this.fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
			clickOverride = false;
		}
	}

	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		// Process the command depending on the menu and the item selected
		switch (menuItemIndex) {
			case R.id.reactionmaterialreaction:
				this.getPilot().putAction4Item(this.getCastedModel().getTypeID(), "REACTION");
				break;
			case R.id.refinedmaterialrefine:
				this.getPilot().putAction4Item(this.getCastedModel().getTypeID(), item.getTitle().toString());
				break;
			case R.id.reactionmaterialbuy:
				this.getPilot().putAction4Item(this.getCastedModel().getTypeID(), "BUY");
				break;
			case R.id.refinedmaterialbuy:
				this.getPilot().putAction4Item(this.getCastedModel().getTypeID(), "BUY");
				break;
			case R.id.componentbuild:
				this.getPilot().putAction4Item(this.getCastedModel().getTypeID(), "BUILD");
				break;
			case R.id.componentbuy:
				this.getPilot().putAction4Item(this.getCastedModel().getTypeID(), "BUY");
				break;

			default:
				break;
		}
		this.invalidate();
		// REFACTOR The event fires a EVENTSTRUCTURE_NEEDSREFRESH that is not
		// processed by the different event managers.
		this.fireStructureChange(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE, this, this);
		return true;
	}

	/**
	 * This method is activated when the user makes a long click on any list element. There are two solutions,
	 * or to use a contextual menu or to activate a long click listener (performed by implementing the
	 * corresponding interface). The contextual menu allows for a better control of the interaction because
	 * allows to create a selection menu or a dialog.
	 */
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		// Clear click detection.
		clickOverride = false;
		// get the industry group to determine the right actions.
		final EIndustryGroup industryGroup = this.getCastedModel().getItemIndustryGroup();
		switch (industryGroup) {
			case COMPONENTS:
			case HULL:
			case CHARGE:
			case ITEMS:
				this.getActivity().getMenuInflater().inflate(R.menu.actioncomponent_menu, menu);
				break;
			case REFINEDMATERIAL:
				this.getActivity().getMenuInflater().inflate(R.menu.actionrefinedmaterial_menu, menu);
				break;
			case REACTIONMATERIALS:
				this.getActivity().getMenuInflater().inflate(R.menu.actionreactionmaterial_menu, menu);
				break;
			case PLANETARYMATERIALS:
				this.getActivity().getMenuInflater().inflate(R.menu.actionplanetarymaterial_menu, menu);
				break;
			case BLUEPRINT:
				// Identify a blueprint of the correct type and open the Invention
				// activity.
				final ArrayList<EveTask> tasks = this.getCastedModel().getTasks();
				final NeoComAsset asset = tasks.get(0).getReferencedAsset();
				// Check this is a T2 blueprint and then get its T1 version.
				if (asset.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
					final ArrayList<Integer> ids = AppConnector.getDBConnector()
							.searchInventionableBlueprints(Integer.valueOf(asset.getTypeID()).toString());
					// The first element is the blueprint that invents the target.
					// Now search for an asset of that type.
					final Integer t1bpid = ids.get(0);
					final ArrayList<NeoComAsset> targetbpassetid = AppConnector.getDBConnector()
							.searchAsset4Type(this.getPilot().getCharacterID(), t1bpid);
					if (targetbpassetid.size() > 0) {
						final NeoComAsset targetAsset = targetbpassetid.get(0);
						final Intent intent = new Intent(this.getActivity(), IndustryT2Activity.class);
						intent.putExtra(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(), this.getPilot().getCharacterID());
						intent.putExtra(AppWideConstants.EExtras.EXTRA_BLUEPRINTID.name(),
								Long.valueOf(targetAsset.getAssetID()).longValue());
						intent.putExtra(AppWideConstants.EExtras.EXTRA_BLUEPRINTACTIVITY.name(),
								ModelWideConstants.activities.INVENTION);
						this.getActivity().startActivity(intent);

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

	//	@Override
	//	public ArrayList<IPart> collaborate2View() {
	//		final ArrayList<IPart> result = new ArrayList<IPart>();
	//		//		result.add(this);
	//		// Add the children only if the model is expanded.
	//		if (this.isExpanded()) {
	//			Vector<IPart> ch = this.getChildren();
	//			for (final AbstractPropertyChanger node : ch) {
	//				// Convert the node to a part.
	//				final AbstractAndroidPart part = (AbstractAndroidPart) node;
	//				result.add(part);
	//				// Check if the node is expanded. Then add its children.
	//				if (part.isExpanded()) {
	//					final ArrayList<AbstractAndroidPart> grand = part.collaborate2View();
	//					result.addAll(grand);
	//				}
	//			}
	//		}
	//		return result;
	//	}
	/**
	 * The default actions inside this method usually are the sorting of the children nodes. For actions there
	 * is not sorting required so do nothing with the input.
	 */
	@Override
	public Vector<IPart> runPolicies(final Vector<IPart> targets) {
		return targets;
	}

	public void setBlueprintID(final long assetID) {
		blueprintID = assetID;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("ActionPart [");
		final String action = this.getCastedModel().getUserAction();
		buffer.append(action).append(" ");
		buffer.append("State: ").append(this.getCastedModel().isCompleted()).append(" ");
		buffer.append("Item: ").append(this.getCastedModel().getItemName()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected AbstractHolder selectHolder() {
		if (this.getRenderMode() == AppWideConstants.rendermodes.RENDER_SKILLACTION)
			return new SkillRender(this, _activity);
		return new ActionRender(this, _activity);
	}
}

// - UNUSED CODE
// ............................................................................................

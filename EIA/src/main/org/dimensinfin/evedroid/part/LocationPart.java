//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.interfaces.IEditPart;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.interfaces.INamedPart;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.NeoComAsset;
import org.dimensinfin.evedroid.model.Separator;

import android.text.Html;
import android.text.Spanned;
// - CLASS IMPLEMENTATION ...................................................................................

//- CLASS IMPLEMENTATION ...................................................................................
public abstract class LocationPart extends EveAbstractPart implements INamedPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long																	serialVersionUID	= 6823835480477273409L;
	private static Logger																			logger						= Logger.getLogger("LocationPart");

	// - F I E L D - S E C T I O N ............................................................................
	@SuppressWarnings("rawtypes")
	private final HashMap<Long, HashMap<Integer, AssetPart>>	stackList					= new HashMap();
	protected double																					itemsValueISK			= 0.0;
	protected double																					itemsVolume				= 0.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public LocationPart(final EveLocation location) {
		super(location);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public ArrayList accessLocationFunction() {
		return this.getPilot().getLocationRoles(this.getCastedModel().getID(), "-NO-FUNCTION-");
	}

	public long get_locationID() {
		return this.getCastedModel().getID();
	}

	public Spanned get_locationRegion() {
		double security = this.getCastedModel().getSecurityValue();
		return Html.fromHtml(this.generateSecurityColor(security, this.getCastedModel().getRegion()
				+ AppWideConstants.FLOW_ARROW_RIGHT + this.getCastedModel().getConstellation()));
	}

	public Spanned get_locationStation() {
		StringBuffer htmlLocation = new StringBuffer();
		double security = this.getCastedModel().getSecurityValue();
		htmlLocation.append(this.generateSecurityColor(security, EveAbstractPart.securityFormatter.format(security)));
		htmlLocation.append(" ").append(this.getCastedModel().getStation());
		return Html.fromHtml(htmlLocation.toString());
	}

	public String get_locationSystem() {
		return this.getCastedModel().getSystem();
	}

	public EveLocation getCastedModel() {
		return (EveLocation) this.getModel();
	}

	@Override
	public long getModelID() {
		return ((EveLocation) this.getModel()).getID();
	}

	public String getName() {
		return this.getCastedModel().getName();
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartChildren() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		Vector<AbstractPropertyChanger> ch = this.getChildren();
		Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_ITEM_TYPE));

		for (AbstractPropertyChanger node : ch) {
			// Convert the node to a part.
			AbstractAndroidPart part = (AbstractAndroidPart) node;
			result.add(part);
			// Check if the node is expanded. Then add its children.
			if (part.isExpanded()) {
				ArrayList<AbstractAndroidPart> grand = part.getPartChildren();
				result.addAll(grand);
				// Add a separator.
				result.add(new TerminatorPart(new Separator("")));
			}
		}
		return result;
	}

	public int searchStationType() {
		return AppConnector.getDBConnector().searchStationType(this.getCastedModel().getStationID());
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("LocationPart [");
		buffer.append(this.getCastedModel());
		buffer.append(" ]");
		return buffer.toString();
	}

	protected void calculateValue(final NeoComAsset asset, final AssetPart apart) {
		// Skip blueprints from the value calculations
		if (null != asset) {
			EveItem item = asset.getItem();
			if (null != item) {
				String category = item.getCategory();
				String group = item.getGroupName();
				if (null != category) if (!category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
					// Add the value and volume of the stack to the global result.
					long quantity = apart.getCastedModel().getQuantity();
					double price = apart.getCastedModel().getItem().getHighestBuyerPrice().getPrice();
					itemsValueISK += price * quantity;
					// Add volume but if it is an assembled ship or assembled container
					if ((group.equalsIgnoreCase("Secure Cargo Container")) || (category.equalsIgnoreCase("Ship")))
						itemsVolume += 0;
					else
						itemsVolume += quantity * apart.getCastedModel().getItem().getVolume();
				}
			}
		}
	}

	protected void checkAssetStacking(/* final LocationPart target, */final AssetPart apart) {
		// Locate the stack if exists.
		HashMap<Integer, AssetPart> container = stackList.get(this.getCastedModel().getID());
		int type = apart.getCastedModel().getTypeID();
		if (null != container) {
			AssetPart stack = container.get(type);
			if (null != stack) {
				//	if (stack instanceof AssetPart) {
				int count = stack.getCastedModel().getQuantity();
				stack.getCastedModel().setQuantity(count + 1);
				return;
				//	}
				// Do nothing because we do not know how to add to Assets.
			} else {
				// Add a new stack for this type to the current container.
				container.put(type, apart);
				this.addChild((IEditPart) apart);
			}
		} else {
			// There is no container also with this stack.
			container = new HashMap<Integer, AssetPart>();
			container.put(type, apart);
			this.addChild((IEditPart) apart);
			stackList.put(this.getCastedModel().getID(), container);
		}
	}
}
// - UNUSED CODE ............................................................................................

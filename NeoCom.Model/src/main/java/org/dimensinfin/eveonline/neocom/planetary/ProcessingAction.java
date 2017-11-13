//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.planetary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.dimensinfin.android.model.AbstractViewableNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.planetary.Schematics.ESchematicDirection;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Stores the data to transform a set of resources into a new set where some of them are processed from a Tier
 * to a higher Tier. It uses the CCP database information to generate the quantity conversions from the input
 * data to the result data. <br>
 * Also stores the number of cycles required for the complete transformation of the resources and then the
 * total time to perfrom the transformation.
 * 
 * @author Adam Antinoo
 */
public class ProcessingAction extends AbstractViewableNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID	= 3885877535917258089L;

	// - F I E L D - S E C T I O N ............................................................................
	private int																targetId					= 0;
	public EveItem														targetItem				= null;
	private Vector<Schematics>								schematics				= new Vector<Schematics>();
	private final Vector<Schematics>					inputList					= new Vector<Schematics>();
	private Schematics												output						= null;
	private final HashMap<Integer, Resource>	actionResources		= new HashMap<Integer, Resource>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * Instance a new <code>ProcessingAction</code> and set the target product id to be produced by this action.
	 * This should get the schematics information so the action can process the quantities and the length of the
	 * cycles.
	 * 
	 * @param targetId
	 */
	public ProcessingAction(final int targetId) {
		this.targetId = targetId;
		// Get the item for the target id to be identified on the Json serialization.
		targetItem = ModelAppConnector.getSingleton().getCCPDBConnector().searchItembyID(targetId);
		// Get the schematics information.
		schematics = ModelAppConnector.getSingleton().getCCPDBConnector().searchSchematics4Output(targetId);
		// Store the inputs into another list.
		for (Schematics sche : schematics) {
			if (sche.getDirection() == ESchematicDirection.INPUT) {
				inputList.add(sche);
			}
			if (sche.getDirection() == ESchematicDirection.OUTPUT) {
				output = sche;
			}
		}
		// Do some completion checks. If the Item ot the output are null then there is some error during the initialization.
		if ((null == targetItem) || (null == output))
			throw new RuntimeException("Some key element not found on database while initializing the Action.");
		jsonClass = "ProcessingAction";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addResource(final Resource resource) {
		this.stockResource(resource);
	}

	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		results.add(new PlanetaryTarget(targetItem));
		return results;
	}

	/**
	 * Return the list of resources left and new from the action processing following the current schematics.
	 * This is the result of subtracting from the input resources the input quantity multiplied by the cycles
	 * and adding to the result the output resource quantity multiplied by the same cycles.
	 * 
	 * @return
	 */
	public Vector<Resource> getActionResults() {
		int cycles = this.getPossibleCycles();
		Vector<Resource> results = new Vector<Resource>();
		if (cycles > 0) {
			for (Schematics sche : inputList) {
				results.add(this.processResource(sche, cycles));
			}
			// Add the output
			results.add(new Resource(output.getTypeId(), output.getQty() * cycles));
		}
		return results;
	}
	//[01]

	public Vector<Schematics> getInputs() {
		return inputList;
	}

	public Schematics getOutput() {
		return output;
	}

	/**
	 * Return the number of cycles that can be run with the current quantities of input resources.
	 * 
	 * @return
	 */
	public int getPossibleCycles() {
		int cycles = Integer.MAX_VALUE;
		for (Schematics schematics : inputList) {
			int inputType = schematics.getTypeId();
			Resource resource = actionResources.get(inputType);
			if (null == resource)
				return 0;
			else {
				cycles = Math.min(cycles, resource.getQuantity() / schematics.getQty());
			}
		}
		return cycles;
	}

	/**
	 * Returns the list of resources at the end of the transformations.
	 * 
	 * @return
	 */
	public HashMap<Integer, Resource> getResources() {
		return actionResources;
	}

	public int getTargetId() {
		return targetId;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("ProcessingAction [");
		buffer.append(inputList).append(" ").append(output).append(" ");
		buffer.append("resources: ").append(actionResources).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected ProcessingAction clone() throws CloneNotSupportedException {
		ProcessingAction clone = new ProcessingAction(targetId);
		for (Integer resource : actionResources.keySet()) {
			clone.addResource(new Resource(resource, actionResources.get(resource).getQuantity()));
		}
		return clone;
	}

	/**
	 * Processes one resource from the number of cycles indicated for the schematic received.
	 * 
	 * @param sche
	 * @return
	 */
	private Resource processResource(final Schematics sche, final int cycles) {
		// Get the resource from the list of available resources.
		Resource res = actionResources.get(sche.getTypeId());
		if (null != res)
			return new Resource(sche.getTypeId(), res.getQuantity() - (sche.getQty() * cycles));
		else
			return null;
	}

	/**
	 * Adds a new PlanetaryResource to the list of current resources stacking it to an already existing
	 * resource. If the resource is not already in the list then we put it on the aaction resource list.
	 * 
	 * @param typeid
	 *          the resource item id
	 * @param quantity
	 *          the quantity of the resource to stack.
	 */
	private void stockResource(final Resource newResource) {
		//		logger.info(">> [ProcessingAction.stockResource]");
		Resource hit = actionResources.get(newResource.getTypeID());
		if (null == hit) {
			actionResources.put(newResource.getTypeID(), newResource);
		} else {
			hit.setQuantity(hit.getQuantity() + newResource.getQuantity());
		}
	}
}

// - UNUSED CODE ............................................................................................

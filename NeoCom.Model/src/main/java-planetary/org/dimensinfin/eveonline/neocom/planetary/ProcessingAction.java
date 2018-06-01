//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.planetary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.core.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.dimensinfin.eveonline.neocom.planetary.Schematics.ESchematicDirection;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * Stores the data to transform a set of resources into a new set where some of them are processed from a Tier
 * to a higher Tier. It uses the CCP database information to generate the quantity conversions from the input
 * data to the result data. <br>
 * Also stores the number of cycles required for the complete transformation of the resources and then the
 * total time to perfrom the transformation.
 * @author Adam Antinoo
 */
public class ProcessingAction extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 3885877535917258089L;

	// - F I E L D - S E C T I O N ............................................................................
	public int targetId = 0;
	public EveItem targetItem = null;
	protected List<Schematics> schematics = new Vector<Schematics>();
	protected final List<Schematics> inputList = new Vector<Schematics>();
	protected Schematics output = null;
	protected final Map<Integer, Resource> actionResources = new HashMap<Integer, Resource>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	/**
	 * Instance a new <code>ProcessingAction</code> and set the target product id to be produced by this action.
	 * This should get the schematics information so the action can process the quantities and the length of the
	 * cycles.
	 * @param targetId
	 */
	public ProcessingAction( final int targetId ) {
		this.targetId = targetId;
		// Get the item for the target id to be identified on the Json serialization.
		try {
			targetItem = accessGlobal().searchItem4Id(targetId);
		} catch (NeoComRuntimeException neoe) {
			targetItem = new EveItem();
		}
		// Get the schematics information.
		schematics = accessSDEDBHelper().searchSchematics4Output(targetId);
		// Store the inputs into another list.
		for (final Schematics sche : schematics) {
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
	public void addResource( final Resource resource ) {
		this.stockResource(resource);
	}

	/**
	 * Return the list of resources left and new from the action processing following the current schematics.
	 * This is the result of subtracting from the input resources the input quantity multiplied by the cycles
	 * and adding to the result the output resource quantity multiplied by the same cycles.
	 * @return
	 */
	public List<Resource> getActionResults() {
		final int cycles = this.getPossibleCycles();
		final List<Resource> results = new Vector<Resource>();
		if (cycles > 0) {
			for (final Schematics sche : inputList) {
				results.add(this.processResource(sche, cycles));
			}
			// Add the output
			results.add(new Resource(output.getTypeId(), output.getQty() * cycles));
		}
		return results;
	}
	//[01]

	public List<Schematics> getInputs() {
		return inputList;
	}

	public Schematics getOutput() {
		return output;
	}

	/**
	 * Return the number of cycles that can be run with the current quantities of input resources.
	 * @return
	 */
	public int getPossibleCycles() {
		int cycles = Integer.MAX_VALUE;
		for (final Schematics schematics : inputList) {
			final int inputType = schematics.getTypeId();
			final Resource resource = actionResources.get(inputType);
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
	 * @return
	 */
	public Map<Integer, Resource> getResources() {
		return actionResources;
	}

	public int getTargetId() {
		return targetId;
	}


	/**
	 * Processes one resource from the number of cycles indicated for the schematic received.
	 * @param sche
	 * @return
	 */
	protected Resource processResource( final Schematics sche, final int cycles ) {
		// Get the resource from the list of available resources.
		final Resource res = actionResources.get(sche.getTypeId());
		if (null != res)
			return new Resource(sche.getTypeId(), res.getQuantity() - (sche.getQty() * cycles));
		else
			return null;
	}

	/**
	 * Adds a new PlanetaryResource to the list of current resources stacking it to an already existing
	 * resource. If the resource is not already in the list then we put it on the aaction resource list.
	 * @param newResource the resource to stack.
	 */
	private void stockResource( final Resource newResource ) {
		//		logger.info(">> [ProcessingAction.stockResource]");
		final Resource hit = actionResources.get(newResource.getTypeId());
		if (null == hit) {
			actionResources.put(newResource.getTypeId(), new Resource(newResource.getTypeId(), newResource.getQuantity()));
		} else {
			hit.setQuantity(hit.getQuantity() + newResource.getQuantity());
		}
	}

	// --- I C O L L A B O R A T I O N   I N T E R F A C
	@Override
	public List<ICollaboration> collaborate2Model( final String variant ) {
		final ArrayList<ICollaboration> results = new ArrayList<ICollaboration>();
		results.add(new PlanetaryTarget(targetItem));
		return results;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("ProcessingAction [ ");
		buffer.append(inputList).append(" ").append(output).append("\n");
		buffer.append("output: #").append(targetItem.getTypeId()).append(" ").append(targetItem.getName()).append(" ");
		buffer.append("resources: ").append(actionResources).append(" ");
		buffer.append(" ]");
		return buffer.toString();
	}

	@Override
	protected ProcessingAction clone() throws CloneNotSupportedException {
		final ProcessingAction clone = new ProcessingAction(targetId);
		for (final Integer resource : actionResources.keySet()) {
			clone.addResource(new Resource(resource, actionResources.get(resource).getQuantity()));
		}
		return clone;
	}
}

// - UNUSED CODE ............................................................................................

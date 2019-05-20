package org.dimensinfin.eveonline.neocom.model;

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IJsonAngular;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This model class will serve as the base placeholder for the NeoCom application nodes. Will define the
 * common methods and implement the default behavior for nodes.
 */
public abstract class NeoComNode extends ANeoComEntity implements ICollaboration, IJsonAngular {
	protected static Logger logger = LoggerFactory.getLogger(NeoComNode.class);
	protected static final long serialVersionUID = 6506043294337948561L;

	public static String capitalizeFirstLetter( String original ) {
		if (original == null || original.length() == 0) {
			return original;
		}
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	// - F I E L D - S E C T I O N ............................................................................
	protected String jsonClass = "NeoComNode";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComNode() {
		jsonClass = this.getClass().getSimpleName();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public List<ICollaboration> collaborate2Model( final String variant ) {
		return new ArrayList<>();
	}

	public String getJsonClass() {
		return jsonClass;
	}

	//	private void setJsonClass( final String jsonClass ) {
	//		this.jsonClass = jsonClass;
	//	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("NeoComNode [");
		buffer.append(" ]");
		return buffer.toString();
	}

	@Override
	public int compareTo( final Object target ) {
		return 0;
	}
}

package org.dimensinfin.eveonline.neocom.model;

import java.util.List;
import java.util.Vector;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IJsonAngular;

/**
 * Created by Adam on 06/12/2017.
 */

public class NeoComNode implements ICollaboration, IJsonAngular {
	private static final long	serialVersionUID	= 6506043294337948561L;
	protected String					jsonClass					= "AbstractViewableNode";

	public List<ICollaboration> collaborate2Model(final String variant) {
		return new Vector<ICollaboration>();
	}

	public String getJsonClass() {
		return jsonClass;
	}
}

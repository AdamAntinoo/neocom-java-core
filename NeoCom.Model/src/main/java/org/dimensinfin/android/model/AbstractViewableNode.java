package org.dimensinfin.android.model;

import org.dimensinfin.core.interfaces.ICollaboration;

import java.util.List;
import java.util.Vector;

/**
 * Created by Adam on 06/12/2017.
 */

public class AbstractViewableNode implements ICollaboration{
	protected String jsonClass="AbstractViewableNode";
	public List<ICollaboration> collaborate2Model(final String variant) {
		return new Vector<ICollaboration>();
	}
}

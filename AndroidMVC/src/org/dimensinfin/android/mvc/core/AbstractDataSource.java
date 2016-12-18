//	PROJECT:        AndroidMVC
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.android.mvc.core;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IDataSource;
import org.dimensinfin.core.model.AbstractPropertyChanger;

import android.os.Bundle;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractDataSource extends AbstractPropertyChanger implements IDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID	= 3697796903140301443L;
	public static Logger											logger						= Logger.getLogger("AbstractDataSource");

	// - F I E L D - S E C T I O N ............................................................................
	protected ArrayList<AbstractAndroidPart>	_root							= new ArrayList<AbstractAndroidPart>();
	protected ArrayList<AbstractAndroidPart>	_adapterData			= null;
	protected Bundle													_parameters				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	//	public void createContentHierarchy() {
	//		// Clear the current list of elements.
	//		_root.clear();
	//	}

	public int getItemsCount() {
		if (null != _adapterData)
			return _adapterData.size();
		else
			return 0;
	}

	//	@Deprecated
	//	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
	//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		for (AbstractAndroidPart part : _root) {
	//			result.add(part);
	//			// Check if the node is expanded. Then add its children.
	//			if (part.isExpanded()) {
	//				for (IPart child : part.collaborate2View()) {
	//					result.add((AbstractAndroidPart) child);
	//				}
	//			}
	//		}
	//		_adapterData = result;
	//		return result;
	//	}

	public void propertyChange(final PropertyChangeEvent event) {
	}

	public void setArguments(final Bundle arguments) {
		if (null != arguments) _parameters = arguments;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("AbstractDataSource [");
		buffer.append("Parts:").append(_root.size()).append(" ");
		if (null != _adapterData) buffer.append("Elements:").append(_adapterData.size()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................

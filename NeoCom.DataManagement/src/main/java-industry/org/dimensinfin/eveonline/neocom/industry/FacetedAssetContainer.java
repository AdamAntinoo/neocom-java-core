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
package org.dimensinfin.eveonline.neocom.industry;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.interfaces.IResourceContainer;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class FacetedAssetContainer<T> implements IResourceContainer {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("FacetedAssetContainer");

	// - F I E L D - S E C T I O N ............................................................................
	private Map<Integer, Resource> _contents = new HashMap<>();
	private T _facet;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FacetedAssetContainer( final T facet ) {
		this._facet = facet;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public int addResource( final Resource resource ) {
		// Before setting the contents check if there already a resource with this id. If exists then add the resources.
		final Resource hit = _contents.get(resource.getTypeId());
		if (null == hit)
			_contents.put(resource.getTypeId(), resource);
		else
			hit.setQuantity(hit.getQuantity() + resource.getQuantity());
		return _contents.size();
	}

	public Map<Integer, Resource> getContents() {
		return _contents;
	}

	public FacetedAssetContainer<T> setContents( final Map<Integer, Resource> _contents ) {
		this._contents = _contents;
		return this;
	}

	public T getFacet() {
		return _facet;
	}

	public FacetedAssetContainer<T> setFacet( final T _facet ) {
		this._facet = _facet;
		return this;
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		return new StringBuffer("FacetedAssetContainer [")
//				.append("field:").append().append(" ")
				.append("]")
//				.append("->").append(super.toString())
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]

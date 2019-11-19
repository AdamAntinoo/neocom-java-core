package org.dimensinfin.eveonline.neocom.domain;

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.core.interfaces.ICollaboration;

public class ExpandableContainer<M extends ICollaboration> extends NeoComExpandableNode {
	private static final long serialVersionUID = -3759714638563369946L;
	private List<M> contents = new ArrayList<>();

	public ExpandableContainer() {}

	// -  C O N T E N T
	public int addContent( final M item ) {
		this.contents.add( item );
		return this.contents.size();
	}

	public int getContentCount() {
		return this.contents.size();
	}

	// - I E X P A N D A B L E
	public boolean isEmpty() {
		return this.contents.isEmpty();
	}

	// - I C O L L A B O R A T I O N
	@Override
	public List<ICollaboration> collaborate2Model( final String variant ) {
		final ArrayList<ICollaboration> collaboration = new ArrayList<>();
		collaboration.addAll( this.contents );
		return collaboration;
	}

//	// - B U I L D E R
//	public static class Builder {
//		private ExpandableContainer onConstruction;
//
//		public Builder() {
//			this.onConstruction = new ExpandableContainer();
//		}
//
//		public ExpandableContainer build() {
//			return this.onConstruction;
//		}
//	}
}

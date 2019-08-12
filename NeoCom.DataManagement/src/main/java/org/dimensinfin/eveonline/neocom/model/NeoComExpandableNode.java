package org.dimensinfin.eveonline.neocom.model;

import org.dimensinfin.core.interfaces.IExpandable;

/**
 * This model class will serve as the base placeholder for the NeoCom application nodes. Will define the
 * common methods and implement the default behavior for nodes.
 */
public abstract class NeoComExpandableNode extends NeoComNode implements IExpandable {
	private static final long serialVersionUID = -3742179733511283434L;

	protected boolean expanded = false;

	// - C O N S T R U C T O R S
	public NeoComExpandableNode () {
		super();
	}

	// - I E X P A N D A B L E   I N T E R F A C E
	public boolean collapse () {
		this.expanded = false;
		return this.expanded;
	}

	public boolean expand () {
		expanded = true;
		return expanded;
	}

	public boolean toggleExpand() {
		this.expanded = !this.expanded;
		return this.isExpanded();
	}

	public abstract boolean isEmpty ();

	public boolean isExpanded () {
		return expanded;
	}

	public boolean isRenderWhenEmpty () {
		return true;
	}

	public IExpandable setRenderWhenEmpty (final boolean renderWhenEmpty) {
//		_renderIfEmpty = renderWhenEmpty;
		return this;
	}

//	@Override
//	public String toString () {
//		final StringBuffer buffer = new StringBuffer("NeoComExpandableNode [");
//		buffer.append(" ]");
//		return buffer.toString();
//	}
}

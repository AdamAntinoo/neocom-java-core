//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.evedroid.core;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.enums.EAssetsFields;
import org.dimensinfin.evedroid.enums.EMode;
import org.dimensinfin.evedroid.model.Asset;

// - CLASS IMPLEMENTATION ...................................................................................
public class WhereClause {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger	logger	= Logger.getLogger("blueprintlistproof.filter");

	// - F I E L D - S E C T I O N ............................................................................
	private EAssetsFields	category;
	private EMode					mode;
	private String				filterString	= null;
	private long					filterInt			= 0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public WhereClause() {
	}

	public WhereClause(final EAssetsFields category, final EMode mode, final long intFilter) {
		this.category = category;
		this.mode = mode;
		filterInt = intFilter;
	}

	public WhereClause(final EAssetsFields category, final EMode mode, final String filter) {
		this.category = category;
		this.mode = mode;
		filterString = filter;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean isFiltered(final Asset target) {
		// Detect the category to use the right type filter, or for Strings or for numbers.
		switch (category) {
			case NAME:
				return isStringFiltered(target);
			case CATEGORY:
				return isStringFiltered(target);
			case GROUP:
				return isStringFiltered(target);
			case REGION:
				return isStringFiltered(target);
			case TECH:
				return isStringFiltered(target);
			case COUNT:
				return isNumberFiltered(target);
				//			case META:
				//				return isNumberFiltered(target);
			case TYPEID:
				return isNumberFiltered(target);
			default:
				break;
		}
		return false;
	}

	public boolean isNumberFiltered(final Asset target) {
		long field = getIntField(target, category);
		switch (mode) {
			case EQUALS:
				return field == filterInt;
			case NOTEQUALS:
				return field != filterInt;
			case GREATER:
				return field > filterInt;
			case LESS:
				return field < filterInt;
			default:
				return false;
		}
	}

	public boolean isStringFiltered(final Asset target) {
		String field = getField(target, category);
		if (null == field) return false;
		switch (mode) {
			case EQUALS:
				return field.equalsIgnoreCase(filterString);
			case NOTEQUALS:
				return !field.equalsIgnoreCase(filterString);
			case CONTAINS:
				return field.contains(filterString);
			default:
				return false;
		}
	}

	private String getField(final Asset target, final EAssetsFields category) {
		switch (category) {
			case NAME:
				return target.getName();
			case CATEGORY:
				return target.getCategory();
			case GROUP:
				return target.getGroupName();
			case REGION:
				return target.getLocation().getRegion();
			case TECH:
				return target.getTech();
			default:
				return null;
		}
	}

	private long getIntField(final Asset target, final EAssetsFields category) {
		switch (category) {
			case COUNT:
				return target.getQuantity();
				//			case META:
				//				return target.getMeta();
			case TYPEID:
				return target.getTypeID();
			default:
				return 0;
		}
	}
}

// - UNUSED CODE ............................................................................................

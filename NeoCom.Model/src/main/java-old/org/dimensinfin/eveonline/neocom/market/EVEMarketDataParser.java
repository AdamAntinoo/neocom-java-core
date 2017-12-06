//	PROJECT:        EveMarket
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
package org.dimensinfin.eveonline.neocom.market;

//- IMPORT SECTION .........................................................................................
import java.util.Vector;

import org.dimensinfin.core.model.IModelStore;
import org.dimensinfin.core.parser.AbstractXMLHandler;
import org.xml.sax.Attributes;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * The current parse format is the one shown next
 * <tr class="r1">
 * <td class=""><span class="sec_null">0.0</span> Catch - 25S-6P</td>
 * <td class="qty ">60,428,316</td>
 * <td class="isk ">6.00 <span class="isk_format_isk">ISK</span></td>
 * <td class=" ">Station</td>
 * <td class=" " style="text-align:center"><span class="seconds_ago_bad">9 hours ago</span></td>
 * <td class="numeric ">&nbsp;</td>
 * </tr>
 * 
 * @author Adam Antinoo
 * 
 */
public class EVEMarketDataParser extends AbstractXMLHandler {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private String							tagContent					= "";
	private TrackEntry					entry								= null;
	private boolean							awaitingStart				= true;
	private boolean							awaitingStationName	= false;
	private boolean							awaitingQty					= false;
	private boolean							awaitingPrice				= false;
	private Vector<TrackEntry>	entryList						= new Vector<TrackEntry>();
	private IModelStore					modelStore;
	private boolean							awaitingRange				= false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void characters(final char[] ch, final int start, final int length) {
		// Add new data to buffer.
		tagContent = tagContent + new String(ch);
	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) {
		if (!awaitingStart) {
			if (localName.equalsIgnoreCase("span")) {
				if (awaitingStationName) {
					entry.setSecurity(tagContent);
					//	tagContent = "";
					return;
				}
			}
			//			if (localName.equalsIgnoreCase("td")) {
			//				if (awaitingStationName) {
			//					// // This is the station name content that is not inside
			//					// any tag.
			//					entry.setStationName(tagContent);
			//					awaitingStationName = false;
			//				}
			//			}
			if (localName.equalsIgnoreCase("td")) {
				if (!tagContent.isEmpty()) {
					if (awaitingQty) {
						if (null != entry) entry.setQty(tagContent);
						awaitingQty = false;
					}
					if (awaitingPrice) {
						if (null != entry) {
							entry.setPrice(tagContent);
							entryList.add(entry);
						}
						awaitingPrice = false;
					}
				}
			}
		}
	}

	public Vector<TrackEntry> getEntries() {
		return entryList;
	}

	public void reset() {
		entryList = new Vector<TrackEntry>();
		entry = null;
		awaitingStationName = false;
		awaitingQty = false;
		awaitingPrice = false;
	}

	/**
	 * The detection of the attributes has to be upgraded because new SAX2 implementation requires the use of
	 * namespaces. Because this is disabled for HTML I will add code to detect the right attribute.
	 */
	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {
		if (localName.equalsIgnoreCase("tbody")) {
			awaitingStart = false;
		}
		if (!awaitingStart) {
			if (localName.equalsIgnoreCase("span")) {
				if (awaitingStationName) {
					// This is the station name content that is not inside any tag.
					entry.setStationName(tagContent);
					awaitingStationName = false;
					tagContent = "";
					return;
				}
				String classValue = getAttributeValue(attributes, "class");
				if (null != classValue) {
					if (classValue.equalsIgnoreCase("sec_high")) {
						entry = new TrackEntry();
						awaitingStationName = true;
					}
					if (classValue.equalsIgnoreCase("sec_null")) {
						entry = new TrackEntry();
						awaitingStationName = true;
					}
					if (classValue.equalsIgnoreCase("sec_low")) {
						entry = new TrackEntry();
						awaitingStationName = true;
					}
				}
				// Code to save the TD ISK value because there are other tags
				// before
				// the TD end.
				if (awaitingQty) {
					if (null != entry) {
						entry.setQty(tagContent);
						awaitingQty = false;
					}
				}
				if (awaitingPrice) {
					if (null != entry) {
						entry.setPrice(tagContent);
						entryList.add(entry);
						awaitingPrice = false;
					}
				}
			}
			if (localName.equalsIgnoreCase("td")) {
				String classValue = getAttributeValue(attributes, "class");
				if (null != classValue) {
					if (classValue.startsWith("qty")) {
						if (null != entry) awaitingQty = true;
					}
					if (classValue.startsWith("isk")) {
						if (null != entry) awaitingPrice = true;
					}
					if (classValue.startsWith("range")) {
						if (null != entry) awaitingRange = true;
					}
				}
			}
		}
		tagContent = "";
	}

	/**
	 * Added to bypass the use of namespaces. The method searches for the index of the selected QName and then
	 * retrieves the right attribute by index.
	 * 
	 * @param string
	 * @return
	 */
	private String getAttributeValue(final Attributes attributes, final String name) {
		int attrs = attributes.getLength();
		if (0 == attrs) return null;
		for (int i = 0; i < attrs; i++) {
			String local = attributes.getLocalName(i);
			String qname = attributes.getQName(i);
			if (qname.equalsIgnoreCase(name)) return attributes.getValue(i);
		}
		return null;
	}
}

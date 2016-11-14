package com.beimin.eveapi.shared.blueprints;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.beimin.eveapi.core.AbstractContentHandler;

public class BlueprintListHandler extends AbstractContentHandler {
	private BlueprintListResponse												response;
	private EveBlueprint<EveBlueprint<?>>								currentBlueprint;
	private final Stack<EveBlueprint<EveBlueprint<?>>>	stack	= new Stack<EveBlueprint<EveBlueprint<?>>>();

	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {
		if (qName.equals("rowset") && !stack.isEmpty()) {
			EveBlueprint<EveBlueprint<?>> asset = stack.pop();
			if (stack.isEmpty()) {
				response.add(asset);
				currentBlueprint = null;
			}
		}
		if (qName.equals("row") && stack.isEmpty() && (currentBlueprint != null)) {
			response.add(currentBlueprint);
			currentBlueprint = null;
		}
		super.endElement(uri, localName, qName);
	}

	@Override
	public BlueprintListResponse getResponse() {
		return response;
	}

	@Override
	public void startDocument() throws SAXException {
		response = new BlueprintListResponse();
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attrs)
			throws SAXException {
		if (qName.equals("rowset")) {
			if (currentBlueprint != null) {
				stack.add(currentBlueprint);
				currentBlueprint = null;
			}
		}
		if (qName.equals("row")) {
			currentBlueprint = new EveBlueprint<EveBlueprint<?>>();
			currentBlueprint.setItemID(getLong(attrs, "itemID"));
			currentBlueprint.setLocationID(getLong(attrs, "locationID"));
			currentBlueprint.setTypeID(getInt(attrs, "typeID"));
			currentBlueprint.setTypeName(getString(attrs, "typeName"));
			currentBlueprint.setFlag(getInt(attrs, "flagID"));
			currentBlueprint.setQuantity(getInt(attrs, "quantity"));
			currentBlueprint.setTimeEfficiency(getInt(attrs, "timeEfficiency"));
			currentBlueprint.setMaterialEfficiency(getInt(attrs, "materialEfficiency"));
			currentBlueprint.setRuns(getInt(attrs, "runs"));
			//			if (!stack.isEmpty()) {
			//				EveBlueprint<EveBlueprint<?>> peek = stack.peek();
			//				peek.addAsset(currentBlueprint);
			//			}
		}
		super.startElement(uri, localName, qName, attrs);
		accumulator.setLength(0);
	}
}
//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.json;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.market.MarketDataSet;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;

// - CLASS IMPLEMENTATION ...................................................................................
public class MarketDataSetDeserializer extends StdDeserializer<MarketDataSet> {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 4288872631297140585L;
	//	private static Logger logger = Logger.getLogger("MarketDataDeserializer.java");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketDataSetDeserializer() {
		this(null);
	}

	public MarketDataSetDeserializer(final Class<?> vc) {
		super(vc);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public MarketDataSet deserialize(final JsonParser jp, final DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		int id = (Integer) ((IntNode) node.get("id")).numberValue();
		String itemName = node.get("itemName").asText();
		int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();

		return new MarketDataSet();
	}
}

// - UNUSED CODE ............................................................................................

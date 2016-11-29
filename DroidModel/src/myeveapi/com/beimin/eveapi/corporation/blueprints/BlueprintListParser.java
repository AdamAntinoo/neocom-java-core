package com.beimin.eveapi.corporation.blueprints;

import com.beimin.eveapi.core.ApiPath;
import com.beimin.eveapi.shared.blueprints.AbstractBlueprintListParser;

public class BlueprintListParser extends AbstractBlueprintListParser {
	public static BlueprintListParser getInstance() {
		return new BlueprintListParser();
	}

	private BlueprintListParser() {
		super(ApiPath.CORPORATION);
	}
}
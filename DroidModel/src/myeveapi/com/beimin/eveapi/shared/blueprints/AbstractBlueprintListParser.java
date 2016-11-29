package com.beimin.eveapi.shared.blueprints;

import com.beimin.eveapi.core.AbstractApiParser;
import com.beimin.eveapi.core.AbstractContentHandler;
import com.beimin.eveapi.core.ApiAuth;
import com.beimin.eveapi.core.ApiPage;
import com.beimin.eveapi.core.ApiPath;
import com.beimin.eveapi.exception.ApiException;

public abstract class AbstractBlueprintListParser extends AbstractApiParser<BlueprintListResponse> {
	protected AbstractBlueprintListParser(final ApiPath path) {
		super(BlueprintListResponse.class, 2, path, ApiPage.BLUEPRINT_LIST);
	}

	public BlueprintListResponse getResponse(final ApiAuth<?> auth) throws ApiException {
		return super.getResponse(auth);
	}

	@Override
	protected AbstractContentHandler getContentHandler() {
		return new BlueprintListHandler();
	}
}
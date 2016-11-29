package com.beimin.eveapi.shared.industryjobs;

import com.beimin.eveapi.core.AbstractListParser;
import com.beimin.eveapi.core.ApiAuth;
import com.beimin.eveapi.core.ApiPage;
import com.beimin.eveapi.core.ApiPath;
import com.beimin.eveapi.exception.ApiException;

public abstract class AbstractNewIndustryJobsParser extends
		AbstractListParser<NewIndustryJobsHandler, NewIndustryJobsResponse, ApiNewIndustryJob> {
	protected AbstractNewIndustryJobsParser(final ApiPath path) {
		super(NewIndustryJobsResponse.class, 2, path, ApiPage.INDUSTRY_JOBS, NewIndustryJobsHandler.class);
	}

	@Override
	public NewIndustryJobsResponse getResponse(final ApiAuth<?> auth) throws ApiException {
		return super.getResponse(auth);
	}
}
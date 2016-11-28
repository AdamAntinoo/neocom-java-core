package com.beimin.eveapi.shared.industryjobs;

import com.beimin.eveapi.core.AbstractListParser;
import com.beimin.eveapi.core.ApiAuth;
import com.beimin.eveapi.core.ApiPage;
import com.beimin.eveapi.core.ApiPath;
import com.beimin.eveapi.exception.ApiException;

public abstract class AbstractNewIndustryJobsHistoryParser extends
		AbstractListParser<NewIndustryJobsHistoryHandler, NewIndustryJobsHistoryResponse, ApiNewIndustryJob> {
	protected AbstractNewIndustryJobsHistoryParser(final ApiPath path) {
		super(NewIndustryJobsHistoryResponse.class, 2, path, ApiPage.INDUSTRY_JOBS_HISTORY,
				NewIndustryJobsHistoryHandler.class);
	}

	@Override
	public NewIndustryJobsHistoryResponse getResponse(final ApiAuth<?> auth) throws ApiException {
		return super.getResponse(auth);
	}
}
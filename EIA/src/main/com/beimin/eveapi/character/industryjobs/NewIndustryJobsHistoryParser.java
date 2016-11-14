package com.beimin.eveapi.character.industryjobs;

import com.beimin.eveapi.core.ApiPath;
import com.beimin.eveapi.shared.industryjobs.AbstractNewIndustryJobsHistoryParser;

public class NewIndustryJobsHistoryParser extends AbstractNewIndustryJobsHistoryParser {
	public static NewIndustryJobsHistoryParser getInstance() {
		return new NewIndustryJobsHistoryParser();
	}

	private NewIndustryJobsHistoryParser() {
		super(ApiPath.CHARACTER);
	}
}
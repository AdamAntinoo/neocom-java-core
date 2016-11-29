package com.beimin.eveapi.character.industryjobs;

import com.beimin.eveapi.core.ApiPath;
import com.beimin.eveapi.shared.industryjobs.AbstractNewIndustryJobsParser;

public class NewIndustryJobsParser extends AbstractNewIndustryJobsParser {
	public static NewIndustryJobsParser getInstance() {
		return new NewIndustryJobsParser();
	}

	private NewIndustryJobsParser() {
		super(ApiPath.CHARACTER);
	}
}
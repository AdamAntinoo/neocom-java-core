package com.beimin.eveapi.shared.industryjobs;

import org.xml.sax.Attributes;

import com.beimin.eveapi.core.AbstractContentListHandler;

public class NewIndustryJobsHandler extends AbstractContentListHandler<NewIndustryJobsResponse, ApiNewIndustryJob> {

	public NewIndustryJobsHandler() {
		super(NewIndustryJobsResponse.class);
	}

	@Override
	protected ApiNewIndustryJob getItem(final Attributes attrs) {
		ApiNewIndustryJob job = new ApiNewIndustryJob();
		job.setJobID(getLong(attrs, "jobID"));
		job.setInstallerID(getLong(attrs, "installerID"));
		job.setInstallerName(getString(attrs, "installerName"));
		job.setFacilityID(getLong(attrs, "facilityID"));
		job.setSolarSystemID(getLong(attrs, "solarSystemID"));
		job.setSolarSystemName(getString(attrs, "solarSystemName"));
		job.setStationID(getLong(attrs, "stationID"));
		job.setActivityID(getInt(attrs, "activityID"));
		job.setBlueprintID(getLong(attrs, "blueprintID"));
		job.setBlueprintTypeID(getInt(attrs, "blueprintTypeID"));
		job.setBlueprintTypeName(getString(attrs, "blueprintTypeName"));
		job.setBlueprintLocationID(getLong(attrs, "blueprintLocationID"));
		job.setOutputLocationID(getLong(attrs, "outputLocationID"));
		job.setRuns(getInt(attrs, "runs"));
		job.setCost(getDouble(attrs, "cost"));
		job.setLicensedRuns(getInt(attrs, "licensedRuns"));
		job.setProductTypeID(getInt(attrs, "productTypeID"));
		job.setProductTypeName(getString(attrs, "productTypeName"));
		job.setStatus(getInt(attrs, "status"));
		job.setTimeInSeconds(getInt(attrs, "timeInSeconds"));
		job.setStartDate(getDate(attrs, "startDate"));
		job.setEndDate(getDate(attrs, "endDate"));
		job.setPauseDate(getDate(attrs, "pauseDate"));
		job.setCompletedDate(getDate(attrs, "completedDate"));
		job.setCompletedCharacterID(getLong(attrs, "completedCharacterID"));
		job.setSuccessfulRuns(getInt(attrs, "successfulRuns"));

		return job;
	}
}
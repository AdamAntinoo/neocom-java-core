package com.beimin.eveapi.shared.industryjobs;

import java.util.Date;

public class ApiNewIndustryJob {
	private long		jobID;
	private long		installerID;					// OwnerID : int
	private String	installerName;
	private long		facilityID;
	private long		solarSystemID;
	private String	solarSystemName;
	private long		stationID;
	private int			activityID;
	private long		blueprintID;
	private int			blueprintTypeID;
	private String	blueprintTypeName;
	private long		blueprintLocationID;
	private long		outputLocationID;
	private int			runs;
	private double	cost;
	private int			licensedRuns;
	private int			productTypeID;
	private String	productTypeName;
	private int			status;
	private int			timeInSeconds;
	private Date		startDate;
	private Date		endDate;
	private Date		pauseDate;
	private Date		completedDate;
	private long		completedCharacterID;
	private int			successfulRuns;

	public int getActivityID() {
		return activityID;
	}

	public long getBlueprintID() {
		return blueprintID;
	}

	public long getBlueprintLocationID() {
		return blueprintLocationID;
	}

	public int getBlueprintTypeID() {
		return blueprintTypeID;
	}

	public String getBlueprintTypeName() {
		return blueprintTypeName;
	}

	public long getCompletedCharacterID() {
		return completedCharacterID;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public double getCost() {
		return cost;
	}

	public Date getEndDate() {
		return endDate;
	}

	public long getFacilityID() {
		return facilityID;
	}

	public long getInstallerID() {
		return installerID;
	}

	public String getInstallerName() {
		return installerName;
	}

	public long getJobID() {
		return jobID;
	}

	public int getLicensedRuns() {
		return licensedRuns;
	}

	public long getOutputLocationID() {
		return outputLocationID;
	}

	public Date getPauseDate() {
		return pauseDate;
	}

	public int getProductTypeID() {
		return productTypeID;
	}

	public String getProductTypeName() {
		return productTypeName;
	}

	public int getRuns() {
		return runs;
	}

	public long getSolarSystemID() {
		return solarSystemID;
	}

	public String getSolarSystemName() {
		return solarSystemName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public long getStationID() {
		return stationID;
	}

	public int getStatus() {
		return status;
	}

	public int getSuccessfulRuns() {
		return successfulRuns;
	}

	public int getTimeInSeconds() {
		return timeInSeconds;
	}

	public void setActivityID(final int activityID) {
		this.activityID = activityID;
	}

	public void setBlueprintID(final long blueprintID) {
		this.blueprintID = blueprintID;
	}

	public void setBlueprintLocationID(final long blueprintLocationID) {
		this.blueprintLocationID = blueprintLocationID;
	}

	public void setBlueprintTypeID(final int blueprintTypeID) {
		this.blueprintTypeID = blueprintTypeID;
	}

	public void setBlueprintTypeName(final String blueprintTypeName) {
		this.blueprintTypeName = blueprintTypeName;
	}

	public void setCompletedCharacterID(final long completedCharacterID) {
		this.completedCharacterID = completedCharacterID;
	}

	public void setCompletedDate(final Date completedDate) {
		this.completedDate = completedDate;
	}

	public void setCost(final double cost) {
		this.cost = cost;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	public void setFacilityID(final long facilityID) {
		this.facilityID = facilityID;
	}

	public void setInstallerID(final long installerID) {
		this.installerID = installerID;
	}

	public void setInstallerName(final String installerName) {
		this.installerName = installerName;
	}

	public void setJobID(final long jobID) {
		this.jobID = jobID;
	}

	public void setLicensedRuns(final int licensedRuns) {
		this.licensedRuns = licensedRuns;
	}

	public void setOutputLocationID(final long outputLocationID) {
		this.outputLocationID = outputLocationID;
	}

	public void setPauseDate(final Date pauseDate) {
		this.pauseDate = pauseDate;
	}

	public void setProductTypeID(final int productTypeID) {
		this.productTypeID = productTypeID;
	}

	public void setProductTypeName(final String productTypeName) {
		this.productTypeName = productTypeName;
	}

	public void setRuns(final int runs) {
		this.runs = runs;
	}

	public void setSolarSystemID(final long solarSystemID) {
		this.solarSystemID = solarSystemID;
	}

	public void setSolarSystemName(final String solarSystemName) {
		this.solarSystemName = solarSystemName;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public void setStationID(final long stationID) {
		this.stationID = stationID;
	}

	public void setStatus(final int status) {
		this.status = status;
	}

	public void setSuccessfulRuns(final int successfulRuns) {
		this.successfulRuns = successfulRuns;
	}

	public void setTimeInSeconds(final int timeInSeconds) {
		this.timeInSeconds = timeInSeconds;
	}

}
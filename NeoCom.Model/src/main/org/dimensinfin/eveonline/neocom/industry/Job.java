//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.neocom.industry;

// - IMPORT SECTION .........................................................................................
import java.util.Date;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Jobs")
public class Job extends AbstractComplexNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long			serialVersionUID	= -6841505320348309318L;
	private static final int			maxRuns						= 10;
	//	public static long						NEXTGENERATEDJOBID	= 7000000;

	// - F I E L D - S E C T I O N ............................................................................
	/**
	 * This is a generated identifier to allow having duplicated asset numbers when processing updates. This is
	 * the primary key identifier and it is generated by an incremental sequence.
	 */
	@DatabaseField(id = true)
	private long									jobID;
	@DatabaseField
	private String								jobType						= "CCP";
	@DatabaseField
	private long									facilityID;
	@DatabaseField
	private long									stationID;
	@DatabaseField
	private int										activityID;
	@DatabaseField(index = true)
	private long									blueprintID;
	@DatabaseField
	private int										blueprintTypeID		= -1;
	@DatabaseField(index = true)
	private long									blueprintLocationID;
	@DatabaseField
	private int										runs							= 0;
	@DatabaseField
	private double								cost							= 0.0;
	@DatabaseField
	private int										licensedRuns;
	@DatabaseField
	private int										productTypeID;
	@DatabaseField
	private int										status;
	@DatabaseField
	private int										timeInSeconds;
	@DatabaseField
	private Date									startDate;
	@DatabaseField
	private Date									endDate;
	@DatabaseField
	private Date									completedDate;
	@DatabaseField
	private long									completedCharacterID;
	@DatabaseField
	private int										successfulRuns;

	/** Here starts the fields that come form item data but useful for search operations. */
	@DatabaseField
	private long									ownerID						= -1;

	/** Derived fields that store cached data. */
	private transient EveItem			blueprintItem			= null;
	private transient EveLocation	jobLocation				= null;
	private final String					moduleName				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Job() {
	}

	public Job(final long newJobID) {
		jobID = newJobID;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getActivityID() {
		return activityID;
	}

	public long getBlueprintID() {
		return blueprintID;
	}

	public long getBlueprintLocationID() {
		return blueprintLocationID;
	}

	public String getBlueprintName() {
		if (null == blueprintItem) {
			blueprintItem = AppConnector.getCCPDBConnector().searchItembyID(blueprintTypeID);
		}
		return blueprintItem.getName();
	}

	public int getBlueprintTypeID() {
		return blueprintTypeID;
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

	public long getJobID() {
		return jobID;
	}

	public EveLocation getJobLocation() {
		if (null == jobLocation) {
			jobLocation = AppConnector.getCCPDBConnector().searchLocationbyID(facilityID);
		}
		return jobLocation;
	}

	public String getJobType() {
		return jobType;
	}

	public int getLicensedRuns() {
		return licensedRuns;
	}

	public int getMaxRuns() {
		return Job.maxRuns;
	}

	public String getModuleName() {
		return moduleName;
	}

	public int getProductTypeID() {
		return productTypeID;
	}

	public int getRuns() {
		return runs;
	}

	public Date getStartDate() {
		return startDate;
	}

	//	public DateTime getStart() {
	//		return start;
	//	}

	public long getStationID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getStatus() {
		return status;
	}

	public int getSuccessfulRuns() {
		return successfulRuns;
	}

	//	public int getThreads() {
	//		return threads;
	//	}

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
		// Load the blueprint item reference.
		blueprintItem = AppConnector.getCCPDBConnector().searchItembyID(blueprintTypeID);
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

	//	public void setInstallerID(final long installerID) {
	//		ownerID = installerID;
	//	}

	public void setFacilityID(final long facilityID) {
		this.facilityID = facilityID;
	}

	public void setJobType(final String jobType) {
		this.jobType = jobType;
	}

	public void setLicensedRuns(final int licensedRuns) {
		this.licensedRuns = licensedRuns;
	}

	public void setOwnerID(final long ownerID) {
		this.ownerID = ownerID;
	}

	public void setProductTypeID(final int productTypeID) {
		this.productTypeID = productTypeID;
	}

	public void setRuns(final int newRuns) {
		runs = newRuns;
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

	//	public void setThreads(final int quantity) {
	//		threads = quantity;
	//	}

	public void setTimeInSeconds(final int timeInSeconds) {
		this.timeInSeconds = timeInSeconds;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Job [");
		buffer.append(jobID).append(" ");
		buffer.append("[").append(blueprintID).append("] ");
		buffer.append("#").append(blueprintTypeID).append(" ");
		buffer.append(this.getBlueprintName()).append(" ");
		if (activityID == ModelWideConstants.activities.MANUFACTURING) {
			buffer.append("MANUFACTURE").append(" ");
		}
		if (activityID == ModelWideConstants.activities.INVENTION) {
			buffer.append("INVENTION").append(" ");
		}
		buffer.append("Output:#").append(productTypeID).append(" ");
		//		buffer.append("Module [").append(moduleName).append("] ");
		buffer.append("Start:").append(startDate).append(" - End:").append(endDate);
		buffer.append("Run/Licensed:").append(runs).append("/").append(licensedRuns).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	//	protected Duration calculateSpan() {
	//		// The span is the period by the number of runs.
	//		long spanDuration = period.toDurationFrom(new DateTime()).getMillis() * runs;
	//		return new Duration(spanDuration);
	//	}

	//	protected Duration calculateSpan(final int newRuns) {
	//		runs = newRuns;
	//		return calculateSpan();
	//	}
}

// - UNUSED CODE ............................................................................................

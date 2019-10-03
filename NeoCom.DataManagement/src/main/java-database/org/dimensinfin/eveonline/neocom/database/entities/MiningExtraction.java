package org.dimensinfin.eveonline.neocom.database.entities;

import java.util.Objects;
import javax.persistence.Entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.core.IAggregableItem;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;

/**
 * This class represents the database entity to store the ESI character's mining extractions. That data are records that are kept for 30 days
 * and that contain the incremental values of what was mined on a data for a particular resource on a determinate solar system.
 *
 * The records are stored on the database by creating an special unique identifier that is generated from the esi read data.
 *
 * The date is obtained from the esi record but the processing hour is set from the current creation time if the record is from today'ss date or
 * fixed to 24 if the record has a date different from today.
 *
 * Records can be read at any time and current date records values can increase if there is more mining done since the last esi data request. So
 * our system will record quantities by the hour and later calculate the deltas so the record will represent the estimated quantity mined on that
 * hour and not the aggregated quantity mined along the day.
 *
 * @author Adam Antinoo
 */
@Entity(name = "MiningExtractions")
@DatabaseTable(tableName = "MiningExtractions")
public class MiningExtraction extends UpdatableEntity implements IAggregableItem {
	public static final String EXTRACTION_DATE_FORMAT = "YYYY-MM-dd";
	// - F I E L D - S E C T I O N
	@DatabaseField(id = true)
	private String id = "YYYY-MM-DD:HH-SYSTEMID-TYPEID-OWNERID";
	@DatabaseField
	private int typeId = -1;
	@DatabaseField
	private int solarSystemId = -2;
	@DatabaseField
	private int quantity = 0;
	@DatabaseField
	private long delta = 0;
	@DatabaseField
	private String extractionDateName;
	@DatabaseField
	private int extractionHour = 24;
	@DatabaseField(index = true)
	private long ownerId = -1;

	private transient EveItem resourceItem;
	private transient EsiLocation solarSystemLocation;
	// - C O N S T R U C T O R S
	private MiningExtraction() {
		super();
	}

	/**
	 * The record id creation used two algorithms. If the date is the current date we add the hour as an identifier. But id the date is not
	 * the current date we should not change any data on the database since we understand that old data is not being modified. But it can
	 * happen that old data is the first time the it is added to the database. So we set the hour of day to the number 24.
	 */
	@Deprecated
	public static String generateRecordId( final LocalDate date, final int typeId, final long systemId, final long ownerId ) {
		// Check the date.
		final String todayDate = DateTime.now().toString(EXTRACTION_DATE_FORMAT);
		final String targetDate = date.toString(EXTRACTION_DATE_FORMAT);
		if (todayDate.equalsIgnoreCase(targetDate))
			return new StringBuffer()
					       .append(date.toString(EXTRACTION_DATE_FORMAT)).append(":")
					       .append(DateTime.now().getHourOfDay()).append("-")
					       .append(systemId).append("-")
					       .append(typeId).append("-")
					       .append(ownerId)
					       .toString();
		else
			return new StringBuffer()
					       .append(date.toString(EXTRACTION_DATE_FORMAT)).append(":")
					       .append(24).append("-")
					       .append(systemId).append("-")
					       .append(typeId).append("-")
					       .append(ownerId)
					       .toString();
	}

	@Deprecated
	public static String generateRecordId( final String date, final int hour, final int typeId, final long systemId, final long ownerId ) {
		return "".concat(date).concat(":")
				       .concat(Integer.toString(hour)).concat("-")
				       .concat(Long.toString(systemId)).concat("-")
				       .concat(Integer.toString(typeId)).concat("-")
				       .concat(Long.toString(ownerId));
	}

	public static String generateRecordId( final LocalDate date, final int hour, final int typeId,
	                                       final long systemId, final long ownerId ) {
		return "".concat(date.toString(EXTRACTION_DATE_FORMAT)).concat(":")
				       .concat(Integer.toString(hour)).concat("-")
				       .concat(Long.toString(systemId)).concat("-")
				       .concat(Integer.toString(typeId)).concat("-")
				       .concat(Long.toString(ownerId));
	}

	// -  G E T T E R S   &   S E T T E R S
	public String getId() {
		return this.id;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public String getResourceName() {
		return this.resourceItem.getName();
	}

	public MiningExtraction setResourceItem( final EveItem resourceItem ) {
		this.resourceItem = resourceItem;
		return this;
	}

	public LocalDate getExtractionDate() {
		return new LocalDate(this.extractionDateName);
	}

	public String getExtractionDateName() {
		return this.extractionDateName;
	}

	public int getExtractionHour() {
		return this.extractionHour;
	}

	public MiningExtraction setExtractionHour( final int extractionHour ) {
		this.extractionHour = extractionHour;
		this.id = MiningExtraction.generateRecordId(new LocalDate(this.extractionDateName),
		                                            this.extractionHour, this.typeId, this.solarSystemId, this.ownerId);
		return this;
	}

	public int getSolarSystemId() {
		return this.solarSystemId;
	}

	public MiningExtraction setSolarSystemLocation( final EsiLocation solarSystemLocation ) {
		Objects.requireNonNull(solarSystemLocation);
		this.solarSystemLocation = solarSystemLocation;
		//this.solarSystemId=this.solarSystemLocation.getSystemId(); // This can change the extraction identifier so do not change
		return this;
	}

	public String getSystemName() {return this.solarSystemLocation.getSystemName();}

	public long getDelta() {
		return this.delta;
	}

	public MiningExtraction setDelta( final long delta ) {
		this.delta = delta;
		return this;
	}

	public long getOwnerId() {
		return this.ownerId;
	}

	public String getURLForItem() {
		return this.resourceItem.getURLForItem();
	}

	// - I A G G R E G A B L E I T E M
	public int getQuantity() {
		return this.quantity;
	}

	public MiningExtraction setQuantity( final int quantity ) {
		this.quantity = quantity;
		return this;
	}

	public double getVolume() {
		return this.resourceItem.getVolume();
	}

	public double getPrice() {
		return this.resourceItem.getPrice();
	}

	// - C O R E
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				       .append("id", this.id)
				       .append("typeId", this.typeId)
				       .append("solarSystemId", this.solarSystemId)
				       .append("quantity", this.quantity)
				       .append("delta", this.delta)
				       .append("extractionDateName", this.extractionDateName)
				       .append("extractionHour", this.extractionHour)
				       .append("ownerId", this.ownerId)
				       .toString();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final MiningExtraction that = (MiningExtraction) o;
		return new EqualsBuilder()
				       .appendSuper(super.equals(o))
				       .append(this.typeId, that.typeId)
				       .append(this.solarSystemId, that.solarSystemId)
				       .append(this.quantity, that.quantity)
				       .append(this.delta, that.delta)
				       .append(this.extractionHour, that.extractionHour)
				       .append(this.ownerId, that.ownerId)
				       .append(this.id, that.id)
				       .append(this.extractionDateName, that.extractionDateName)
				       .isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				       .appendSuper(super.hashCode())
				       .append(this.id)
				       .append(this.typeId)
				       .append(this.solarSystemId)
				       .append(this.quantity)
				       .append(this.delta)
				       .append(this.extractionDateName)
				       .append(this.extractionHour)
				       .append(this.ownerId)
				       .toHashCode();
	}

	// - B U I L D E R
	public static class Builder {
		private MiningExtraction onConstruction;

		public Builder() {
			this.onConstruction = new MiningExtraction();
		}

		public Builder withTypeId( final int typeId ) {
			this.onConstruction.typeId = typeId;
			this.onConstruction.resourceItem = new EveItem(typeId);
			return this;
		}

		// TODO - Helper to allow setting the id required system identifier without locading the Location
		public Builder withSolarSystemId( final Integer solarSystemId ) {
			Objects.requireNonNull(solarSystemId);
			this.onConstruction.solarSystemId = solarSystemId;
			return this;
		}

		public Builder withSolarSystemLocation( final EsiLocation solarSystemLocation ) {
			Objects.requireNonNull(solarSystemLocation);
			this.onConstruction.solarSystemLocation = solarSystemLocation;
			this.onConstruction.solarSystemId = this.onConstruction.solarSystemLocation.getSystemId();
			return this;
		}

		public Builder withQuantity( final int quantity ) {
			this.onConstruction.quantity = quantity;
			return this;
		}

		public Builder withOwnerId( final int ownerId ) {
			this.onConstruction.ownerId = ownerId;
			return this;
		}

		public Builder withExtractionDate( final LocalDate extractionDate ) {
			Objects.requireNonNull(extractionDate);
			this.onConstruction.extractionDateName = extractionDate.toString(EXTRACTION_DATE_FORMAT);
			return this;
		}

		public Builder withExtractionHour( final int extractionHour ) {
			this.onConstruction.extractionHour = extractionHour;
			return this;
		}

		public Builder fromMining( final GetCharactersCharacterIdMining200Ok mineInstance ) {
			this.withTypeId(mineInstance.getTypeId());
			this.withQuantity(mineInstance.getQuantity().intValue());
			this.withExtractionDate(mineInstance.getDate());
			return this;
		}

		/**
		 * The unique and special extraction identifier is created at this point and using the current extraction time. This will exclude proper
		 * testing so there is special code to create special identifier when the <code>onConstruction.extractionHour</code> is set.
		 */
		public MiningExtraction build() {
			Objects.requireNonNull(this.onConstruction.resourceItem);
			// TODO - This comment allows for lazy load of the extraction location so converters do not depend on ESIAdapter
//			Objects.requireNonNull(this.onConstruction.solarSystemLocation);
			this.onConstruction.id = MiningExtraction.generateRecordId(
					new LocalDate(this.onConstruction.extractionDateName),
					this.onConstruction.extractionHour,
					this.onConstruction.typeId,
					this.onConstruction.solarSystemId,
					this.onConstruction.ownerId);
			return this.onConstruction;
		}
	}
}

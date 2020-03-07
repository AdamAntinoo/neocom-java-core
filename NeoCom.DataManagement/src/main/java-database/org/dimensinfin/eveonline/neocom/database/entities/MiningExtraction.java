package org.dimensinfin.eveonline.neocom.database.entities;

import java.util.Objects;
import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.annotation.RequiresNetwork;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystem;
import org.dimensinfin.eveonline.neocom.service.NeoItemFactory;

/**
 * This class represents the database entity to store the ESI character's mining extractions. That data are records that are kept for 30 days and
 * contain the incremental values of what was mined on a data for a particular resource on a determinate solar system.
 * The records are stored on the database by creating an special unique identifier that is generated from the esi read data.
 *
 * The date is obtained from the esi record but the processing hour is set from the current creation time if the record is from today'ss date or
 * fixed to 24 if the record has a date different from today.
 *
 * Records can be read at any time and current date records values can increase if there is more mining done since the last esi data request. So
 * our system will record quantities by the hour and later calculate the deltas so the record will represent the estimated quantity mined on that
 * hour and not the aggregated quantity mined along the day.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.14.0
 */
//@Entity(name = "MiningExtractions")
//@DatabaseTable(tableName = "MiningExtractions")
@JsonIgnoreProperties
public class MiningExtraction /*extends UpdatableEntity */ /*implements IAggregableItem */ {
	public static final String EXTRACTION_DATE_FORMAT = "YYYY-MM-dd";

	/**
	 * The record id creation used two algorithms. If the date is the current date we add the hour as an identifier. But id the date is not the
	 * current date we should not change any data on the database since we understand that old data is not being modified. But it can happen that
	 * old data is the first time the it is added to the database. So we set the hour of day to the number 24.
	 */
	@Deprecated
	public static String generateRecordId( final LocalDate date, final int typeId, final long systemId, final long ownerId ) {
		// Check the date.
		final String todayDate = DateTime.now().toString( EXTRACTION_DATE_FORMAT );
		final String targetDate = date.toString( EXTRACTION_DATE_FORMAT );
		if (todayDate.equalsIgnoreCase( targetDate ))
			return new StringBuffer()
					.append( date.toString( EXTRACTION_DATE_FORMAT ) ).append( ":" )
					.append( DateTime.now().getHourOfDay() ).append( "-" )
					.append( systemId ).append( "-" )
					.append( typeId ).append( "-" )
					.append( ownerId )
					.toString();
		else
			return new StringBuffer()
					.append( date.toString( EXTRACTION_DATE_FORMAT ) ).append( ":" )
					.append( 24 ).append( "-" )
					.append( systemId ).append( "-" )
					.append( typeId ).append( "-" )
					.append( ownerId )
					.toString();
	}

	@Deprecated
	public static String generateRecordId( final String date, final int hour, final int typeId, final long systemId, final long ownerId ) {
		return "".concat( date ).concat( ":" )
				.concat( Integer.toString( hour ) ).concat( "-" )
				.concat( Long.toString( systemId ) ).concat( "-" )
				.concat( Integer.toString( typeId ) ).concat( "-" )
				.concat( Long.toString( ownerId ) );
	}

	public static String generateRecordId( final LocalDate date, final int hour, final int typeId,
	                                       final long systemId, final long ownerId ) {
		return "".concat( date.toString( EXTRACTION_DATE_FORMAT ) ).concat( ":" )
				.concat( Integer.toString( hour ) ).concat( "-" )
				.concat( Long.toString( systemId ) ).concat( "-" )
				.concat( Integer.toString( typeId ) ).concat( "-" )
				.concat( Long.toString( ownerId ) );
	}

	// - F I E L D - S E C T I O N
//	@Id
//	@DatabaseField(id = true)
//	@Column(name = "id", updatable = false, nullable = false)
//	private String id = "YYYY-MM-DD:HH-SYSTEMID-TYPEID-OWNERID";
	//	@DatabaseField
//	@Column(name = "typeId")
//	private Integer typeId; // The eve type identifier for the resource being extracted
//	@DatabaseField
//	@Column(name = "solarSystemId")
//	private Integer solarSystemId; // The solar system where the extraction is recorded.
	@DatabaseField
	@Column(name = "quantity")
	private int quantity = 0;
	@DatabaseField
	@Column(name = "delta")
	private long delta = 0;
	@DatabaseField(index = true)
	@Column(name = "extractionDateName")
	private String extractionDateName;
	@DatabaseField
	@Column(name = "extractionHour")
	private int extractionHour = 24; // The hour of the day for this extraction delta or 24 if this is the date aggregated value.
	@DatabaseField(index = true)
	@Column(name = "ownerId")
	private Integer ownerId; // The credential identifier of the pilot's extraction.
	private transient NeoItem resourceItem;
	private transient SpaceSystem solarSystemLocation;

	// - C O N S T R U C T O R S
	private MiningExtraction() {
		super();
	}

	// -  G E T T E R S   &   S E T T E R S
	public String getId() {
		return "".concat( this.extractionDateName ).concat( ":" )
				.concat( Integer.toString( this.extractionHour ) ).concat( "-" )
				.concat( this.getLocationId().toString() ).concat( "-" )
				.concat( this.getTypeId().toString() ).concat( "-" )
				.concat( this.ownerId.toString() );
	}

	public Integer getTypeId() {return this.resourceItem.getTypeId();}

	public Long getLocationId() {return this.solarSystemLocation.getLocationId();}

	public String getResourceName() {
		return this.resourceItem.getName();
	}

	public LocalDate getExtractionDate() {
		return new LocalDate( this.extractionDateName );
	}

	public String getExtractionDateName() {
		return this.extractionDateName;
	}

	public int getExtractionHour() {
		return this.extractionHour;
	}

	public String getSystemName() {
		return this.solarSystemLocation.getSolarSystemName();
	}

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

	public MiningExtraction setOwnerId( final Integer ownerId ) {
		this.ownerId = ownerId;
		return this;
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

	public MiningExtraction setResourceItem( final NeoItem resourceItem ) {
		this.resourceItem = resourceItem;
		return this;
	}

	public MiningExtraction setSolarSystemLocation( final SpaceSystem solarSystemLocation ) {
		Objects.requireNonNull( solarSystemLocation );
		this.solarSystemLocation = solarSystemLocation;
		return this;
	}

//	// - I C O L L A B O R A T I O N
//	@Override
//	public List<ICollaboration> collaborate2Model( final String s ) {
//		return new ArrayList<>(  );
//	}
//
//	@Override
//	public int compareTo( @NotNull final Object o ) {
//		return this.equals( o )
//	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( this.getId() )
//				.append( this.typeId )
//				.append( this.solarSystemId )
				.append( this.quantity )
				.append( this.delta )
				.append( this.extractionDateName )
				.append( this.extractionHour )
				.append( this.ownerId )
				.toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final MiningExtraction that = (MiningExtraction) o;
		return new EqualsBuilder()
				.appendSuper( super.equals( o ) )
//				.append( this.typeId, that.typeId )
//				.append( this.solarSystemId, that.solarSystemId )
				.append( this.quantity, that.quantity )
				.append( this.delta, that.delta )
				.append( this.extractionHour, that.extractionHour )
				.append( this.ownerId, that.ownerId )
//				.append( this.id, that.id )
				.append( this.extractionDateName, that.extractionDateName )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "id", this.getLocationId() )
				.append( "typeId", this.getTypeId() )
				.append( "solarSystemId", this.getLocationId() )
				.append( "quantity", this.quantity )
				.append( "delta", this.delta )
				.append( "extractionDateName", this.extractionDateName )
				.append( "extractionHour", this.extractionHour )
				.append( "ownerId", this.ownerId )
				.toString();
	}

	// - B U I L D E R
	public static class Builder {
		private MiningExtraction onConstruction;

		public Builder() {
			this.onConstruction = new MiningExtraction();
		}

		/**
		 * The unique and special extraction identifier is created at this point and using the current extraction time. This will
		 * exclude proper
		 * testing so there is special code to create special identifier when the <code>onConstruction.extractionHour</code> is
		 * set.
		 */
		public MiningExtraction build() {
			Objects.requireNonNull( this.onConstruction.resourceItem );
			Objects.requireNonNull( this.onConstruction.solarSystemLocation );
			Objects.requireNonNull( this.onConstruction.extractionDateName );
//			this.onConstruction.id = MiningExtraction.generateRecordId(
//					new LocalDate( this.onConstruction.extractionDateName ),
//					this.onConstruction.extractionHour,
//					this.onConstruction.typeId,
//					this.onConstruction.solarSystemId,
//					this.onConstruction.ownerId );
			return this.onConstruction;
		}

//		public Builder fromMining( final GetCharactersCharacterIdMining200Ok mineInstance ) {
//			this.withTypeId( mineInstance.getTypeId() );
//			this.withQuantity( mineInstance.getQuantity().intValue() );
//			this.withExtractionDate( mineInstance.getDate() );
//			return this;
//		}

		public MiningExtraction.Builder withExtractionDate( final String extractionDate ) {
			Objects.requireNonNull( extractionDate );
			this.onConstruction.extractionDateName = extractionDate;
			return this;
		}

		public Builder withExtractionHour( final Integer extractionHour ) {
			Objects.requireNonNull( extractionHour );
			this.onConstruction.extractionHour = extractionHour;
			return this;
		}

//		public Builder withOwnerId( final Integer ownerId ) {
//			this.onConstruction.ownerId = ownerId;
//			return this;
//		}

		public MiningExtraction.Builder withOwnerId( final Integer ownerId ) {
			Objects.requireNonNull( ownerId );
			this.onConstruction.ownerId = ownerId;
			return this;
		}

//		// TODO - Helper to allow setting the id required system identifier without searching for the Location
//		@RequiresNetwork
//		public Builder withSolarSystemId( final Integer solarSystemId ) {
//			Objects.requireNonNull( solarSystemId );
//			this.onConstruction.solarSystemId = solarSystemId;
//			this.onConstruction.solarSystemLocation=
//			return this;
//		}

		public MiningExtraction.Builder withQuantity( final Integer quantity ) {
			if (null == quantity) this.onConstruction.quantity = 0;
			this.onConstruction.quantity = quantity;
			return this;
		}

		public MiningExtraction.Builder withSpaceSystem( final SpaceSystem solarSystemLocation ) {
			Objects.requireNonNull( solarSystemLocation );
			this.onConstruction.solarSystemLocation = solarSystemLocation;
			return this;
		}

		@RequiresNetwork
		public MiningExtraction.Builder withTypeId( final Integer typeId ) {
			Objects.requireNonNull( typeId );
			this.onConstruction.resourceItem = NeoItemFactory.getItemById( typeId );
			return this;
		}
	}
}

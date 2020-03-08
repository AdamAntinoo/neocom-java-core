package org.dimensinfin.eveonline.neocom.miningextraction.domain;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystem;

/**
 * This class represents a mining extraction to be used for rendering of for other data management operations. It is constructed from the persisted
 * esi record and has also access to location data and esi item information.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.14.0
 */
@JsonIgnoreProperties
public class MiningExtraction {
	private Long quantity = 0L;
	private long delta = 0;
	private String extractionDateName;
	private int extractionHour = 24; // The hour of the day for this extraction delta or 24 if this is the date aggregated value.
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

	public String getURLForItem() {
		return this.resourceItem.getURLForItem();
	}

	public Long getQuantity() {
		return this.quantity;
	}

	public double getVolume() {
		return this.resourceItem.getVolume();
	}

	public double getPrice() {
		return this.resourceItem.getPrice();
	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( this.getId() )
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
				.append( this.quantity, that.quantity )
				.append( this.delta, that.delta )
				.append( this.extractionHour, that.extractionHour )
				.append( this.ownerId, that.ownerId )
				.append( this.extractionDateName, that.extractionDateName )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "quantity", this.quantity )
				.append( "delta", this.delta )
				.append( "extractionDateName", this.extractionDateName )
				.append( "extractionHour", this.extractionHour )
				.append( "ownerId", this.ownerId )
				.append( "id", getId() )
				.append( "typeId", getTypeId() )
				.append( "locationId", getLocationId() )
				.append( "resourceName", getResourceName() )
				.append( "systemName", getSystemName() )
				.append( "URLForItem", getURLForItem() )
				.append( "volume", getVolume() )
				.append( "price", getPrice() )
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
			return this.onConstruction;
		}

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

		public MiningExtraction.Builder withNeoItem( final NeoItem resourceItem ) {
			Objects.requireNonNull( resourceItem );
			this.onConstruction.resourceItem = resourceItem;
			return this;
		}

		public MiningExtraction.Builder withOwnerId( final Integer ownerId ) {
			Objects.requireNonNull( ownerId );
			this.onConstruction.ownerId = ownerId;
			return this;
		}

		public MiningExtraction.Builder withQuantity( final Long quantity ) {
			if (null == quantity) this.onConstruction.quantity = 0L;
			this.onConstruction.quantity = quantity;
			return this;
		}

		public MiningExtraction.Builder withSpaceSystem( final SpaceSystem solarSystemLocation ) {
			Objects.requireNonNull( solarSystemLocation );
			this.onConstruction.solarSystemLocation = solarSystemLocation;
			return this;
		}
	}
}

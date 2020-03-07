package org.dimensinfin.eveonline.neocom.database.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Database entities are the data views that are stored on repositories. Usually they require transformation to and from the storage. This class
 * represents a mineral extraction delta.
 *
 * During mining the Esi system aggregates mineral extractions on records per system per date. So in the api results we found the complete
 * extractions for a resource and system for a past date and the mined resource per location of the current date.
 *
 * Two list of extractions at different times of the same date will keep past dates records equal while today extractions will reflect the
 * new aggregated extraction for a mineral per system so the difference with the first result will give the delta extractions between the two
 * record times.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.19.0
 */
@Entity(name = "MiningExtractions")
@DatabaseTable(tableName = "MiningExtractions")
public class MiningExtractionEntity extends UpdatableEntity {
	// - F I E L D - S E C T I O N
	@Id
	@DatabaseField(id = true)
	@Column(name = "id", updatable = false, nullable = false)
	private String id = "YYYY-MM-DD:HH-SYSTEMID-TYPEID-OWNERID";
	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false)
	private int typeId; // The eve type identifier for the resource being extracted
	@DatabaseField
	private int solarSystemId; // The solar system where the extraction is recorded.
	@DatabaseField
	private long quantity = 0;
	@DatabaseField
	private long delta = 0L;
	@DatabaseField(dataType = DataType.STRING, canBeNull = false, index = true)
	private String extractionDateName;
	@DatabaseField
	private int extractionHour = 24; // The hour of the day for this extraction delta or 24 if this is the date aggregated value.
	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false, index = true)
	@Column(name = "ownerId")
	private int ownerId; // The credential identifier of the pilot's extraction.

	private MiningExtractionEntity() {}

	// - G E T T E R S   &   S E T T E R S
	public String getId() {
		return this.id;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public Integer getSolarSystemId() {
		return this.solarSystemId;
	}

	public Long getQuantity() {
		return this.quantity;
	}

	public MiningExtractionEntity setQuantity( final long quantity ) {
		this.quantity = quantity;
		return this;
	}

	public Long getDelta() {
		return this.delta;
	}

	public String getExtractionDateName() {
		return this.extractionDateName;
	}

	public Integer getExtractionHour() {
		return this.extractionHour;
	}

	public Integer getOwnerId() {
		return this.ownerId;
	}

	// - B U I L D E R
	public static class Builder {
		private MiningExtractionEntity onConstruction;

		public Builder() {
			this.onConstruction = new MiningExtractionEntity();
		}

		public MiningExtractionEntity build() {
			return this.onConstruction;
		}

		public MiningExtractionEntity.Builder withDelta( final Long delta ) {
			Objects.requireNonNull( delta );
			this.onConstruction.delta = delta;
			return this;
		}

		public MiningExtractionEntity.Builder withExtractionDateName( final String extractionDateName ) {
			Objects.requireNonNull( extractionDateName );
			this.onConstruction.extractionDateName = extractionDateName;
			return this;
		}

		public MiningExtractionEntity.Builder withExtractionHour( final Integer extractionHour ) {
			Objects.requireNonNull( extractionHour );
			this.onConstruction.extractionHour = extractionHour;
			return this;
		}

		public MiningExtractionEntity.Builder withId( final String id ) {
			Objects.requireNonNull( id );
			this.onConstruction.id = id;
			return this;
		}

		public MiningExtractionEntity.Builder withOwnerId( final Integer ownerId ) {
			Objects.requireNonNull( ownerId );
			this.onConstruction.ownerId = ownerId;
			return this;
		}

		public MiningExtractionEntity.Builder withQuantity( final Integer quantity ) {
			Objects.requireNonNull( quantity );
			this.onConstruction.quantity = quantity;
			return this;
		}

		public MiningExtractionEntity.Builder withSolarSystemId( final Integer solarSystemId ) {
			Objects.requireNonNull( solarSystemId );
			this.onConstruction.solarSystemId = solarSystemId;
			return this;
		}

		public MiningExtractionEntity.Builder withTypeId( final Integer typeId ) {
			Objects.requireNonNull( typeId );
			this.onConstruction.typeId = typeId;
			return this;
		}
	}
}
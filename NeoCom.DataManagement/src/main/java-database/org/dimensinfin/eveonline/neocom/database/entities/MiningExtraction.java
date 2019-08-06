package org.dimensinfin.eveonline.neocom.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.interfaces.IAggregableItem;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Objects;

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
@DatabaseTable(tableName = "MiningExtractions")
public class MiningExtraction extends NeoComNode implements IAggregableItem {
	/**
	 * The record id creation used two algorithms. If the date is the current date we add the hour as an identifier. But id the date is not
	 * the current date we should not change any data on the database since we understand that old data is not being modified. But it can
	 * happen that old data is the first time the it is added to the database. So we set the hour of day to the number 24.
	 */
	public static String generateRecordId( final LocalDate date, final int typeId, final long systemId, final long ownerId ) {
		// Check the date.
		final String todayDate = DateTime.now().toString("YYYY/MM/dd");
		final String targetDate = date.toString("YYYY/MM/dd");
		if (todayDate.equalsIgnoreCase(targetDate))
			return new StringBuffer()
					       .append(date.toString("YYYY/MM/dd")).append(":")
					       .append(DateTime.now().getHourOfDay()).append("-")
					       .append(systemId).append("-")
					       .append(typeId).append("-")
					       .append(ownerId)
					       .toString();
		else
			return new StringBuffer()
					       .append(date.toString("YYYY/MM/dd")).append(":")
					       .append(24).append("-")
					       .append(systemId).append("-")
					       .append(typeId).append("-")
					       .append(ownerId)
					       .toString();
	}

	public static String generateRecordId( final String date, final int hour, final int typeId, final long systemId, final long ownerId ) {
		return "".concat(date).concat(":")
		         .concat(Integer.toString(hour)).concat("-")
		         .concat(Long.toString(systemId)).concat("-")
		         .concat(Integer.toString(typeId)).concat("-")
		         .concat(Long.toString(ownerId));
	}

	// - F I E L D - S E C T I O N
	@DatabaseField(id = true)
	private String id = "YYYY/MM/DD:HH-SYSTEMID-TYPEID-OWNERID";
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
	private transient EsiLocation systemCache;

	// - C O N S T R U C T O R S
	private MiningExtraction() {
		super();
	}

	// -  G E T T E R S   &   S E T T E R S
	public String getId() {
		return this.id;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public String getResourceName() {
//		if (null == this.resourceItem) {
//			this.resourceItem = new EveItem(this.getTypeId());
//			// TODO - Reconnect the event listeners with the new implementation
////			this.resourceItem.addPropertyChangeListener(this);
//		}
		return this.resourceItem.getName();
	}

	public String getSystemName() {
		if (null == this.systemCache) {
			this.systemCache = new EsiLocation(this.solarSystemId);
		}
		return this.systemCache.getSystemName();
	}

	public String getExtractionDateName() {
		return this.extractionDateName;
	}

	public int getExtractionHour() {
		return this.extractionHour;
	}

	public int getSolarSystemId() {
		return this.solarSystemId;
	}

	public long getDelta() {
		return this.delta;
	}

	public long getOwnerId() {
		return this.ownerId;
	}

	public MiningExtraction setQuantity( final int quantity ) {
		this.quantity = quantity;
		return this;
	}

	public MiningExtraction setDelta( final long delta ) {
		this.delta = delta;
		return this;
	}

	public String getURLForItem() {
//		if (null == this.resourceItem) {
//			this.resourceItem = new EveItem(this.getTypeId());
//			// TODO - Reconnect the event listeners with the new implementation
////			this.resourceItem.addPropertyChangeListener(this);
//		}
		return this.resourceItem.getURLForItem();
	}

	// - I A G G R E G A B L E I T E M
	public int getQuantity() {
		return this.quantity;
	}

	public double getVolume() {
//		if (null == this.resourceItem) {
//			this.resourceItem = new EveItem(this.getTypeId());
//			// TODO - Reconnect the event listeners with the new implementation
////			this.resourceItem.addPropertyChangeListener(this);
//		}
		return this.resourceItem.getVolume();
	}

	public double getPrice() {
//		if (null == this.resourceItem) {
//			this.resourceItem = new EveItem(this.getTypeId());
//			// TODO - Reconnect the event listeners with the new implementation
////			this.resourceItem.addPropertyChangeListener(this);
//		}
		return this.resourceItem.getPrice();
	}

//	@Override
//	public void propertyChange( final PropertyChangeEvent event ) {
//		if (event.getPropertyName().equalsIgnoreCase(EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name())) {
//			// TODO - Reconnect the event listeners with the new implementation
////			this.sendChangeEvent(event);
//		}
//	}

	// - C O R E
	@Override
	public String toString() {
		return new StringBuffer("MiningExtraction [ ")
		.append("#").append(typeId).append("-").append(this.getResourceName()).append(" ")
		.append("x").append(quantity).append(" ")
		.append("@").append(solarSystemId).append("-").append(this.getSystemName()).append(" ")
		.append("]").toString();
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

		public Builder withSolarSystemId( final int solarSystemId ) {
			this.onConstruction.solarSystemId = solarSystemId;
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
			// Update the extractions date string.
			this.onConstruction.extractionDateName = extractionDate.toString("YYYY/MM/dd");
			final String todayDate = DateTime.now().toString("YYYY/MM/dd");
			final String targetDate = extractionDate.toString("YYYY/MM/dd");
			if (todayDate.equalsIgnoreCase(targetDate))
				this.onConstruction.extractionHour = DateTime.now().getHourOfDay();
			else
				this.onConstruction.extractionHour = 24;
			return this;
		}

		public Builder withExtractionHour( final int extractionHour ) {
			this.onConstruction.extractionHour = extractionHour;
			return this;
		}

		public Builder fromMining ( final GetCharactersCharacterIdMining200Ok mineInstance){
			this.withTypeId(mineInstance.getTypeId());
			this.withSolarSystemId(mineInstance.getSolarSystemId());
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
			final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy/MM/dd");
			if (null == this.onConstruction.extractionDateName) this.withExtractionDate(LocalDate.now());
			final LocalDate dt = dtf.parseLocalDate(this.onConstruction.extractionDateName);
			if (this.onConstruction.extractionHour == 24) this.onConstruction.id = MiningExtraction.generateRecordId(
					dt
					, this.onConstruction.typeId
					, this.onConstruction.solarSystemId
					, this.onConstruction.ownerId);
			else this.onConstruction.id = MiningExtraction.generateRecordId(
					this.onConstruction.extractionDateName
					, this.onConstruction.extractionHour
					, this.onConstruction.typeId
					, this.onConstruction.solarSystemId
					, this.onConstruction.ownerId);
			return this.onConstruction;
		}
	}
}

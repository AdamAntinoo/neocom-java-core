package org.dimensinfin.eveonline.neocom.miningextraction.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.annotation.LogEnterExit;
import org.dimensinfin.eveonline.neocom.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.miningextraction.converter.GetCharactersCharacterIdMiningToMiningExtractionConverter;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class MiningExtractionDownloader /*extends Job*/ {
	private Credential credential;
	private ESIDataProvider esiDataProvider;
	private LocationCatalogService locationCatalogService;
	private MiningRepository miningRepository;

	private MiningExtractionDownloader() {}

	//	// - J O B
//	@Override
//	public int getUniqueIdentifier() {
//		return 0;
//	}
//
//	@Override
//	public String getName() {
//		return this.getClass().getSimpleName();
//	}
//
//	@Override
//	public Boolean call() throws Exception {
//		return null;
//	}
	/**
	 * Mining actions are small records that register the ore collected by a Pilot or the Moon mining done by a Corporation. The time
	 * base is 10 minutes and I suppose that those records are aggregated during a day. The data is a list of entries, each one
	 * declaring the quantity of one ore mined on a date and related to a single star system.
	 *
	 * The processing algorithm should add the capturing hour of day to the search index and update with the current value or create a
	 * new record when the index matches. The <b>id</b> of the record is changed to an string having the next fields:
	 * YEAR/MONTH/DAY:HOUR-TYPEID-SYSTEMID-OWNERID.
	 * Once one record is found instead storing on the database a single record per day/item/system/owner we store this same data for
	 * each hour until the date changes and then we stop processing entries until we have new current mining extractions.
	 */
	@TimeElapsed
	@LogEnterExit
	public List<MiningExtraction> downloadMiningExtractions() {
		// Get to the Network and download the data from the ESI api.
		NeoComLogger.info( "Starting download of credential {} mining extractions...", this.credential.getAccountId() + "" );
		return Stream.of( Objects.requireNonNull( this.esiDataProvider.getCharactersCharacterIdMining( credential ) ) )
				.map( ( extractionOk ) -> {
					final MiningExtraction extraction = new GetCharactersCharacterIdMiningToMiningExtractionConverter(
							this.locationCatalogService,
							this.credential.getAccountId(),
							LocalDate.now() )
							.convert( extractionOk );
					// Before mapping this record see if this is a delta. Search for an already existing record.
					final String recordId = extraction.getId();
					NeoComLogger.info( "Generating record identifier: {}.", recordId );
					final MiningExtractionEntity targetRecord = this.miningRepository.accessMiningExtractionFindById( recordId );
					if (null != targetRecord) {
						NeoComLogger.info( "Found previous record on database: {}.", targetRecord.getId() );
						// There was a previous record so calculate the delta for this hour.
						final long currentQty = targetRecord.getQuantity();
						try {
							this.miningRepository.persist( targetRecord.setQuantity( extractionOk.getQuantity() ) );
						} catch (final SQLException sqle) {
							NeoComLogger.error( sqle );
							throw new NeoComRuntimeException(
									ErrorInfoCatalog.MINING_EXTRACTION_PERSISTENCE_FAILED.getErrorMessage(
											targetRecord.getId(),
											sqle.getCause().toString() ) );
						}
						NeoComLogger.info( "Updating mining extraction: {} > Quantity: {}/{}",
								recordId + "", extractionOk.getQuantity() + "", currentQty + "" );
					}
					return extraction;
				} )
				.collect( Collectors.toList() );
	}

	// - B U I L D E R
	public static class Builder {
		private MiningExtractionDownloader onConstruction;

		public Builder() {
			this.onConstruction = new MiningExtractionDownloader();
		}

		public MiningExtractionDownloader build() {
			return this.onConstruction;
		}

		public MiningExtractionDownloader.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public MiningExtractionDownloader.Builder withEsiDataProvider( final ESIDataProvider esiDataProvider ) {
			Objects.requireNonNull( esiDataProvider );
			this.onConstruction.esiDataProvider = esiDataProvider;
			return this;
		}

		public MiningExtractionDownloader.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public MiningExtractionDownloader.Builder withMiningRepository( final MiningRepository miningRepository ) {
			Objects.requireNonNull( miningRepository );
			this.onConstruction.miningRepository = miningRepository;
			return this;
		}
	}
}
package org.dimensinfin.eveonline.neocom.support;

import java.util.List;

import com.annimon.stream.Stream;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class IntegrationReport {
	public static void generateMiningExtractionReport( final List<MiningExtraction> extractionList ) {
		Stream.of( extractionList )
				.forEach( extraction -> NeoComLogger.info( extraction.toString() ) );
	}
}
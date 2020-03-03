package org.dimensinfin.eveonline.neocom.exception;

import java.text.MessageFormat;

public enum ErrorInfoCatalog {
	FILESYSTEM_FAILURE_RETROFIT_CACHE_RELATED( "data.management.retrofit.cache.file.system.error",
			"File System exception error during retrofit cache configuration." ),
	RUNTIME_PROCESSING_ASSET( "data.management.assets.download.process.runtime.exception",
			"Runtime while processing asset; {0}" );

	public final String errorCode;
	public final String errorMessage;

	ErrorInfoCatalog( final String errorCode, final String errorMessage ) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage( final String... arguments ) {
		return MessageFormat.format( this.errorMessage, (Object) arguments );
	}

	public String getErrorMessage( final Exception exception, final String... arguments ) {
		return MessageFormat.format( this.errorMessage, (Object) arguments ) + exception;
	}
}

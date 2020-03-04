package org.dimensinfin.eveonline.neocom.backend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;

public class NeoComBackendServiceTest {
	@Test
	public void buildComplete() {
		final RetrofitFactory retrofitFactory = Mockito.mock( RetrofitFactory.class );
		final NeoComBackendService neoComBackendService = new NeoComBackendService.Builder()
				.withRetrofitFactory( retrofitFactory )
				.build();
		Assertions.assertNotNull( neoComBackendService );
	}

	@Test
	public void buildFailure() {
		final RetrofitFactory retrofitFactory = Mockito.mock( RetrofitFactory.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final NeoComBackendService neoComBackendService = new NeoComBackendService.Builder()
							.withRetrofitFactory( null )
							.build();
				},
				"Expected NeoComBackendService.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final NeoComBackendService neoComBackendService = new NeoComBackendService.Builder()
							.build();
				},
				"Expected NeoComBackendService.Builder() to throw null verification, but it didn't." );
	}

	@Test
	public void putCredential() {
		final RetrofitFactory retrofitFactory = Mockito.mock( RetrofitFactory.class );
		final NeoComBackendService neoComBackendService = new NeoComBackendService.Builder()
				.withRetrofitFactory( retrofitFactory )
				.build();
	}
}
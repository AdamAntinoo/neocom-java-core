package org.dimensinfin.eveonline.neocom.backend.rest.v1;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NeoComApiv1 {
	@PUT("/api/v1/neocom/credentials/{credentialId}")
	Call<CredentialStoreResponse> putCredential( @Header("Content-Type") String contentType,
	                                             @Path("credentialId") Integer credentialId,
	                                             @Body Credential credential);
}

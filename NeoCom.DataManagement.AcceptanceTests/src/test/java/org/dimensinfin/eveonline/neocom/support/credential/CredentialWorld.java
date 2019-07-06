package org.dimensinfin.eveonline.neocom.support.credential;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class CredentialWorld {
	private List<Map<String, String>> authorisationData;
	private Credential credentialUnderTest;
//	private Credential credentialRead;

	public List<Map<String, String>> getAuthorisationData() {
		Objects.requireNonNull(this.authorisationData);
		return this.authorisationData;
	}

	public void setAuthorisationData( final List<Map<String, String>> authorisationData ) {
		this.authorisationData = authorisationData;
	}

	public Credential getCredentialUnderTest() {
		return credentialUnderTest;
	}

	public void setCredentialUnderTest( final Credential credentialUnderTest ) {
		this.credentialUnderTest = credentialUnderTest;
	}

//	public Credential getCredentialRead() {
//		return credentialRead;
//	}

//	public void setCredentialRead( final Credential credentialRead ) {
//		this.credentialRead = credentialRead;
//	}

	public String getCredentialId() {
		return this.credentialUnderTest.getUniqueId();
	}
}

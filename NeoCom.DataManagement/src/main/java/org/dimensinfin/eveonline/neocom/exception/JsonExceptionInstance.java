//  PROJECT:     NeoCom.Microservices (NEOC.MS)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 / SpringBoot-1.3.5 / Angular 5.0
//  DESCRIPTION: This is the SpringBoot MicroServices module to run the backend services to complete the web
//               application based on Angular+SB. This is the web version for the NeoCom Android native
//               application. Most of the source code is common to both platforms and this module includes
//               the source for the specific functionality for the backend services.
package org.dimensinfin.eveonline.neocom.exception;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class JsonExceptionInstance {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
//	private String errorMessage = "-NO MESSAGE-";
	private Exception target = null;
	private String jsonClass = "Exception";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public JsonExceptionInstance( final String message ) {
		target = new NeoComRegisteredException(NEOE.NOT_CLASSIFIED, message);
	}
	public JsonExceptionInstance( final Exception exc ) {
		target = exc;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String toJson() {
		if (target instanceof NeoComRegisteredException) {
			return new StringBuffer()
					.append("[{").append('\n')
					.append(quote("jsonClass")).append(" : ").append(quote(((NeoComRegisteredException) target).getJsonClass())).append(",")
					.append(quote("code")).append(" : ").append(quote(((NeoComRegisteredException) target).getCode().name())).append(" ")
					.append(quote("message")).append(" : ").append(quote(target.getMessage())).append(" ")
					.append("}]")
					.toString();
		} else
			return new StringBuffer()
					.append("[{").append('\n')
					.append(quote("jsonClass")).append(" : ").append(quote(jsonClass)).append(",")
					.append(quote("message")).append(" : ").append(quote(target.getMessage())).append(" ")
					.append("}]")
					.toString();
	}

	private String quote( final String content ) {
		return String.format("\"%s\"", content);
	}

	public String getJsonClass() {
		return jsonClass;
	}

	@Override
	public String toString() {
		if (null != target)
			return new StringBuffer("JsonExceptionInstance[")
					.append("message:").append(target.getMessage()).append(" ")
					.append("]")
//				.append("->").append(super.toString())
					.toString();
		else return new StringBuffer("JsonExceptionInstance []").toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]

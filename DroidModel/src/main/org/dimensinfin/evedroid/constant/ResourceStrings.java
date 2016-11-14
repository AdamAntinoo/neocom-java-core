//  PROJECT:        DroidModel
//  AUTHORS:        Adam Antinoo - haddockgit@gmail.com
//  COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.evedroid.constant;

// - IMPORT SECTION .........................................................................................
import java.util.MissingResourceException;
import java.util.ResourceBundle;

// - CLASS IMPLEMENTATION ...................................................................................
public class ResourceStrings {
	private static final String					BUNDLE_NAME			= "org.dimensinfin.evedroid.constant.strings";	//$NON-NLS-1$

	private static final ResourceBundle	RESOURCE_BUNDLE	= ResourceBundle.getBundle(BUNDLE_NAME);

	private ResourceStrings() {
	}

	public static String getResource(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}

// - UNUSED CODE ............................................................................................

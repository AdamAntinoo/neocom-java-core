//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.config;

//- IMPORT SECTION .........................................................................................
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

// - CLASS IMPLEMENTATION ...................................................................................
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
	public static void main(final String[] args) throws Exception {
		writeConfigFile("ormlite_config.txt");
	}
}
// - UNUSED CODE ............................................................................................

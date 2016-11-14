//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.activity;

//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.IActivityCallback;
import org.dimensinfin.android.mvc.core.IDataSource;
import org.dimensinfin.evedroid.activity.core.DefaultPagerActivity;
import org.dimensinfin.evedroid.activity.core.SafeStopActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.fragment.FittingFragment;
import org.dimensinfin.evedroid.fragment.FittingsDataSource;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

//- CLASS IMPLEMENTATION ...................................................................................
public class FittingActivity extends DefaultPagerActivity implements IActivityCallback {
	// - S T A T I C - S E C T I O N ..........................................................................
	public static Logger	logger	= Logger.getLogger("FittingActivity");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public IDataSource getDataSource(final int fragmentID) {
		if (fragmentID == AppWideConstants.fragment.FRAGMENT_FITTINGS) return new FittingsDataSource();
		throw new RuntimeException("Fragment Identifier did not matched any DataSource.");
	}

	protected void onCreate(final Bundle savedInstanceState) {
		logger.info(">> FittingActivity.onCreate"); //$NON-NLS-1$
		super.onCreate(savedInstanceState);
		// Configure the activity pages.
		FittingFragment frag = new FittingFragment();
		frag.setIdentifier(AppWideConstants.fragment.FRAGMENT_FITTINGS);
		frag.setTitle("FITTINGS");
		frag.setDataSource(new FittingsDataSource());
		_pageAdapter.addPage(frag);
		logger.info("<< FittingActivity.onCreate"); //$NON-NLS-1$
	}

	/**
	 * For really unrecoverable or undefined exceptions the application should go to a safe spot. That spot is
	 * defined by the application so this is another abstract method.
	 * 
	 * @param exception
	 */
	protected void stopActivity(final Exception exception) {
		final Intent intent = new Intent(this, SafeStopActivity.class);
		// Pass the user message to the activity for display.
		intent.putExtra(AppWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
		startActivity(intent);
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * This is the standard page adapter that will return the right adapter for the page number selected. This
 * adapter is valid for Fitting pages.
 * 
 * @author Adam Antinoo
 */
final class FittingsPagerAdapter extends FragmentStatePagerAdapter {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger		= Logger.getLogger("FittingsPagerAdapter");

	// - F I E L D - S E C T I O N ............................................................................
	private int						pageCount	= 1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingsPagerAdapter(final FragmentManager fm) {
		super(fm);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public int getCount() {
		return pageCount;
	}

	@Override
	public Fragment getItem(final int position) {
		FittingFragment frag = null;
		switch (position) {
			case 0:
				frag = new FittingFragment();
				frag.setIdentifier(AppWideConstants.fragment.FRAGMENT_FITTINGS);
				break;
		}
		if (null == frag)
			return getItem(0);
		else
			return frag;
	}

	public void setNoPages(final int pages) {
		pageCount = pages;
	}
}
//- UNUSED CODE ............................................................................................

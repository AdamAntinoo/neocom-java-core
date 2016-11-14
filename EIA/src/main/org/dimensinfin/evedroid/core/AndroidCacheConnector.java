//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.core;

// - IMPORT SECTION .........................................................................................
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.core.SimpleDiskCache.BitmapEntry;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.service.PendingRequestEntry;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

// - CLASS IMPLEMENTATION ...................................................................................
public class AndroidCacheConnector implements ICache {
	private class DrawableCache extends Statisticscache {
		// - F I E L D - S E C T I O N
		// ............................................................................
		private SimpleDiskCache cacheDrawables = null;
		private volatile int loads = 0;
		private final CompressFormat mCompressFormat = CompressFormat.PNG;
		private final int mCompressQuality = 90;

		public DrawableCache() {
			final File cacheFolder = new File(AppConnector.getStorageConnector().getCacheStorage(),
					AppConnector.getResourceString(R.string.drawablecachefoldername));
			try {
				this.cacheDrawables = SimpleDiskCache.open(cacheFolder, 1, 100 * 1024 * 1024);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		// - M E T H O D - S E C T I O N
		// ..........................................................................
		public synchronized void add(final String key, final Drawable target) {
			logger.info("-- Storing Drawable instance [" + key + "] ");
			// cacheDrawables.put(key, target);
			writeDrawableToCache(key, target);
		}

		public synchronized Drawable getByURL(final String url) {
			final String hash = new Integer(Math.abs(new Integer(url.hashCode()))).toString();
			try {
				final BitmapEntry bit = this.cacheDrawables.getBitmap(hash);
				if (null == bit)
					return null;
				final Drawable draw = new BitmapDrawable(
						EVEDroidApp.getSingletonApp().getApplicationContext().getResources(), bit.getBitmap());
				return draw;
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		public void loads() {
			this.loads++;
		}

		@Override
		public String toString() {
			return super.toString();
		}

		private void writeDrawableToCache(final String urlname, final Drawable data) {
			logger.info(">> NewCacheStorage.writeDrawableToDisk");
			OutputStream out = null;
			try {
				// Create a valid hask key from the resource URL
				final String hash = new Integer(Math.abs(new Integer(urlname.hashCode()))).toString();
				if (data instanceof BitmapDrawable) {
					final Bitmap bit = ((BitmapDrawable) data).getBitmap();
					out = this.cacheDrawables.openStream(hash, null);
					bit.compress(this.mCompressFormat, this.mCompressQuality, out);
				}
				logger.info("<< NewCacheStorage.writeDrawableToDisk [true]"); //$NON-NLS-1$
				return;
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					out.flush();
					out.close();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logger.info("<< NewCacheStorage.writeDrawableToDisk [false]"); //$NON-NLS-1$
		}
	}

	private class DrawableDownloaderTask extends AsyncTask<String, Void, Drawable> {
		// - F I E L D - S E C T I O N
		// ............................................................................
		private ImageView targetImage = null;
		private Drawable source = null;

		// - M E T H O D - S E C T I O N
		// ..........................................................................
		public void setImageTarget(final ImageView target) {
			// TODO Auto-generated method stub
			this.targetImage = target;
		}

		@Override
		protected Drawable doInBackground(final String... reference) {
			InputStream is = null;
			URLConnection urlConn = null;
			try {
				urlConn = new URL(reference[0]).openConnection();
				is = urlConn.getInputStream();
				this.source = Drawable.createFromStream(is, "src");
				EVEDroidApp.getTheCacheConnector().addDrawableToCache(reference[0], this.source);
				return this.source;
			} catch (final Exception ex) {
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (final IOException e) {
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Drawable result) {
			// Invalidate the view to force a refresh.
			if (null != result)
				if (null != this.targetImage) {
					this.targetImage.setImageDrawable(this.source);
					this.targetImage.invalidate();
					super.onPostExecute(result);
				}
		}
	}

	/**
	 * Stores the cache data for all the items accessed. A put of another item
	 * into the cache will add its reference to both lists, the one accessed by
	 * ID and the one accessed by Name.<br>
	 * The class also keeps track of the access statistics counting the hits,
	 * misses and faults. For the later the class stores the references that
	 * where not found for later reporting.
	 */
	private class EveItemCache extends Statisticscache {

		// - F I E L D - S E C T I O N
		// ............................................................................
		// private volatile int access = 0;
		// private volatile int hit = 0;
		// private volatile int miss = 0;
		// private volatile int fault = 0;
		private final HashMap<Integer, EveItem> eveItemCachebyID = new HashMap<Integer, EveItem>();
		private final HashMap<String, EveItem> eveItemCachebyName = new HashMap<String, EveItem>();
		private final Vector<String> itemFaults = new Vector<String>();

		// - M E T H O D - S E C T I O N
		// ..........................................................................
		// public synchronized void access() {
		// access++;
		// hit++;
		// }

		public synchronized void add(final EveItem item) {
			final int id = item.getItemID();
			final String name = item.getName();
			logger.info("-- Storing EveItem instance [" + id + "] " + name);
			this.eveItemCachebyID.put(id, item);
			this.eveItemCachebyName.put(name, item);
		}

		// public synchronized void fault(final String reference) {
		// fault++;
		// hit--;
		// itemFaults.add(reference);
		// }

		public synchronized EveItem getByID(final int typeID) {
			return this.eveItemCachebyID.get(typeID);
		}

		public synchronized EveItem getByName(final String name) {
			if (null != name)
				return this.eveItemCachebyName.get(name);
			else
				return null;
		}

		//
		// public synchronized void miss() {
		// miss++;
		// hit--;
		// }

		@Override
		public String toString() {
			// StringBuffer buffer = new
			// StringBuffer("NewCacheStorage.EveItemCache [");
			// buffer.append("access=").append(access).append("
			// hits=").append(hit).append(" miss=").append(miss);
			// buffer.append(" faults=").append(fault);
			// if (fault > 0) buffer.append("\n").append("Fault Refs
			// [").append(itemFaults).append("]");
			// buffer.append(" ]");
			return super.toString();
		}
	}

	private abstract class Statisticscache {
		// - F I E L D - S E C T I O N
		// ............................................................................
		private volatile int access = 0;
		private volatile int hit = 0;
		private volatile int miss = 0;
		private volatile int fault = 0;
		private final Vector<String> itemFaults = new Vector<String>();

		// - M E T H O D - S E C T I O N
		// ..........................................................................
		public synchronized void access() {
			this.access++;
			this.hit++;
		}

		public synchronized void fault(final String reference) {
			this.fault++;
			this.hit--;
			this.itemFaults.add(reference);
		}

		public synchronized void miss() {
			this.miss++;
			this.hit--;
		}

		@Override
		public String toString() {
			final StringBuffer buffer = new StringBuffer("NewCacheStorage.Statisticscache [");
			buffer.append("access=").append(this.access).append(" hits=").append(this.hit).append(" miss=")
					.append(this.miss);
			buffer.append(" faults=").append(this.fault);
			if (this.fault > 0) {
				buffer.append("\n").append("Fault Refs [").append(this.itemFaults).append("]");
			}
			buffer.append(" ]");
			return buffer.toString();
		}
	}

	// - S T A T I C - S E C T I O N
	// ..........................................................................
	private static Logger logger = Logger.getLogger("AndroidCacheConnector");

	// - F I E L D - S E C T I O N
	// ............................................................................
	private Context _context = null;
	private final EveItemCache _eveItemCache = new EveItemCache();
	private DrawableCache _cacheDrawables = null;
	// private final HashMap<String, CacheEntry> _pendingDrawableDownloads = new
	// HashMap<String, CacheEntry>();
	private HashMap<PendingRequestEntry, Integer> _pendingRequests = new HashMap<PendingRequestEntry, Integer>();

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public AndroidCacheConnector(final Context app) {
		this._context = app;
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	/**
	 * Adds a new request to download character information. The information to
	 * download depends on the timing for the different blocks of data. Each
	 * block has a different refresh time. When a blocks finishes it may trigger
	 * the update of another block.
	 */
	public synchronized void addCharacterUpdateRequest(final long localizer) {
		Log.i("AndroidCacheConnector", ">> AndroidCacheConnector.addCharacterUpdateRequest");
		final PendingRequestEntry request = new PendingRequestEntry(localizer);
		request.reqClass = ERequestClass.CHARACTERUPDATE;
		final int priority = 20;
		request.setPriority(priority);

		// Check for duplicates before adding the new element.
		final String requestid = request.getIdentifier();
		for (final PendingRequestEntry entry : this._pendingRequests.keySet()) {
			final String entryid = entry.getIdentifier();
			if (entryid.equalsIgnoreCase(requestid))
				return;
		}
		this._pendingRequests.put(request, priority);
		Log.i("AndroidCacheConnector", "<< AndroidCacheConnector.addCharacterUpdateRequest");
	}

	@TargetApi(12)
	public void addDrawableToCache(final String key, final Drawable image) {
		getCache().add(key, image);
		getCache().loads();
	}

	/**
	 * Queues a new request to download Market Data for an Item. We only
	 * register the ID of the item because the side will not be used. On the
	 * download phase we will download both sides. Using the ID as key will
	 * avoid requesting the same item multiple times. <br>
	 * 
	 * @param localizer
	 *            identifier of the item related to the data to download.
	 */
	public synchronized void addMarketDataRequest(final long localizer) {
		// Log.i("AndroidCacheConnector", ">>
		// AndroidCacheConnector.addMarketDataRequest");
		final EveItem item = AppConnector.getDBConnector().searchItembyID(new Long(localizer).intValue());
		Log.i("AndroidCacheConnector",
				"-- AndroidCacheConnector.addMarketDataRequest. Posting market update for: " + item.getName());
		// Detect priority from the Category of the item. Download data from
		// Asteroids and Minerals first.
		final String category = item.getCategory();
		final String group = item.getGroupName();
		int priority = 1;
		if (category.equalsIgnoreCase("Asteroid")) {
			priority = 6;
		}
		if (category.equalsIgnoreCase("Material")) {
			priority = 5;
		}
		if (category.equalsIgnoreCase("Module")) {
			priority = 8;
		}
		if (group.equalsIgnoreCase("Datacores")) {
			priority = 3;
		}
		final PendingRequestEntry request = new PendingRequestEntry(localizer);
		request.setPriority(priority);

		// Check for duplicates before adding the new element.
		final String requestid = request.getIdentifier();
		for (final PendingRequestEntry entry : this._pendingRequests.keySet()) {
			final String entryid = entry.getIdentifier();
			if (entryid.equalsIgnoreCase(requestid))
				return;
		}
		this._pendingRequests.put(request, priority);
		// incrementMarketCounter();
		// Log.i("AndroidCacheConnector", "<<
		// AndroidCacheConnector.addMarketDataRequest");
	}

	public synchronized void clearPendingRequest(final String localizer) {
		for (final PendingRequestEntry entry : this._pendingRequests.keySet()) {
			final String entryid = entry.getIdentifier();
			if (entryid.equalsIgnoreCase(localizer)) {
				entry.state = ERequestState.COMPLETED;
				decrementMarketCounter();
			}
		}
	}

	/**
	 * Gets a drawable by its URL. Most of the Eve icons can be reached though
	 * an URL and also this is valid for the pilot avatar. The process checks if
	 * the image is available at the cache. If the image is not there then it
	 * will try to locate it on the cache filesystem. If not found there then it
	 * will open a request to get it form the internet location once the network
	 * is available.
	 * 
	 * @param urlString
	 *            the location of the resource. This is already developed by the
	 *            caller and it is treated as a black box name.
	 * @param target
	 *            the UI object where we have to write the drawable once we get
	 *            it to replace the dummy image that will be shown while we
	 *            retrieve the not cache image. This will be kept on a list
	 *            because there may be more that one pending call for the same
	 *            image resource.
	 * @return the cached image or a dummy is still not available.
	 */
	public synchronized Drawable getCacheDrawable(final String urlString, final ImageView target) {
		// Try to get a hit from the memory cache.
		Drawable hit = getCache().getByURL(urlString);
		if (null == hit) {
			synchronized (this) {
				hit = getCache().getByURL(urlString);
				if (null == hit) {
					try {
						// hit = readDrawableFromDisk(urlString);
						// if (null == hit) {
						// No luck. We have to download the data from the
						// network.
						postDrawableRequest(urlString, target);
						getCache().miss();
						// }
					} catch (final Exception rtex) {
						logger.info("E> Exception reading cached data. " + rtex.getMessage());
						getCache().fault(urlString);
					}
				}
			}
		}
		getCache().access();
		if (null == hit) {
			hit = EVEDroidApp.getSingletonApp().getApplicationContext().getResources()
					.getDrawable(R.drawable.defaultitemicon);
		}
		return hit;
	}

	public File getCacheStorage() {
		return null;
	}

	public synchronized Vector<PendingRequestEntry> getPendingRequests() {
		if (null == this._pendingRequests) {
			this._pendingRequests = new HashMap<PendingRequestEntry, Integer>();
		}
		// Clean up all completed requests.
		final HashMap<PendingRequestEntry, Integer> openRequests = new HashMap<PendingRequestEntry, Integer>();
		for (final PendingRequestEntry entry : this._pendingRequests.keySet())
			if (entry.state != ERequestState.COMPLETED) {
				openRequests.put(entry, this._pendingRequests.get(entry));
			}
		this._pendingRequests = openRequests;
		return new Vector<PendingRequestEntry>(this._pendingRequests.keySet());
	}

	public String getURLForItem(final int typeID) {
		final String iconUrl = "http://image.eveonline.com/Type/" + typeID + "_64.png";
		return iconUrl;
	}

	public String getURLForStation(final int typeID) {
		final String iconUrl = "http://image.eveonline.com/Render/" + typeID + "_64.png";
		return iconUrl;
	}

	// /**
	// * Searches inside the memory data structures for an already available
	// <code>EveItem</code> with a matching
	// * ID. If the object is found we return the single reference to the
	// persistent database information.<br>
	// * If the reference is not found we go to the database layer to search for
	// that object and then we keep a
	// * copy on memory.<br>
	// * Multiple access will require synchronization. The synch has to be
	// performed to the item level to allow
	// * other threads searching for different items to continue undisturbed.
	// Double locking will be implemented
	// * to avoid waiting threads to go again to database once the lock is
	// removed.<br>
	// * The locking will be set on the database access activity. Read
	// operations or update changes will not need
	// * to be synchronized.<br>
	// * There is a coupled method entry to search by Name instead of ID. The
	// cache data structures keep track of
	// * pointers from ID and Name to the same Item.
	// *
	// * @param typeID
	// * identifier of the item being searched.
	// * @return the persistent <code>EveItem</code> object with the id
	// requested from the
	// * <code>evedroid.db</code> or <code>null</code> if not found.
	// */
	// public EveItem searchItembyID(final int typeID) {
	// // Check if the item already on the cache.
	// EveItem hit = _eveItemCache.getByID(typeID);
	// if (null == hit) {
	// synchronized (this) {
	// hit = _eveItemCache.getByID(typeID);
	// if (null == hit) {
	// // Get for the data to the database.
	// try {
	// Dao<EveItem, String> dao = AppConnector.getDBConnector().getItemDAO();
	// hit = dao.queryForId(new Integer(typeID).toString());
	// _eveItemCache.add(hit);
	// _eveItemCache.miss();
	// } catch (final Exception ex) {
	// logger.warning("W> Item <" + typeID + "> not found.");
	// _eveItemCache.fault(new Integer(typeID).toString());
	// hit = new EveItem();
	// }
	// }
	// }
	// }
	// _eveItemCache.access();
	// return hit;
	// }

	// /**
	// * This method does the same of the coupled <code>searchItembuID</code>
	// but instead searching for the
	// * <code>EveItem</code> by it's ID it will perform the search by the Item
	// Name.
	// *
	// * @param name
	// * @return
	// */
	// public EveItem searchItembyName(final String name) {
	// // Check if the item already on the cache.
	// EveItem hit = _eveItemCache.getByName(name);
	// if (null == hit) {
	// synchronized (this) {
	// hit = _eveItemCache.getByName(name);
	// if (null == hit) {
	// // Get for the data to the database.
	// try {
	// Dao<EveItem, String> itemDao =
	// AppConnector.getDBConnector().getItemDAO();
	// QueryBuilder<EveItem, String> queryBuilder = itemDao.queryBuilder();
	// Where<EveItem, String> where = queryBuilder.where();
	// where.eq("name", name);
	// PreparedQuery<EveItem> preparedQuery = queryBuilder.prepare();
	// List<EveItem> itemList = itemDao.query(preparedQuery);
	// if (itemList.size() < 2) {
	// _eveItemCache.fault(name);
	// hit = null;
	// } else {
	// hit = itemList.get(0);
	// _eveItemCache.miss();
	// }
	// } catch (java.sql.SQLException sqle) {
	// sqle.printStackTrace();
	// logger.warning("W> Item <" + name + "> not found.");
	// _eveItemCache.fault(name);
	// hit = null;
	// }
	// }
	// }
	// }
	// return hit;
	// }

	private void decrementMarketCounter() {
		EVEDroidApp.marketCounter--;
		if (EVEDroidApp.marketCounter < 0) {
			EVEDroidApp.marketCounter = 0;
		}
		final Activity activity = EVEDroidApp.getAppStore().getActivity();
		if (null != activity) {
			activity.runOnUiThread(new Runnable() {
				public void run() {
					EVEDroidApp.updateProgressSpinner();
				}
			});
		}
	}

	private DrawableCache getCache() {
		if (null == this._cacheDrawables) {
			this._cacheDrawables = new DrawableCache();
		}
		return this._cacheDrawables;
	}

	// private void incrementMarketCounter() {
	// EVEDroidApp.marketCounter++;
	// Activity activity = EVEDroidApp.getAppContext().getActivity();
	// if (null != activity) {
	// activity.runOnUiThread(new Runnable() {
	// public void run() {
	// EVEDroidApp.updateProgressSpinner();
	// }
	// });
	// }
	// }

	/**
	 * Keeps track of the requests to download data that are pending and of all
	 * the images that are waiting to replace their content by the new
	 * downloaded content. The real download process is done on an asych task as
	 * the writing on disk of the downloaded data for later accesses.<br>
	 * The tasks will kept the request list updated with the downloaded data and
	 * theirs download states.
	 * 
	 * @param urlString
	 *            url of the resource to access.
	 * @param target
	 *            UI image that is waiting for this drawable to replace the
	 *            dummy we have set while we download the data.
	 */
	private void postDrawableRequest(final String urlString, final ImageView target) {
		// TODO Check if already on pending list.
		// CacheEntry request = _pendingDrawableDownloads.get(urlString);

		// Launch a background task to get the image.
		final DrawableDownloaderTask task = new DrawableDownloaderTask();
		task.setImageTarget(target);
		task.execute(urlString);
	}

}

//// - CLASS IMPLEMENTATION
//// ...................................................................................
// final class CacheEntry implements Serializable {
// // - S T A T I C - S E C T I O N
//// ..........................................................................
// private static final long serialVersionUID = -1265302408516975156L;
//// private static Logger logger = Logger.getLogger("CacheEntry");
//
// // - F I E L D - S E C T I O N
//// ............................................................................
//// private MarketDataSet data = null;
// private ECacheState state = ECacheState.EMPTY;
// private long timestamp = 0;
//
// // - C O N S T R U C T O R - S E C T I O N
//// ................................................................
//// /**
//// * When creating a new card fill it with a new Market Data information so we
//// can have the information that
//// * needs to be updated. There are two ways to create on request, an empty
//// one (this is the default) and one
//// * that already exists but that we like to be udpated.
//// *
//// * @param id
//// * item id related to the market data to be updated.
//// */
//// public CacheEntry(final int id) {
//// data = new MarketDataSet(id, ModelWideConstants.marketSide.SELLER);
//// }
//
//// public CacheEntry(final MarketDataSet data) {
//// this.data = data;
//// }
//
// // - M E T H O D - S E C T I O N
//// ..........................................................................
// public MarketDataSet getContent() {
// return data;
// }
//
// public Integer getLocalizer() {
// return new Integer(data.getItemID());
// }
//
// /**
// * Updates the real cache entry state from the elapsed time from the last
//// update end the refresh period set
// * to this element class. Also detects empty data that requires a prioritized
//// update.
// *
// * @return
// */
// public ECacheState getState() {
// if (state == ECacheState.UPDATED) {
// // Check if the state should be changed.
// boolean needsUpdate = AppConnector.checkExpiration(timestamp,
//// ModelWideConstants.MINUTES60);
// if (needsUpdate) setState(ECacheState.NEEDSUPDATE);
// }
// return state;
// }
//
// public void markRefresh() {
// setState(ECacheState.NEEDSUPDATE);
// }
//
// public void setState(final ECacheState newState) {
// state = newState;
// }
//
// @Override
// public String toString() {
// StringBuffer buffer = new StringBuffer("CacheEntry [");
// buffer.append("[").append(state).append("]").append(data);
// buffer.append(" ]");
// return buffer.toString();
// }
//
// public void update() {
// setState(ECacheState.COMPLETED);
// timestamp = GregorianCalendar.getInstance().getTimeInMillis();
// }
// }
//
// enum ECacheState {
// EMPTY, NEEDSUPDATE, UPDATED, ON_PROGRESS, ERROR, NOT_FOUND, COMPLETED
// }

// - UNUSED CODE
// ............................................................................................

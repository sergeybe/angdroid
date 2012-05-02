package com.scoreloop.client.android.ui.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Handler;

public class Cache<K, V> {

	private static final int	DEFAULT_HARD_CACHE_CAPACITY	= 100;

	class CacheEntry {
		private long		_lastAccess;
		private final long	_timeToLive;
		private final V		_value;

		CacheEntry(final V value, final long lastAccess, final long timeToLive) {
			_value = value;
			_lastAccess = lastAccess;
			_timeToLive = timeToLive;
		}

		long getLastAccess() {
			return _lastAccess;
		}

		void setLastAccess(final long lastAccess) {
			_lastAccess = lastAccess;
		}

		long getTimeToLive() {
			return _timeToLive;
		}

		V getValue() {
			return _value;
		}
	}

	private int												_hardCacheCapacity;
	private long											_minPurgeInterval;
	private HashMap<K, CacheEntry>							_hardCache;
	private ConcurrentHashMap<K, SoftReference<CacheEntry>>	_softCache;
	private Handler											_purgeHandler;
	private final Runnable									_purger	= new Runnable() {
																		@Override
																		public void run() {
																			purgeCache();
																		}
																	};

	public Cache() {
		this(DEFAULT_HARD_CACHE_CAPACITY);
	}

	/**
	 * @param hardCacheCapacity the capacity of the hard cache, default is 100
	 */
	public Cache(final int hardCacheCapacity) {
		_hardCacheCapacity = hardCacheCapacity;
		_minPurgeInterval = 0;
		_purgeHandler = new Handler();
		initHardCache();
		initSoftCache();
	}

	private void initHardCache() {
		_hardCache = new LinkedHashMap<K, CacheEntry>(_hardCacheCapacity / 2, 0.75f, true) {
			private static final long	serialVersionUID	= 1L;

			@Override
			protected boolean removeEldestEntry(final Map.Entry<K, CacheEntry> eldest) {
				if (size() > _hardCacheCapacity) {
					// Entries push-out of hard reference cache are transferred to soft reference cache
					_softCache.put(eldest.getKey(), new SoftReference<CacheEntry>(eldest.getValue()));
					return true;
				}
				return false;
			}
		};
	}

	private void initSoftCache() {
		_softCache = new ConcurrentHashMap<K, SoftReference<CacheEntry>>(_hardCacheCapacity / 2);
	}

	public void purgeCache() {
		final long now = System.currentTimeMillis();
		final Set<K> hardKeys = new HashSet<K>(_hardCache.keySet());
		_softCache.clear();
		for (final K k : hardKeys) {
			final CacheEntry e = _hardCache.get(k);
			if ((e.getTimeToLive() != -1) && ((e.getLastAccess() + e.getTimeToLive()) < now)) {
				synchronized (_hardCache) {
					_softCache.put(k, new SoftReference<CacheEntry>(e));
					_hardCache.remove(k);
				}
			}
		}
		resetPurgeTimer(_minPurgeInterval);
	}

	private void resetPurgeTimer(final long purgeInterval) {
		if (purgeInterval > 0) {
			if (purgeInterval < _minPurgeInterval) {
				_minPurgeInterval = purgeInterval; // new minimum interval found
			} else if (_minPurgeInterval == 0) {
				_minPurgeInterval = purgeInterval; // new maximum interval found
			}
		}
		_purgeHandler.removeCallbacks(_purger);
		if (_minPurgeInterval > 0) {
			_purgeHandler.postDelayed(_purger, _minPurgeInterval);
		}
	}

	public void put(final K key, final V value, final long timeToLive) {
		final long now = System.currentTimeMillis();
		synchronized (_hardCache) {
			_hardCache.put(key, new CacheEntry(value, now, timeToLive));
		}
		resetPurgeTimer(timeToLive);
	}

	public V get(final K key) {
		final CacheEntry cacheEntry = getCacheEntry(key);
		return cacheEntry != null ? cacheEntry.getValue() : null;
	}

	public CacheEntry getCacheEntry(final K key) {
		final long now = System.currentTimeMillis();
		// try hard cache
		synchronized (_hardCache) {
			final CacheEntry e = _hardCache.get(key);
			if (e != null) {
				e.setLastAccess(now);
				// pop
				_hardCache.remove(key);
				_hardCache.put(key, e);
				return e;
			}
		}

		// Then try the soft reference cache
		final SoftReference<CacheEntry> eReference = _softCache.get(key);
		if (eReference != null) {
			final CacheEntry e = eReference.get();
			if (e != null) {
				synchronized (_hardCache) {
					e.setLastAccess(now);
					_hardCache.put(key, e);
				}
				_softCache.remove(e);
				return e;
			}
			// already GC'ed, so remove the weak ref
			_softCache.remove(eReference);
		}
		return null;
	}

}

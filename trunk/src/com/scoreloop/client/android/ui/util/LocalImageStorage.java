package com.scoreloop.client.android.ui.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class LocalImageStorage {

	private static LocalImageStorage	instance;
	private static final int			ONE_DAY	= 1000 * 60 * 60 * 24;

	public static LocalImageStorage get() {
		if (instance == null) {
			instance = new LocalImageStorage();
		}
		return instance;
	}

	private boolean isStorageWritable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	private boolean isStorageReadable() {
		return isStorageWritable() || Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
	}

	private File getCacheDir(Context context) {
		File cacheDir = null;
		File storageDir = Environment.getExternalStorageDirectory();
		if (storageDir != null) {
			File tmp = new File(storageDir, "/Android/data/" + context.getPackageName() + "/cache/scoreloop");
			if ((tmp.exists() && tmp.isDirectory()) || tmp.mkdirs()) {
				cacheDir = tmp;
			}
		}
		return cacheDir;
	}

	public void purge(Context context, long timeToLive) {
		if (isStorageWritable()) {
			File cacheDir = getCacheDir(context);
			if (cacheDir != null) {
				File[] files = cacheDir.listFiles();
				for (File file : files) {
					if (file.isFile() && !isValid(timeToLive, file)) {
						deleteQuietly(file);
					}
				}
			}
		}
	}

	/**
	 * currently deleting all files which are older than 7 days
	 * purge is triggered just once per 24h
	 * @param context
	 */
	public void tryPurge(final Context context) {
		SharedPreferences pref = context.getSharedPreferences("localStorage", 0);
		String lastPurgeKey = "lastPurge";
		long lastPurge = pref.getLong(lastPurgeKey, -1);
		if (Math.abs(System.currentTimeMillis() - lastPurge) > ONE_DAY) {
			pref.edit().putLong(lastPurgeKey, System.currentTimeMillis()).commit();
			purge(context, 7 * ONE_DAY);
		}
	}

	private static void deleteQuietly(File file) {
		try {
			file.delete();
		} catch (Exception e) {
			// to nothing
		}
	}

	private File getCacheFile(Context context, String url) {
		File cacheFile = null;
		File cacheDir = getCacheDir(context);
		if (cacheDir != null) {
			String fileName = Base64.encodeBytes(url.getBytes());
			cacheFile = new File(cacheDir, fileName);
		}
		return cacheFile;
	}

	public ImageDownloader.BitmapResult getBitmap(Context context, String url, long timeToLive) {
		if (isStorageReadable()) {
			File cacheFile = getCacheFile(context, url);
			if (cacheFile != null && cacheFile.exists() && cacheFile.canRead()) {
				boolean valid = isValid(timeToLive, cacheFile);
				if (valid) {
					if (cacheFile.length() == 0) {
						return ImageDownloader.BitmapResult.createNotFound();
					} else {
						return new ImageDownloader.BitmapResult(BitmapFactory.decodeFile(cacheFile.getAbsolutePath()));
					}
				} else {
					deleteQuietly(cacheFile);
				}
			}
		}
		return null;
	}

	private boolean isValid(long timeToLive, File cacheFile) {
		long lastModified = cacheFile.lastModified();
		final long lived = System.currentTimeMillis() - lastModified;
		return timeToLive == -1 || lived <= timeToLive;
	}

	public boolean putBitmap(Context context, String url, Bitmap bitmap) {
		return putBitmap(context, url, new ImageDownloader.BitmapResult(bitmap));
	}

	public boolean putBitmap(Context context, String url, ImageDownloader.BitmapResult bitmapResult) {
		if (isStorageWritable()) {
			File cacheFile = getCacheFile(context, url);
			try {
				if (bitmapResult.isNotFound()) {
					cacheFile.createNewFile();
					return true;
				} else {
					FileOutputStream os = new FileOutputStream(cacheFile);
					bitmapResult.getBitmap().compress(Bitmap.CompressFormat.PNG, 90, os);
					os.close();
					return true;
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return false;
	}

	public File putStream(Context context, String url, InputStream in) {
		if (isStorageWritable()) {
			File file = getCacheFile(context, url);

			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);

				// this is storage overwritten on each iteration with bytes
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];

				// we need to know how may bytes were read to write them to the byteBuffer
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				return file;
			} catch (Exception e) {
				// ignore
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

}

package org.angdroid.angband;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;
import android.os.Environment;

public class Installer {

	public Installer() {}

	public boolean needsInstall() {
		// validate sdcard here
		String check = Environment.getExternalStorageState();
		Log.v("Angband", "media check:" + check);
		if (check.compareTo(Environment.MEDIA_MOUNTED) != 0) {
			NativeWrapper.installResult = 1;
			return true;
		}

		return (Preferences.getInstalledVersion().compareTo(Preferences.getVersion()) != 0);
	}

	public void install() {
		if (NativeWrapper.installResult > 0) return; // media error

		boolean result = true;;
		for(int i = 0; i < Preferences.getInstalledPlugins().length; i++) {
			if (!extractPluginResources(Preferences.getInstalledPlugins()[i]))
				result = false;
		}
		if (result) {
			Preferences.setInstalledVersion(Preferences.getVersion());
			NativeWrapper.installResult = 0;
		}
		else
			NativeWrapper.installResult = 2;
	}

	private boolean extractPluginResources(int plugin) {
		boolean result = true;
		try {
			File f = new File(Preferences.getAngbandFilesDirectory(plugin));
			f.mkdirs();
			String abs_path = f.getAbsolutePath();

			ZipInputStream zis = Preferences.getPluginZip(plugin);
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
				String ze_name = ze.getName();
				Log.v("Angband", "extracting " + ze_name);

				String filename = abs_path + "/" + ze_name;
				File myfile = new File(filename);

				if (ze.isDirectory()) {
					myfile.mkdirs();
					continue;
				}

				byte contents[] = new byte[(int) ze.getSize()];

				FileOutputStream fos = new FileOutputStream(myfile);
				int remaining = (int) ze.getSize();

				int totalRead = 0;

				while (remaining > 0) {
					int readlen = zis.read(contents, 0, remaining);
					fos.write(contents, 0, readlen);
					totalRead += readlen;
					remaining -= readlen;
				}

				fos.close();
				
				// perform a basic length validation
				myfile = new File(filename);
				if (myfile.length() != totalRead) {
					throw new IllegalStateException();					
				}
				
				zis.closeEntry();
			}
			zis.close();
		} catch (Exception e) {
			result = false;
			Log.v("Angband", "error extracting files: " + e);
		}
		return result;
	}

	/*
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		//Log.d("Angband", "delete old file: "+dir.getAbsolutePath());
		return dir.delete();
	}
	*/
}

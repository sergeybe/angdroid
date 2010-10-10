package org.angdroid.angband;

import android.util.Log;

public class Profile {

	protected int id = 0;
	protected String name = "";
    protected String saveFile = "";
	protected boolean autoBorg = false;
    protected int plugin = 0;
	protected static String dl = "~";

	public Profile(int id, String name, String saveFile, boolean autoBorg, int plugin) {
		this.id = id;
		this.name = name;
		this.saveFile = saveFile;
		this.autoBorg = autoBorg;
		this.plugin = plugin;
	}

	public Profile() {}

	public String toString() {
		return name;
	}

	public int getId() {
		return id;
	}
	public void setId(int value) {
		id = value;
	}

	public String getName() {
		return name;
	}
	public void setName(String value) {
		name = value;
	}

	public String getSaveFile() {
		return saveFile;
	}
	public void setSaveFile(String value) {
		saveFile = value;
	}

	public boolean getAutoBorg() {
		return autoBorg;
	}
	public void setAutoBorg(boolean value) {
		autoBorg = value;
	}

	public int getPlugin() {
		return plugin;
	}
	public void setPlugin(int value) {
		plugin = value;
	}

	public String serialize() {
		return id+dl+name+dl+saveFile+dl+autoBorg+dl+plugin;
	}
	public static Profile deserialize(String value) {
		String[] tk = value.split(dl);
		Profile p = new Profile();
		try {
			if (tk.length>0) p.id = Integer.parseInt(tk[0]);
			if (tk.length>1) p.name = tk[1];
			if (tk.length>2) p.saveFile = tk[2];
			if (tk.length>3) p.autoBorg = Boolean.parseBoolean(tk[3]);
			if (tk.length>4) p.plugin = Integer.parseInt(tk[4]);
		} catch (Exception ex) {}
		return p;
	}
}

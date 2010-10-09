package org.angdroid.angband;

import android.util.Log;

public class Profile {

	protected int id = 0;
	protected String name = "";
    protected String saveFile = "";
	protected boolean autoBorg = false;
	protected static String dl = "~";

	public Profile(int id, String name, String saveFile, boolean autoBorg) {
		this.id = id;
		this.name = name;
		this.saveFile = saveFile;
		this.autoBorg = autoBorg;
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

	public String serialize() {
		return id+dl+name+dl+saveFile+dl+autoBorg;
	}
	public static Profile deserialize(String value) {
		String[] tk = value.split(dl);
		if (tk.length>3) 
			return new Profile (
				   	Integer.parseInt(tk[0]),
					tk[1],
					tk[2],
					Boolean.parseBoolean(tk[3])
			       );
		else
			return new Profile();
	}
}

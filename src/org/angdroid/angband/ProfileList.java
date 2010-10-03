package org.angdroid.angband;

import java.util.ArrayList;
import java.lang.StringBuffer;

public class ProfileList extends ArrayList<Profile> {

	protected static String dl = "|";

	public ProfileList(){}

	public static ProfileList deserialize(String value) {
		String[] tk = value.split(dl);
		ProfileList pl = new ProfileList();
		for(int i=0; i < tk.length; i++)
			pl.add(Profile.deserialize(tk[i]));
		return pl;
	}

	public String serialize() {
		StringBuffer s = new StringBuffer();
		for(int i=0; i < this.size(); i++) {
		    if (s.length() > 0) s.append(dl);
		    s.append(this.get(i).serialize());
		}
		return s.toString();
	}
}



package org.angdroid.angband;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;

public class ProfilesActivity extends Activity{

	protected static final int CONTEXTMENU_DELETEITEM = 0;
	protected static final int CONTEXTMENU_EDITITEM = 1;
	protected static final int CONTEXTMENU_ADDITEM = 2;

	protected ListView proList;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.profile);

		this.proList = (ListView) this.findViewById(R.id.list_profiles);
		initListView();
	}

	private void refreshProListItems() {
		//	  proList.setAdapter(new IconicAdapter(this));
		proList.setAdapter(new ArrayAdapter<Profile>(
						   this,
						   android.R.layout.simple_list_item_1, Preferences.getProfiles())
						   );
	}

	private void initListView() {

		/* Loads the items to the ListView. */
		refreshProListItems();

		/* Add Context-Menu listener to the ListView. */
		proList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
													ContextMenuInfo menuInfo) {
					menu.setHeaderTitle("ContextMenu");
					menu.add(0, CONTEXTMENU_EDITITEM, 0, "Edit"); 
					//menu.add(0, CONTEXTMENU_ADDITEM, 0, "New Profile"); 
					menu.add(0, CONTEXTMENU_DELETEITEM, 0, "Delete"); 
				}
			});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = new MenuInflater(getApplication());
		inflater.inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent;
		switch (item.getNumericShortcut()) {
		case '1':
			intent = new Intent(this, ProfileAddActivity.class);
			startActivity(intent);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem aItem) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();
		Profile pro = (Profile) proList.getAdapter().getItem(menuInfo.position);

		switch (aItem.getItemId()) {
		case CONTEXTMENU_DELETEITEM:
			Preferences.getProfiles().remove(pro);
			refreshProListItems();
			return true; 
		case CONTEXTMENU_EDITITEM:
			Intent intent = new Intent(this, ProfileAddActivity.class);
			startActivity(intent);
			return true; 
		//case CONTEXTMENU_ADDITEM:
			//Intent intent = new Intent(this, ProfileAddActivity.class);
			//startActivity(intent);
			//return true; 
		}
		return false;
	}

	/* experimental list items with icons
	public class IconicAdapter extends ArrayAdapter { 
		Activity context; 
 
		IconicAdapter(Activity context) { 
			super(context, R.layout.profile_row, profiles); 
 
			this.context=context; 
		} 
 
		public View getView(int position, View convertView, ViewGroup parent) { 
			LayoutInflater inflater = LayoutInflater.from(context);
			View row=inflater.inflate(R.layout.profile_row, null); 
			TextView label=(TextView)row.findViewById(R.id.label); 
 
			label.setText(profiles.get(position).name); 
 
			if (position == 0) { 
				ImageView icon=(ImageView)row.findViewById(R.id.icon); 
 
				icon.setImageResource(R.drawable.menu_add); 
			}	   
 
			return(row); 
		} 
	}
	*/

}

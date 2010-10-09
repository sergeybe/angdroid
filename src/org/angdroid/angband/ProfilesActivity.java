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
import android.widget.AdapterView.OnItemClickListener;


public class ProfilesActivity extends Activity{

	protected static final int CONTEXTMENU_DELETEITEM = 0;
	protected static final int CONTEXTMENU_EDITITEM = 1;
	protected static final int CONTEXTMENU_ADDITEM = 2;

	protected ListView proList;
	protected ProfileList profiles;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.profile);

		this.proList = (ListView) this.findViewById(R.id.list_profiles);
		this.proList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		initListView();
	}

	private void refreshProListItems() {
		profiles = Preferences.getProfiles();

		proList.setAdapter (
			new ProfileAdapter(
				this, 
				profiles, 
				profiles.indexOf(Preferences.getActiveProfile())
			)
		);
	}

	private void initListView() {

		refreshProListItems();

		proList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
													ContextMenuInfo menuInfo) {
					menu.setHeaderTitle("ContextMenu");
					menu.add(0, CONTEXTMENU_EDITITEM, 0, "Edit"); 
					menu.add(0, CONTEXTMENU_DELETEITEM, 0, "Delete"); 
				}
			});

        proList.setOnItemClickListener(new OnItemClickListener() 
        {
            public void onItemClick(AdapterView parent, 
            View v, int position, long id) 
            {                
				Profile pro = (Profile) proList.getAdapter().getItem(position);
				Preferences.setActiveProfile(pro);
				finish();
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
			if (profiles.size()>1) {
				profiles.remove(pro);
				Preferences.saveProfiles();
				refreshProListItems();
			}
			else
				Preferences.alert(this, "Not Allowed", "You cannot delete the last profile");
			return true; 
		case CONTEXTMENU_EDITITEM:
			Intent intent = new Intent(this, ProfileAddActivity.class);
			intent.putExtra("profile", pro.id);
			startActivity(intent);
			return true; 
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshProListItems();
	}

	public class ProfileAdapter extends ArrayAdapter<Profile> { 
		Activity context; 
		ProfileList profiles;
		int activeIndex;

		ProfileAdapter(Activity context, ProfileList profiles, int activeIndex) { 
			super(context, R.layout.profilerow, profiles); 
 
			this.profiles = profiles;
			this.activeIndex = activeIndex;
			this.context=context; 
		} 

		public View getView(int position, View convertView, ViewGroup parent) { 

			LayoutInflater inflater = LayoutInflater.from(context);
            View row=inflater.inflate(R.layout.profilerow, parent, false); 

            TextView label=(TextView)row.findViewById(R.id.label); 
			label.setText(profiles.get(position).toString()); 

			if (position == activeIndex) {
				ImageView icon=(ImageView)row.findViewById(R.id.icon); 
				icon.setImageResource(R.drawable.btn_radio_on); 
			}
			return row;
		}    
	}
}

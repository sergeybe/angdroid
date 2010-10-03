package org.angdroid.angband;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.util.Log;

public class ProfileAddActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileadd);

        populateWidgets();

        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
           public void onClick(View arg0) {
        	   persistWidgetData();
	           setResult(RESULT_OK);
	           finish();
           }
        });

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
 	           setResult(RESULT_CANCELED);
 	           finish();
            }
         });
    }

    private void populateWidgets(){
    	EditText editSaveFile = (EditText) findViewById(R.id.editSaveFile);
    	editSaveFile.setText("PLAYER2");
    }

    private void persistWidgetData(){
    }
}
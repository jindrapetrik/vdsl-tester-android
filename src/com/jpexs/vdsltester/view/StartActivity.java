package com.jpexs.vdsltester.view;

import com.jpexs.vdsltester.R;
import com.jpexs.vdsltester.R.layout;
import com.jpexs.vdsltester.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		if ((savedInstanceState == null) || (savedInstanceState.isEmpty())) {
			Intent myIntent = new Intent(this, ConnectionSettingsActivity.class);
			startActivityForResult(myIntent, 0);
		}
	}

	@Override
	protected void onResume() {
		System.exit(0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

}

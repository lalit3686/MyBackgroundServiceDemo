package com.example.mybackgroundservicedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent service = new Intent(this, BackgroundService.class);
		service.putExtra(MyApplication.PERIOD_PARAM, MyApplication.DEFAULT_IDLE_PERIOD);
		startService(service);
	}
	
	@Override
	public void onBackPressed() {
	}
}

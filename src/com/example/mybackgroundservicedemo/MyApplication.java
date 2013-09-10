package com.example.mybackgroundservicedemo;

import android.app.Application;

public class MyApplication extends Application{

	public static final int DEFAULT_IDLE_PERIOD = 60000;
	public static final int APP_CHECKER_INTERVAL = 500;
	private long idleTime = 0;
	public static String PERIOD_PARAM = "PERIOD_PARAM";
	
	public long getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(long idleTime) {
		this.idleTime = idleTime;
	}
}

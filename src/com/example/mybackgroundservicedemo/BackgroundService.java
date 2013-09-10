package com.example.mybackgroundservicedemo;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * This Service works as a Background Service which keeps checking that our Application is always in foreground,
 * If any other Application is launched then our Application is moved ahead(foreground) of that Application.
 * 
 * @author plalit
 *
 */

public class BackgroundService extends Service {

	private int IdlePeriod = 0;
	private Handler handler = new Handler();
	private static String currentActivityName = "";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * return START_STICKY to start the Service automatically when Application is killed by OS due to 
	 * lack of resource. And service will be re-started again by OS when resources are available/free.
	 * 
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(intent != null){
			IdlePeriod = intent.getIntExtra(MyApplication.PERIOD_PARAM, MyApplication.DEFAULT_IDLE_PERIOD);
		}
		else{
			// if intent is null then keep default idle period 
			IdlePeriod = MyApplication.DEFAULT_IDLE_PERIOD;
		}
		
		// to continue Runnable running.
		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, MyApplication.APP_CHECKER_INTERVAL);
		
		return START_STICKY;
	}
	
	/**
	 * Runnable that runs in background checking that the Application is always running if foreground.
	 */
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			try {
				((MyApplication)getApplicationContext()).setIdleTime(((MyApplication)getApplicationContext()).getIdleTime() + 500);
				long currentIdle = ((MyApplication)getApplicationContext()).getIdleTime();
				
				fetchRecentAppList();

				Log.e("time", currentIdle+"");
				
				// check if currentIdle period is greater than or equal to constant IdlePeriod then redirect user to MainActivity. 
				if(currentIdle >= IdlePeriod){
					// navigate to MainActivity.
					
					if(currentActivityName.equalsIgnoreCase(MainActivity.class.getCanonicalName())){ 
						// if its already on MainActivity then do nothing just reset the Idle time to 0
						if(((MyApplication)getApplicationContext()).getIdleTime() >= MyApplication.DEFAULT_IDLE_PERIOD){
							((MyApplication)getApplicationContext()).setIdleTime(0);
						}
	        		}
	        		else{
	        			
	        			// set the current Activity as MainActivity before starting it.
	        			currentActivityName = MainActivity.class.getCanonicalName();
	        			// set the current Activity as MainActivity before starting it.
	        			
	        			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
	        		}
				}
				else{
					// do nothing.
				}
				
				// to continue Runnable running.
				handler.removeCallbacks(runnable);
				handler.postDelayed(runnable, MyApplication.APP_CHECKER_INTERVAL);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * 
	 * Get current running application.
	 * 
	 * IF condition is for checking that our App runs at the top(Home Pressed & Settings)
	 * ELSE condition is for checking if App is launched from RECENT Apps
	 * 
	 **/
	private void fetchRecentAppList() {
	ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
    for (int i=0; i<recentTasks.size();i++) {
    	
    	currentActivityName = recentTasks.get(0).topActivity.getClassName();
    	
    	if (i == 1 && recentTasks.get(i).baseActivity.toShortString().indexOf(getPackageName()) > -1) {
    		// Our Application has gone in background, so restart service so that the MainActivity is launched.
    		
    		((MyApplication)getApplicationContext()).setIdleTime(0);
    		
    		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
    		break;
        }
    	else{
    		// skipping the printer activity or our ContentcardApp.
        	if(recentTasks.get(0).baseActivity.getPackageName().equalsIgnoreCase(getPackageName()) || recentTasks.get(0).topActivity.toShortString().equalsIgnoreCase(currentActivityName)){
        		// do nothing as current running app is ContentcardApp or Printer App
        	}
        	else{
        		// App launched from Recent Apps
        		// Our Application has gone in background, so restart service so that the HomeActivity is launched.
        		
        		((MyApplication)getApplicationContext()).setIdleTime(0);
        		
        		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    			break;
        	}
    	}
    }
}
	
	
	/**
	 * 
	 * Stop the Runnable when Service is destroyed.
	 * 
	 **/
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(runnable);
		((MyApplication)getApplicationContext()).setIdleTime(0);
	}
}
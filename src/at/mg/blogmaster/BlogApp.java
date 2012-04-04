<<<<<<< HEAD
package at.mg.blogmaster;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import at.mg.blogmaster.common.Log;
import at.mg.blogmaster.db.DataAccess;
import at.mg.blogmaster.service.BlogService;

@ReportsCrashes(formKey = "dGZXRXRVamlORjJ0UXpjOVREYmtnUUE6MQ")
public class BlogApp extends Application {

	private static DataAccess da;
	private static Context context;
	public static BlogApp blogApp;

	@Override
	public void onCreate() {
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
		super.onCreate();
		context = getApplicationContext();
		blogApp = this;

		manageAlarm();
	}

	public static Context getAppContext() {
		return context;
	}

	public static DataAccess getDA() {
		if (da == null) {
			da = new DataAccess(context);
		}
		if (!da.isOpen()) {
			da.open();
		}

		return da;
	}

	/**
	 * manages the alarm, starts/stops it, set the correct interval
	 */
	public void manageAlarm() {

		try {

			Context context = getApplicationContext();
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);

			boolean enableAlarm = prefs.getBoolean("BM_PREF_NOT", true);

			String interval = prefs.getString("BM_PREF_NOT_INTERVAL", "15");

			long lInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

			if (interval.equals("15")) {
				lInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
			} else if (interval.equals("30")) {
				lInterval = AlarmManager.INTERVAL_HALF_HOUR;
			} else if (interval.equals("60")) {
				lInterval = AlarmManager.INTERVAL_HOUR;
			} else if (interval.equals("720")) {
				lInterval = AlarmManager.INTERVAL_HALF_DAY;
			} else if (interval.equals("1440")) {
				lInterval = AlarmManager.INTERVAL_DAY;
			}

			Log.i("manageAlarm " + enableAlarm + " " + lInterval);

			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

			PendingIntent pi = PendingIntent.getService(this, 0, new Intent(
					this, BlogService.class), 0);

			// first cancel all pending intent, then set the new one
			am.cancel(pi);

			if (enableAlarm) {
				am.setInexactRepeating(AlarmManager.RTC, 0, lInterval, pi);
			}
		} catch (Exception e) {
			Log.e("manageAlarm", e);
		}
	}

}
=======
package at.mg.blogmaster;

import android.app.Application;
import android.content.Context;
import at.mg.blogmaster.db.DataAccess;

public class BlogApp extends Application {

	private static DataAccess da;
	private static Context context;
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
	}
	
	public static DataAccess getDA(){
		if(da != null){
			da = new DataAccess(context);
		}
		return da;
	}
}
>>>>>>> 772787fb2d117805e128f3309073c6c5bcba3b65

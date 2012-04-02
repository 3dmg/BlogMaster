package at.mg.blogmaster.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import at.mg.blogmaster.BlogApp;
import at.mg.blogmaster.R;
import at.mg.blogmaster.activities.FeedList;
import at.mg.blogmaster.activities.MobileView;
import at.mg.blogmaster.activities.PostDetail;
import at.mg.blogmaster.common.Configuration;
import at.mg.blogmaster.common.Constants;
import at.mg.blogmaster.common.Log;
import at.mg.blogmaster.parser.BlogPost;

public class NotificationHandler extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		try {

			String action = intent.getAction();

			if (action != null && action.equals(Constants.BC_DELETENOT)) {
				Log.i("clear notifications");
				// user clicked on the clean notification button in the android
				// statusbar
				BlogApp.getDA().markAllAsRead();
				return;
			}

			if (action != null
					&& action.equals("android.intent.action.BOOT_COMPLETED")) {
				BlogApp.blogApp.manageAlarm();
				return;
			}

			BlogPost posts[] = BlogApp.getDA().getUnreadPosts();

			if (posts != null && posts.length > 0) {
				Log.i("unread posts: " + (posts != null ? posts.length : "00"));
				NotificationManager nm = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);

				String notText = posts.length > 1 ? posts.length
						+ " new blog posts " : posts[0].title;

				Notification not = new Notification(R.drawable.notification,
						notText, System.currentTimeMillis());

				Intent listIntent = null;

				if (Configuration.LIGHT_VERSION) {
					if (posts.length > 1) {
						listIntent = new Intent(BlogApp.getAppContext(),
								MobileView.class);
						not.number = posts.length;
					} else {
						listIntent = new Intent(BlogApp.getAppContext(),
								MobileView.class);
						listIntent.putExtra(Constants.EXTRA_CALLURL,
								posts[0].link);
					}
				} else {
					if (posts.length > 1) {
						listIntent = new Intent(BlogApp.getAppContext(),
								FeedList.class);
						not.number = posts.length;
					} else {
						listIntent = new Intent(BlogApp.getAppContext(),
								PostDetail.class);
						listIntent.putExtra(Constants.EXTRA_POSTID,
								posts[0].localID);
					}
				}

				// to make the intent unique
				listIntent.setAction("" + System.currentTimeMillis());

				listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent pi = PendingIntent.getActivity(context, 0,
						listIntent, 0);
				not.contentIntent = pi;

				Intent cleari = new Intent(Constants.BC_DELETENOT);
				not.deleteIntent = PendingIntent.getBroadcast(context, 0,
						cleari, 0);

				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(context);

				boolean doVibrate = prefs.getBoolean("BM_PREF_NOT_VIBRATE",
						true);
				boolean playSound = prefs.getBoolean("BM_PREF_NOT_SOUND", true);

				Log.i("vibrate: " + doVibrate + " playsound: " + playSound);

				not.flags = not.flags | Notification.FLAG_AUTO_CANCEL;
				not.ledARGB = Color.GREEN;
				not.ledOffMS = 1000;
				not.ledOnMS = 1000;
				not.flags = not.flags | Notification.FLAG_SHOW_LIGHTS;
				not.flags = not.flags | Notification.FLAG_AUTO_CANCEL;

				not.defaults = not.defaults
						| (playSound ? Notification.DEFAULT_SOUND : 0);
				not.defaults = not.defaults
						| (doVibrate ? Notification.DEFAULT_VIBRATE : 0);

				not.setLatestEventInfo(BlogApp.getAppContext(), "mobiFlip.de",
						notText, pi);

				nm.notify(0, not);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

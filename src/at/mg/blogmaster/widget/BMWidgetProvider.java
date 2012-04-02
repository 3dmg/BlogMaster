package at.mg.blogmaster.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.RemoteViews;
import at.mg.blogmaster.BlogApp;
import at.mg.blogmaster.R;
import at.mg.blogmaster.activities.MobileView;
import at.mg.blogmaster.common.Constants;
import at.mg.blogmaster.common.Log;
import at.mg.blogmaster.parser.BlogPost;

public class BMWidgetProvider extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.i("widget receives intent");
		if (intent.getAction().equals(Constants.BC_WIDGET_UPD)) {
			AppWidgetManager awm = AppWidgetManager.getInstance(context);
			ComponentName thisWidget = new ComponentName(context,
					BMWidgetProvider.class);
			onUpdate(context, awm, awm.getAppWidgetIds(thisWidget));
		}
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(
				Constants.BC_WIDGET_UPD), 0);

		// first cancel all pending intent, then set the new one
		am.cancel(pi);

		am.setInexactRepeating(AlarmManager.RTC, 0, 20000, pi);

	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(
				Constants.BC_WIDGET_UPD), 0);

		// first cancel all pending intent, then set the new one
		am.cancel(pi);

	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		try {
			Log.i("updateWidget");

			String title = "loading", date = "loading";
			PendingIntent pendingIntent2 = null;

			SharedPreferences sp = context.getSharedPreferences("WIDGET", 0);
			int index = sp.getInt("index", 0);
			BlogPost[] posts = BlogApp.getDA().getBlogPosts();

			if (posts != null && posts.length > 0) {
				if (index >= posts.length) {
					index = 0;
				}
				BlogPost post = posts[index];

				title = post.title;
				date = post.getDate();

				Intent intent2 = new Intent(context, MobileView.class);
				intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent2.putExtra(Constants.EXTRA_CALLURL, post.link);
				// to make the intent unique
				intent2.setAction("" + System.currentTimeMillis());
				pendingIntent2 = PendingIntent.getActivity(context, 0, intent2,
						0);

				index++;
				Editor ed = sp.edit();
				ed.putInt("index", index);
				ed.commit();
			}

			final int N = appWidgetIds.length;

			// Perform this loop procedure for each App Widget that belongs to
			// this
			// provider
			for (int i = 0; i < N; i++) {
				int appWidgetId = appWidgetIds[i];

				// Get the layout for the App Widget and attach an on-click
				// listener
				// to the button
				RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.small_widget);

				Intent intent = new Intent(context, MobileView.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widget_pic, pendingIntent);

				views.setTextViewText(R.id.widget_title, title);
				views.setTextViewText(R.id.widget_date, date);

				if (pendingIntent2 != null) {
					views.setOnClickPendingIntent(R.id.widget_title,
							pendingIntent2);
					views.setOnClickPendingIntent(R.id.widget_date,
							pendingIntent2);
				}

				// Tell the AppWidgetManager to perform an update on the current
				// app
				// widget
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		} catch (Exception e) {
			Log.e("", e);
		}
	}
}

package at.mg.blogmaster.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import at.mg.blogmaster.BlogApp;
import at.mg.blogmaster.common.Configuration;
import at.mg.blogmaster.common.Constants;
import at.mg.blogmaster.common.Log;
import at.mg.blogmaster.parser.BlogPost;
import at.mg.blogmaster.parser.FeedParser;

public class BlogService extends Service {

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		handleCommand(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		handleCommand(intent, startId);

		return Service.START_NOT_STICKY;
	}

	private void handleCommand(Intent intent, int startId) {
		new Thread() {
			public void run() {
				readRSS();

				BlogService.this.stopSelf();
			};
		}.start();

	}

	/**
	 * read from the rss stream, and save new blogposts
	 */
	private synchronized void readRSS() {
		Log.i("readRSS");
		FeedParser parser = new FeedParser(Configuration.feedUrl);
		List<BlogPost> entries = parser.parse();
		Log.i("entries " + entries.size());

		// TODO only the newest necessary
		BlogPost[] oldEntries = BlogApp.getDA().getBlogPosts();

		if (oldEntries != null) {

			if (entries.get(entries.size() - 1).getDateTime() > oldEntries[0]
					.getDateTime()) {
				// the oldest post of the new entries is NEWER than the newest
				// saved post
				// delete all saved posts
//				Log.i("deleteAll "
//						+ entries.get(entries.size() - 1).getDateTime() + " "
//						+ oldEntries[0].getDateTime());
				BlogApp.getDA().deleteAllBlogPosts();
				
			} else {
				BlogPost post;
				for (int i = entries.size() - 1; i >= 0; i--) {
					post = entries.get(i);
					if (post.getDateTime() <= oldEntries[0].getDateTime()) {
						//remove new post if post is younger than the newest saved post
//						Log.i("remove new post " + post.getDateTime() + " "
//								+ oldEntries[0].getDateTime());
						entries.remove(post);
					}
				}
			}
		}

		for (BlogPost post : entries) {
			BlogApp.getDA().saveBlogpost(post);
		}
		sendBroadcast(new Intent(Constants.BC_UPDATELIST));
	}

}

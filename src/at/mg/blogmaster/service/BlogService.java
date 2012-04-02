package at.mg.blogmaster.service;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import at.mg.blogmaster.BlogApp;
import at.mg.blogmaster.common.Configuration;
import at.mg.blogmaster.common.Constants;
import at.mg.blogmaster.common.Log;
import at.mg.blogmaster.db.DBHelper;
import at.mg.blogmaster.parser.BlogPost;
import at.mg.blogmaster.parser.FeedParser;

public class BlogService extends IntentService {

	public BlogService() {
		super("BlogService");

	}

	/**
	 * read from the rss stream, and save new blogposts
	 */
	private synchronized void readRSS() {
		try {
			Log.i("readRSS");
			FeedParser parser = new FeedParser(Configuration.feedUrl);
			List<BlogPost> entries = parser.parse();

			if (entries == null) {
				return;
			}

			Log.i("entries " + entries.size());

			BlogPost[] oldEntries = BlogApp.getDA().getBlogPosts();

			if (oldEntries != null && entries.size() > 0) {

				if (entries.get(entries.size() - 1).getDateTime() > oldEntries[0]
						.getDateTime()) {
					// the oldest post of the new entries is NEWER than the
					// newest
					// saved post
					// delete all saved posts
					// Log.i("deleteAll "
					// + entries.get(entries.size() - 1).getDateTime() + " "
					// + oldEntries[0].getDateTime());
					BlogApp.getDA().deleteAllBlogPosts(null);
					oldEntries = null;
				} else {
					BlogPost post;
					for (int i = entries.size() - 1; i >= 0; i--) {
						post = entries.get(i);
						if (post.getDateTime() <= oldEntries[0].getDateTime()) {
							// remove new post if post is younger than the
							// newest
							// saved post
							// Log.i("remove new post " + post.getDateTime() +
							// " "
							// + oldEntries[0].getDateTime());
							entries.remove(post);
						}
					}
				}

				if (oldEntries != null) {
					int diff = (oldEntries.length + entries.size())
							- Configuration.MAX_POSTS;
					if (diff > 0) {
						Log.i("max post count reached " + diff);
						String whereClause = "";
						for (int i = oldEntries.length - 1, j = 1; i >= oldEntries.length
								- diff; i--, j++) {
							whereClause += DBHelper.KEY_ID + "="
									+ oldEntries[i].localID
									+ (j < diff ? " OR " : "");
						}
						BlogApp.getDA().deleteAllBlogPosts(whereClause);
					}
				}

			}

			Log.i("new entries " + entries.size());
			for (BlogPost post : entries) {
				post.unread = true;
				BlogApp.getDA().saveBlogpost(post);
			}

			sendOrderedBroadcast(new Intent(Constants.BC_UPDATELIST), null);

		} catch (Exception e) {
			Log.e("parseRSS", e);
		}

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String start = intent.getStringExtra("start");
		if (start != null) {
			Log.i("BlogService:onHandle " + start);
		}

		readRSS();
		if (start != null && start.equals("mv")) {
			BlogApp.getDA().markAllAsRead();
		}
	}

}

package at.mg.blogmaster.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import at.mg.blogmaster.BlogApp;
import at.mg.blogmaster.common.Configuration;
import at.mg.blogmaster.common.Log;
import at.mg.blogmaster.parser.BlogPost;
import at.mg.blogmaster.parser.FeedParser;

public class BlogService extends Service {

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		readRSS();
		
		this.stopSelf();
		
		return Service.START_NOT_STICKY;
	}
	
	
	private void readRSS(){
		FeedParser parser = new FeedParser(Configuration.feedUrl);
		List<BlogPost> entries = parser.parse();
		Log.i("entries " + entries.size());
		for(BlogPost post : entries){
			BlogApp.getDA().savePost(post);
		}
	}
	
}

package at.mg.blogmaster.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import at.mg.blogmaster.common.Configuration;
import at.mg.blogmaster.common.Log;
import at.mg.blogmaster.parser.Entry;
import at.mg.blogmaster.parser.FeedParser;

public class FeedList extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		new Thread(){
			public void run() {
				FeedParser parser = new FeedParser(Configuration.feedUrl);
				List<Entry> entries = parser.parse();
				Log.i("entries " + entries.size());
				for(Entry e : entries){
					Log.i(e.title + " \n " + e.content);
				}
			};
		}.start();
	}
}

package at.mg.blogmaster.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import at.mg.blogmaster.R;
import at.mg.blogmaster.common.Configuration;
import at.mg.blogmaster.common.Log;
import at.mg.blogmaster.parser.BlogPost;
import at.mg.blogmaster.parser.FeedParser;

public class FeedList extends Activity {

	private List<BlogPost> entries;
	private EntryAdapter ad;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ad = new EntryAdapter();
		
		setContentView(R.layout.main);
		
		ListView liste = (ListView)findViewById(R.id.liste);
		liste.setAdapter(ad);
		
		
		
		new Thread(){
			public void run() {
				
				
				updater.sendEmptyMessage(0);
				
			};
		}.start();
	}
	
	private Handler updater = new Handler(){
		public void handleMessage(android.os.Message msg) {
			ad.notifyDataSetChanged();
		};
	};
	
	private class EntryAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(entries != null){
				return entries.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			BlogPost en = entries.get(position);
			
			LinearLayout ll = new LinearLayout(FeedList.this);
			
			ll.setOrientation(LinearLayout.VERTICAL);
			TextView title = new TextView(FeedList.this);
			title.setText(en.title);
			ll.addView(title);
			
			TextView desc = new TextView(FeedList.this);
			desc.setText(en.desc);
			ll.addView(desc);
			
			return ll;
		}
		
	}
}

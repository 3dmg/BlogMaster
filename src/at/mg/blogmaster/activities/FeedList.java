package at.mg.blogmaster.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import at.mg.blogmaster.BlogApp;
import at.mg.blogmaster.R;
import at.mg.blogmaster.activities.parents.PostActivity;
import at.mg.blogmaster.common.Constants;
import at.mg.blogmaster.parser.BlogPost;
import at.mg.blogmaster.service.BlogService;

public class FeedList extends PostActivity {

	private BlogPost[] entries;
	private EntryAdapter ad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ad = new EntryAdapter();

		setContentView(R.layout.feedlist);

		ListView liste = (ListView) findViewById(R.id.liste);
		liste.setAdapter(ad);
		liste.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(FeedList.this, PostDetail.class);
				intent.putExtra(Constants.EXTRA_POSTID, entries[position].localID);
				startActivity(intent);
			}
			
		});

		startService(new Intent(this, BlogService.class));

	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, new IntentFilter(Constants.BC_UPDATELIST));
	}

	private Handler updater = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ad.notifyDataSetChanged();
		};
	};

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			new Thread() {
				public void run() {
					entries = BlogApp.getDA().getBlogPosts();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ad.notifyDataSetChanged();
						}
					});

				};
			}.start();

		}
	};

	private class EntryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (entries != null) {
				return entries.length;
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

			BlogPost en = entries[position];

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

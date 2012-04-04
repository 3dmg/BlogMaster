package at.mg.blogmaster.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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

		ad = new EntryAdapter(getLayoutInflater());

		setContentView(R.layout.feedlist);

		ListView liste = (ListView) findViewById(R.id.liste);
		liste.setAdapter(ad);
		liste.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(FeedList.this, PostDetail.class);
				intent.putExtra(Constants.EXTRA_POSTID,
						entries[position].localID);
				startActivity(intent);
			}

		});

		Intent i = new Intent(FeedList.this, BlogService.class);
		i.putExtra("start", "list");
		startService(i);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter inF = new IntentFilter(Constants.BC_UPDATELIST);
		inF.setPriority(10);
		registerReceiver(receiver, inF);

		new GuiUpdater().execute((Void[]) null);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			new GuiUpdater().execute((Void[]) null);
			this.abortBroadcast();
		}
	};

	private class GuiUpdater extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			BlogApp.getDA().markAllAsRead();
			entries = BlogApp.getDA().getBlogPosts();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			ad.notifyDataSetChanged();

		}

	}

	private class EntryAdapter extends BaseAdapter {

		LayoutInflater li;

		public EntryAdapter(LayoutInflater li) {
			this.li = li;
		}

		public int getCount() {
			if (entries != null) {
				return entries.length;
			}
			return 0;
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			BlogPost en = entries[position];

			ViewHolder holder;

			if (convertView != null) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				convertView = li.inflate(R.layout.post_row, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}

			// holder.getDate().setText(en.getDate());
			holder.getDesc().setText(en.desc);
			holder.getTitle().setText(en.title);

			return convertView;
		}

		/**
		 * helper class for holding the row elements
		 * 
		 * @author Markus
		 * 
		 */
		class ViewHolder {
			private TextView date;
			private TextView title;
			private TextView desc;
			private View base;

			public ViewHolder(View base) {
				this.base = base;
			}

//			public TextView getDate() {
//				if (date == null) {
//					// date = (TextView) base.findViewById(R.id.postrow_date);
//				}
//				return date;
//			}

			public TextView getTitle() {
				if (title == null) {
					title = (TextView) base.findViewById(R.id.postrow_title);
				}
				return title;
			}

			public TextView getDesc() {
				if (desc == null) {
					desc = (TextView) base.findViewById(R.id.postrow_desc);
				}
				return desc;
			}

		}

	}
}
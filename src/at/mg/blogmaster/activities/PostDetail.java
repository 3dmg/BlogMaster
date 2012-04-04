<<<<<<< HEAD
package at.mg.blogmaster.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import at.mg.blogmaster.BlogApp;
import at.mg.blogmaster.R;
import at.mg.blogmaster.activities.parents.PostActivity;
import at.mg.blogmaster.common.Constants;
import at.mg.blogmaster.parser.BlogPost;

public class PostDetail extends PostActivity {

	private BlogPost post;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postdetail);

		final int localID = getIntent().getIntExtra(Constants.EXTRA_POSTID,
				Constants.UNDEFINED);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				BlogApp.getDA().markAllAsRead();
				post = BlogApp.getDA().getBlogPostsByLocalID(localID);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				updateGUI();

			}

		}.execute((Void[]) null);

	}

	/**
	 * fill all gui elements with data
	 */
	private final void updateGUI() {
		if (post == null) {
			return;
		}
		TextView title = (TextView) findViewById(R.id.detail_title);
		title.setText(post.title);

		TextView date = (TextView) findViewById(R.id.detail_date);
		date.setText(post.getDate());

		WebView content = (WebView) findViewById(R.id.detail_content);
		content.loadDataWithBaseURL("fake://fake.fk", post.content,
				"text/html", "UTF-8", null);
	}
}
=======
package at.mg.blogmaster.activities;

import android.app.Activity;

public class PostDetail extends Activity {

}
>>>>>>> 772787fb2d117805e128f3309073c6c5bcba3b65

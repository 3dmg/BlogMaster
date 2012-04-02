package at.mg.blogmaster.activities;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import at.mg.blogmaster.BlogApp;
import at.mg.blogmaster.BuildConfig;
import at.mg.blogmaster.R;
import at.mg.blogmaster.activities.parents.PostActivity;
import at.mg.blogmaster.common.Configuration;
import at.mg.blogmaster.common.Constants;
import at.mg.blogmaster.service.BlogService;

public class MobileView extends PostActivity {

	private ProgressBar progress;
	private ImageView logo;
	private WebView wv;
	private String lastURL;
	private boolean firstStartup = false;

	private static final int REQUEST_SETTINGS = 1;

	private static final String SAVE_URL = "saveURL";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.webview);

		progress = (ProgressBar) findViewById(R.id.wv_progress);
		logo = (ImageView) findViewById(R.id.wv_logo);

		wv = (WebView) findViewById(R.id.wv_webview);

		wv.getSettings().setJavaScriptEnabled(true);

		wv.setWebChromeClient(new MyChrome());
		wv.setWebViewClient(new MyWebClient());

		Intent i = getIntent();

		lastURL = null;

		if (i != null) {
			lastURL = i.getStringExtra(Constants.EXTRA_CALLURL);
		}

		if (savedInstanceState != null
				&& savedInstanceState.containsKey(SAVE_URL)) {
			String savedUrl = savedInstanceState.getString(SAVE_URL);
			if (savedUrl != null) {
				this.lastURL = savedUrl;
			}
		}

		if (lastURL == null || lastURL.length() == 0) {
			lastURL = Configuration.mainURL;
		}
		firstStartup = true;

		// start service to mark all as read
		Intent s = new Intent(MobileView.this, BlogService.class);
		s.putExtra("start", "mv");
		startService(s);
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

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		String savedUrl = savedInstanceState.getString(SAVE_URL);
		if (savedUrl != null) {
			this.lastURL = savedUrl;
		}
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
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			wv.loadUrl(lastURL);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putString(SAVE_URL, lastURL);
		super.onSaveInstanceState(outState);
	}

	private class MyChrome extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			progress.setProgress(newProgress);
		}
	}

	private class MyWebClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			progress.setVisibility(View.GONE);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!url.startsWith(Configuration.mainURL)
					&& !url.startsWith("http://intensedebate.com")) {
				startActivity(new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse(url)));

				return true;
			}

			return false;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			lastURL = url;
			if (firstStartup) {
				Animation ani = AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.fadein);
				logo.startAnimation(ani);
				ani.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {
						logo.setVisibility(View.VISIBLE);
					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						Animation ani = AnimationUtils.loadAnimation(
								getApplicationContext(), R.anim.fadeout);
						logo.startAnimation(ani);
						ani.setAnimationListener(new AnimationListener() {

							public void onAnimationStart(Animation animation) {
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationEnd(Animation animation) {
								logo.setVisibility(View.GONE);
							}
						});
						firstStartup = false;
					}
				});
			}
			progress.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (wv.canGoBack() == true
						&& !lastURL.equals(Configuration.mainURL)) {
					wv.goBack();
				} else {
					finish();
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inf = getMenuInflater();
		inf.inflate(R.menu.mobileview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_info:

			AlertDialog.Builder build = new Builder(this);
			build.setTitle(R.string.app_name);

			build.setMessage(R.string.info);

			build.setNeutralButton("Ok", null);

			build.show();

			return true;
		case R.id.menu_settings:
			startActivityForResult(new Intent(this, Settings.class),
					REQUEST_SETTINGS);

			return true;
		case R.id.menu_share:

			Intent intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(android.content.Intent.EXTRA_TEXT, this.lastURL);
			startActivity(Intent.createChooser(intent, "Teilen"));

			return true;
		case R.id.menu_exit:
			finish();
			return true;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_SETTINGS) {
			BlogApp.blogApp.manageAlarm();
		}
	}
}

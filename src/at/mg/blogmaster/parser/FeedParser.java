package at.mg.blogmaster.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import at.mg.blogmaster.common.Configuration;
import at.mg.blogmaster.common.Log;

public class FeedParser {

	private static final String CHANNEL = "channel";
	private static final String ITEM = "item";

	private URL feedUrl;

	public FeedParser(String feedUrl) {
		try {
			this.feedUrl = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream getInputStream() {
		try {
			return feedUrl.openConnection().getInputStream();
		} catch (IOException e) {

		}
		return null;
	}

	public List<BlogPost> parse() {
		List<BlogPost> messages = null;
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = null;
		try {
			// auto-detect the encoding from the stream
			is = getInputStream();
			if (is == null) {
				return null;
			}
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			BlogPost currentMessage = null;
			boolean done = false;
			while (eventType != XmlPullParser.END_DOCUMENT && !done) {
				String name = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					messages = new ArrayList<BlogPost>();
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					// Log.d("parseName: " + name);
					if (name.equalsIgnoreCase(ITEM)) {
						currentMessage = new BlogPost();
					} else if (currentMessage != null) {
						if (name.equalsIgnoreCase(BlogPost.TAG_LINK)) {
							currentMessage.link = safeNextText(parser);
						} else if (name
								.equalsIgnoreCase(BlogPost.TAG_DESCRIPTION)) {

							currentMessage.desc = safeNextText(parser);

						} else if (name.equalsIgnoreCase(BlogPost.TAG_DATE)) {
							currentMessage.setDate(safeNextText(parser));
						} else if (name.equalsIgnoreCase(BlogPost.TAG_TITLE)) {
							currentMessage.title = safeNextText(parser);
						} else if (name.equals(BlogPost.TAG_CONTENT)) {
							if (Configuration.LIGHT_VERSION) {
								currentMessage.content = "";
							} else {
								currentMessage.content = safeNextText(parser);
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase(ITEM) && currentMessage != null) {
						messages.add(currentMessage);
					} else if (name.equalsIgnoreCase(CHANNEL)) {
						done = true;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e("AndroidNews::PullFeedParser", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {

				}
			}
		}
		return messages;
	}

	/**
	 * bugfix
	 * http://android-developers.blogspot.com/2011/12/watch-out-for-xmlpullparsernexttext.html
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String safeNextText(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		String result = parser.nextText();
		if (parser.getEventType() != XmlPullParser.END_TAG) {
			parser.nextTag();
		}
		return result;
	}

}
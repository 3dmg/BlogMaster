package at.mg.blogmaster.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;
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
			throw new RuntimeException(e);
		}
	}
	
	public List<BlogPost> parse() {
		List<BlogPost> messages = null;
		XmlPullParser parser = Xml.newPullParser();
		try {
			// auto-detect the encoding from the stream
			parser.setInput(this.getInputStream(), null);
			int eventType = parser.getEventType();
			BlogPost currentMessage = null;
			boolean done = false;
			while (eventType != XmlPullParser.END_DOCUMENT && !done){
				String name = null;
				switch (eventType){
					case XmlPullParser.START_DOCUMENT:
						messages = new ArrayList<BlogPost>();
						break;
					case XmlPullParser.START_TAG:
						name = parser.getName();
//						Log.d("parseName: " + name);
						if (name.equalsIgnoreCase(ITEM)){
							currentMessage = new BlogPost();
						} else if (currentMessage != null){
							if (name.equalsIgnoreCase(BlogPost.TAG_LINK)){
								currentMessage.link = (parser.nextText());
							} else if (name.equalsIgnoreCase(BlogPost.TAG_DESCRIPTION)){
								currentMessage.desc = (parser.nextText());
							} else if (name.equalsIgnoreCase(BlogPost.TAG_DATE)){
								currentMessage.setDate(parser.nextText());
							} else if (name.equalsIgnoreCase(BlogPost.TAG_TITLE)){
								currentMessage.title = parser.nextText();
							} else if (name.equals(BlogPost.TAG_CONTENT)){
								currentMessage.content = parser.nextText();
							}
						}
						break;
					case XmlPullParser.END_TAG:
						name = parser.getName();
						if (name.equalsIgnoreCase(ITEM) && currentMessage != null){
							messages.add(currentMessage);
						} else if (name.equalsIgnoreCase(CHANNEL)){
							done = true;
						}
						break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e("AndroidNews::PullFeedParser", e);
			throw new RuntimeException(e);
		}
		return messages;
	}	
	
}

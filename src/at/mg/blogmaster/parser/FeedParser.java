package at.mg.blogmaster.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class FeedParser {

	private static final String RSS = "rss";
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

	public List<Entry> parse() {
		final Entry currentEntry = new Entry();
		RootElement root = new RootElement(RSS);
		final List<Entry> messages = new ArrayList<Entry>();
		Element channel = root.getChild(CHANNEL);
		Element item = channel.getChild(ITEM);
		item.setEndElementListener(new EndElementListener() {
			public void end() {
				messages.add(currentEntry.copy());
			}
		});
		item.getChild(Entry.TAG_TITLE).setEndTextElementListener(
				new EndTextElementListener() {
					public void end(String body) {
						currentEntry.title = body;
					}
				});
		item.getChild(Entry.TAG_LINK).setEndTextElementListener(
				new EndTextElementListener() {
					public void end(String body) {
						currentEntry.link = body;
					}
				});
		item.getChild(Entry.TAG_DESCRIPTION).setEndTextElementListener(
				new EndTextElementListener() {
					public void end(String body) {
						currentEntry.desc = body;
					}
				});
		item.getChild(Entry.TAG_DATE).setEndTextElementListener(
				new EndTextElementListener() {
					public void end(String body) {
						currentEntry.setDate(body);
					}
				});
		item.getChild(Entry.TAG_CONTENT).setEndTextElementListener(
				new EndTextElementListener() {
					public void end(String body) {
						currentEntry.content = body;
					}
				});
		try {
			Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8,
					root.getContentHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return messages;
	}
}

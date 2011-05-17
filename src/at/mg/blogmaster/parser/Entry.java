package at.mg.blogmaster.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Entry {

	public static String TAG_TITLE = "title";
	public static String TAG_LINK = "link";
	public static String TAG_DESCRIPTION = "description";
	public static String TAG_DATE = "pubDate";
	public static String TAG_CONTENT = "encoded";
	
	private static SimpleDateFormat FORMATTER = 
		new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	
	public String title;
	public String link;
	public String desc;
	public String content;
	private Date date;
	
	public Entry copy(){
		Entry entry = new Entry();
		entry.title = title;
		entry.link = link;
		entry.desc = desc;
		return entry;
	}
	
	public String getDate() {
		return FORMATTER.format(this.date);
	}

	public void setDate(String date) {
		// pad the date if necessary
		while (!date.endsWith("00")){
			date += "0";
		}
		try {
			this.date = FORMATTER.parse(date.trim());
		} catch (ParseException e) {
//			throw new RuntimeException(e);
		}
	}	
	
}

<<<<<<< HEAD
package at.mg.blogmaster.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BlogPost {

	public static String TAG_TITLE = "title";
	public static String TAG_LINK = "link";
	public static String TAG_DESCRIPTION = "description";
	public static String TAG_DATE = "pubDate";
	public static String TAG_CONTENT = "encoded";

	private static SimpleDateFormat FORMATTER_INPUT = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss", Locale.US);
	private static SimpleDateFormat FORMATTER_OUTPUT = new SimpleDateFormat(
			"dd.MM.yy HH:mm");

	public int localID;

	public String title;
	public String link;
	public String desc;
	public String content;

	private Date date;

	public boolean unread;

	// public BlogPost copy(){
	// BlogPost entry = new BlogPost();
	// entry.title = title;
	// entry.link = link;
	// entry.desc = desc;
	// return entry;
	// }

	public String getDate() {
		return FORMATTER_OUTPUT.format(this.date);
	}

	public long getDateTime() {
		return this.date.getTime();
	}

	public void setDate(String date) {
		// pad the date if necessary
		while (!date.endsWith("00")) {
			date += "0";
		}
		date = date.replace("+0000", "").trim();
		try {
			this.date = FORMATTER_INPUT.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void setDate(long time) {
		this.date = new Date(time);
	}

}
=======
package at.mg.blogmaster.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BlogPost {

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
	
	public BlogPost copy(){
		BlogPost entry = new BlogPost();
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
>>>>>>> 772787fb2d117805e128f3309073c6c5bcba3b65

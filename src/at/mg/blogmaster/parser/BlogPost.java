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
	
	private static SimpleDateFormat FORMATTER_INPUT = 
		new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	private static SimpleDateFormat FORMATTER_OUTPUT = 
		new SimpleDateFormat("dd.MM.yy HH:mm");	
	
	public int localID;
	
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
		return FORMATTER_OUTPUT.format(this.date);
	}
	
	public long getDateTime(){
		return this.date.getTime();
	}

	public void setDate(String date) {
		// pad the date if necessary
		while (!date.endsWith("00")){
			date += "0";
		}
		try {
			this.date = FORMATTER_INPUT.parse(date.trim());
		} catch (ParseException e) {
//			throw new RuntimeException(e);
		}
	}	
	
	public void setDate(long time){
		this.date = new Date(time);
	}
	
}

package at.mg.blogmaster.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import at.mg.blogmaster.parser.BlogPost;

public class DataAccess {

	
	private static final String DBNAME = "posts.db";
	private final static int DBVERSION = 1;
	
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	
	
	public DataAccess(Context context){
		dbHelper = new DBHelper(context, DBNAME, null, DBVERSION);
	}

	public DataAccess open(){
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void savePost(BlogPost post){
		
	}
}

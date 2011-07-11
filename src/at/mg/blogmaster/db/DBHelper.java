package at.mg.blogmaster.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String TABLE_BLOGPOST = "blogpost";

	// The index (key) column name for use in where clauses.
	public static final String KEY_ID = "_id";
	public static final int ID_COLUMN = 0;
	// The name and column index of each column in your database.
	public static final String KEY_TITLE = "title";
	public static final int TITLE_COLUMN = 1;
	public static final String KEY_DESC = "desc";
	public static final int DESC_COLUMN = 2;
	public static final String KEY_LINK = "link";
	public static final int LINK_COLUMN = 3;
	public static final String KEY_CONTENT = "content";
	public static final int CONTENT_COLUMN = 4;
	public static final String KEY_DATE = "date";
	public static final int DATE_COLUMN = 5;

	private static final String CREATE_BLOGPOST = " create table "
			+ TABLE_BLOGPOST + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_TITLE + " text ,"
			+ KEY_DESC + " text ," + KEY_LINK + " text ," + KEY_CONTENT
			+ " text ," + KEY_DATE + " integer" + ");";

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BLOGPOST);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOGPOST);

	}

}

package at.mg.blogmaster.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import at.mg.blogmaster.common.Log;
import at.mg.blogmaster.parser.BlogPost;

public class DataAccess {

	private static final String DBNAME = "posts.db";
	private final static int DBVERSION = 1;

	private DBHelper dbHelper;
	private SQLiteDatabase db;

	private final boolean usePragma = true;

	public DataAccess(Context context) {
		dbHelper = new DBHelper(context, DBNAME, null, DBVERSION);
	}

	/**
	 * open the database
	 */
	public final void open() {
		close();
		try {
			db = dbHelper.getWritableDatabase();
			// if(usePragma) db.execSQL("PRAGMA synchronous=OFF");
		} catch (SQLException e) {
			/** TODO inform someone about this issue!!! */
			Log.i("DB notWriteable!");
			db = dbHelper.getReadableDatabase();
		} catch (Exception e) {
			Log.e("open DB", e);
		}
	}

	/**
	 * close the database
	 */
	public final void close() {
		try {
			if (db != null) {
				db.close();
			}
		} catch (Exception e) {
			Log.e("DB close " + e.toString(), e);
		}
	}

	/**
	 * check if the db is ready
	 * 
	 * @return
	 */
	public final boolean isOpen() {
		return db != null ? db.isOpen() : false;
	}

	/**
	 * get all blogposts
	 * 
	 * @return
	 */
	public final BlogPost[] getBlogPosts() {

		BlogPost[] posts;

		String[] values = null;
		String where = null;

		// if (accountID == Constants.VAL_INVALID) {
		// values = new String[] { "" + status };
		// where = "status = ?";
		// } else {
		// values = new String[] { "" + status, accountID + "" };
		// where = "status = ? AND accountId = ?";
		// }

		try {
			if (usePragma)
				db.execSQL("PRAGMA synchronous=OFF");
			Cursor postCursor = db.query(DBHelper.TABLE_BLOGPOST,
					new String[] {}, where, values, null, null, "date DESC");

			if (!postCursor.moveToFirst()) {
				postCursor.close();
				// cursor is empty
				return null;
			}

			posts = new BlogPost[postCursor.getCount()];

			int i = 0;
			do {
				posts[i] = getBlogPostFromCursor(postCursor);
				i++;
			} while (postCursor.moveToNext());
			postCursor.close();
		} catch (Exception e) {
			Log.e("getPosts " + e.toString(), e);
			return null;
		}
		return posts;
	}

	/**
	 * return a blogpost with a given local id
	 * 
	 * @param localid
	 * @return
	 */
	public final BlogPost getBlogPostsByLocalID(int localid) {

		BlogPost post = null;

		String[] values = null;
		String where = null;

		values = new String[] { "" + localid };
		where = DBHelper.KEY_ID + " = ?";

		try {
			if (usePragma)
				db.execSQL("PRAGMA synchronous=OFF");
			Cursor postCursor = db.query(DBHelper.TABLE_BLOGPOST,
					new String[] {}, where, values, null, null, "date DESC");

			if (!postCursor.moveToFirst()) {
				postCursor.close();
				// cursor is empty
				return null;
			}

			post = new BlogPost();

			int i = 0;
			do {
				post = getBlogPostFromCursor(postCursor);
				i++;
			} while (postCursor.moveToNext());
			postCursor.close();
		} catch (Exception e) {
			Log.e("getPost " + e.toString(), e);
			return null;
		}
		return post;
	}

	/**
	 * extract the blogpost data from a cursor
	 * 
	 * @param postCursor
	 * @return
	 */
	private final BlogPost getBlogPostFromCursor(Cursor postCursor) {
		BlogPost post = new BlogPost();

		post.localID = postCursor.getInt(DBHelper.ID_COLUMN);
		post.title = postCursor.getString(DBHelper.TITLE_COLUMN);
		post.desc = postCursor.getString(DBHelper.DESC_COLUMN);
		post.link = postCursor.getString(DBHelper.LINK_COLUMN);
		post.content = postCursor.getString(DBHelper.CONTENT_COLUMN);
		post.setDate(postCursor.getLong(DBHelper.DATE_COLUMN));

		return post;
	}

	/**
	 * save a new BlogPost
	 * 
	 * @param post
	 */
	public void saveBlogpost(BlogPost post) {

		if (post == null) {
			return;
		}

		SQLiteStatement postInsert = null;

		try {
			if (usePragma)
				db.execSQL("PRAGMA synchronous=OFF");
			postInsert = db
					.compileStatement("INSERT INTO " + DBHelper.TABLE_BLOGPOST
							+ " (title,desc,link,content,date) "
							+ "VALUES (?,?,?,?,?)");

			int c = 0;
			postInsert.bindString(++c, post.title);
			postInsert.bindString(++c, post.desc);
			postInsert.bindString(++c, post.link);
			postInsert.bindString(++c, post.content);
			postInsert.bindLong(++c, post.getDateTime());

			post.localID = (int) postInsert.executeInsert();

		} catch (Exception e) {
			Log.e("saveBP " + e.toString(), e);
		} finally {
			if (postInsert != null) {
				postInsert.close();
			}
		}
	}

	/**
	 * delete all blogposts to clean up the db
	 */
	public void deleteAllBlogPosts() {
		try {
			db.delete(DBHelper.TABLE_BLOGPOST, null, null);
		} catch (Exception e) {
			Log.e("deleteAllBP", e);
		}
	}

}

package at.mg.blogmaster;

import android.app.Application;
import android.content.Context;
import at.mg.blogmaster.db.DataAccess;

public class BlogApp extends Application {

	private static DataAccess da;
	private static Context context;
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
	}
	
	public static DataAccess getDA(){
		if(da != null){
			da = new DataAccess(context);
		}
		return da;
	}
}

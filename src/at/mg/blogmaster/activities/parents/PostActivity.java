package at.mg.blogmaster.activities.parents;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import at.mg.blogmaster.common.Configuration;

public class PostActivity extends Activity {
@Override
protected void onCreate(Bundle savedInstanceState) {
    if (Configuration.DEV_MODE) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }
	super.onCreate(savedInstanceState);
}
}

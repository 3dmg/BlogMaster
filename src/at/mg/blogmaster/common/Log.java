<<<<<<< HEAD
package at.mg.blogmaster.common;

import org.acra.ErrorReporter;

import at.mg.blogmaster.BuildConfig;

public final class Log {

	public static void d(Object obj) {
		if (BuildConfig.DEBUG) {
			android.util.Log.d(getTag(), String.valueOf(obj));
		}
	}

	public static void i(Object obj) {
		if (BuildConfig.DEBUG) {
			android.util.Log.i(getTag(), String.valueOf(obj));
		}
	}

	public static void w(Object obj) {
		if (BuildConfig.DEBUG) {
			android.util.Log.w(getTag(), String.valueOf(obj));
		}
	}

	public static void e(Object obj, Throwable e) {
		android.util.Log.e(getTag(), String.valueOf(obj));
		e.printStackTrace();
		ErrorReporter.getInstance().handleException(e);
		// ErrorReporter.getInstance().handleException(e);
	}

	private static String getTag() {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
		String tag = caller.getClassName();
		if (BuildConfig.DEBUG) {
			tag += ", " + caller.getMethodName() + "()";
		}
		return tag;
	}

}
=======
package at.mg.blogmaster.common;


public final class Log {

    public static void d(Object obj) {
        android.util.Log.d(getTag(), String.valueOf(obj));
    }

    public static void i(Object obj) {
        android.util.Log.i(getTag(), String.valueOf(obj));
    }

    public static void w(Object obj) {
    	android.util.Log.w(getTag(), String.valueOf(obj));
    }
    
    public static void e(Object obj, Throwable e) {
        android.util.Log.e(getTag(), String.valueOf(obj));
        e.printStackTrace();
//        ErrorReporter.getInstance().handleException(e);
    }

    private static String getTag() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String tag = caller.getClassName();
        if (Configuration.DEV_MODE) {
            tag += ", " + caller.getMethodName() + "()";
        }
        return tag;
    }

}
>>>>>>> 772787fb2d117805e128f3309073c6c5bcba3b65

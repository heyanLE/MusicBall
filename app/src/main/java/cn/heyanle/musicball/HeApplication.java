package cn.heyanle.musicball;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.tencent.bugly.crashreport.CrashReport;

import static com.tencent.bugly.Bugly.applicationContext;

public class HeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(applicationContext, "fb4ba45400", C.IS_DEBUG );
    }
}

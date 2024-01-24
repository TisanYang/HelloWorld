package hf.car.wifi.video

import android.R
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.niklaus.mvvm.utils.PreferencesUtil
import hf.car.wifi.video.ui.activity.HomeActivity


class MineApplication : Application() {

    private val tag: String = javaClass.simpleName

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "application onCreate")

        PreferencesUtil.instance.init(applicationContext)

        initLifecycle()
        initCrash()
    }

    private fun initCrash() {
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
            .enabled(true) //default: true
            .showErrorDetails(true) //default: true
            .showRestartButton(true) //default: true
            .logErrorOnRestart(true) //default: true
            .trackActivities(false) //default: false
            .minTimeBetweenCrashesMs(2000) //default: 3000
            .errorDrawable(R.drawable.ic_delete) //default: bug image
            .restartActivity(HomeActivity::class.java) //default: null (your app's launch activity)
            .errorActivity(DefaultErrorActivity::class.java) //default: null (default error activity)
            .apply()
    }

    private fun initLifecycle() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }
}
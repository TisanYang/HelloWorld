package hf.car.wifi.video.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import hf.car.wifi.video.base.EmptyViewModel
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.databinding.ActivityGuideBinding

class GuideActivity :
    MBaseActivity<EmptyViewModel, ActivityGuideBinding>(ActivityGuideBinding::inflate) {

    private val mHandler: Handler = Handler(Looper.getMainLooper())

    override fun initViewModel(): EmptyViewModel =
        ViewModelProvider(this)[EmptyViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    override fun initView() {
        super.initView()

        mHandler.postDelayed({
            startActivity(Intent(this@GuideActivity, HomeActivity::class.java))
            //startActivity(Intent(this@GuideActivity, BsdMovingActivity::class.java))
            finish()
        }, 50)
    }

    override fun onDestroy() {
        super.onDestroy()

        mHandler.removeCallbacksAndMessages(null)
    }
}
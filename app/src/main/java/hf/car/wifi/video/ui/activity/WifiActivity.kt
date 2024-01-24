package hf.car.wifi.video.ui.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.ViewModelProvider
import hf.car.wifi.video.R
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.databinding.ActivityWifiBinding
import hf.car.wifi.video.viewmodel.WifiViewModel

class WifiActivity :
    MBaseActivity<WifiViewModel, ActivityWifiBinding>(ActivityWifiBinding::inflate) {

    override fun initViewModel(): WifiViewModel =
        ViewModelProvider(
            this@WifiActivity,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[WifiViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initListener()
    }

    override fun initListener() {
        super.initListener()

        mViewModel.wifiResponse.observe(this@WifiActivity) { ret ->
            if (0 == ret) {
                binding.imgWifi.setImageResource(R.drawable.icon_wifi_connect)
                binding.tvWifi.text = getString(R.string.tv_wifi_success)
            } else {
                binding.imgWifi.setImageResource(R.drawable.icon_wifi_disconnect)
                binding.tvWifi.text = getString(R.string.tv_wifi_fail)
            }
        }

        binding.topBar.setBackClickListener { finish() }

        binding.btnWifi.setOnClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()

        mViewModel.loadWifiInfo()
    }
}
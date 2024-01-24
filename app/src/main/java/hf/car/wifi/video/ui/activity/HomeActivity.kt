package hf.car.wifi.video.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.niklaus.mvvm.utils.MvvmGsonUtil
import com.niklaus.mvvm.utils.NetUtils
import hf.car.wifi.video.R
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.constant.ApiConstant
import hf.car.wifi.video.databinding.ActivityHomeBinding
import hf.car.wifi.video.viewmodel.WifiViewModel

class HomeActivity :
    MBaseActivity<WifiViewModel, ActivityHomeBinding>(ActivityHomeBinding::inflate) {

    private val permissionsArray =
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            //Manifest.permission.WRITE_SETTINGS
        )

    private lateinit var connectivityManager: ConnectivityManager

    override fun initViewModel(): WifiViewModel =
        ViewModelProvider(
            this@HomeActivity,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[WifiViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityManager = getSystemService(ConnectivityManager::class.java)

        initView()
        initListener()
        checkPermission()
    }

    private fun checkPermission(): Boolean {
        val permissions = mutableListOf<String>()

        for (permission: String in permissionsArray) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(permission)
            }
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this@HomeActivity, permissions.toTypedArray(), 1)
        }

        Log.d(TAG, "permissions:${MvvmGsonUtil.toJson(permissions)}")

        return permissions.isEmpty()
    }

    override fun initView() {
        super.initView()

        binding.tvNetwork.text = getString(R.string.tv_network_disconnect)

        binding.btnTest.visibility = View.GONE
    }

    override fun initListener() {
        super.initListener()

        mViewModel.wifiResponse.observe(this@HomeActivity) { ret ->
            if (0 == ret) {
                binding.tvNetwork.text = getString(R.string.tv_network_connect)
            } else {
                binding.tvNetwork.text = getString(R.string.tv_network_disconnect)
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val build =
                NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            connectivityManager.registerNetworkCallback(build.build(), networkCallback)
        }

        binding.tvNetwork.setOnClickListener {
            startActivity(Intent(this@HomeActivity, WifiActivity::class.java))
        }

        binding.btnBsd.setOnClickListener {
            startActivity(Intent(this@HomeActivity, BsdSettingActivity::class.java))
        }
        binding.btnDsm.setOnClickListener {
            startActivity(Intent(this@HomeActivity, DmsSettingActivity::class.java))
        }
        binding.btnVideo.setOnClickListener {
            startActivity(Intent(this@HomeActivity, VideoInfoActivity::class.java))
        }
        binding.btnReplayTheater.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ReplayTheaterActivity::class.java))
        }
        binding.btnTimeCalibration.setOnClickListener {
            startActivity(Intent(this@HomeActivity, TimeCalibrationActivity::class.java))
        }
        binding.btnFace.setOnClickListener {
            //if (checkPermission()) {
                startActivity(Intent(this@HomeActivity, FaceInfoActivity::class.java))
           /* } else {
                Toast.makeText(this@HomeActivity, "有未经同意权限", Toast.LENGTH_SHORT).show()

            }*/
        }
        binding.btnUpgrade.setOnClickListener {
            startActivity(Intent(this@HomeActivity, UpgradeActivity::class.java))
        }

        binding.btnTest.setOnClickListener {
            startActivity(Intent(this@HomeActivity, MainActivity::class.java))
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            Log.d(TAG, "The default network changed capabilities: $networkCapabilities")
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val ret = NetUtils.pingIP(ApiConstant.SOCKET_IP)
                if (0 == ret) {
                    runOnUiThread {
                        binding.tvNetwork.text = getString(R.string.tv_network_connect)
                    }
                } else {
                    runOnUiThread {
                        binding.tvNetwork.text = getString(R.string.tv_network_disconnect)
                    }
                }
            } else {
                runOnUiThread {
                    binding.tvNetwork.text = getString(R.string.tv_network_disconnect)
                }
            }
        }

        override fun onLost(network: Network) {
            Log.e(
                TAG,
                "The application no longer has a default network. The last default network was $network"
            )
            runOnUiThread {
                binding.tvNetwork.text = getString(R.string.tv_network_disconnect)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        mViewModel.loadWifiInfo()
    }
}
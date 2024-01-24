package hf.car.wifi.video.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.niklaus.mvvm.utils.ByteUtil
import com.niklaus.mvvm.utils.NetUtils
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.databinding.ActivityTimeCalibrationBinding
import hf.car.wifi.video.model.DeviceModel
import hf.car.wifi.video.viewmodel.DeviceVideoViewModel
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class TimeCalibrationActivity :
    MBaseActivity<DeviceVideoViewModel, ActivityTimeCalibrationBinding>(
        ActivityTimeCalibrationBinding::inflate
    ) {

    private var ip: String? = ""
    private val port: Int = 4343
    private val channels = mutableListOf(0x01, 0x02, 0x04, 0x08, 0x10, 0x20)


    override fun initViewModel(): DeviceVideoViewModel =
        ViewModelProvider(this@TimeCalibrationActivity)[DeviceVideoViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initView()
        initListener()
        loadData()
    }


    /**
     * 更新设备信息
     */
    private fun updateDeviceInfo(model: DeviceModel) {
        val deviceID = ByteUtil.byteToStr(model.deviceID)
        val deviceName = ByteUtil.byteToStr(model.deviceName)
        val carNumber = String(model.carNumber, StandardCharsets.UTF_8)
        val driverName = String(model.driverName, StandardCharsets.UTF_8)
    }


    override fun initView() {
        super.initView()
        ip = NetUtils.getIPAddress(applicationContext)
        binding.tvSysTime.text = getDataTime()
    }

    override fun initListener() {
        super.initListener()

        mViewModel.deviceTimeResponse.observe(this@TimeCalibrationActivity) {
            when (it.code) {
                0 -> {
                    it.data?.let { model ->
                        binding.tvSysTime.text = getDataTime()
                        binding.tvDeviceTime.text =
                            "" + ByteUtil.byteArrayToInt(model.hour) + ":" + ByteUtil.byteArrayToInt(
                                model.min
                            ) + ":" + ByteUtil.byteArrayToInt(
                                model.sec
                            )
                    }
                }
                100 -> {
                    Toast.makeText(this@TimeCalibrationActivity,"时间同步成功",Toast.LENGTH_SHORT).show()
                    mViewModel.loadDeviceTime()
                }
                else -> {
                    Toast.makeText(this@TimeCalibrationActivity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.topBar.setBackClickListener { finish() }
        binding.tvSync.setOnClickListener {
            mViewModel.syncTime(getDataTime().toByteArray())
//            mViewModel.loadDeviceBsd()
        }
    }

    private fun getData() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
//                mViewModel.loadDeviceTime()
                post {
                    binding.tvSysTime.text = getDataTime()
                }
            }
        }, 0, 1000)
    }

    override fun loadData() {
        super.loadData()
        mViewModel.loadDeviceTime()
        getData()
    }

    private fun getDataTime(): String {
        /*val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return current.format(formatter)
*/

        val dateFormat = SimpleDateFormat("HH:mm:ss")
        val dataStr = dateFormat.format(Date())
        return dataStr
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mViewModel.stopServer()
    }
}
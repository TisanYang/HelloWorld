package hf.car.wifi.video.ui.activity

import android.graphics.Outline
import android.graphics.Rect
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.niklaus.mvvm.utils.ByteUtil
import com.niklaus.mvvm.utils.NetUtils
import hf.car.wifi.video.R
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.callback.ItemChooseCallback
import hf.car.wifi.video.databinding.ActivityRepleyTheaterBinding
import hf.car.wifi.video.model.*
import hf.car.wifi.video.ui.adapter.VideoChannelAdapter
import hf.car.wifi.video.ui.adapter.VideoReplayAdapter
import hf.car.wifi.video.ui.custom.SpacesItemDecoration
import hf.car.wifi.video.viewmodel.ReplayTheaterViewModel
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class ReplayTheaterActivity :
    MBaseActivity<ReplayTheaterViewModel, ActivityRepleyTheaterBinding>(ActivityRepleyTheaterBinding::inflate) {


    private var ip: String? = ""

    private val port: Int = 4345
    private val channels = mutableListOf(0x01, 0x02, 0x04, 0x08, 0x10, 0x20)

    private var surfaceHolder: SurfaceHolder? = null
    private var mCodec: MediaCodec? = null
    private var mFrameIndex = 0

    private var isPlaying = false
    private var playChannel: Int = -1

    private val mAdapter = VideoChannelAdapter(mutableListOf())
    private val mReplayAdapter: VideoReplayAdapter by lazy { VideoReplayAdapter() }
    private val mReplayList = mutableListOf<DataReplayBean>()

    private var currentChannel: Byte = 4
    lateinit var startTimeArr: ByteArray
    lateinit var endTimeArr: ByteArray

    override fun initViewModel(): ReplayTheaterViewModel =
        ViewModelProvider(this@ReplayTheaterActivity)[ReplayTheaterViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initView()
        initListener()
        loadData()
    }

    /**
     * 播放视频
     */
    private fun playVideo(channel: Int) {
        if (isPlaying) {
            mViewModel.closeVideoInfo(playChannel, channel)
            mViewModel.stopServer()
        } else {
            if (!TextUtils.isEmpty(ip)) {
                ip?.let { realIp ->
                    mViewModel.startServer(realIp, port)
                    isPlaying = true
                    playChannel = channel
                    //mViewModel.openVideoInfo(realIp, port, channel)
                }
            } else {
                Toast.makeText(this@ReplayTheaterActivity, "ip获取失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 创建编码器
     */
    var width: Int = 640
    var height = 480

    private fun initMediaCodec() {
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1280, 720)
//        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1)
//        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 1)
//        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
        try {
            mCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
            mCodec?.configure(mediaFormat, binding.surfaceView.holder.surface, null, 0)
            mCodec?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 播放视频流
     */
    val tag = "ReplayTheaterActivity"

    /**
     * 获取系统信息
     */
    private fun updateSysInfo(model: SysInfoModel) {
        val count = ByteUtil.byteArrayToInt(model.sysChannel)
        Log.d(TAG, "sys channel count:$count")

        val channelData = mutableListOf<VideoChannelModel>()
        try {
            for (i in 1..count) {
                val ch = VideoChannelModel(getString(R.string.tv_channels, i), channels[i - 1])
                channelData.add(ch)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mAdapter.setData(channelData)
        //mViewModel.getReplayData(1)
        //playVideo(2)
    }

    /**
     * 更新设备信息
     */
    private fun updateDeviceInfo(model: DeviceModel) {
        val deviceID = ByteUtil.byteToStr(model.deviceID)
        val deviceName = ByteUtil.byteToStr(model.deviceName)
        val carNumber = String(model.carNumber, StandardCharsets.UTF_8)
        val driverName = String(model.driverName, StandardCharsets.UTF_8)

//        val deviceStr = if (!TextUtils.isEmpty(deviceName)) {
//            getString(R.string.tv_device_name, deviceName, deviceID)
//        } else {
//            getString(R.string.tv_device_name, "佚名", deviceID)
//        }
        binding.tvDeviceInfo.text = deviceID

        binding.tvDriverName.text = getString(R.string.tv_driver_name, driverName)
        binding.tvCarNumber.text = getString(R.string.tv_car_number, carNumber)
    }

    /**
     * 更新设备状态
     */
    private fun updateDeviceStatus(model: DeviceStatus) {
        val deviceStr =
            getString(R.string.tv_signal_number, ByteUtil.byteArrayToInt(model.deviceSignal))
        binding.tvSignal.text = deviceStr
    }

    /**
     * 设置SurfaceView圆角
     */
    private fun roundSurfaceView() {
        binding.surfaceView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                val leftMargin = 0
                val topMargin = 0
                val selfRect = Rect(
                    leftMargin, topMargin,
                    rect.right - rect.left - leftMargin,
                    rect.bottom - rect.top - topMargin
                )
                outline.setRoundRect(selfRect, 12f)
            }
        }
        binding.surfaceView.clipToOutline = true
    }

    override fun initView() {
        super.initView()

        ip = NetUtils.getIPAddress(applicationContext)
        surfaceHolder = binding.surfaceView.holder
        surfaceHolder?.addCallback(surfaceHolderCallback)

//        val deviceStr = getString(R.string.tv_device_name, "设备名称", "设备ID")
        val deviceStr = "设备ID"
        binding.tvDeviceInfo.text = deviceStr

        val deviceSignalStr = getString(R.string.tv_signal_number, 0)
        binding.tvSignal.text = deviceSignalStr

        binding.tvDriverName.text = getString(R.string.tv_driver_name, "--")
        binding.tvCarNumber.text = getString(R.string.tv_car_number, "---")

        val lm = GridLayoutManager(this@ReplayTheaterActivity, 4)
        binding.recyclerView.layoutManager = lm
        binding.recyclerView.addItemDecoration(SpacesItemDecoration(12))
        binding.recyclerView.setPadding(0, 0, 0, 0)
        binding.recyclerView.adapter = mAdapter


        binding.recyclerViewList.layoutManager = LinearLayoutManager(this@ReplayTheaterActivity)
        binding.recyclerViewList.adapter = mReplayAdapter
        mReplayAdapter.isEmptyViewEnable = true
        mReplayAdapter.setEmptyViewLayout(this@ReplayTheaterActivity, R.layout.empty_layout)
        binding.smartRefresh.setOnRefreshListener {
            it.finishRefresh(3000)
        }
        binding.smartRefresh.setOnLoadMoreListener {
            it.finishLoadMore(3000)
        }
    }

    override fun initListener() {
        super.initListener()

        mViewModel.sysInfoResponse.observe(this@ReplayTheaterActivity) {
            if (0 == it.code) {
                it.data?.let { model ->
                    updateSysInfo(model)
                }
            } else {
                Toast.makeText(this@ReplayTheaterActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        mViewModel.deviceInfoResponse.observe(this@ReplayTheaterActivity) {
            if (0 == it.code) {
                it.data?.let { model ->
                    updateDeviceInfo(model)
                }
            } else {
                Toast.makeText(this@ReplayTheaterActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        mViewModel.deviceStatusResponse.observe(this@ReplayTheaterActivity) {
            if (0 == it.code) {
                it.data?.let { model ->
                    updateDeviceStatus(model)
                }
            } else {
                Toast.makeText(this@ReplayTheaterActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        mViewModel.playVideoResponse.observe(this@ReplayTheaterActivity) {
            Log.d(TAG, "接收到的视频数据长度,length:${it.size}")
            decodeUpdate(it, it.size)
            //mVideoEncoder?.inputFrameToEncoder(it)
        }

        mViewModel.startVideoResponse.observe(this@ReplayTheaterActivity) {
            if (0 != it.code) {
                Toast.makeText(this@ReplayTheaterActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        mViewModel.stopVideoResponse.observe(this@ReplayTheaterActivity) {
            if (0 == it.code) {
                if (!TextUtils.isEmpty(ip)) {
                    it.data?.let { channel ->
                        mViewModel.startServer(ip!!, port)
                        isPlaying = true
                        playChannel = channel
                        //mViewModel.openVideoInfo(ip!!, port, channel)
                    }
                } else {
                    Toast.makeText(this@ReplayTheaterActivity, it.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.topBar.setBackClickListener { finish() }

        mAdapter.setItemChooseCallback(object : ItemChooseCallback {
            override fun onChoose(id: Int, type: Int) {
                //更新选中的channel
                mAdapter.updateChooseItem(id)
                //播放数据
                playVideo(mAdapter.getData()[id].id)
                //获取播放的条目数据
                mViewModel.getReplayData(mAdapter.getData()[id].id)
            }
        })

        mReplayAdapter.setOnItemClickListener { adapter, view, position ->
            //这里发起播放指令
            Toast.makeText(this, "点击了第$position 个", Toast.LENGTH_SHORT).show()
            currentChannel = mReplayList[position].channel[0]
            startTimeArr = mReplayList[position].sttime
            endTimeArr = mReplayList[position].etime

            mViewModel.play(mReplayList[position], currentChannel, ip!!)
        }

        mViewModel.videoPlayResponse.observe(this) {
            mReplayList.clear()
            mReplayList.addAll(it.data!!)
            mReplayAdapter.addAll(mReplayList)
        }

        binding.tvPause.setOnClickListener {
            btnControlEnable(binding.tvPause)
        }

        binding.tvBack.setOnClickListener{
            btnControlEnable(binding.tvBack)
        }

        binding.tvFront.setOnClickListener {
           btnControlEnable(binding.tvFront)
            // TODO: 如果起始时间和结束时间数组没有，要提示先选择视频
            Toast.makeText(this, "快进", Toast.LENGTH_SHORT).show()
            mViewModel.goFront(currentChannel, startTimeArr, endTimeArr)
        }

        binding.tvStop.setOnClickListener {
            btnControlEnable(binding.tvStop)
            Toast.makeText(this, "停止", Toast.LENGTH_SHORT).show()
            mViewModel.stopPlay(currentChannel, startTimeArr, endTimeArr)
        }
    }

    // 用于对准视频的时间戳
    private fun decodeUpdate(bytes: ByteArray, length: Int) {
        try {
            mCodec?.let {
                //等待的时间（毫秒），-1表示一直等，0表示不等。
                val inputBufferIndex = it.dequeueInputBuffer(-1)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = it.getInputBuffer(inputBufferIndex)
                    val timestamp: Long = (mFrameIndex++ * 1000000 / 30).toLong()
                    inputBuffer?.clear()
                    inputBuffer?.put(bytes, 0, length)
                    it.queueInputBuffer(inputBufferIndex, 0, length, timestamp, 0)
                }
                val bufferInfo = MediaCodec.BufferInfo()
                var outputBufferIndex = it.dequeueOutputBuffer(bufferInfo, 0)
                while (outputBufferIndex >= 0) {
                    it.releaseOutputBuffer(outputBufferIndex, true)
                    //outputBufferIndex = it.dequeueOutputBuffer(bufferInfo, 0)
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Log.e(TAG, "decodeUpdate t:$t, message:${t.message}")
        }
    }

    override fun loadData() {
        super.loadData()
        mViewModel.loadSysInfo()
        mViewModel.loadDeviceInfo()
        mViewModel.loadDeviceStatus()
    }

    private val surfaceHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            Log.d(TAG, "surfaceCreated")

            initMediaCodec()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Log.d(TAG, "surfaceChanged")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.d(TAG, "surfaceDestroyed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (-1 != playChannel) {
            mViewModel.closeVideoInfo(playChannel, -1)
        }
        mViewModel.stopServer()
    }

    private fun btnControlEnable(btn: AppCompatButton) {
        val list = mutableListOf<AppCompatButton>(binding.tvFront,binding.tvBack,binding.tvStop,binding.tvPause)

        for (item in list){
            item.isEnabled = btn != item
        }
    }


}
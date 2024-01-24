package hf.car.wifi.video.ui.activity

import android.graphics.Outline
import android.graphics.Rect
import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.niklaus.mvvm.utils.ByteUtil
import com.niklaus.mvvm.utils.NetUtils
import hf.car.wifi.video.R
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.callback.ItemChooseCallback
import hf.car.wifi.video.databinding.ActivityBsdSettingBinding
import hf.car.wifi.video.model.SysInfoModel
import hf.car.wifi.video.model.VideoChannelModel
import hf.car.wifi.video.ui.adapter.VideoChannelAdapter
import hf.car.wifi.video.ui.view.NumPickDialog
import hf.car.wifi.video.ui.view.TimeChoiceListener
import hf.car.wifi.video.viewmodel.DeviceVideoViewModel
import java.io.IOException


class BsdSettingActivity :
    MBaseActivity<DeviceVideoViewModel, ActivityBsdSettingBinding>(ActivityBsdSettingBinding::inflate) {

    private var ip: String? = ""
    private val port: Int = 4343
    private val channels = mutableListOf(0x01, 0x02, 0x04, 0x08, 0x10, 0x20)

    private var surfaceHolder: SurfaceHolder? = null
    private var mCodec: MediaCodec? = null
    private var mFrameIndex = 0

    private var isPlaying = false
    private var playChannel: Int = -1
    private var mChannel: Int = -1

    private val mAdapter = VideoChannelAdapter(mutableListOf())

    var surfaceViewWidth = 0f
    var surfaceViewHeight = 0f

    lateinit var points: IntArray

    override fun initViewModel(): DeviceVideoViewModel =
        ViewModelProvider(this@BsdSettingActivity)[DeviceVideoViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initView()
        initListener()
        loadData()
    }

    /**
     * 切换播放与关闭
     */
    private fun switchPlayVideo(channel: Int) {
        isPlaying = false
        binding.touchBsd.visibility = View.GONE
        mViewModel.closeVideoInfo(playChannel, channel)
        mViewModel.stopServer()
    }

    private fun switchClick(channel: Int) {
        binding.tvLeft.isClickable = channel != 8
        binding.tvRight.isClickable = channel != 1
        binding.tvTop.isClickable = channel != 4
        binding.tvBottom.isClickable = channel != 2
    }


    /**
     * 播放视频
     */
    private fun playVideo(channel: Int) {
        mChannel = channel
        if (isPlaying) {
            mViewModel.closeVideoInfo(playChannel, channel)
            mViewModel.stopServer()
        } else {
            if (!TextUtils.isEmpty(ip)) {
                ip?.let { realIp ->
                    mViewModel.startServer(realIp, port)
                    isPlaying = true
                    playChannel = channel
                    mViewModel.openVideoInfo(realIp, port, channel)
                }
            } else {
                Toast.makeText(this@BsdSettingActivity, "ip获取失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 创建编码器
     */
    private fun initMediaCodec() {
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1280, 720)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1)
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 1)
        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
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
    private fun decodeUpdate(data: ByteArray, length: Int) {
        try {
            mCodec?.let {
                //等待的时间（毫秒），-1表示一直等，0表示不等。
                val inputBufferIndex = it.dequeueInputBuffer(-1)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = it.getInputBuffer(inputBufferIndex)
                    val timestamp: Long = (mFrameIndex++ * 1000000 / 30).toLong()
                    inputBuffer?.clear()
                    inputBuffer?.put(data, 0, length)
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
        binding.touchBsd.setVm(mViewModel)

    }

    override fun initListener() {
        super.initListener()

        binding.tvLeft.setOnClickListener {
            //获取DMS参数
            playVideo(8)
            binding.touchBsd.visibility = View.GONE
            switchClick(8)
        }
        binding.tvRight.setOnClickListener {
            playVideo(1)
            switchClick(1)
            binding.touchBsd.visibility = View.GONE
        }
        binding.tvTop.setOnClickListener {
            binding.touchBsd.visibility = View.VISIBLE
            playVideo(4)
            switchClick(4)
            //开局自动获取BSD信息
            mViewModel.loadDeviceBsd()
        }
        binding.tvBottom.setOnClickListener {
            binding.touchBsd.visibility = View.VISIBLE
            playVideo(2)
            switchClick(2)
            //开局自动获取BSD信息
            mViewModel.loadDeviceBsd()
        }
        binding.tvGetConfig.setOnClickListener {

//            if (mChannel != 4 || mChannel != 2) {
//                Toast.makeText(this, "当前通道不能设置BSD信息", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
            if (mViewModel.isDataIllegal) {
                Toast.makeText(this, "数据设置不符合规范", Toast.LENGTH_SHORT).show()
            } else {
                mViewModel.setBsdData(mChannel, points)
            }
        }

        mViewModel.sysInfoResponse.observe(this@BsdSettingActivity) {
            if (0 == it.code) {
                it.data?.let { model ->
                    updateSysInfo(model)
                }
            } else {
                Toast.makeText(this@BsdSettingActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        mViewModel.playVideoResponse.observe(this@BsdSettingActivity) {
            Log.d(TAG, "接收到的视频数据长度,length:${it.size}")
            decodeUpdate(it, it.size)
        }

        mViewModel.startVideoResponse.observe(this@BsdSettingActivity) {
            if (0 != it.code) {
                Toast.makeText(this@BsdSettingActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        mViewModel.stopVideoResponse.observe(this@BsdSettingActivity) {
            if (0 == it.code) {
                if (!TextUtils.isEmpty(ip)) {
                    it.data?.let { channel ->
                        mViewModel.startServer(ip!!, port)
                        isPlaying = true
                        playChannel = channel
                        mViewModel.openVideoInfo(ip!!, port, channel)
                    }
                } else {
                    Toast.makeText(this@BsdSettingActivity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.topBar.setBackClickListener { finish() }

        mAdapter.setItemChooseCallback(object : ItemChooseCallback {
            override fun onChoose(id: Int, type: Int) {
                mAdapter.updateChooseItem(id)
                playVideo(mAdapter.getData()[id].id)
            }
        })

        binding.tvGetDefaule.setOnClickListener {
            points.let {
                val ints = binding.touchBsd.init()
                val ratioW = 1280 / surfaceViewWidth
                val ratioH = 720 / surfaceViewHeight

                for (element in 0 until ints.size) {
                    if (element % 2 == 0) {
                        points[element] = (ints[element] * ratioW).toInt()
                    } else {
                        points[element] = (ints[element] * ratioH).toInt()
                    }
                }

                mViewModel.isDataIllegal = false
                mViewModel.setBsdData(mChannel, points)

            }
        }

        //处理点信息
        mViewModel.bsdPointResponse.observe(this@BsdSettingActivity) { it ->
            it.data?.let {

                //要有一个比例,转换成对应的宽高坐标点
                val ratioW: Float
                val ratioH: Float
                //如果是从画线过来的
                val operationPoints: IntArray = if (it.isFromView) {
                    it.points
                } else {
                    //以前是一组线，现在变成了2组线
                    val totalPoint = it.points
                    val backPoint = IntArray(16)
                    val frontPoint = IntArray(16)
                    System.arraycopy(totalPoint, 2, backPoint, 0, 16)
                    System.arraycopy(totalPoint, 20, frontPoint, 0, 16)
                    if (mChannel == 4) frontPoint else backPoint
                }
                if (it.isFromView) {
                    ratioW = 1280 / surfaceViewWidth
                    ratioH = 720 / surfaceViewHeight
                } else {
                    ratioW = surfaceViewWidth / 1280
                    ratioH = surfaceViewHeight / 720
                }

                val intArray = intArrayOf(
                    (operationPoints[0] * ratioW).toInt(), (operationPoints[1] * ratioH).toInt(),
                    (operationPoints[2] * ratioW).toInt(), (operationPoints[3] * ratioH).toInt(),
                    (operationPoints[4] * ratioW).toInt(), (operationPoints[5] * ratioH).toInt(),
                    (operationPoints[6] * ratioW).toInt(), (operationPoints[7] * ratioH).toInt(),
                    (operationPoints[8] * ratioW).toInt(), (operationPoints[9] * ratioH).toInt(),
                    (operationPoints[10] * ratioW).toInt(), (operationPoints[11] * ratioH).toInt(),
                    (operationPoints[12] * ratioW).toInt(), (operationPoints[13] * ratioH).toInt(),
                    (operationPoints[14] * ratioW).toInt(), (operationPoints[15] * ratioH).toInt()
                )
                points = intArray
                if (!it.isFromView) {
                    binding.touchBsd.setArrayData(points)
                }
            }
        }
        mViewModel.lockCarResponse.observe(this) {
            binding.tvLuckTime.text = "锁车时间:${it.data}s"
        }
        binding.tvLuckTimeSet.setOnClickListener {

            val pickDialog = NumPickDialog(this)
            pickDialog.listener = object : TimeChoiceListener {
                override fun choiceTime(value: Int) {
                    Toast.makeText(this@BsdSettingActivity, value.toString(), Toast.LENGTH_SHORT)
                        .show()

                    mViewModel.setLockCarTime(value)
                }

            }
            pickDialog.show()
        }
    }

    override fun loadData() {
        super.loadData()

        mViewModel.loadSysInfo()
        mViewModel.loadDeviceInfo()
        mViewModel.loadDeviceStatus()
        //开局自动获取BSD信息
        //mViewModel.loadDeviceBsd()
        //获取锁车时间
        mViewModel.getLockCarTime()

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        surfaceViewWidth = binding.surfaceView.width.toFloat()
        surfaceViewHeight = binding.surfaceView.height.toFloat()
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
}
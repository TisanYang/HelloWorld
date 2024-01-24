package hf.car.wifi.video.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Outline
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.MediaCodec
import android.media.MediaFormat
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
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
import hf.car.wifi.video.viewmodel.DeviceVideoViewModel
import java.io.IOException


/*
class BsdSettingActivity1 :
    MBaseActivity<DeviceVideoViewModel, ActivityBsdSettingBinding>(ActivityBsdSettingBinding::inflate) {

    var mImageReader = ImageReader.newInstance(780, 540, ImageFormat.RGB_565, 2)
    private var ip: String? = ""
    private val port: Int = 4343
    private val channels = mutableListOf(0x01, 0x02, 0x04, 0x08, 0x10, 0x20)

    private var surfaceHolder: SurfaceHolder? = null
    private var mCodec: MediaCodec? = null
    private var mFrameIndex = 0

    private var isPlaying = false
    private var playChannel: Int = -1

    private val mAdapter = VideoChannelAdapter(mutableListOf())

    lateinit var mBitmap: Bitmap
    lateinit var mImageName: String
    lateinit var mMediaProjectionManager : MediaProjectionManager
    lateinit var mMediaProjection : MediaProjection
    lateinit var mVirtualDisplay : VirtualDisplay
     var mResultCode : Int = 0
    lateinit var mResultData : Intent
    val mScreenDensity = 3

    override fun initViewModel(): DeviceVideoViewModel =
        ViewModelProvider(this@BsdSettingActivity1)[DeviceVideoViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initView()
        initListener()
        loadData()
    }

    val REQUEST_MEDIA_PROJECTION = 102

    override fun onResume() {
        super.onResume()
        //截图
//        val bitmap =
//            ScreenNopUtil.takeScreenshot(this@BsdSettingActivity, R.id.ll_direction)
//        binding.ivSnap.setImageBitmap(bitmap)
         mMediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        startActivityForResult(
            mMediaProjectionManager.createScreenCaptureIntent(),
            REQUEST_MEDIA_PROJECTION);

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != RESULT_OK) {
                Log.d(TAG, "User cancelled")
                Toast.makeText(this, "User cancelled", Toast.LENGTH_SHORT).show()
                return
            }
            if (this == null) {
                return
            }
            Log.d(TAG, "Starting screen capture")
            mResultCode = resultCode
            if (data != null) {
                mResultData = data
            }
            setUpMediaProjection()
            setUpVirtualDisplay()
            //继续执行截图或者录屏操作
            // do somthing...
            startCapture()
        }
    }

    private fun setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData)
    }

    private fun setUpVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(
            "ScreenCapture",
            780, 540, mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mImageReader.getSurface(), null, null
        )
    }

    private fun startCapture() {
        mImageName = System.currentTimeMillis().toString() + ".png"
        Log.i(TAG, "image name is : $mImageName")
        val image: Image = mImageReader.acquireLatestImage()
        if (image == null) {
            Log.e(TAG, "image is null.")
            return
        }
        val width = image.width
        val height = image.height
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width
        mBitmap =
            Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
        mBitmap.copyPixelsFromBuffer(buffer)
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height)
        image.close()
        if (mBitmap != null) {
            // 保存或者显示...
        }
    }




    */
/**
     * 播放视频
     *//*

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
                    mViewModel.openVideoInfo(realIp, port, channel)
                }
            } else {
                Toast.makeText(this@BsdSettingActivity1, "ip获取失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    */
/**
     * 创建编码器
     *//*

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

    */
/**
     * 播放视频流
     *//*

    private fun decodeUpdate(data: ByteArray, length: Int) {
        try {
            mCodec?.let {
                //等待的时间（毫秒），-1表示一直等，0表示不等。
                val inputBufferIndex = it.dequeueInputBuffer(0)
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
                    outputBufferIndex = it.dequeueOutputBuffer(bufferInfo, 0)
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Log.e(TAG, "decodeUpdate t:$t, message:${t.message}")
        }
    }

    */
/**
     * 获取系统信息
     *//*

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


    */
/**
     * 设置SurfaceView圆角
     *//*

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

    }

    override fun initListener() {
        super.initListener()

        binding.tvLeft.setOnClickListener {
            //获取DMS参数
            mViewModel.getDmsData()
        }
        binding.tvRight.setOnClickListener {
            mViewModel.setDmsData()
        }
        binding.tvTop.setOnClickListener { }
        binding.tvBottom.setOnClickListener { }
        binding.tvGetConfig.setOnClickListener {
            mViewModel.loadDeviceBsd()
        }

        mViewModel.sysInfoResponse.observe(this@BsdSettingActivity1) {
            if (0 == it.code) {
                it.data?.let { model ->
                    updateSysInfo(model)
                }
            } else {
                Toast.makeText(this@BsdSettingActivity1, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        mViewModel.playVideoResponse.observe(this@BsdSettingActivity1) {
            Log.d(TAG, "接收到的视频数据长度,length:${it.size}")
            decodeUpdate(it, it.size)
        }

        mViewModel.startVideoResponse.observe(this@BsdSettingActivity1) {
            if (0 != it.code) {
                Toast.makeText(this@BsdSettingActivity1, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        mViewModel.stopVideoResponse.observe(this@BsdSettingActivity1) {
            if (0 == it.code) {
                if (!TextUtils.isEmpty(ip)) {
                    it.data?.let { channel ->
                        mViewModel.startServer(ip!!, port)
                        isPlaying = true
                        playChannel = channel
                        mViewModel.openVideoInfo(ip!!, port, channel)
                    }
                } else {
                    Toast.makeText(this@BsdSettingActivity1, it.message, Toast.LENGTH_SHORT).show()
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
    }

    override fun loadData() {
        super.loadData()

        mViewModel.loadSysInfo()
        mViewModel.loadDeviceInfo()
        mViewModel.loadDeviceStatus()
        mViewModel.loadDeviceBsd()
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
}*/

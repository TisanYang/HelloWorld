package hf.car.wifi.video.processor

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.SoundEffectConstants
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.niklaus.mvvm.utils.BitmapUtil
import hf.car.wifi.video.R
import hf.car.wifi.video.callback.CameraCallback
import hf.car.wifi.video.constant.CameraConstant
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 图像管理类
 */
class PreviewViewController(
    private val mContext: Context,
    private val mLifecycleOwner: LifecycleOwner,
    private val mPreviewView: PreviewView
) {

    private val mCameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private var mCameraMode: Int = CameraConstant.IMAGE_CAPTURE

    //默认后置摄像头
    private var mCameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private lateinit var mCamera: Camera

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var imageAnalysis: ImageAnalysis? = null

    private var mRecording: Recording? = null

    private var mCameraCallback: CameraCallback? = null

    private var needShowTap: Boolean = true

    init {
        initCamera()

        mPreviewView.setOnTouchListener { view, event ->
            if (needShowTap) {
                val action = FocusMeteringAction.Builder(
                    mPreviewView.meteringPointFactory.createPoint(event.x, event.y)
                ).build()
                showTapView(event.x.toInt(), event.y.toInt())
                mCamera.cameraControl.startFocusAndMetering(action)
                view.performClick()
            }
            true
        }
    }

    /**
     * 图像捕捉
     */
    fun takePhoto() {
        //使用当前时间来命名
        val imgName = "IMG_" + SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.CHINA
        ).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imgName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TikTok")
            }
        }
        //metadata-判断当前是前置or后置（处理结果镜像问题）
        val metadata = ImageCapture.Metadata()
        metadata.isReversedHorizontal = CameraSelector.DEFAULT_BACK_CAMERA != mCameraSelector
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            mContext.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).setMetadata(metadata).build()
        // TODO: 后续留意一下 OnImageSavedCallback 和 OnImageCapturedCallback的区别
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(mContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val imagePath = outputFileResults.savedUri
                    Log.d(TAG, "onImageSaved:$imagePath")
                    mCameraCallback?.onPhotoCatch(CameraConstant.PHOTO_TAKE_SUCCESS, imagePath, "")
                }

                override fun onError(exception: ImageCaptureException) {
                    val errorCode = exception.message
                    Log.e(TAG, "imageCapture failed:$errorCode", exception)
                    mCameraCallback?.onPhotoCatch(
                        CameraConstant.PHOTO_TAKE_FAIL,
                        null,
                        errorCode
                    )
                }
            })
    }

    /**
     * 视频录制
     */
    fun recordVideo() {
        if (PackageManager.PERMISSION_DENIED != ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.RECORD_AUDIO
            )
        ) {
            mCameraCallback?.onVideoRecord(
                CameraConstant.VIDEO_RECORD_ERROR, null, -1
            )
            return
        }
        if (null != mRecording) {
            mRecording!!.stop()
            mRecording = null
            return
        }
        val videoName = "VID_" + SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.CHINA
        ).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, videoName)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/TikTok")
            }
        }
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(mContext.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        mRecording =
            videoCapture!!.output.prepareRecording(mContext, mediaStoreOutputOptions)
                .withAudioEnabled() //包括录制声音
                .start(ContextCompat.getMainExecutor(mContext)) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            //开始录像
                            mCameraCallback?.onVideoRecord(
                                CameraConstant.VIDEO_RECORD_START,
                                null,
                                0
                            )
                        }
                        is VideoRecordEvent.Finalize -> {
                            //结束录像
                            if (!recordEvent.hasError()) {
                                //判断录像是否有错
                                val videoPath = recordEvent.outputResults.outputUri
                                Log.d(TAG, "视频已保存至:$videoPath")
                                mCameraCallback?.onVideoRecord(
                                    CameraConstant.VIDEO_RECORD_END,
                                    videoPath,
                                    0
                                )
                            } else {
                                mRecording?.close()
                                mRecording = null
                                val errorCode = recordEvent.error
                                Log.e(TAG, "videoCapture error:$errorCode")
                                mCameraCallback?.onVideoRecord(
                                    CameraConstant.VIDEO_RECORD_ERROR,
                                    null,
                                    errorCode
                                )
                            }
                        }
                    }
                }
    }

    /**
     * 切换镜头
     */
    fun switchCamera() {
        if (null != mRecording) {
            mRecording!!.stop()
            mRecording = null
        }
        mCameraSelector = if (CameraSelector.DEFAULT_BACK_CAMERA == mCameraSelector) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        initCamera()
    }

    /**
     * 对焦
     */
    private fun showTapView(x: Int, y: Int) {
        val popupWindow =
            PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val imageView = ImageView(mContext)
        imageView.setImageResource(R.drawable.camera_top)
        popupWindow.contentView = imageView
        popupWindow.showAsDropDown(mPreviewView, x, y)
        mPreviewView.postDelayed({ popupWindow.dismiss() }, 600)
        mPreviewView.playSoundEffect(SoundEffectConstants.CLICK)
    }

    /**
     * 初始化相机参数
     */
    private fun initCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(mContext)

        //给cameraProviderFuture添加监听
        cameraProviderFuture.addListener({
            //获取相机信息
            val cameraProvider = cameraProviderFuture.get()
            //viewFinder设置预览画面
            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(mPreviewView.surfaceProvider) }
            imageCapture = ImageCapture.Builder().setTargetResolution(Size(600, 600)).build()
            videoCapture = VideoCapture.withOutput(
                Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HD)).build()
            )
            imageAnalysis = ImageAnalysis.Builder().setTargetResolution(Size(1920, 1080))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
            imageAnalysis!!.setAnalyzer(ContextCompat.getMainExecutor(mContext)) { imageProxy ->
                // 在这里处理图片的解析，比如解析成二维码之类的
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()
                val image = imageProxy.image
                //这里可以留意camerax自带的ImageUtil里面关于转换的方法
                val bitmap = BitmapUtil.imageToBitmap(image)
                val rotateBitmap = BitmapUtil.rotateBitmap(bitmap, rotationDegrees)
                mCameraCallback?.onAnalysisImage(rotateBitmap)
                imageProxy.close()
            }

            //先解除再绑定生命周期
            cameraProvider.unbindAll()
            cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
            cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
            //Bind use cases to camera
            when (mCameraMode) {
                CameraConstant.IMAGE_CAPTURE -> {
                    mCamera = cameraProvider.bindToLifecycle(
                        mLifecycleOwner,
                        mCameraSelector,
                        preview,
                        imageCapture
                    )
                }

                CameraConstant.VIDEO_CAPTURE -> {
                    mCamera = cameraProvider.bindToLifecycle(
                        mLifecycleOwner,
                        mCameraSelector,
                        preview,
                        videoCapture
                    )
                }

                CameraConstant.IMAGE_ANALYSIS -> {
                    mCamera = cameraProvider.bindToLifecycle(
                        mLifecycleOwner,
                        mCameraSelector,
                        preview,
                        imageAnalysis
                    )
                }
            }

            feature()
        }, ContextCompat.getMainExecutor(mContext))
    }

    private fun feature() {
        //配置常用的相机功能
        val cameraControl: CameraControl = mCamera.cameraControl
        //查询这些常用相机功能状态
        val cameraInfo: CameraInfo = mCamera.cameraInfo
        //变焦（0到1.0之间线性变焦）
        //cameraControl.setZoomRatio(0.5f)
        //开启关闭手电筒
        //cameraControl.enableTorch(true)
    }

    /**
     * 设置相机模式
     */
    fun setCameraMode(mode: Int) {
        mCameraMode = mode
        initCamera()
    }

    /**
     * 获取相机模式
     */
    fun getCameraMode(): Int {
        return mCameraMode
    }

    /**
     * 设置回调
     */
    fun setCameraCallBack(callBack: CameraCallback) {
        mCameraCallback = callBack
    }

    fun release() {
        mCameraExecutor.shutdown()
    }

    companion object {
        private const val TAG: String = "PreviewViewController"
    }
}
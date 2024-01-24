package hf.car.wifi.video.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Outline
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.lifecycle.ViewModelProvider
import hf.car.wifi.video.base.EmptyViewModel
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.callback.CameraCallback
import hf.car.wifi.video.constant.AppConstant
import hf.car.wifi.video.constant.CameraConstant
import hf.car.wifi.video.databinding.ActivityImageCameraBinding
import hf.car.wifi.video.processor.PreviewViewController
import hf.car.wifi.video.utils.FileUtils

class ImageCameraActivity :
    MBaseActivity<EmptyViewModel, ActivityImageCameraBinding>(ActivityImageCameraBinding::inflate) {

    private lateinit var mPreviewViewController: PreviewViewController

    private var uid: Int = 0

    override fun initViewModel(): EmptyViewModel =
        ViewModelProvider(this@ImageCameraActivity)[EmptyViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()
        initView()
        initListener()
    }

    override fun initData() {
        super.initData()

        if (intent.hasExtra(AppConstant.UID)) {
            uid = intent.getIntExtra(AppConstant.UID, 0)
        }
    }

    override fun initView() {
        super.initView()

        binding.previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
        val outline = Outline().also {
            it.setRoundRect(
                Rect(0, 0, binding.previewView.width, binding.previewView.height),
                20 * resources.displayMetrics.density
            )
        }
        val outlineProvider = binding.previewView.outlineProvider
        outlineProvider.getOutline(binding.previewView, outline)
        binding.previewView.outlineProvider = outlineProvider
        binding.previewView.clipToOutline = true

        mPreviewViewController = PreviewViewController(this, this, binding.previewView)
        mPreviewViewController.setCameraCallBack(mCameraCallback)
    }

    override fun initListener() {
        super.initListener()

        binding.topBar.setBackClickListener { finish() }

        binding.btnStart.setOnClickListener {
            mPreviewViewController.takePhoto()
        }
    }

    private val mCameraCallback = object : CameraCallback {
        override fun onAnalysisImage(bitmap: Bitmap?) {
        }

        override fun onPhotoCatch(status: Int, photoPath: Uri?, errorMessage: String?) {
            if (CameraConstant.PHOTO_TAKE_SUCCESS == status) {
                photoPath?.let {
                    val path = FileUtils.getPath(applicationContext, it)
                    val intent = Intent(this@ImageCameraActivity, UpdateBitmapActivity::class.java)
                    intent.putExtra(AppConstant.PICTURE, path)
                    intent.putExtra(AppConstant.UID, uid)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this@ImageCameraActivity, "", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onVideoRecord(status: Int, videoPath: Uri?, errorCode: Int) {
        }
    }
}
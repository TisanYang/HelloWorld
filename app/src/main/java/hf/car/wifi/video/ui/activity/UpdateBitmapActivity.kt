package hf.car.wifi.video.ui.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.niklaus.mvvm.utils.BitmapUtil
import com.niklaus.mvvm.utils.PreferencesUtil
import hf.car.wifi.video.R
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.constant.AppConstant
import hf.car.wifi.video.databinding.ActivityUpdateBitmapBinding
import hf.car.wifi.video.utils.ImageLoader
import hf.car.wifi.video.viewmodel.DeviceFaceViewModel
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.concurrent.thread

class UpdateBitmapActivity :
    MBaseActivity<DeviceFaceViewModel, ActivityUpdateBitmapBinding>(ActivityUpdateBitmapBinding::inflate) {

    private var filePath: String = ""

    private var uid: Int = 0

    override fun initViewModel(): DeviceFaceViewModel =
        ViewModelProvider(this@UpdateBitmapActivity)[DeviceFaceViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()
        initView()
        initListener()
    }

    private fun updateFace() {
        if (TextUtils.isEmpty(filePath)) {
            val text = getString(R.string.tv_load_pic_fail)
            Toast.makeText(this@UpdateBitmapActivity, text, Toast.LENGTH_SHORT).show()
            return
        }

        val name = binding.edName.text.toString().trim()
        if (TextUtils.isEmpty(name)) {
            val text = getString(R.string.tv_please_ed_name)
            Toast.makeText(this@UpdateBitmapActivity, text, Toast.LENGTH_SHORT).show()
            return
        }

        if (name.length < 2 || name.length > 8) {
            val text = getString(R.string.tv_please_ed_nickname)
            Toast.makeText(this@UpdateBitmapActivity, text, Toast.LENGTH_SHORT).show()
            return
        }

        binding.tvResult.text = getString(R.string.tv_upload_face_ing)
        binding.btnUpdate.isEnabled = false

        try {
            val rotate = BitmapUtil.getPictureDegree(filePath)
            Log.d(TAG, "-------rotate:${rotate}")
            val bitmap = if (0 != rotate) {
                BitmapUtil.rotateBitmap(BitmapFactory.decodeFile(filePath), rotate)
            } else {
                BitmapFactory.decodeFile(filePath)
            }

            thread {
                val realBitmapArray =
                    BitmapUtil.compressByQualityByteArray(bitmap, 180 * 1024, true)
                mViewModel.updateFace(uid, name, realBitmapArray)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun initData() {
        super.initData()

        if (intent.hasExtra(AppConstant.UID)) {
            uid = intent.getIntExtra(AppConstant.UID, 0)
            Log.d(TAG, "uid:$uid")
        }

        if (intent.hasExtra(AppConstant.PICTURE)) {
            filePath = intent.getStringExtra(AppConstant.PICTURE).toString()
            Log.d(TAG, "filePath:$filePath")
        }
    }

    override fun initView() {
        super.initView()

        if (!TextUtils.isEmpty(filePath)) {
            ImageLoader.loadImage(this@UpdateBitmapActivity, filePath, binding.imgResult)
            binding.tvResult.text = getString(R.string.tv_plan_upload_face)
        } else {
            binding.tvResult.text = getString(R.string.tv_load_data_error)
        }
    }

    override fun initListener() {
        super.initListener()

        mViewModel.updateFaceResponse.observe(this@UpdateBitmapActivity) {
            binding.btnUpdate.isEnabled = true
            if (0 == it.code) {
                binding.tvResult.text = getString(R.string.tv_upload_face_success)
                binding.imgResult.setImageResource(R.drawable.icon_success)
                binding.tvPleaseName.visibility = View.GONE
                binding.edName.visibility = View.GONE
                binding.btnUpdate.visibility = View.GONE
                PreferencesUtil.instance.putBoolean(AppConstant.PERSON_REFRESH, true)
            } else {
                binding.tvResult.text = getString(R.string.tv_upload_face_fail)
                binding.imgResult.setImageResource(R.drawable.icon_fail)
                binding.btnUpdate.text = getString(R.string.tv_restart_upload)
                binding.btnUpdate.visibility = View.VISIBLE
            }
        }

        binding.topBar.setBackClickListener { finish() }

        binding.btnUpdate.setOnClickListener { updateFace() }

        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()

        mViewModel.closeUpdateFace()
    }
}
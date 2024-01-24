package hf.car.wifi.video.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.niklaus.mvvm.utils.BitmapUtil
import hf.car.wifi.video.R
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.constant.AppConstant
import hf.car.wifi.video.databinding.ActivityModifyFaceBinding
import hf.car.wifi.video.model.PersonFace
import hf.car.wifi.video.viewmodel.DeviceFaceViewModel

class ModifyFaceActivity :
    MBaseActivity<DeviceFaceViewModel, ActivityModifyFaceBinding>(ActivityModifyFaceBinding::inflate) {

    private var uid: Int = -1
    private var userName: String = "佚名"

    override fun initViewModel(): DeviceFaceViewModel =
        ViewModelProvider(this@ModifyFaceActivity)[DeviceFaceViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()
        initView()
        initListener()
        loadData()
    }

    private fun updatePersonFace(personFace: PersonFace) {
        Log.d(TAG, "model face size:${personFace.face.size}")
        try {
            val faceBitmap = BitmapUtil.byteArrayToBitmap(personFace.face)
            if (null != faceBitmap) {
                binding.imgFace.setImageBitmap(faceBitmap)
            } else {
                Log.e(TAG, "faceBitmap is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun initData() {
        super.initData()

        if (intent.hasExtra("uid")) {
            uid = intent.getIntExtra("uid", -1)
        } else {
            Toast.makeText(this@ModifyFaceActivity, "获取用户信息失败", Toast.LENGTH_SHORT).show()
        }

        if (intent.hasExtra(AppConstant.USER_NAME)) {
            userName = intent.getStringExtra(AppConstant.USER_NAME).toString()
        }
    }

    override fun initView() {
        super.initView()

        binding.tvName.text = getString(R.string.tv_person_name_s, userName)
        binding.tvId.text = getString(R.string.tv_person_number_s, uid.toString())
    }

    override fun initListener() {
        super.initListener()

        mViewModel.personFaceResponse.observe(this@ModifyFaceActivity) {
            updatePersonFace(it)
        }

        binding.topBar.setBackClickListener { finish() }
    }

    override fun loadData() {
        super.loadData()

        if (uid >= 0) {
            mViewModel.loadPersonFaceByID(uid)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mViewModel.closePersonFaceByID()
    }
}
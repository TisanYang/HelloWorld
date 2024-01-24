package hf.car.wifi.video.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.callback.ItemChooseCallback
import hf.car.wifi.video.constant.AppConstant
import hf.car.wifi.video.databinding.ActivityImageLocalBinding
import hf.car.wifi.video.ui.adapter.LocalImageAdapter
import hf.car.wifi.video.ui.custom.SpacesItemDecoration
import hf.car.wifi.video.viewmodel.LocalImageViewModel

class ImageLocalActivity :
    MBaseActivity<LocalImageViewModel, ActivityImageLocalBinding>(ActivityImageLocalBinding::inflate) {

    private var uid: Int = 0

    private var choosePos: Int = -1
    private val mAdapter = LocalImageAdapter(mutableListOf())

    override fun initViewModel(): LocalImageViewModel =
        ViewModelProvider(
            this@ImageLocalActivity,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[LocalImageViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()
        initView()
        initListener()
        loadData()
    }

    override fun initData() {
        super.initData()

        if (intent.hasExtra(AppConstant.UID)) {
            uid = intent.getIntExtra(AppConstant.UID, 0)
        }
    }

    override fun initView() {
        super.initView()
        val lm = GridLayoutManager(this@ImageLocalActivity, 3)
        binding.recyclerView.layoutManager = lm
        binding.recyclerView.addItemDecoration(SpacesItemDecoration(3))
        binding.recyclerView.setPadding(0, 0, 0, 0)
        binding.recyclerView.adapter = mAdapter
    }

    override fun initListener() {
        super.initListener()

        mViewModel.localImageResponse.observe(this@ImageLocalActivity) {
            mAdapter.setData(it)
        }

        binding.topBar.setBackClickListener { finish() }

        mAdapter.setItemChooseCallback(object : ItemChooseCallback {
            override fun onChoose(id: Int, type: Int) {
                choosePos = id
                mAdapter.setChoosePos(choosePos)
            }
        })

        binding.btnSubmit.setOnClickListener {
            try {
                if (choosePos >= 0) {
                    val model = mAdapter.getData()[choosePos]
                    val intent = Intent(this@ImageLocalActivity, UpdateBitmapActivity::class.java)
                    intent.putExtra(AppConstant.PICTURE, model.filePath)
                    intent.putExtra(AppConstant.UID, uid)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ImageLocalActivity, "请选择图片", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun loadData() {
        super.loadData()

        mViewModel.loadLocalImage()
    }
}
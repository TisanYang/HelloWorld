package hf.car.wifi.video.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.databinding.ActivityUpgradeBinding
import hf.car.wifi.video.utils.UriUtil
import hf.car.wifi.video.viewmodel.UpgradeViewModel
import me.rosuh.filepicker.config.FilePickerManager


class UpgradeActivity :
    MBaseActivity<UpgradeViewModel, ActivityUpgradeBinding>(ActivityUpgradeBinding::inflate) {

    var path = ""

    override fun initViewModel(): UpgradeViewModel =
        ViewModelProvider(this@UpgradeActivity)[UpgradeViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initView()
        initListener()
        loadData()
    }


    override fun initView() {
        super.initView()
        binding.topBar.setBackClickListener { finish() }
    }

    override fun initListener() {
        super.initListener()
        binding.tvUpgrade.setOnClickListener {
            //mViewModel.loadUpgradeInfo()
            //选择文件
            choiceFile()
        }
        binding.tvUpgradeInfo.setOnClickListener {
            mViewModel.loadUpgradeInfo()
        }
        mViewModel.versionContent.observe(this@UpgradeActivity) {
            binding.tvContent.text = it.data
        }

        binding.tvBegainUpgrade.setOnClickListener {
            if (TextUtils.isEmpty(path)){
                Toast.makeText(this,"请先选择升级文件",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mViewModel.uadateSystem(FilePickerManager.obtainData()[0],path)
        }


    }

    private fun choiceFile() {

        FilePickerManager
            .from(this@UpgradeActivity)
            .forResult(FilePickerManager.REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            FilePickerManager.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val list = FilePickerManager.obtainData()
                    binding.tvFileName.text = FilePickerManager.obtainData()[0]

                    val split = FilePickerManager.obtainData()[0].split("/")
                    path = split[split.size - 1]
                    //mViewModel.uadateSystem(FilePickerManager.obtainData()[0],path)
                } else {
                    Toast.makeText(
                        this@UpgradeActivity,
                        "You didn't choose anything~",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun loadData() {
        super.loadData()
    }


    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
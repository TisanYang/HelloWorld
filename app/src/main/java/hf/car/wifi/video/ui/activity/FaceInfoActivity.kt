package hf.car.wifi.video.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.niklaus.mvvm.utils.PreferencesUtil
import hf.car.wifi.video.R
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.callback.DialogClickCallback
import hf.car.wifi.video.callback.ItemChooseCallback
import hf.car.wifi.video.constant.AppConstant
import hf.car.wifi.video.databinding.ActivityFaceInfoBinding
import hf.car.wifi.video.model.PersonModel
import hf.car.wifi.video.ui.adapter.FaceInfoAdapter
import hf.car.wifi.video.ui.dialog.ChooseImageDialog
import hf.car.wifi.video.ui.dialog.ConfirmDialog
import hf.car.wifi.video.viewmodel.DeviceFaceViewModel

class FaceInfoActivity :
    MBaseActivity<DeviceFaceViewModel, ActivityFaceInfoBinding>(ActivityFaceInfoBinding::inflate) {

    private val mAdapter: FaceInfoAdapter = FaceInfoAdapter(mutableListOf())

    private var isFirst = true

    override fun initViewModel(): DeviceFaceViewModel =
        ViewModelProvider(this@FaceInfoActivity)[DeviceFaceViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initListener()
        loadData()
    }

    /**
     * 选择弹窗
     */
    private fun showChooseDialog() {
        val dialog = ChooseImageDialog()
        dialog.setDialogClickCallback(object : DialogClickCallback {
            override fun onClick(type: Int) {
                val id = loadUpdateUId(mAdapter.loadAllUid())
                if (type == 1) {
                    val intent = Intent(this@FaceInfoActivity, ImageCameraActivity::class.java)
                    intent.putExtra(AppConstant.UID, id)
                    startActivity(intent)
                } else if (type == 2) {
                    val intent = Intent(this@FaceInfoActivity, ImageLocalActivity::class.java)
                    intent.putExtra(AppConstant.UID, id)
                    startActivity(intent)
                }
            }
        })
        dialog.show(supportFragmentManager, "chooseDialog")
    }

    /**
     * 删除确认弹窗
     */
    private fun showDeleteDialog(id: Int) {
        val dialog = ConfirmDialog()
        dialog.setDialogClickCallback(object : DialogClickCallback {
            override fun onClick(type: Int) {
                mViewModel.deleteFace(id)
                dialog.dismiss()
            }
        })
        dialog.show(supportFragmentManager, "deleteDialog")
    }

    /**
     * 更新所有人员信息
     */
    private fun updatePerson(list: MutableList<PersonModel>) {
        Log.d(TAG, "设备人员人数:${list.size}")
        if (list.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
            mAdapter.setData(list)
        }
    }

    private fun loadUpdateUId(list: MutableList<Int>): Int {
        for (i in 0 until 50) {
            if (!list.contains(i)) {
                return i
            }
        }
        return 0
    }

    override fun initView() {
        super.initView()
        val lm = LinearLayoutManager(this@FaceInfoActivity)
        lm.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerView.layoutManager = lm
        binding.recyclerView.adapter = mAdapter

        initSwipeRefreshView()
    }

    override fun initListener() {
        super.initListener()

        mViewModel.devicePersonResponse.observe(this@FaceInfoActivity) {
            if (binding.swipeRefresh.isRefreshing) {
                binding.swipeRefresh.isRefreshing = false
            }
            if (0 == it.code) {
                PreferencesUtil.instance.putBoolean(AppConstant.PERSON_REFRESH, false)
                it.data?.let { list ->
                    updatePerson(list)
                }
            } else {
                Toast.makeText(this@FaceInfoActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        mViewModel.deleteFaceResponse.observe(this@FaceInfoActivity) {
            if (0 == it.code) {
                Toast.makeText(this@FaceInfoActivity, "删除成功", Toast.LENGTH_SHORT).show()
                mViewModel.loadDeviceFaceInfo()
            } else {
                Toast.makeText(this@FaceInfoActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.topBar.setBackClickListener { finish() }

        binding.btnAddFace.setOnClickListener {
            val total = mAdapter.itemCount
            Log.d(TAG, "当前人员数:$total")
            if (total >= 30) {
                Toast.makeText(
                    this@FaceInfoActivity, getString(R.string.tv_person_total_error),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showChooseDialog()
            }
        }

        mAdapter.setItemChooseCallback(object : ItemChooseCallback {
            override fun onChoose(id: Int, type: Int) {
                showDeleteDialog(id)
            }
        })
    }

    override fun loadData() {
        super.loadData()

        binding.swipeRefresh.isRefreshing = true
        mViewModel.loadDeviceFaceInfo()
    }

    private fun initSwipeRefreshView() {
        //设置下拉进度的背景颜色，默认就是白色的
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(this, R.color.color_3A7AF9)
        )

        //设置下拉进度的主题颜色
        binding.swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.white)
        )

        binding.swipeRefresh.setOnRefreshListener { mViewModel.loadDeviceFaceInfo() }
    }

    override fun onResume() {
        super.onResume()

        val refresh = PreferencesUtil.instance.getBoolean(AppConstant.PERSON_REFRESH, false)
        if (refresh && !isFirst) {
            mViewModel.loadDeviceFaceInfo()
        }
        isFirst = false
    }

    override fun onDestroy() {
        super.onDestroy()

        mViewModel.closeDeviceFaceInfo()
    }
}
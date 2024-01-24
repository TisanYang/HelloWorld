package hf.car.wifi.video.ui.activity

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.niklaus.mvvm.utils.NetUtils
import hf.car.wifi.video.R
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.databinding.ActivityDmsSettingBinding
import hf.car.wifi.video.ui.adapter.DmsAdapter
import hf.car.wifi.video.ui.adapter.DmsDataBean
import hf.car.wifi.video.ui.adapter.DmsDataItemBean
import hf.car.wifi.video.ui.adapter.DmsSaveListener
import hf.car.wifi.video.viewmodel.DeviceVideoViewModel


class DmsSettingActivity :
    MBaseActivity<DeviceVideoViewModel, ActivityDmsSettingBinding>(ActivityDmsSettingBinding::inflate),
    DmsSaveListener {

    private var ip: String? = ""


    private var playChannel: Int = -1

    private val mAdapter: DmsAdapter by lazy { DmsAdapter() }
    val list: MutableList<DmsDataBean> = ArrayList()

    override fun initViewModel(): DeviceVideoViewModel =
        ViewModelProvider(this@DmsSettingActivity)[DeviceVideoViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initView()
        initListener()
        initList()
        loadData()
    }

    private fun initList() {

        for (item in 0..7) {
            //这里创建了7次，对应七个不同的指标
            list.add(
                DmsDataBean(
                    "算法名称：" + if (item == 0) "疲劳驾驶识别" else if (item == 1) "接打电话识别" else if (item == 2) "抽烟识别"
                    else if (item == 3) "分神识别" else if (item == 4) "离岗识别"
                    else if (item == 5) "遮挡摄像头识别" else if (item == 6) "安全帽" else "红外阻断",
                    false, (item + 1).toByte(),
                    DmsDataItemBean(
                        item,
                        "灵敏度(160ms):",
                        2,
                        0,
                        0,
                        100
                    ),
                    DmsDataItemBean(
                        item,
                        "间隔时间(S):",
                        3,
                        0,
                        0,
                        1200
                    ),
                    DmsDataItemBean(
                        item,
                        "vol音量:",
                        6,
                        0,
                        0,
                        60
                    ),
                    DmsDataItemBean(
                        item,
                        "一级警报速度(KM/S)",
                        4,
                        0,
                        0,
                        100
                    ),
                    DmsDataItemBean(
                        item,
                        "二级警报速度(KM/s)",
                        5,
                        0,
                        0,
                        100
                    ),
                )
            )
            mAdapter.add(list[item])
        }

    }


    override fun initView() {
        super.initView()

        ip = NetUtils.getIPAddress(applicationContext)

        val lm = LinearLayoutManager(this@DmsSettingActivity)
        lm.orientation = LinearLayoutManager.VERTICAL
        binding.recycler.layoutManager = lm
        binding.recycler.adapter = mAdapter
        mAdapter.addOnItemChildClickListener(R.id.tv_save) { adapter, view, position ->
            //TODO 点击保存的回调
        }
    }

    override fun initListener() {
        super.initListener()
        mViewModel.deviceInfoResponse.observe(this@DmsSettingActivity) {
            if (0 == it.code) {
                it.data?.let { model ->
                }
            } else {
                Toast.makeText(this@DmsSettingActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        mViewModel.deviceStatusResponse.observe(this@DmsSettingActivity) {
            if (0 == it.code) {
                it.data?.let { model ->
                }
            } else {
                Toast.makeText(this@DmsSettingActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        mViewModel.dmsInfoResponse.observe(this@DmsSettingActivity) {
            val dataList = it.data
            if (dataList != null) {
                for (element in dataList) {
                    for (item in list) {
                        if (item.uTypeId == element.uTypeId){
                            when(element.uParamId){
                                1.toByte() ->{
                                    val tag = element.uVal.toString().toInt()
                                    item.isOpen = tag != 0
                                }
                                2.toByte() -> {
                                    item.item1.currentProgress = element.uVal.toString().toInt()
                                }
                                3.toByte() -> {
                                    item.item2.currentProgress = (element.uVal * 5u ).toInt()
                                }
                                4.toByte() -> {
                                    item.item4.currentProgress = element.uVal.toString().toInt()
                                }
                                5.toByte() -> {
                                    item.item5.currentProgress = element.uVal.toString().toInt()
                                }
                                6.toByte() -> {
                                    item.item3.currentProgress = element.uVal.toString().toInt()
                                }
                            }
                        }
                    }
                }
            }
            mAdapter.notifyDataSetChanged()
        }

        binding.topBar.setBackClickListener { finish() }

        mAdapter.setDmsListener(this@DmsSettingActivity)

    }

    override fun loadData() {
        super.loadData()
        //具体要获取的是 2,3,4,5,6这几个值
        val tags = byteArrayOf(1,2, 3, 4, 5, 6)
        for (element in tags) {
            //第三个是具体指标，这里第二个值是0.是所有，所以不用传具体类型
            val bytes = byteArrayOf(0x65, 0, element, 0)
            mViewModel.getDmsData(bytes)
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

    override fun saveData(item: DmsDataBean?) {

        if (!item!!.isOpen){
            Toast.makeText(this@DmsSettingActivity, "请先打开设置开关", Toast.LENGTH_SHORT).show()
            return
        }
        val bytes1 = byteArrayOf(0x65, item!!.uTypeId, item.item1.uParamId, item.item1.currentProgress.toByte())
        val bytes2 = byteArrayOf(0x65, item.uTypeId, item.item2.uParamId, (item.item2.currentProgress/5).toByte())
        val bytes3 = byteArrayOf(0x65, item.uTypeId, item.item3.uParamId, item.item3.currentProgress.toByte())
        val bytes4 = byteArrayOf(0x65, item.uTypeId, item.item4.uParamId, item.item4.currentProgress.toByte())
        val bytes5 = byteArrayOf(0x65, item.uTypeId, item.item5.uParamId, item.item5.currentProgress.toByte())

        mViewModel.setDmsData(bytes1)
        mViewModel.setDmsData(bytes2)
        mViewModel.setDmsData(bytes3)
        mViewModel.setDmsData(bytes4)
        mViewModel.setDmsData(bytes5)
    }

    override fun switchData(item: DmsDataBean?, checked: Boolean) {
        var tag = if (checked) 1 else 0
        val bytes = byteArrayOf(0x65,item!!.uTypeId,1,tag.toByte())
        mViewModel.setDmsData(bytes)
    }
}
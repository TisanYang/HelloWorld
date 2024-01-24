package hf.car.wifi.video.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hf.car.wifi.video.base.MBaseAdapter
import hf.car.wifi.video.callback.ItemChooseCallback
import hf.car.wifi.video.model.PersonModel
import hf.car.wifi.video.ui.holder.FaceViewHolder
import com.niklaus.mvvm.utils.ByteUtil

class FaceInfoAdapter(list: MutableList<PersonModel>) : MBaseAdapter<PersonModel>(list) {

    private var itemChooseCallback: ItemChooseCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FaceViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FaceViewHolder) {
            val model = getData()[position]
            holder.bindTo(model, itemChooseCallback)
        }
    }

    /**
     * item点击设置
     */
    fun setItemChooseCallback(callback: ItemChooseCallback) {
        this.itemChooseCallback = callback
    }

    /**
     * 获取当前所有编号
     */
    fun loadAllUid(): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (mode in getData()) {
            val uIdx = ByteArray(1)
            uIdx[0] = mode.person[0]
            val id = ByteUtil.byteArrayToInt(uIdx)
            list.add(id)
        }
        return list
    }
}
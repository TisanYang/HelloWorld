package hf.car.wifi.video.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hf.car.wifi.video.base.MBaseAdapter
import hf.car.wifi.video.callback.ItemChooseCallback
import hf.car.wifi.video.model.PhoneImageModel
import hf.car.wifi.video.ui.holder.LocalImageViewHolder

class LocalImageAdapter(list: MutableList<PhoneImageModel>) : MBaseAdapter<PhoneImageModel>(list) {

    private var choosePos: Int = -1

    private var itemChooseCallback: ItemChooseCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LocalImageViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LocalImageViewHolder) {
            holder.bindTo(position, choosePos, getData()[position], itemChooseCallback)
        }
    }

    /**
     * item点击设置
     */
    fun setItemChooseCallback(callback: ItemChooseCallback) {
        this.itemChooseCallback = callback
    }

    fun setChoosePos(choosePos: Int) {
        this.choosePos = choosePos
        notifyDataSetChanged()
    }
}
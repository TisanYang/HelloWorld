package hf.car.wifi.video.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hf.car.wifi.video.base.MBaseAdapter
import hf.car.wifi.video.callback.ItemChooseCallback
import hf.car.wifi.video.model.VideoChannelModel
import hf.car.wifi.video.ui.holder.VideoChannelViewHolder

class VideoChannelAdapter(list: MutableList<VideoChannelModel>) :
    MBaseAdapter<VideoChannelModel>(list) {

    private var itemChooseCallback: ItemChooseCallback? = null

    private var choosePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoChannelViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VideoChannelViewHolder) {
            holder.bingTo(getData()[position], position, itemChooseCallback)
        }
    }

    /**
     * item点击设置
     */
    fun setItemChooseCallback(callback: ItemChooseCallback) {
        this.itemChooseCallback = callback
    }

    fun updateChooseItem(position: Int) {
        choosePos = position
        for (i in 0 until getData().size) {
            getData()[i].selector = i == choosePos
        }

        notifyDataSetChanged()
    }
}
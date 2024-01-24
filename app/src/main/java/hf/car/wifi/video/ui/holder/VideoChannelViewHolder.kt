package hf.car.wifi.video.ui.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hf.car.wifi.video.R
import hf.car.wifi.video.callback.ItemChooseCallback
import hf.car.wifi.video.databinding.ItemVideoChannelBinding
import hf.car.wifi.video.model.VideoChannelModel

class VideoChannelViewHolder(private val parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_video_channel, parent, false
    )
) {

    private val binding = ItemVideoChannelBinding.bind(itemView)

    fun bingTo(model: VideoChannelModel, position: Int, callback: ItemChooseCallback?) {
        binding.btnCh.text = model.name

        binding.btnCh.setOnClickListener { callback?.onChoose(position, 1) }

        binding.btnCh.isEnabled = !model.selector
    }
}
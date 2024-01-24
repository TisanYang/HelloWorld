package hf.car.wifi.video.ui.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hf.car.wifi.video.R
import hf.car.wifi.video.callback.ItemChooseCallback
import hf.car.wifi.video.databinding.ItemLocalImageBinding
import hf.car.wifi.video.model.PhoneImageModel
import hf.car.wifi.video.utils.ImageLoader

class LocalImageViewHolder(private val parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_local_image, parent, false
    )
) {

    private val binding = ItemLocalImageBinding.bind(itemView)

    fun bindTo(
        position: Int, choosePos: Int,
        model: PhoneImageModel, callback: ItemChooseCallback?
    ) {
        ImageLoader.loadImage(parent.context, model.filePath, binding.imgImage)
        if (position == choosePos) {
            binding.btnChoose.visibility = View.VISIBLE
        } else {
            binding.btnChoose.visibility = View.GONE
        }

        binding.imgImage.setOnClickListener { callback?.onChoose(position, 1) }
    }
}
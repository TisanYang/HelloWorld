package hf.car.wifi.video.ui.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import hf.car.wifi.video.R
import hf.car.wifi.video.model.DataReplayBean


class VideoReplayAdapter : BaseQuickAdapter<DataReplayBean, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_data_replay, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: DataReplayBean?) {

        val tvStart = holder.getView<TextView>(R.id.tv_start)
        val tvEnd = holder.getView<TextView>(R.id.tv_end)

        tvStart.text = position.toString()


    }

}

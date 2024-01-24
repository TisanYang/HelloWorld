package hf.car.wifi.video.ui.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import hf.car.wifi.video.R


class DmsItemListAdapter : BaseQuickAdapter<DmsDataItemBean, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.include_item_dms, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: DmsDataItemBean?) {
        holder.getView<TextView>(R.id.tv_title_include).text = item?.itemTitle
        val seekBar = holder.getView<SeekBar>(R.id.seek_bar)
        val textView = holder.getView<TextView>(R.id.tv_title_value)
        val tvMax = holder.getView<TextView>(R.id.tv_max)
        seekBar.max = item!!.maxProgress
        tvMax.text = item!!.maxProgress.toString()
        holder.getView<TextView>(R.id.tv_title_value).text = "" + item?.currentProgress
        seekBar.progress = item.currentProgress.toString().toInt()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.text = "$progress"

                if (item.uParamId == 3.toByte()) {
                    if (item.currentProgress <= 5) {
                        item.currentProgress = progress
                        seekBar?.progress = (progress / 5) * 5
                    } else {
                        item.currentProgress = (progress / 5) * 5
                        seekBar?.progress = (progress / 5) * 5
                    }
                } else {
                    item.currentProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

}

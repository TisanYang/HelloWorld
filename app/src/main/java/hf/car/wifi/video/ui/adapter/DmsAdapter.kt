package hf.car.wifi.video.ui.adapter

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import hf.car.wifi.video.R


data class DmsDataBean(
    val title: String,
    var isOpen: Boolean,
    val uTypeId: Byte,
    val item1: DmsDataItemBean,
    val item2: DmsDataItemBean,
    val item3: DmsDataItemBean,
    val item4: DmsDataItemBean,
    val item5: DmsDataItemBean
)

data class DmsDataItemBean(
    val id: Int,
    val itemTitle: String,
    val uParamId: Byte,
    var currentProgress: Int,
    var minProgress: Int = 0,
    var maxProgress: Int = 100,
)

class DmsAdapter : BaseQuickAdapter<DmsDataBean, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_dms, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: DmsDataBean?) {
        val recyclerView1 = holder.getView<RecyclerView>(R.id.recycler_view)
        val dmsItemListAdapter = DmsItemListAdapter()
        val linearLayoutManager: LinearLayoutManager =
            object : LinearLayoutManager(holder.itemView.context) {
                override fun canScrollVertically(): Boolean {
                    return false
                }

                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView1.layoutManager = linearLayoutManager
        recyclerView1.adapter = dmsItemListAdapter
        item?.item1?.let {
            dmsItemListAdapter.add(it)
        }
        item?.item2?.let {
            dmsItemListAdapter.add(it)
        }
        item?.item3?.let {
            dmsItemListAdapter.add(it)
        }
        item?.item4?.let {
            dmsItemListAdapter.add(it)
        }
        item?.item5?.let {
            dmsItemListAdapter.add(it)
        }
        holder.getView<TextView>(R.id.tv_title).text = item?.title
        holder.getView<TextView>(R.id.tv_save).setOnClickListener {
            Toast.makeText(holder.itemView.context, "设置${position + 1}", Toast.LENGTH_SHORT).show()
            Log.d(
                "dms:", "${item?.title}," +
                        "${item?.item1?.itemTitle}${item?.item1?.currentProgress}," +
                        "${item?.item2?.itemTitle}${item?.item2?.currentProgress}," +
                        "${item?.item3?.itemTitle}${item?.item3?.currentProgress}," +
                        "${item?.item4?.itemTitle}${item?.item4?.currentProgress}," +
                        "${item?.item5?.itemTitle}${item?.item5?.currentProgress},"
            )
            if (dmsSaveListener != null) {
                dmsSaveListener.saveData(item)
            }
        }

        val switch = holder.getView<Switch>(R.id.switch_view)
        switch.isChecked = item!!.isOpen
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            item.isOpen = isChecked
            dmsSaveListener.switchData(item, isChecked)
        }


    }

    fun setDmsListener(listener: DmsSaveListener) {
        dmsSaveListener = listener
    }

}

lateinit var dmsSaveListener: DmsSaveListener

interface DmsSaveListener {
    fun saveData(item: DmsDataBean?)
    fun switchData(item: DmsDataBean?, checked: Boolean)
}

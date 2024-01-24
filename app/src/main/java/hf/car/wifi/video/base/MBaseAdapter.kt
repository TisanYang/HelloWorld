package hf.car.wifi.video.base

import androidx.recyclerview.widget.RecyclerView

abstract class MBaseAdapter<T>(private var data: MutableList<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val TAG: String = javaClass.simpleName

    override fun getItemCount(): Int {
        return data.size
    }

    fun getData(): MutableList<T> {
        return data
    }

    /**
     * add item
     */
    fun addData(bean: T) {
        data.add(bean)
        notifyItemInserted(data.size - 1)
    }

    /**
     * add list
     */
    fun addData(list: MutableList<T>) {
        val positionStart = data.size
        data.addAll(list)
        notifyItemRangeInserted(positionStart - 1, list.size)
    }

    fun addFirstData(bean: T) {
        data.add(0, bean)
        notifyItemRangeChanged(0, data.size)
    }

    fun addFirstData(list: MutableList<T>) {
        data.addAll(0, list)
        notifyItemRangeChanged(0, data.size)
    }

    fun remove(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size - position)
    }

    /**
     * set list
     */
    fun setData(list: MutableList<T>) {
        data = list
        notifyDataSetChanged()
    }

    /**
     * 获取最后一条数据
     */
    fun getLastData(): T {
        return data[data.size - 1]
    }
}
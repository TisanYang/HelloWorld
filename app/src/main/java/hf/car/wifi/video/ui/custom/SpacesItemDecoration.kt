package hf.car.wifi.video.ui.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        if (parent.layoutManager is GridLayoutManager) {
            outRect.left = space
            outRect.top = space
            outRect.right = space
        }
    }
}
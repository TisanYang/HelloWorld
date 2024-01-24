package hf.car.wifi.video.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineView : View {

    lateinit var mPaintA :Paint

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mPaintA = Paint()
        mPaintA.color = Color.RED
        mPaintA.strokeWidth = 3f
        mPaintA.style = Paint.Style.STROKE
        mPaintA.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawLine(450f,60f,920f,60f,mPaintA)
        canvas.drawLine(400f,180f, 970f,180f,mPaintA)
        canvas.drawLine(350f,380f, 1020f,380f,mPaintA)
        canvas.drawLine(300f,700f, 1070f,700f,mPaintA)

    }
}
package hf.car.wifi.video.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import hf.car.wifi.video.model.ResponseData
import hf.car.wifi.video.model.getBsdDataModel
import hf.car.wifi.video.viewmodel.DeviceVideoViewModel
import kotlin.math.abs

class BsdMovingView : View {


    private var mPaintA: Paint = Paint()
    private var mPaintB: Paint = Paint()
    private var mPaintC: Paint = Paint()
    private var widthBase = 0f
    private var heightBase = 0f
    private val tag = this.javaClass.simpleName

    lateinit var mContext: Context

    //A级描点
    val first_point_a = PointF()
    val first_point_b = PointF()
    val first_point_c = PointF()
    val first_point_d = PointF()

    //B级描点 - 底部两个点和A级是共用的
    val second_point_a = first_point_b
    val second_point_b = PointF()
    val second_point_c = PointF()
    val second_point_d = first_point_c

    //C级描点
    val third_point_a = second_point_b
    val third_point_b = PointF()
    val third_point_c = PointF()
    val third_point_d = second_point_c

    var defender: BsdDefender = BsdDefender(this)
    lateinit var pointsA: FloatArray
    lateinit var pointsB: FloatArray
    lateinit var pointsC: FloatArray
    var model: DeviceVideoViewModel? = null

    var downPointPosition = 0
    var isPointGotted = false

    val aArr: Array<PointF> = arrayOf(
        first_point_a, first_point_b, first_point_c, first_point_d,
        second_point_a, second_point_b, second_point_c, second_point_d,
        third_point_a, third_point_b, third_point_c, third_point_d
    )

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mContext = context
        defender.initPaint(mPaintA, mPaintB, mPaintC)
    }

    private fun initPoint() {
        first_point_a.x = widthBase * 0 + 50
        first_point_a.y = heightBase * 10 - 50
        first_point_b.x = widthBase * 1
        first_point_b.y = heightBase * 7
        first_point_c.x = widthBase * 9
        first_point_c.y = heightBase * 7
        first_point_d.x = widthBase * 10 - 50
        first_point_d.y = heightBase * 10 - 50

        second_point_b.x = widthBase * 2
        second_point_b.y = heightBase * 4
        second_point_c.x = widthBase * 8
        second_point_c.y = heightBase * 4

        third_point_b.x = widthBase * 3
        third_point_b.y = heightBase * 1
        third_point_c.x = widthBase * 7
        third_point_c.y = heightBase * 1
    }

    fun setArrayData(points: IntArray) {

        first_point_a.x = points[12].toFloat()
        first_point_a.y = points[13].toFloat()
        first_point_b.x = points[8].toFloat()
        first_point_b.y = points[9].toFloat()
        first_point_c.x = points[10].toFloat()
        first_point_c.y = points[11].toFloat()
        first_point_d.x = points[14].toFloat()
        first_point_d.y = points[15].toFloat()

        second_point_b.x = points[4].toFloat()
        second_point_b.y = points[5].toFloat()
        second_point_c.x = points[6].toFloat()
        second_point_c.y = points[7].toFloat()


        third_point_b.x = points[0].toFloat()
        third_point_b.y = points[1].toFloat()
        third_point_c.x = points[2].toFloat()
        third_point_c.y = points[3].toFloat()
        postInvalidate()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        widthBase = (width / 10).toFloat()
        heightBase = (height / 10).toFloat()
        initPoint()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLines(canvas)

    }

    private fun drawLines(canvas: Canvas) {
        pointsA = floatArrayOf(
            first_point_a.x, first_point_a.y, first_point_b.x, first_point_b.y,
            first_point_b.x, first_point_b.y, first_point_c.x, first_point_c.y,
            first_point_c.x, first_point_c.y, first_point_d.x, first_point_d.y,
            first_point_d.x, first_point_d.y, first_point_a.x, first_point_a.y
        )
        pointsB = floatArrayOf(
            second_point_a.x, second_point_a.y, second_point_b.x, second_point_b.y,
            second_point_b.x, second_point_b.y, second_point_c.x, second_point_c.y,
            second_point_c.x, second_point_c.y, second_point_d.x, second_point_d.y,
            second_point_d.x, second_point_d.y, second_point_a.x, second_point_a.y
        )
        pointsC = floatArrayOf(
            third_point_a.x, third_point_a.y, third_point_b.x, third_point_b.y,
            third_point_b.x, third_point_b.y, third_point_c.x, third_point_c.y,
            third_point_c.x, third_point_c.y, third_point_d.x, third_point_d.y,
            third_point_d.x, third_point_d.y, third_point_a.x, third_point_a.y
        )

        canvas.drawLines(pointsA, mPaintA)
        canvas.drawLines(pointsB, mPaintB)
        canvas.drawLines(pointsC, mPaintC)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(tag, "ACTION_DOWN${event.x},${event.y}")
                //判断当前落点
                checkPoint(event.x, event.y)

            }

            MotionEvent.ACTION_MOVE -> {
                aArr[downPointPosition].x = event.x
                aArr[downPointPosition].y = event.y
                postInvalidate()
            }

            MotionEvent.ACTION_UP -> {
                Log.d(tag, "ACTION_UP${event.x},${event.y}")
                aArr[downPointPosition].x = event.x
                aArr[downPointPosition].y = event.y
                if (isPointGotted) {
                    isPointGotted = false
                    postInvalidate()
                    //将当前的点回调出去并进行设置
                    val points = intArrayOf(
                        third_point_b.x.toInt(),
                        third_point_b.y.toInt(),
                        third_point_c.x.toInt(),
                        third_point_c.y.toInt(),
                        second_point_b.x.toInt(),
                        second_point_b.y.toInt(),
                        second_point_c.x.toInt(),
                        second_point_c.y.toInt(),
                        first_point_b.x.toInt(),
                        first_point_b.y.toInt(),
                        first_point_c.x.toInt(),
                        first_point_c.y.toInt(),
                        first_point_a.x.toInt(),
                        first_point_a.y.toInt(),
                        first_point_d.x.toInt(),
                        first_point_d.y.toInt(),
                    )

                    //数据校验 - 阶梯数据不能超过 纵向
                    if (third_point_b.y > second_point_b.y || third_point_c.y > second_point_c.y ||
                        second_point_b.y > first_point_b.y || second_point_c.y > first_point_c.y ||
                        first_point_b.y > first_point_a.y || first_point_c.y > first_point_d.y
                    ) {
                        Toast.makeText(mContext, "数据设置不符合规范", Toast.LENGTH_SHORT).show()
                        model?.isDataIllegal = true
                        return true
                    } else {
                        model?.isDataIllegal = false
                    }


                    //把数据回调出去，交给VM保存
                    val responsePoint = ResponseData<getBsdDataModel>()
                    responsePoint.data = getBsdDataModel(points, true)
                    model?.bsdPointResponse?.postValue(responsePoint)
                }
            }

        }

        return true
    }

    private fun checkPoint(x: Float, y: Float) {

        for (i in aArr.indices) {
            if (abs(x - aArr[i].x) < 110 && abs(y - aArr[i].y) < 110) {
                downPointPosition = i
                Log.d(tag, "当前选中第$downPointPosition 个点")
                isPointGotted = true
            }
        }
    }

    fun setVm(viewModel: DeviceVideoViewModel) {
        model = viewModel
    }

    fun init() : IntArray {
        initPoint()
        postInvalidate()

        val intArray = intArrayOf(
            third_point_b.x.toInt(),third_point_b.y.toInt(), third_point_c.x.toInt(),
            third_point_c.y.toInt(),
            second_point_b.x.toInt(), second_point_b.y.toInt(), second_point_c.x.toInt(),
            second_point_c.y.toInt(),
            first_point_b.x.toInt(), first_point_b.y.toInt(), first_point_c.x.toInt(),
            first_point_c.y.toInt(),
            first_point_a.x.toInt(), first_point_a.y.toInt(), first_point_d.x.toInt(), first_point_d.y.toInt()
        )

        return intArray
    }

}


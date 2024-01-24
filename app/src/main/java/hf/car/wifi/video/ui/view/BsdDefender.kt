package hf.car.wifi.video.ui.view

import android.graphics.Color
import android.graphics.Paint

class BsdDefender(val view: BsdMovingView) {
    fun initPaint(mPaintA: Paint, mPaintB: Paint, mPaintC: Paint) {

        mPaintA.color = Color.RED
        mPaintA.strokeWidth = 5f
        mPaintA.style = Paint.Style.STROKE
        mPaintA.isAntiAlias = true

        mPaintB.color = Color.GREEN
        mPaintB.strokeWidth = 5f
        mPaintB.style = Paint.Style.STROKE
        mPaintB.isAntiAlias = true

        mPaintC.color = Color.BLUE
        mPaintC.strokeWidth = 5f
        mPaintC.style = Paint.Style.STROKE
        mPaintC.isAntiAlias = true
    }

    fun t() {

    }
}
package hf.car.wifi.video.ui.view

import android.graphics.PointF

class BsdPoint(pointF: PointF) {
    //首先是点
    var pointF = pointF
    //上边limit
    var topLimit = 0
    //下边limit，不能越界
    var downLimit = 0
    //计算数据是否合法：上面的线的两个Y值 必须大于下面两个线的Y值



}
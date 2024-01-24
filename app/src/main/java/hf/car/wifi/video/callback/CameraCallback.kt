package hf.car.wifi.video.callback

import android.graphics.Bitmap
import android.net.Uri

interface CameraCallback {

    /**
     * 图像分析
     */
    fun onAnalysisImage(bitmap: Bitmap?)

    /**
     * 图像捕捉
     * @param status 捕捉状态
     * @param photoPath 图像路径
     * @param errorMessage 错误信息
     */
    fun onPhotoCatch(status: Int, photoPath: Uri?, errorMessage: String?)

    /**
     * 视频录制回调
     * @param status 录制状态
     * @param videoPath 视频路径
     * @param errorCode 错误信息
     */
    fun onVideoRecord(status: Int, videoPath: Uri?, errorCode: Int)
}
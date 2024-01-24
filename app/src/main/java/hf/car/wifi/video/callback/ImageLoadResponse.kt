package hf.car.wifi.video.callback

import android.graphics.Bitmap

interface ImageLoadResponse {

    fun loadSuccess(bitmap: Bitmap?)

    fun loadFailed()
}
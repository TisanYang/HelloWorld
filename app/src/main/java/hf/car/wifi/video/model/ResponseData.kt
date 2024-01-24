package hf.car.wifi.video.model

/**
 * 返回结果
 */
class ResponseData<T> {

    var code: Int = -1

    var message: String = ""

    var data: T? = null
}
package hf.car.wifi.video.callback

interface SocketCallback {

    fun onResponse(bytes: ByteArray)

    fun onError(message: String)
}
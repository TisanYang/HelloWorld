package hf.car.wifi.video.model

class RequestReplay(cmd: Short, len: Int = 0, page: Int = 1, id: Int = 0) {

    var mark: Int = 0xF4E26bA2.toInt()

    var ver: Int = 0

    var msgfromto: Int = 2

    var mtype: Int = 1

    var rev: Short = 0x00

    var sn: Int = 0x00

    var nCmd: Short = cmd

    var error: Short = 0x00

    var length: Int = len

    var lIp: Int = 0
    var channel: Int = id
    val type: Short = 0xff
    var luid: Int = 0
    var isBack: Int = 0
    var alm: Short = 0xff
    var beginTime: String = ""
    var endTime: String = ""
    var page: Int = page
}
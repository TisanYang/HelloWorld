package hf.car.wifi.video.model

class RequestCommon(cmd: Short, len: Int = 0, content: Int = 0x00) {

    var mark: Int = 0xF4E26bA2.toInt()

    var ver: Int = 0

    var msgfromto: Int = 2

    var mtype: Int = 1

    var rev: Short = 0x00

    var sn: Int = 0x00

    var nCmd: Short = cmd

    var error: Short = 0x00

    var length: Int = len

    var data: Int = content
}
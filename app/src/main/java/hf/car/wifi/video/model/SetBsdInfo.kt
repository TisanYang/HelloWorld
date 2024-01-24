package hf.car.wifi.video.model

import hf.car.wifi.video.constant.ApiConstant
import java.io.File

class SetBsdInfo(
    cmd: Short = ApiConstant.FILE_SEND_PACKAGE,
    len: Int = 0,
    channel : Int = 0,
    dataArray: IntArray
)  {
    var mark: Int = 0xF4E26bA2.toInt()

    var ver: Int = 0

    var msgfromto: Int = 2

    var mtype: Int = 1

    var rev: Short = 0x00

    var sn: Int = 0x00

    var nCmd: Short = cmd

    var error: Short = 0x00

    var length: Int = len

    var channel : Int = channel

    val dataArray = dataArray
}
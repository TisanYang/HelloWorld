package hf.car.wifi.video.model

import hf.car.wifi.video.constant.ApiConstant

class SetLockCarInfo(
    cmd: Short = ApiConstant.SET_BLAND_TIME,
    len: Int = 0,
    time: Int,
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

    var time = time
}
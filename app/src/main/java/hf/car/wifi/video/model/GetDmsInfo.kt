package hf.car.wifi.video.model

import hf.car.wifi.video.constant.ApiConstant

class GetDmsInfo(
    cmd: Short = ApiConstant.GET_DMS_DATA,
    len: Int = 0,
    dataArray: ByteArray,
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

    var dataArray = dataArray
}
package hf.car.wifi.video.model

import hf.car.wifi.video.constant.ApiConstant
import java.io.File

class GetVideoReplayInfo(
    cmd: Short = ApiConstant.START_REPLY_DATA,
    len: Int = 0,
    bean: DataReplayBean,
    channel: Byte,
    ip: String,
) {
    var mark: Int = 0xF4E26bA2.toInt()

    var ver: Int = 0

    var msgfromto: Int = 2

    var mtype: Int = 1

    var rev: Short = 0x00

    var sn: Int = 0x00

    var nCmd: Short = cmd

    var error: Short = 0x00

    var length: Int = len

    var channel: Byte = channel

    var bean : DataReplayBean = bean

    var ip : String = ip

}
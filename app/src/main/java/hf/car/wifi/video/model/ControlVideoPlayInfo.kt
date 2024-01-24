package hf.car.wifi.video.model

import hf.car.wifi.video.constant.ApiConstant
import java.io.File

class ControlVideoPlayInfo(
    cmd: Short = ApiConstant.CONTROL_PLAY,
    ch : Byte ,
    controlOrder : Byte,
    factor : Byte,
    status : Byte,
    seektime : ByteArray,
    timeout : ByteArray,
) {
    var mark: Int = 0xF4E26bA2.toInt()

    var ver: Int = 0

    var msgfromto: Int = 2

    var mtype: Int = 1

    var rev: Short = 0x00

    var sn: Int = 0x00

    var nCmd: Short = cmd

    var error: Short = 0x00

    //通道按照位掩码处理
    var channel :Byte = ch
    //控制指令
    var controlOrder :Byte = controlOrder
    //快进因子
    var factor :Byte = factor
    var status :Byte = status
    var seektime :ByteArray = seektime
    var timeout :ByteArray = timeout




}
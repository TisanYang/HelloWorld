package hf.car.wifi.video.model

import hf.car.wifi.video.constant.ApiConstant
import java.io.File

class SendFileInfo(
    cmd: Short = ApiConstant.FILE_SEND_PACKAGE,
    len: Int = 0,
    checkId: ByteArray,
    dataType: Byte = 0,
    rev: Byte = 0x00,
    sync: Short,
    packageLen: Int,
    dataArray: ByteArray,
    file: File
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

    var checkId: ByteArray = checkId

    var sync : Short = sync  //升级包序号

    var packageLen: Int = packageLen  //后面数据包的长度

    var dataArray: ByteArray = dataArray  //数据包实体

    var file :File = file
}
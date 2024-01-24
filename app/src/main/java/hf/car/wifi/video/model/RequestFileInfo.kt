package hf.car.wifi.video.model

class RequestFileInfo(
    cmd: Short, /*总长度*/len: Int = 0,byteArray: ByteArray,fileLen : Int) {
    var mark: Int = 0xF4E26bA2.toInt()

    var ver: Int = 0

    var msgfromto: Int = 2

    var mtype: Int = 1

    var rev: Short = 0x00

    var sn: Int = 0x00

    var nCmd: Short = cmd

    var error: Short = 0x00

    var length: Int = len

    val byteArray = byteArray

    val fileLen : Int = fileLen

    /*var varCheckMark: Int = cMake

    var pathArr: ByteArray = pathArr

    var version: Int = version

    var type: Int = type

    var delcfg: Int = delcfg

    var saveflag: Int = saveflag

    var updateflag: Int = updateflag

    var digest: Short = digest

    var dataLength: Int = dataLength*/


}
package hf.car.wifi.video.model

class RequestTimeCalibration(
    cmd: Short, len: Int = 0, year: Int, mon: Int, day: Int,
    hour: Int, min: Int, sec: Int
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

    var year: Int= year

    var mon: Int = mon
    var day: Int = day

    var hour: Int = hour

    var min: Int = min

    var sec: Int = sec


}
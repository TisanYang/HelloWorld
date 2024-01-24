package hf.car.wifi.video.model

import hf.car.wifi.video.ui.adapter.DmsDataBean
import hf.car.wifi.video.ui.adapter.DmsDataItemBean

data class PhoneImageModel(var name: String, var size: Long, var filePath: String)

data class PhoneVideoModel(var filePath: String)

/**
 * 人员信息
 */
data class PersonModel(var name: String) {
    var person: ByteArray = ByteArray(1)
    var face: ByteArray = ByteArray(1)
}

data class PersonFace(var id: Int, var face: ByteArray)

/**
 * 系统信息
 */
data class SysInfoModel(var name: String) {
    var sysChannel: ByteArray = ByteArray(1)
}

/**
 * 设备信息
 */
data class DeviceModel(var name: String) {
    var deviceID: ByteArray = ByteArray(1)
    var deviceName: ByteArray = ByteArray(1)
    var carNumber: ByteArray = ByteArray(1)
    var driverName: ByteArray = ByteArray(1)
}

data class DeviceTimeModel(var name: String) {
    var year: ByteArray = ByteArray(2)
    var mon: ByteArray = ByteArray(1)
    var day: ByteArray = ByteArray(1)
    var hour: ByteArray = ByteArray(1)
    var min: ByteArray = ByteArray(1)
    var sec: ByteArray = ByteArray(1)
    var week: ByteArray = ByteArray(1)
    var data: String = ""
}

/**
 * 设备状态
 */
data class DeviceStatus(var name: String) {
    var deviceSignal: ByteArray = ByteArray(1)
}

/**
 * 通道信息
 * @param name 通道名字
 * @param id 通道id
 */
data class VideoChannelModel(var name: String, var id: Int) {
    var selector: Boolean = false
}

/**
 * BSD点信息
 * @param points 数组点
 */
data class getBsdDataModel(var points : IntArray,var isFromView : Boolean = false)

/**
 * DMS设置信息
 */
data class SingleDmsInfo(var list: MutableList<DmsDataBean>)

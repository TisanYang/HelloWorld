package hf.car.wifi.video.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niklaus.mvvm.utils.ByteUtil
import hf.car.wifi.video.callback.SocketCallback
import hf.car.wifi.video.constant.ApiConstant
import hf.car.wifi.video.model.*
import hf.car.wifi.video.processor.*
import hf.car.wifi.video.ui.adapter.DmsDataBean
import hf.car.wifi.video.utils.TimeUtils
import java.nio.ByteBuffer
import java.util.Arrays
import kotlin.concurrent.thread


@SuppressLint("NewApi")
class DeviceVideoViewModel : ViewModel() {


    var isDataIllegal: Boolean = false
    private var tag: String = javaClass.simpleName

    private val server = EchoServer()

    /**
     * 系统信息
     */
    val sysInfoResponse: MutableLiveData<ResponseData<SysInfoModel>> = MutableLiveData()

    /**
     * 设备信息
     */
    val deviceInfoResponse: MutableLiveData<ResponseData<DeviceModel>> = MutableLiveData()

    /**
     * 设备时间参数
     */
    val deviceTimeResponse: MutableLiveData<ResponseData<DeviceTimeModel>> = MutableLiveData()

    /**
     * 设备状态
     */
    val deviceStatusResponse: MutableLiveData<ResponseData<DeviceStatus>> = MutableLiveData()

    /**
     * 获取实时视频流
     */
    val playVideoResponse: MutableLiveData<ByteArray> = MutableLiveData()

    /**
     * 开启视频
     */
    val startVideoResponse: MutableLiveData<ResponseData<Int>> = MutableLiveData()

    /**
     * 关闭视频
     */
    val stopVideoResponse: MutableLiveData<ResponseData<Int>> = MutableLiveData()

    /**
     * 测试vm
     */
    val testCmdResponse: MutableLiveData<ResponseData<String>> = MutableLiveData()

    /**
     * BSD点信息
     */
    val bsdPointResponse: MutableLiveData<ResponseData<getBsdDataModel>> = MutableLiveData()

    /**
     * DMS信息
     */

    val dmsInfoResponse: MutableLiveData<ResponseData<List<DmsInfoBean>>> = MutableLiveData()

    /**
     * 获取锁车时间
     */
    val lockCarResponse: MutableLiveData<ResponseData<Int>> = MutableLiveData()


    /**
     * 获取测试指令信息
     */
    fun loadTestInfo() {
        val request = RequestCommon(ApiConstant.TEST_INFO)
        val handler = EchoClientHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                //处理回来的数据
            }

            override fun onError(message: String) {
                val response = ResponseData<String>()
                response.code = 500
                response.message = message
                testCmdResponse.postValue(response)
            }
        })
        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    /**
     * 获取系统信息
     */
    fun loadSysInfo() {
        val request = RequestCommon(ApiConstant.SYS_INFO)
        val handler = EchoClientHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handleSysInfo(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<SysInfoModel>()
                response.code = 500
                response.message = message
                sysInfoResponse.postValue(response)
            }
        })

        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    /**
     * 同步设备时间
     */

    fun syncTime(data: ByteArray) {
        val ymdhms = TimeUtils().getYMDHMS()
        val request = RequestTimeCalibration(
            ApiConstant.SET_DEVICE_TIM,
            data.size,
            ymdhms[0].toInt(),
            ymdhms[1].toInt(),
            ymdhms[2].toInt(),
            ymdhms[3].toInt(),
            ymdhms[4].toInt(),
            ymdhms[5].toInt()
        )
        val handler = UpdateTimeHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handleSyncTime(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<DeviceModel>()
                response.code = 500
                response.message = message
                deviceInfoResponse.postValue(response)
            }
        })

        thread {
            EchoClient().updateTime(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    /**
     * 获取设备信息
     */
    fun loadDeviceInfo() {
        val request = RequestCommon(ApiConstant.DEVICE_INFO)
        val handler = EchoClientHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handleDeviceInfo(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<DeviceModel>()
                response.code = 500
                response.message = message
                deviceInfoResponse.postValue(response)
            }
        })

        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    /**
     * 获取盲区画线标定区域
     */
    fun loadDeviceBsd() {
        val request = RequestCommon(ApiConstant.GET_BSD)
        val handler = EchoClientHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handleDeviceBsd(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<DeviceTimeModel>()
                response.code = 500
                response.message = message
                deviceTimeResponse.postValue(response)
            }
        })

        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    /**
     * 获取设备时间
     */
    fun loadDeviceTime() {
        val request = RequestCommon(ApiConstant.GET_DEVICE_TIME)
        val handler = EchoClientHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handleDeviceTime(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<DeviceTimeModel>()
                response.code = 500
                response.message = message
                deviceTimeResponse.postValue(response)
            }
        })

        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    /**
     * 获取连接设备状态
     */
    fun loadDeviceStatus() {
        val request = RequestCommon(ApiConstant.DEVICE_STATE)
        val handler = EchoClientHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handleDeviceStatus(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<DeviceStatus>()
                response.code = 500
                response.message = message
                deviceStatusResponse.postValue(response)
            }
        })

        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    /**
     * 开启服务
     */
    fun startServer(ip: String, port: Int) {
        Log.d(tag, "开启服务 ip:$ip,port:$port")
        val handler = EchoServerHandler()
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                Log.d(tag, "接收到的视频数据原始长度,length:${bytes.size}")

                val mark = ByteArray(4)
                var markIndex = 0
                val codeType = ByteArray(1)
                val cVOPType = ByteArray(1)
                val dataLen = ByteArray(8)
                var dataLenIndex = 0
                for (i in 0 until 32) {
                    if (i in 0..3) {
                        mark[markIndex] = bytes[i]
                        markIndex++
                    }
                    if (i in 5..5) {
                        codeType[0] = bytes[i]
                    }
                    if (i in 7..7) {
                        cVOPType[0] = bytes[i]
                    }
                    if (i in 12..15) {
                        dataLen[dataLenIndex] = bytes[i]
                        dataLenIndex++
                    }
                }
                Log.d(tag, "received mark:${ByteUtil.bytes2HexStringLE(mark)}")
                Log.d(tag, "received codeType:${ByteUtil.bytes2HexStringLE(codeType)}")
                Log.d(tag, "received cVOPType:${ByteUtil.bytes2HexStringLE(cVOPType)}")
                Log.d(tag, "received dataLen:${ByteUtil.bytes2HexStringLE(dataLen)}")

                val dataSize = bytes.size - 32
                val data = ByteArray(dataSize)
                System.arraycopy(bytes, 32, data, 0, dataSize)
                playVideoResponse.postValue(data)
            }

            override fun onError(message: String) {
                Log.e(tag, "startServer error:$message")
            }
        })

        thread {
            server.start(ip, port, handler)
        }
    }

    /**
     * 关闭服务
     */
    fun stopServer() {
        server.stop()
    }

    /**
     * 开启视频
     */
    fun openVideoInfo(ip: String, port: Int, channel: Int) {
        val request = RequestVideo(ApiConstant.OPEN_REAL_VIDEO)
        request.ip = ip
        request.port = port
        request.channel = channel.toShort()
        val handler = QueryVideoHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handleOpenVideo(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<Int>()
                response.code = 500
                response.message = message
                startVideoResponse.postValue(response)
            }
        })

        thread {
            EchoClient().openVideo(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    /**
     * 关闭视频
     */
    fun closeVideoInfo(playChannel: Int, nextChannel: Int) {
        Log.d(tag, "关闭视频 playChannel:$playChannel,nextChannel:$nextChannel")
        val handler = EchoClientHandler(RequestCommon(ApiConstant.CLOSE_REAL_VIDEO, 4, playChannel))
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handleCloseVideo(bytes, nextChannel)
            }

            override fun onError(message: String) {
                val response = ResponseData<Int>()
                response.code = 500
                response.message = message
                stopVideoResponse.postValue(response)
            }
        })

        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    /**
     * 处理系统信息结果
     */
    private fun handleSysInfo(bytes: ByteArray) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "系统信息 data:${ByteUtil.bytes2HexString(bytes)}")

        val response = ResponseData<SysInfoModel>()
        //获取成功失败
        val successArray = ByteArray(4)
        var successIndex = 0
        val sysChannelArray = ByteArray(1)
        if (size >= 20) {
            for (i in 0 until 20) {
                if (i in 12..15) {
                    successArray[successIndex] = bytes[i]
                    successIndex++
                }
            }
        }
        if (size >= 21) {
            for (i in 21..21) {
                sysChannelArray[0] = bytes[i]
            }
        }

        val success = ByteUtil.byteArrayToInt(successArray)
        Log.d(tag, "received success:$success")

        if (success == 0) {
            val model = SysInfoModel("Sys")
            model.sysChannel = sysChannelArray

            response.code = 0
            response.data = model
        } else {
            response.code = success
            response.message = "服务返回错误:${success}"
        }

        sysInfoResponse.postValue(response)
    }

    /**
     * 获取设备BSD点信息
     */
    private fun handleDeviceBsd(bytes: ByteArray) {

        val totalPointString = ByteUtil.bytes2HexString(bytes)
        Log.d(tag, "设备BSD点信息 data:$totalPointString")
        //解析回过来的点
        val pointString = totalPointString.substring(40, totalPointString.length)
        val list: MutableList<Int> = ArrayList()

        for (i in 0..pointString.length - 4 step 4) {
            //截取长度
            val substring = pointString.substring(i, i + 4)
            //调换位置
            val chars = substring.toCharArray()
            val value =
                StringBuilder().append(chars[2]).append(chars[3]).append(chars[0]).append(chars[1])
                    .toString()
            list.add(Integer.parseInt(value, 16))
        }
        //把数据回调出去
        val responsePoint = ResponseData<getBsdDataModel>()
        responsePoint.data = getBsdDataModel(list.toIntArray())
        bsdPointResponse.postValue(responsePoint)

        //返回响应的坐标点数据
        //setBsdData()

    }

    /**
     * 设置BSD标签
     */
    fun setBsdData(channel: Int,ints: IntArray) {

        var realChannel =  if (channel == 4) 1 else 0

        val handler = SetBsdHandler(SetBsdInfo(ApiConstant.SET_BSD_ARR, 36, realChannel,ints))

        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                Log.d(tag, "uadateSystem data:${ByteUtil.bytes2HexString(bytes)}")
                Log.d(tag + "uadateSystem onResponse", bytes.contentToString())

            }

            override fun onError(message: String) {
                Log.d(tag + "uadateSystem error", message)
            }
        })
        thread {
            EchoClient().setBsdData(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }


    }

    /**
     * 处理设备时
     */
    private fun handleDeviceTime(bytes: ByteArray) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "received data size:${bytes.toString()}")
        Log.d(tag, "设备时间 data:${ByteUtil.bytes2HexString(bytes)}")
        val response = ResponseData<DeviceTimeModel>()
        //获取成功失败
        val successArray = ByteArray(4)
        var successIndex = 0
        if (size >= 20) {
            for (i in 0 until 20) {
                if (i in 12..15) {
                    successArray[successIndex] = bytes[i]
                    successIndex++
                }
            }
        }
        //16+8+8+8+8+8+8
        val year = ByteArray(2)
        val mon = ByteArray(1)
        val day = ByteArray(1)
        val hour = ByteArray(1)
        val min = ByteArray(1)
        val sec = ByteArray(1)
        val week = ByteArray(1)
        if (size >= 28) {
            for (i in 20 until 28) {

                if (i in 20..21) {
                    year[0] = bytes[20]
                    year[1] = bytes[21]
                }
                if (i in 22..22) {
                    mon[0] = bytes[i]
                }
                if (i in 23..23) {
                    day[0] = bytes[i]
                }
                if (i in 24..24) {
                    hour[0] = bytes[i]
                }
                if (i in 25..25) {
                    min[0] = bytes[i]

                }
                if (i in 26..26) {
                    sec[0] = bytes[i]
                }
            }
        }
        val success = ByteUtil.byteArrayToInt(successArray)
        Log.d(tag, "received success:$success")

        if (success == 0) {
            val model = DeviceTimeModel("Time")
            model.year = year
            model.mon = mon
            model.day = day
            model.hour = hour
            model.min = min
            model.sec = sec
            model.week = week
            response.code = 0
            response.data = model
        } else {
            response.code = success
            response.message = "服务返回错误:${success}"
        }

        deviceTimeResponse.postValue(response)
    }


    /**
     * 处理设备信息结果
     */
    private fun handleSyncTime(bytes: ByteArray) {
        val size = bytes.size

        val response = ResponseData<DeviceTimeModel>()
        //获取成功失败
        val successArray = ByteArray(4)
        var successIndex = 0
        if (size >= 20) {
            for (i in 0 until 20) {
                if (i in 12..15) {
                    successArray[successIndex] = bytes[i]
                    successIndex++
                }
            }
        }

        val success = ByteUtil.byteArrayToInt(successArray)
        Log.d(tag, "received success:$success")

        if (success == 0) {
            val model = DeviceModel("Time")
            response.code = 100
        } else {
            response.code = success
            response.message = "服务返回错误:${success}"
        }

        deviceTimeResponse.postValue(response)
    }


    /**
     * 处理设备信息结果
     */
    private fun handleDeviceInfo(bytes: ByteArray) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "设备信息 data:${ByteUtil.bytes2HexString(bytes)}")

        val response = ResponseData<DeviceModel>()
        //获取成功失败
        val successArray = ByteArray(4)
        var successIndex = 0
        if (size >= 20) {
            for (i in 0 until 20) {
                if (i in 12..15) {
                    successArray[successIndex] = bytes[i]
                    successIndex++
                }
            }
        }
        val deiceIDArray = ByteArray(32)
        var deiceIDIndex = 0
        val deiceNameArray = ByteArray(32)
        var deiceNameIndex = 0
        val carNumberArray = ByteArray(16)
        var carNumberIndex = 0
        val driverNameArray = ByteArray(16)
        var driverNameIndex = 0
        if (size >= 152) {
            for (i in 20 until 151) {
                if (i in 20..51) {
                    deiceIDArray[deiceIDIndex] = bytes[i]
                    deiceIDIndex++
                }
                if (i in 56..87) {
                    deiceNameArray[deiceNameIndex] = bytes[i]
                    deiceNameIndex++
                }
                if (i in 88..103) {
                    carNumberArray[carNumberIndex] = bytes[i]
                    carNumberIndex++
                }
                if (i in 104..119) {
                    driverNameArray[driverNameIndex] = bytes[i]
                    driverNameIndex++
                }
            }
        }
        val success = ByteUtil.byteArrayToInt(successArray)
        Log.d(tag, "received success:$success")

        if (success == 0) {
            val model = DeviceModel("John")
            model.deviceID = deiceIDArray
            model.deviceName = deiceNameArray
            model.carNumber = carNumberArray
            model.driverName = driverNameArray

            response.code = 0
            response.data = model
        } else {
            response.code = success
            response.message = "服务返回错误:${success}"
        }

        deviceInfoResponse.postValue(response)
    }

    /**
     * 处理设备状态结果
     */
    private fun handleDeviceStatus(bytes: ByteArray) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "设备状态 data:${ByteUtil.bytes2HexString(bytes)}")

        val response = ResponseData<DeviceStatus>()
        //获取成功失败
        val successArray = ByteArray(4)
        var successIndex = 0
        val deiceSignalArray = ByteArray(1)
        if (size >= 20) {
            for (i in 0 until 20) {
                if (i in 12..15) {
                    successArray[successIndex] = bytes[i]
                    successIndex++
                }
            }
        }
        if (size >= 52) {
            for (i in 21..21) {
                deiceSignalArray[0] = bytes[i]
            }
        }

        val success = ByteUtil.byteArrayToInt(successArray)
        Log.d(tag, "received success:$success")

        if (success == 0) {
            val model = DeviceStatus("John")
            model.deviceSignal = deiceSignalArray

            response.code = 0
            response.data = model
        } else {
            response.code = success
            response.message = "服务返回错误:${success}"
        }

        deviceStatusResponse.postValue(response)
    }

    /**
     * 处理开启视频结果
     */
    private fun handleOpenVideo(bytes: ByteArray) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "开启视频 data:${ByteUtil.bytes2HexString(bytes)}")

        val response = ResponseData<Int>()
        //获取成功失败
        val successArray = ByteArray(4)
        var successIndex = 0
        if (size >= 20) {
            for (i in 0 until 20) {
                if (i in 12..15) {
                    successArray[successIndex] = bytes[i]
                    successIndex++
                }
            }
        }
        val success = ByteUtil.byteArrayToInt(successArray)
        Log.d(tag, "received success:$success")

        if (success == 0) {
            response.code = 0
        } else {
            response.code = success
            response.message = "服务返回错误:${success}"
        }

        startVideoResponse.postValue(response)
    }

    /**
     * 处理关闭视频结果
     */
    private fun handleCloseVideo(bytes: ByteArray, nextChannel: Int) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "关闭视频 data:${ByteUtil.bytes2HexString(bytes)}")

        val response = ResponseData<Int>()
        //获取成功失败
        val successArray = ByteArray(4)
        var successIndex = 0
        if (size >= 20) {
            for (i in 0 until 20) {
                if (i in 12..15) {
                    successArray[successIndex] = bytes[i]
                    successIndex++
                }
            }
        }
        val success = ByteUtil.byteArrayToInt(successArray)
        Log.d(tag, "received success:$success")

        if (success == 0) {
            response.code = 0
            response.data = nextChannel
        } else {
            response.code = success
            response.message = "服务返回错误:${success}"
        }

        stopVideoResponse.postValue(response)
    }

    /**
     * 获取DMS参数
     */
    fun getDmsData(bytes: ByteArray) {
        //获取参数
//        typedef struct{
//            u_8 uAdasId;/*0x64-adas 0x65-dsm/HOD 0x67-bsd,0xff-所有*/
//            u_8 uTypeId;/*0-所有，非零*/
//            u_8 uParamId;/*按照字段编号来定义*/
//            u_8 uVal;
//        }
//        val bytes = byteArrayOf(0x65, 0, 6, 0) //第三个是具体指标，这里第二个值是0.是所有，所以不用传具体类型
        val request = GetDmsInfo(ApiConstant.GET_DMS_DATA, bytes.size, bytes)
        val handler = GetDmsHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                val bytes2HexString = ByteUtil.bytes2HexString(bytes)
                Log.d(tag, "获取DMS数据16进制字符串:$bytes2HexString")
                val list : MutableList<DmsInfoBean> = ArrayList()
                if (bytes2HexString.length > 40) {
                    val dataString = bytes2HexString.substring(40, bytes2HexString.length)
                    println(dataString)
                    var i = 0
                    while (i + 8 <= dataString.length) {
                        val single = dataString.substring(i, i + 8)
                        i += 8
                        val category = single.substring(0,2).toByte()
                        val type = single.substring(2,4).toByte()
                        val subType = single.substring(4,6).toByte()
                        val value = single.substring(6,8)

                        val realValue = Integer.parseInt(value, 16).toUByte()
                        list.add(DmsInfoBean(category,type,subType,realValue))
                    }

                    val response = ResponseData<List<DmsInfoBean>>()
                    response.data = list
                    dmsInfoResponse.postValue(response)
                }

            }

            override fun onError(message: String) {
                val response = ResponseData<DeviceTimeModel>()
                response.code = 500
                response.message = message
                deviceTimeResponse.postValue(response)
            }
        })

        thread {
            EchoClient().getDmsData(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    fun setDmsData(bytes: ByteArray) {
        //第二个值指监控类型，第三个指标，最后一个是值
        //val bytes = byteArrayOf(0x65, 2, 6, 55)
        val request = SetDmsInfo(ApiConstant.SET_DMS_DATA, bytes.size, bytes)
        val handler = SetDmsHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                Log.d(tag, "获取DMS数据16进制字符串:${ByteUtil.bytes2HexString(bytes)}")
                Log.d(tag, "获取DMS数据数组:${Arrays.toString(bytes)}")

            }

            override fun onError(message: String) {
                val response = ResponseData<DeviceTimeModel>()
                response.code = 500
                response.message = message
                deviceTimeResponse.postValue(response)
            }
        })

        thread {
            EchoClient().setDmsData(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    fun getLockCarTime() {
        val request = RequestCommon(ApiConstant.GET_BLAND_TIME)
        val handler = EchoClientHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                val buffer = ByteBuffer.allocate(4)
                System.arraycopy(bytes,20,buffer.array(),0,4)
                val int = bytesToInt(buffer.array())
                val response = ResponseData<Int>()
                response.data = int
                lockCarResponse.postValue(response)

            }

            override fun onError(message: String) {
                val response = ResponseData<SysInfoModel>()
                response.code = 500
                response.message = message
                sysInfoResponse.postValue(response)
            }
        })

        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    fun setLockCarTime(time: Int) {

        val request = SetLockCarInfo(ApiConstant.SET_BLAND_TIME,4,time)
        val handler = SetLockCarHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                val hexString = ByteUtil.bytes2HexString(bytes)
                Arrays.toString(bytes)
                getLockCarTime()

            }

            override fun onError(message: String) {
                val response = ResponseData<SysInfoModel>()
                response.code = 500
                response.message = message
                sysInfoResponse.postValue(response)
            }
        })

        thread {
            EchoClient().setLockCarTime(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    fun bytesToInt(a: ByteArray): Int {
        var ans = 0
        for (i in 0..3) {
            ans = ans shl 8 //左移 8 位
            ans = ans or a[3 - i].toInt() //保存 byte 值到 ans 的最低 8 位上
        }
        return ans
    }
}
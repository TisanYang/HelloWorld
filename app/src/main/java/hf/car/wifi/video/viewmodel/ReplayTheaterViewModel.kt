package hf.car.wifi.video.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niklaus.mvvm.utils.ByteUtil
import hf.car.wifi.video.MineApplication
import hf.car.wifi.video.callback.SocketCallback
import hf.car.wifi.video.constant.ApiConstant
import hf.car.wifi.video.model.*
import hf.car.wifi.video.processor.*
import hf.car.wifi.video.utils.UriUtil
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ArrayBlockingQueue
import kotlin.concurrent.thread

class ReplayTheaterViewModel : ViewModel() {

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
     * 视频回放数据信息
     */
    val videoPlayResponse: MutableLiveData<ResponseData<MutableList<DataReplayBean>>> =
        MutableLiveData()

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
     *获取录像回放数据
     */
    fun getReplayData(id: Int) {
        val request = RequestReplay(ApiConstant.GET_REPLY_DATA, id = id)
        val handler = ReplayClientHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handleReplyData(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<DeviceStatus>()
                response.code = 500
                response.message = message
                deviceStatusResponse.postValue(response)
            }
        })

        thread {
            EchoClient().getReplayData(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
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
    var lastBytes2HexStringLE = ""
    fun startServer(ip: String, port: Int) {
        Log.d(tag, "开启服务 ip:$ip,port:$port")
        val handler = EchoServerHandler()
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                Log.d(tag, "接收到的视频数据原始长度,length:${bytes.size}")
                //Thread.sleep(18)

                val mark = ByteArray(4)
                var markIndex = 0
                val codeType = ByteArray(1)
                val cVOPType = ByteArray(1)
                val channel = ByteArray(1)
                val dataLen = ByteArray(8)
                var dataLenIndex = 0
                var syncDataIndex = 0

                val syncData = ByteArray(4)


                for (i in 0 until 32) {
                    if (i in 0..3) {
                        mark[markIndex] = bytes[i]
                        markIndex++
                    }
                    if (i in 4..4) {
                        channel[0] = bytes[i]
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
                    if (i in 16..19) {
                        syncData[syncDataIndex] = bytes[i]
                        syncDataIndex++
                    }
                }

//                val bytes2HexStringLE = ByteUtil.bytes2HexStringLE(syncData)
//                var repeated = (bytes2HexStringLE == lastBytes2HexStringLE)
//                lastBytes2HexStringLE = bytes2HexStringLE
//                if (repeated) {
//                    return
//                }

                Log.d(tag, "received mark:${ByteUtil.bytes2HexStringLE(mark)}")
                Log.d(tag, "received codeType:${ByteUtil.bytes2HexStringLE(codeType)}")
                Log.d(tag, "received cVOPType:${ByteUtil.bytes2HexStringLE(cVOPType)}")
                Log.d(tag, "received dataLen:${ByteUtil.bytes2HexStringLE(dataLen)}")
                Log.d(
                    tag,
                    "received cVOPType - synType:length:${bytes.size} - ${
                        ByteUtil.bytes2HexStringLE(cVOPType)
                    } --" +
                            "${ByteUtil.bytes2HexStringLE(channel)}-- " +
                            "${ByteUtil.bytes2HexStringLE(syncData)}"
                )


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
        val request = RequestVideo(ApiConstant.START_REPLY_DATA)
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
     * 处理设备时
     */
    private fun handleDeviceBsd(bytes: ByteArray) {
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
        val x1 = ByteArray(2)
        val x2 = ByteArray(2)
        val y1 = ByteArray(2)
        val y2 = ByteArray(2)


        if (size >= 28) {
            for (i in 20 until 28) {

                if (i in 20..21) {
                    x1[0] = bytes[20]
                    x1[1] = bytes[21]
                }
                if (i in 22..23) {
                    x2[0] = bytes[22]
                    x2[1] = bytes[23]
                }

            }
        }
        val success = ByteUtil.byteArrayToInt(successArray)
        val succes = ByteUtil.byteArrayToInt(x2)
        val successs = ByteUtil.byteArrayToInt(x1)
        Log.d(tag, "received success:$success")
        Log.d(tag, "received succes:$succes")
        Log.d(tag, "received successs:$successs")



        deviceTimeResponse.postValue(response)
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
     * 处理返回的视频数据
     */
    private fun handleReplyData(bytes: ByteArray) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "播放回放数据 data:${ByteUtil.bytes2HexString(bytes)}")

        val response = ResponseData<DeviceStatus>()
        //获取成功失败
        val successArray = ByteArray(4)
        var successIndex = 0
        val deiceSignalArray = ByteArray(4)
        var index = 0
        if (size >= 20) {
            for (i in 0 until 20) {
                if (i in 12..15) {
                    successArray[successIndex] = bytes[i]
                    successIndex++
                }
            }
        }
        if (size >= 21) {
            for (i in 20 until size) {
                if (i in 20..23) {
                    deiceSignalArray[index] = bytes[i]
                    index++
                }
            }
        }

        val success = ByteUtil.byteArrayToInt(successArray)
        Log.d(tag, "received success:$success")
        Log.d(tag, "received deiceSignalArray:" + ByteUtil.byteArrayToInt(deiceSignalArray))

        if (success == 0) {
            val model = DeviceStatus("John")
            model.deviceSignal = deiceSignalArray

            response.code = 0
            response.data = model
        } else {
            response.code = success
            response.message = "服务返回错误:${success}"
        }

        //解析返回的值
//        {
//            u_32  pageCount;              //  总共的页数
//            u_32  pageSize;                //  每页大小,该值始终等于  PAGE_SIZE=25
//            u_32  total;                        //  总录像数
//            u_32  page;                        //  当前页
//            u_32  count;                        //  当前页录像数
//            u_32  res;
//            File_res_t    files[MAX_REC_PAGE_NUM];        //  录像详细信息,数组大小为count.
//        }
        val pageCountArr = ByteBuffer.allocate(4)
        System.arraycopy(bytes, 20, pageCountArr.array(), 0, 4)
        val pageCount = UriUtil.bytesToInt(pageCountArr.array())

        val pageSizeArr = ByteBuffer.allocate(4)
        System.arraycopy(bytes, 24, pageSizeArr.array(), 0, 4)
        val pageSize = UriUtil.bytesToInt(pageSizeArr.array())

        val totalArr = ByteBuffer.allocate(4)
        System.arraycopy(bytes, 28, totalArr.array(), 0, 4)
        val total = UriUtil.bytesToInt(totalArr.array())

        val pageArr = ByteBuffer.allocate(4)
        System.arraycopy(bytes, 32, pageArr.array(), 0, 4)
        val page = UriUtil.bytesToInt(pageArr.array())

        val countArr = ByteBuffer.allocate(4)
        System.arraycopy(bytes, 36, countArr.array(), 0, 4)
        val count = UriUtil.bytesToInt(countArr.array())

        val resArr = ByteBuffer.allocate(4)
        System.arraycopy(bytes, 40, resArr.array(), 0, 4)
        val res = UriUtil.bytesToInt(resArr.array())


        deviceStatusResponse.postValue(response)
        //解析完前面24个字节，剩余600个字节长度/总视频长度pageSize = 24个字节，也就是每一个视频的信息
        val singleBuffer = ByteBuffer.allocate(24)
        val lastTotal = ByteBuffer.allocate(bytes.size - 44)
        System.arraycopy(bytes, 44, lastTotal.array(), 0, lastTotal.array().size)
        //剩余数据总长度
        val lasted = bytes.size - 44 - 1 //因为底下的循环控制，所以把索引减少了一个
        val list = mutableListOf<DataReplayBean>()
        val replayResponse = ResponseData<MutableList<DataReplayBean>>()
        replayResponse.code = 0
        replayResponse.data = list

        //如果没有数据，不做组装
        if (total == 0) {
            videoPlayResponse.postValue(replayResponse)
            return
        }

        for (i in 0..lasted step 24) {
            System.arraycopy(lastTotal.array(), i, singleBuffer.array(), 0, 24)
            val array = singleBuffer.array()
            //拆分掩码
            var index = 0
            when(array[8].toInt()){
                0 -> index = 1
                1 -> index = 2
                2 -> index = 4
                3 -> index = 8
            }
            //解析单个数据
            val dataReplayBean = DataReplayBean(
                byteArrayOf(array[0], array[1], array[2], array[3]), //filename
                byteArrayOf(array[4], array[5], array[6], array[7]), //lIP
                byteArrayOf(index.toByte()),                         //channel
                byteArrayOf(array[9]),                               //type
                byteArrayOf(array[10]),                              //alm
                byteArrayOf(array[11]),                              //rev
                byteArrayOf(array[12], array[13], array[14], array[15]),//sttime
                byteArrayOf(array[16], array[17], array[18], array[19]),//etime
                byteArrayOf(array[20], array[21], array[22], array[23]) //size
            )
            list.add(dataReplayBean)
        }


        videoPlayResponse.postValue(replayResponse)
    }


    /**
     * 处理设备状态结果
     */
    private fun handleDeviceStatus(bytes: ByteArray) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "设备状态fsa data:${ByteUtil.bytes2HexString(bytes)}")

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

    fun play(dataReplayBean: DataReplayBean, currentChannel: Byte, ip: String) {
        val handler = ReplayVideoHandler(
            GetVideoReplayInfo(
                ApiConstant.START_REPLY_DATA,
                36,
                dataReplayBean,
                currentChannel,
                ip
            )
        )

        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                Log.d(tag, "replay_data:${ByteUtil.bytes2HexString(bytes)}")
                Log.d(tag + "uadateSystem onResponse", bytes.contentToString())

            }

            override fun onError(message: String) {
                Log.d(tag + "uadateSystem error", message)
            }
        })
        thread {
            EchoClient().requestReplay(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    fun goFront(channel: Byte, startTimeArr: ByteArray, endTimeArr: ByteArray) {
        val seekTimeArr = byteArrayOf(0, 0, 0, 0)
        val timeoutArr = byteArrayOf(0, 0, 0, 0)

        val cn = getChannel(channel)


        val handler = ControlVidoPlayHandler(
            ControlVideoPlayInfo(
                ApiConstant.CONTROL_PLAY,
                channel,
                3,
                8,
                0,
                seekTimeArr,
                timeoutArr
            )
        )

        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                Log.d(tag, "control_replay_data:${ByteUtil.bytes2HexString(bytes)}")
                Log.d(tag + "uadateSystem onResponse", bytes.contentToString())

            }

            override fun onError(message: String) {
                Log.d(tag + "uadateSystem error", message)
            }
        })
        thread {
            EchoClient().controlPlay(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }


    }

    private fun getChannel(channel: Byte): Byte {
       var index = 0;
        when(channel.toInt()){
            0 -> index = 1
            1 -> index = 2
            2 -> index = 4
            3 -> index = 8
        }
        return index.toByte()
    }

    fun stopPlay(channel: Byte, startTimeArr: ByteArray, endTimeArr: ByteArray) {
        val handler = EchoClientHandler(RequestCommon(ApiConstant.STOP_PLAY))

        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                Log.d(tag, "control_replay_data:${ByteUtil.bytes2HexString(bytes)}")
                Log.d(tag + "uadateSystem onResponse", bytes.contentToString())

            }

            override fun onError(message: String) {
                Log.d(tag + "uadateSystem error", message)
            }
        })
        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }
}
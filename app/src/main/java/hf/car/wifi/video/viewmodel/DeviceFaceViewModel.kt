package hf.car.wifi.video.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niklaus.mvvm.utils.ByteUtil
import hf.car.wifi.video.MineApplication
import hf.car.wifi.video.callback.SocketCallback
import hf.car.wifi.video.constant.ApiConstant
import hf.car.wifi.video.model.PersonFace
import hf.car.wifi.video.model.PersonModel
import hf.car.wifi.video.model.RequestCommon
import hf.car.wifi.video.model.RequestFace
import hf.car.wifi.video.model.ResponseData
import hf.car.wifi.video.processor.EchoClient
import hf.car.wifi.video.processor.EchoClientHandler
import hf.car.wifi.video.processor.QueryFaceHandler
import hf.car.wifi.video.processor.UpdateFaceHandler
import kotlin.concurrent.thread
import kotlin.math.ceil

class DeviceFaceViewModel : ViewModel() {

    private var tag: String = javaClass.simpleName

    private var echoClient = EchoClient()

    private var echoClient2 = EchoClient()

    private var echoClient3 = EchoClient()

    /**
     * 获取所有人员信息
     */
    val devicePersonResponse: MutableLiveData<ResponseData<MutableList<PersonModel>>> =
        MutableLiveData()

    /**
     * 拉取人员人脸照片信息
     */
    val personFaceResponse: MutableLiveData<PersonFace> = MutableLiveData()

    /**
     * 更新人员信息
     */
    val updateFaceResponse: MutableLiveData<ResponseData<Boolean>> = MutableLiveData()

    /**
     * 删除人员信息
     */
    val deleteFaceResponse: MutableLiveData<ResponseData<Boolean>> = MutableLiveData()

    /**
     * 获取设备人员信息
     */
    fun loadDeviceFaceInfo() {
        val handler = EchoClientHandler(RequestCommon(ApiConstant.DEVICE_FACE, 4, 0xFF))
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handlerDeviceFace(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<MutableList<PersonModel>>()
                response.code = 500
                response.message = message
                devicePersonResponse.postValue(response)
            }
        })

        thread {
            echoClient.connect(
                ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler
            )
        }
    }

    fun closeDeviceFaceInfo() {
        echoClient.close()
    }

    /**
     * 根据id获取用户的人脸信息
     */
    fun loadPersonFaceByID(uid: Int) {
        val handler = QueryFaceHandler(RequestCommon(ApiConstant.DEVICE_FACE, 4, uid))
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handlerPersonFace(uid, bytes)
            }

            override fun onError(message: String) {
                Log.e(tag, "loadPersonFaceByID error:$message")
            }
        })

        thread {
            echoClient2.queryPersonFace(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    fun closePersonFaceByID() {
        echoClient2.close()
    }

    /**
     * 上报人员人脸信息
     */
    fun updateFace(uIdx: Int, name: String, bitmapArray: ByteArray) {
        Log.d(tag, "update bitmapArray:${bitmapArray.size}")
        val requestFace =
            RequestFace(ApiConstant.SETTING_DEVICE_FACE, bitmapArray.size, bitmapArray)
        requestFace.uIdx = uIdx
        requestFace.name = name
        val uZGZ: Long = System.currentTimeMillis() / 1000
        requestFace.uZGZ = uZGZ.toString()
        val handler = UpdateFaceHandler(requestFace)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handleUpdateFace(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<Boolean>()
                response.code = 500
                response.message = message
                updateFaceResponse.postValue(response)
            }
        })

        thread {
            echoClient3.updateFace(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    fun closeUpdateFace() {
        echoClient3.close()
    }

    /**
     * 同步时间
     */
    fun syncTime(id: Int) {
        val handler = EchoClientHandler(RequestCommon(ApiConstant.SET_DEVICE_TIM, id))
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                Toast.makeText(MineApplication().applicationContext, "同步成功", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onError(message: String) {
                val response = ResponseData<Boolean>()
                response.code = 500
                response.message = message
                deleteFaceResponse.postValue(response)
            }
        })

        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }


    /**
     * 删除人员信息
     */
    fun deleteFace(id: Int) {
        val handler = EchoClientHandler(RequestCommon(ApiConstant.DELETE_DEVICE_FACE, 4, id))
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                handleDeleteFace(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<Boolean>()
                response.code = 500
                response.message = message
                deleteFaceResponse.postValue(response)
            }
        })

        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    /**
     * 处理所有用户的返回数据
     */
    private fun handlerDeviceFace(bytes: ByteArray) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "获取所有用户信息 data:${ByteUtil.bytes2HexString(bytes)}")

        val response = ResponseData<MutableList<PersonModel>>()
        try {
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

            //数据拼装
            if (size > 20) {
                if (success == 0) {
                    val n = ceil((bytes.size / 72).toDouble()).toInt()
                    val list = mutableListOf<PersonModel>()
                    for (i in 0 until n) {
                        val bb = ByteArray(72)
                        val person = PersonModel("John")
                        person.person = bb
                        list.add(person)
                    }
                    Log.d(tag, "list size:${list.size}")

                    var temp = 71
                    var listIndex = 0
                    var byteArrayIndex = 0
                    for (i in 20 until size) {
                        if ((temp + 20) < i) {
                            temp += 72
                            listIndex++
                            byteArrayIndex = 0
                        }
                        list[listIndex].person[byteArrayIndex] = bytes[i]
                        byteArrayIndex++
                    }

                    response.code = 0
                    response.data = list
                } else {
                    response.code = success
                    response.message = "服务返回错误:${success}"
                }
            } else if (size == 20) {
                if (success == 0) {
                    response.code = 0
                    response.data = mutableListOf()
                } else {
                    response.code = success
                    response.message = "服务返回错误:${success}"
                }
            } else {
                //失败
                response.message = "返回数据内容异常"
            }
        } catch (e: Exception) {
            response.message = e.message.toString()
        }

        devicePersonResponse.postValue(response)
    }

    /**
     * 处理获取到的人员人脸数据
     */
    private fun handlerPersonFace(uid: Int, bytes: ByteArray) {
        val size = bytes.size
        val response = ByteUtil.bytes2HexString(bytes)
        Log.d(tag, "received data size:$size")
        Log.d(tag, "单个人脸数据 data:$response")

        //获取成功失败
        val successArray = ByteArray(4)
        var successIndex = 0
        val dataArray = ByteArray(4)
        var dataIndex = 0
        if (size >= 20) {
            for (i in 0 until 20) {
                if (i in 12..15) {
                    successArray[successIndex] = bytes[i]
                    successIndex++
                }
                if (i in 16..19) {
                    dataArray[dataIndex] = bytes[i]
                    dataIndex++
                }
            }
        }

        val faceArray = ByteArray(size - 92)
        if (size >= 92) {
            var faceIndex = 0
            for (i in 92 until size) {
                faceArray[faceIndex] = bytes[i]
                faceIndex++
            }
        }
        val success = ByteUtil.bytes2HexStringLE(successArray)
        Log.d(tag, "received success:$success")

        Log.d(tag, "received dataLen:${ByteUtil.bytes2HexString(dataArray)}")

        val personFace = PersonFace(uid, faceArray)
        personFaceResponse.postValue(personFace)
    }

    /**
     * 处理更新人员的返回数据
     */
    private fun handleUpdateFace(bytes: ByteArray) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "上报人脸数据 data:${ByteUtil.bytes2HexString(bytes)}")

        val response = ResponseData<Boolean>()
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
        updateFaceResponse.postValue(response)
    }

    /**
     * 处理删除人员的返回数据
     */
    private fun handleDeleteFace(bytes: ByteArray) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "删除人员信息 data:${ByteUtil.bytes2HexString(bytes)}")

        val response = ResponseData<Boolean>()
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
        deleteFaceResponse.postValue(response)
    }
}
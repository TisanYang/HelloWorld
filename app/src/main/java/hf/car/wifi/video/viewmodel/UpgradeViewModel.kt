package hf.car.wifi.video.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niklaus.mvvm.utils.ByteUtil
import hf.car.wifi.video.callback.SocketCallback
import hf.car.wifi.video.constant.ApiConstant
import hf.car.wifi.video.model.RequestCommon
import hf.car.wifi.video.model.RequestFileInfo
import hf.car.wifi.video.model.ResponseData
import hf.car.wifi.video.model.SendFileInfo
import hf.car.wifi.video.processor.EchoClient
import hf.car.wifi.video.processor.EchoClientHandler
import hf.car.wifi.video.processor.EchoServer
import hf.car.wifi.video.processor.SendFilePackageHandler
import hf.car.wifi.video.processor.UpdateSystemHandler
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import kotlin.concurrent.thread


class UpgradeViewModel : ViewModel() {

    private var tag: String = javaClass.simpleName

    private val server = EchoServer()

    val versionContent: MutableLiveData<ResponseData<String>> = MutableLiveData()

    var packageNum: Short = 0

    var off = 0
    val packageSize = 163840

    lateinit var checkIdArr: ByteArray

    /**
     * 获取版本升级
     */
    fun loadUpgradeInfo() {
        val request = RequestCommon(ApiConstant.UPGRADE)
        val handler = EchoClientHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                Log.d(tag, "loadUpgradeInfo data:${ByteUtil.bytes2HexString(bytes)}")
                handleUpgradeInfo(bytes)
            }

            override fun onError(message: String) {
                val response = ResponseData<String>()
                response.code = 500
                response.message = message
                versionContent.postValue(response)
            }
        })

        thread {
            EchoClient().connect(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }
    }

    /**
     * 处理系统信息结果
     */
    private fun handleUpgradeInfo(bytes: ByteArray) {
        val size = bytes.size
        Log.d(tag, "received data size:$size")
        Log.d(tag, "系统信息 data:${ByteUtil.bytes2HexString(bytes)}")

        val response = ResponseData<String>()
        //获取成功失败
        val successArray = ByteArray(4)
        var successIndex = 0
        val textContent = ByteArray(size - 19)
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
                textContent[index] = bytes[i]
                index++
            }
        }

        val success = ByteUtil.byteArrayToInt(successArray)
        val sysChannelArray = ByteUtil.byteToStr(textContent)
        Log.d(tag, "received success:$success")
        Log.d(tag, "received success:$sysChannelArray")

        if (success == 0) {
            response.code = 0
            response.data = ByteUtil.byteToStr(textContent)
        } else {
            response.code = success
            response.message = "服务返回错误:${success}"
        }

        versionContent.postValue(response)
    }

    /**
     * 文件预升级
     */
    fun uadateSystem(totalPath: String, path: String) {
        val file = File(totalPath)
        val length = file.length().toInt()

        val fis = FileInputStream(file)
        val buffer = ByteBuffer.allocate(64)
        fis.read(buffer.array(), 0, 64)
        fis.close()

        val request = RequestFileInfo(ApiConstant.FILE_UPGRADE, 64, buffer.array(), length)
        val handler = UpdateSystemHandler(request)
        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                Log.d(tag, "uadateSystem data:${ByteUtil.bytes2HexString(bytes)}")
                Log.d(tag + "uadateSystem onResponse", bytes.contentToString())
                //响应以后 准备发送数据包
                checkIdArr = ByteArray(4)
                System.arraycopy(bytes, 20, checkIdArr, 0, 4)
                //发送
                sendPackage(checkIdArr, file)
            }

            override fun onError(message: String) {
                Log.d(tag + "uadateSystem error", message)
            }
        })

        thread {
            EchoClient().preUpdateSystem(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }

    }

    @Synchronized
    private fun sendPackage(checkIdArr: ByteArray, file: File) {

        val totalLen = file.length()
        var packageLength = packageSize
        if ((totalLen - (off * packageSize + 64) < packageLength)) {
            packageLength = (totalLen - (off * packageSize + 64)).toInt()
        }

        val contentArr = ByteBuffer.allocate(packageLength)
        val fis = FileInputStream(file)
        fis.skip((off * packageSize + 64).toLong())
        fis.read(contentArr.array())
        //fis.close()

        if (packageLength < 512) {
            Log.d(tag, "升级数据发送完毕")
            return
        }

        val request = SendFileInfo(
            ApiConstant.FILE_SEND_PACKAGE, 12 + packageLength, checkIdArr,
            0, 0, packageNum, packageLength, contentArr.array(), file
        )
        val handler = SendFilePackageHandler(request)

        handler.setSocketCallback(object : SocketCallback {
            override fun onResponse(bytes: ByteArray) {
                Log.d(tag, "发送升级包响应的数据:${ByteUtil.bytes2HexString(bytes)}")
                //Log.d(tag + "uadateSystem onResponse", bytes.contentToString())
                //每发送一包，就要重新连接一次，这是短连接
                off += 1
                if (packageLength != 0) {
                    Log.d(tag, "准备发送第${off}包")
                    packageNum = (packageNum + 1).toShort()
                }
                val totalLen = file.length()
                //先判断当前已发送长度
                if (off > totalLen / packageSize + 1 || packageLength == 0) {
                    Log.d(tag, "升级完毕:${ByteUtil.bytes2HexString(bytes)}")
                    return
                } else {
                    sendPackage(checkIdArr, file)
                }
            }

            override fun onError(message: String) {
                Log.d(tag + "uadateSystem error", message)
                fis.close()
            }
        })


        thread {
            EchoClient().sendPackageData(ApiConstant.SOCKET_IP, ApiConstant.SOCKET_PORT, handler)
        }

    }


    fun path2Bytes(path: String): ByteArray {
        val sorc: ByteArray = path.toByteArray()
        val digestByte = ByteArray(32)
        if (sorc.size <= digestByte.size) {
            System.arraycopy(sorc, 0, digestByte, 0, sorc.size)
        } else {
            System.arraycopy(sorc, 0, digestByte, 0, 32)
        }

        return digestByte
    }


}
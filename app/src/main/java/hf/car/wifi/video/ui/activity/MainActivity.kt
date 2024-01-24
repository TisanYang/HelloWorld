package hf.car.wifi.video.ui.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.niklaus.mvvm.utils.NetUtils
import hf.car.wifi.video.R
import hf.car.wifi.video.base.EmptyViewModel
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.constant.ApiConstant
import com.niklaus.mvvm.utils.ByteUtil
import hf.car.wifi.video.databinding.ActivityMainBinding
import hf.car.wifi.video.model.RequestCommon
import hf.car.wifi.video.model.RequestFace
import hf.car.wifi.video.model.RequestVideo
import hf.car.wifi.video.processor.EchoClient
import hf.car.wifi.video.processor.EchoClientHandler
import hf.car.wifi.video.processor.UpdateFaceHandler
import hf.car.wifi.video.processor.QueryVideoHandler
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread

class MainActivity :
    MBaseActivity<EmptyViewModel, ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val ip: String = ApiConstant.SOCKET_IP

    private val port: Int = ApiConstant.SOCKET_PORT

    override fun initViewModel(): EmptyViewModel =
        ViewModelProvider(this@MainActivity)[EmptyViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initListener()
    }

    override fun initView() {
        super.initView()

        binding.edIp.setText(ip)
        binding.edPort.setText(port.toString())
    }

    override fun initListener() {
        super.initListener()

        binding.btnConnect.setOnClickListener {
            val tvIp = binding.edIp.text.toString().trim()
            val tvPort = binding.edPort.text.toString().trim()
            if (TextUtils.isEmpty(tvIp) || TextUtils.isEmpty(tvPort)) {
                Toast.makeText(this@MainActivity, "请输入ip或port", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            thread {
                val handler = EchoClientHandler(RequestCommon(ApiConstant.SYS_INFO))
                EchoClient().connect(tvIp, tvPort.toInt(), handler)
            }
        }

        binding.btnDisconnect.setOnClickListener {
        }

        binding.btnSysInfo.setOnClickListener {
            val handler = EchoClientHandler(RequestCommon(ApiConstant.SYS_INFO))
            thread {
                EchoClient().connect(ip, port, handler)
            }
        }

        binding.btnDevInfo.setOnClickListener {
            val handler = EchoClientHandler(RequestCommon(ApiConstant.DEVICE_STATE))
            thread {
                EchoClient().connect(ip, port, handler)
            }
        }

        binding.btnFaceInfo.setOnClickListener {
            val handler = EchoClientHandler(RequestCommon(ApiConstant.DEVICE_FACE, 4, 0xFFFF))
            thread {
                EchoClient().connect(ip, port, handler)
            }
        }

        binding.btnUpdateFace.setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.tt_face)
            val os = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            val face = os.toByteArray()
            val handler =
                UpdateFaceHandler(RequestFace(ApiConstant.SETTING_DEVICE_FACE, face.size, face))
            thread {
                EchoClient().updateFace(ip, port, handler)
            }
        }

        binding.btnDeleteFace.setOnClickListener {
            val handler = EchoClientHandler(RequestCommon(ApiConstant.DELETE_DEVICE_FACE, 4, 0xFFFF))
            thread {
                EchoClient().connect(ip, port, handler)
            }
        }

        binding.btnOpenVideo.setOnClickListener {
            val deviceIp = NetUtils.getIpAddress()
            deviceIp?.let {
                val request = RequestVideo(ApiConstant.OPEN_REAL_VIDEO)
                request.ip = it
                request.port = 8888
                val handler = QueryVideoHandler(request)

                thread {
                    EchoClient().openVideo(ip, port, handler)
                }
            }
        }

        binding.btnCloseVideo.setOnClickListener {
            val handler = EchoClientHandler(RequestCommon(ApiConstant.CLOSE_REAL_VIDEO))
            thread {
                EchoClient().connect(ip, port, handler)
            }
        }
    }
}
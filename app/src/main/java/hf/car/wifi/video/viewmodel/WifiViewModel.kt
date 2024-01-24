package hf.car.wifi.video.viewmodel

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.niklaus.mvvm.utils.NetUtils
import hf.car.wifi.video.constant.ApiConstant
import kotlin.concurrent.thread

class WifiViewModel(application: Application) : AndroidViewModel(application) {

    private val tag: String = javaClass.simpleName

    val wifiResponse: MutableLiveData<Int> = MutableLiveData()

    fun loadWifiInfo() {
        thread {
            val connectivityManager =
                ContextCompat.getSystemService(getApplication(), ConnectivityManager::class.java)
            val currentNetwork = connectivityManager?.activeNetwork
            val caps = connectivityManager?.getNetworkCapabilities(currentNetwork)
            val linkProperties = connectivityManager?.getLinkProperties(currentNetwork)
            Log.d(tag, "caps:${caps.toString()}")
            Log.d(tag, "linkProperties:${linkProperties.toString()}")

            var ret = -1
            if (null == caps) {
                ret = -3
            } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                ret = -2
            } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                ret = NetUtils.pingIP(ApiConstant.SOCKET_IP)
            }
            wifiResponse.postValue(ret)
        }
    }
}
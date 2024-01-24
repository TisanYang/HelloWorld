package com.niklaus.mvvm.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Collections

object NetUtils {

    /**
     * 通过ping IP
     * @return 0-成功，其他失败
     */
    fun pingIP(ip: String): Int {
        val runtime = Runtime.getRuntime()
        try {
            val p = runtime.exec("ping -c 3 -w 3 $ip")
            return p.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    fun getIpAddress(): String? {
        try {
            //获取所有网络接口
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                //获取该网络接口的所有 IP 地址
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getIPAddress(context: Context): String? {
        val info =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (info != null && info.isConnected) {
            if (info.type == ConnectivityManager.TYPE_MOBILE) { //当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    val en = NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        val intf = en.nextElement()
                        val enumIpAddr = intf.inetAddresses
                        while (enumIpAddr.hasMoreElements()) {
                            val inetAddress = enumIpAddr.nextElement()
                            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                return inetAddress.getHostAddress()
                            }
                        }
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                }
            } else if (info.type == ConnectivityManager.TYPE_WIFI) { //当前使用无线网络
                val wifiManager =
                    context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                return intIP2StringIP(wifiInfo.ipAddress)
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null
    }

    private fun intIP2StringIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." + (ip shr 8 and 0xFF) + "." + (ip shr 16 and 0xFF) + "." + (ip shr 24 and 0xFF)
    }

    /**
     * 反转ip地址
     */
    fun ipAddressInversion(ip: String): String {
        val sb = StringBuilder()
        val cc = ip.split(".")
        for (i in (cc.size - 1) downTo 0) {
            if (i == 0) {
                sb.append(cc[i])
            } else {
                sb.append(cc[i]).append(".")
            }
        }
        return sb.toString()
    }
}
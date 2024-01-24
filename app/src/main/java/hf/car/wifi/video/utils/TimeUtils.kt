package hf.car.wifi.video.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getYMDHMS(): List<String> {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy:M:d:H:m:s")
        return current.format(formatter).split(":")
    }
}
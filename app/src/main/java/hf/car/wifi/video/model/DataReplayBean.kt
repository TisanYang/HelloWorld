package hf.car.wifi.video.model

data class DataReplayBean(val filename: ByteArray,val lIP: ByteArray,
                          val channel: ByteArray,val type: ByteArray,
                          val alm: ByteArray,val rev: ByteArray,
                          val sttime: ByteArray,val etime: ByteArray,
                          val size: ByteArray)
package hf.car.wifi.video.processor

import android.util.Log
import hf.car.wifi.video.callback.SocketCallback
import hf.car.wifi.video.model.RequestVideo
import com.niklaus.mvvm.utils.ByteUtil
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.nio.ByteBuffer
import java.nio.ByteOrder

class QueryVideoHandler(private val er: RequestVideo) : SimpleChannelInboundHandler<ByteBuf>() {

    private val tag: String = javaClass.simpleName

    private var mCallback: SocketCallback? = null

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        Log.e(tag, "连接断开")
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        val dataSize = 16

        Log.d(tag, "连接成功:${20 + dataSize + er.length}")

        val header = ByteBuffer.allocate(20 + dataSize + er.length)
        header.order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(0, er.mark)
        header.putChar(4, Char(er.ver))
        header.putChar(5, Char(er.msgfromto))
        header.putChar(6, Char(er.mtype))
        header.putShort(7, er.rev)
        header.putInt(8, er.sn)
        header.putShort(10, er.nCmd)
        header.putShort(12, er.error)
        header.putInt(16, dataSize + er.length)

        //channel
        header.putShort(20, er.channel)
        //protocol
        header.putChar(21, Char(1))
        //wrev
        header.putChar(22, Char(0))
        //sessionId
        header.putShort(24, er.channel)
        //ip
        Log.d(tag, "server ip:${er.ip},port:${er.port}")
        val ips = er.ip.split(".")
        for (i in ips.indices) {
            header.putInt(28 + i, ips[i].toInt())
        }
        //port
        header.putInt(32, er.port)
        //type
        header.putChar(34, Char(1))
        //isAudio
//        header.putChar(35, Char(0))
        Log.d(tag, "发送的数据:${ByteUtil.bytes2HexString(header.array())}")

        ctx!!.writeAndFlush(Unpooled.copiedBuffer(header))
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: ByteBuf?) {
        Log.d(tag, "channelRead This is receive server:$msg")

        //读取服务端消息
        msg?.let {
            val bytes = ByteArray(msg.readableBytes())
            msg.readBytes(bytes)
            mCallback?.onResponse(bytes)
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
        super.channelReadComplete(ctx)
        Log.d(tag, "channelReadComplete")
        //读取结束
        ctx!!.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        super.exceptionCaught(ctx, cause)

        //异常处理
        mCallback?.onError(cause?.message ?: "未知错误")
        cause?.printStackTrace()
        ctx?.close()
    }

    fun setSocketCallback(callback: SocketCallback) {
        this.mCallback = callback
    }
}
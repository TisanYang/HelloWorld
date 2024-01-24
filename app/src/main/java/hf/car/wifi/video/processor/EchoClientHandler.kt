package hf.car.wifi.video.processor

import android.util.Log
import com.niklaus.mvvm.utils.ByteUtil
import hf.car.wifi.video.callback.SocketCallback
import hf.car.wifi.video.model.RequestCommon
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.nio.ByteBuffer
import java.nio.ByteOrder

class EchoClientHandler(private val er: RequestCommon) : SimpleChannelInboundHandler<ByteBuf>() {

    private val tag: String = javaClass.simpleName

    private var counter: Int = 0

    private var mCallback: SocketCallback? = null

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        Log.e(tag, "连接断开")
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        Log.d(tag, "连接成功,发送总长度:${20 + er.length}")

        val header = ByteBuffer.allocate(20 + er.length)
        header.order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(0, er.mark)
        header.putChar(4, Char(er.ver))
        header.putChar(5, Char(er.msgfromto))
        header.putChar(6, Char(er.mtype))
        header.putShort(7, er.rev)
        header.putInt(8, er.sn)
        header.putShort(10, er.nCmd)
        header.putShort(12, er.error)
        header.putInt(16, er.length)
        if (er.length > 0) {
            header.putInt(20, er.data)
        }

        Log.d(tag, "发送的数据:${ByteUtil.bytes2HexString(header.array())}")
        ctx!!.writeAndFlush(Unpooled.copiedBuffer(header))
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
        super.channelReadComplete(ctx)
        Log.d(tag, "channelReadComplete")
        //读取结束
        ctx!!.flush()
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: ByteBuf?) {
        counter++
        Log.d(tag, "channelRead This  is " + "$counter" + "times receive server:$msg")

        //读取服务端消息
        msg?.let {
            val bytes = ByteArray(msg.readableBytes())
            msg.readBytes(bytes)
            mCallback?.onResponse(bytes)
        }
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
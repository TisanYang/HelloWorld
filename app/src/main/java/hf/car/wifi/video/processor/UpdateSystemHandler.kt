package hf.car.wifi.video.processor

import android.util.Log
import com.niklaus.mvvm.utils.ByteUtil
import hf.car.wifi.video.callback.SocketCallback
import hf.car.wifi.video.model.RequestFileInfo
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.nio.ByteBuffer
import java.nio.ByteOrder

class UpdateSystemHandler(private val er: RequestFileInfo) :
    SimpleChannelInboundHandler<ByteBuf>() {


    private val tag: String = javaClass.simpleName

    private var mCallback: SocketCallback? = null

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        Log.e(tag, "连接断开")
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        super.channelActive(ctx)
        Log.d(tag, "连接成功:${20 + er.length}")

        //这里是开辟出来的大小
        val header = ByteBuffer.allocate(20 + 64)
        header.order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(0, er.mark)
        header.putChar(4, Char(er.ver))
        header.putChar(5, Char(er.msgfromto))
        header.putChar(6, Char(er.mtype))
        header.putShort(7, er.rev)
        header.putInt(8, er.sn)
        header.putShort(10, er.nCmd)
        header.putShort(12, er.error)
        //这里的dataSize，是后面所有字节内容的长度，不是总长度
        header.putInt(16, 64)
        //第一种写法
//        System.arraycopy(er.byteArray,0,header.array(),20,4)  //mark
//        System.arraycopy(er.byteArray,4,header.array(),24,32) //cpath
//        System.arraycopy(er.byteArray,36,header.array(),56,4) //version
//        System.arraycopy(er.byteArray,40,header.array(),60,1) //type
//        System.arraycopy(er.byteArray,41,header.array(),61,1) //delcfg
//        System.arraycopy(er.byteArray,42,header.array(),62,1) //saveflag
//        System.arraycopy(er.byteArray,43,header.array(),63,1) //updateflag
//        System.arraycopy(er.byteArray,44,header.array(),64,2) //digest
//        header.putInt(66,er.fileLen)
        System.arraycopy(er.byteArray,0,header.array(),20,64)

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
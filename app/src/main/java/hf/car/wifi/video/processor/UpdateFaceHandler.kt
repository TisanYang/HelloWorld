package hf.car.wifi.video.processor

import android.util.Log
import hf.car.wifi.video.callback.SocketCallback
import hf.car.wifi.video.model.RequestFace
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.nio.ByteBuffer
import java.nio.ByteOrder

class UpdateFaceHandler(private val er: RequestFace) : SimpleChannelInboundHandler<ByteBuf>() {

    private val tag: String = javaClass.simpleName

    private var mCallback: SocketCallback? = null

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        Log.e(tag, "连接断开")
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        //4+32+32+4=72(长度不能超过200k)
        val dataSize = 72

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
        if (er.length > 0) {
            val picSize = er.data.size
            val uid: ByteArray = er.name.toByteArray()
            val zgz: ByteArray = er.uZGZ.toByteArray()
            //拼装ai_face_t
            //uIdx--标号
            header.putChar(20, Char(255))
            //rev--保留
            header.putChar(21, Char(1))
            //uIdLen--人脸编号长度
            header.putChar(22, Char(uid.size))
            //uZGZlen--资格证id长度
            header.putChar(23, Char(zgz.size))
            //uFaceId--人脸编号
            for (i in uid.indices) {
                header.put(24 + i, uid[i])
            }
            //uZGZ--资格证id
            for (i in zgz.indices) {
                header.put(56 + i, zgz[i])
            }
            //dlen--图片长度
            header.putInt(88, picSize)

            Log.d(tag, "picSize:${picSize}")
            for (i in 0 until picSize) {
                header.put(92 + i, er.data[i])
            }
        }

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
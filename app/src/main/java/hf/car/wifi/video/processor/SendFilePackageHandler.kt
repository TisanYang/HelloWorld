package hf.car.wifi.video.processor

import android.util.Log
import com.niklaus.mvvm.utils.ByteUtil
import hf.car.wifi.video.callback.SocketCallback
import hf.car.wifi.video.model.SendFileInfo
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SendFilePackageHandler(private val er: SendFileInfo) :
    SimpleChannelInboundHandler<ByteBuf>() {


    private val tag: String = javaClass.simpleName

    private var mCallback: SocketCallback? = null

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        Log.e(tag, "连接断开")
    }

    /**
     * 这里发送第一次的数据
     */
    override fun channelActive(ctx: ChannelHandlerContext?) {
        super.channelActive(ctx)
        Log.d(tag, "连接成功:${20 + er.length}")

        //这里是开辟出来的大小
        val header = ByteBuffer.allocate(20 + 12 + er.dataArray.size)
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
        header.putInt(16, 12 + er.dataArray.size)

        System.arraycopy(er.checkId, 0, header.array(), 20, 4)  //checkId
        header.put(24, 0x00) //datetype
        header.put(25, 0x00) //rev
        header.putShort(26, er.sync) //rev
        header.putInt(28, er.packageLen)
        System.arraycopy(er.dataArray, 0, header.array(), 32, er.dataArray.size)

        //查看发送额数据头
//        val logPackage = ByteBuffer.allocate(32)
//        System.arraycopy(header.array(),0,logPackage.array(),0,96)
//        Log.d(tag, "发送的数据:${ByteUtil.bytes2HexString(logPackage.array())}")
        ctx!!.writeAndFlush(Unpooled.copiedBuffer(header))
    }

    /**
     * 读取以后再重新发送
     */
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: ByteBuf) {
       /* Log.d(tag, "channelRead This is receive server:${msg.array().size}")

        val totalLen = er.file.length()
        //先判断当前已发送长度
        if (er.sync + 1 > totalLen / er.packageLen + 1) {
            //读取服务端消息
            msg.let {
                val bytes = ByteArray(msg.readableBytes())
                msg.readBytes(bytes)
                mCallback?.onResponse(bytes)
            }
            return
        }

        er.sync = (er.sync + 1).toShort()
        for (i in 0 until 4) {
            er.checkId[i] = msg.array()[i]
        }

        //这里是开辟出来的大小
        val header = ByteBuffer.allocate(20 + 12 + 163840)
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
        header.putInt(16, 32 + 163840)
        System.arraycopy(er.checkId, 0, header.array(), 20, 4)  //checkId
        header.put(24, 0x00) //datetype
        header.put(25, 0x00) //rev
        header.putShort(26, er.sync) //rev
        header.putInt(28, er.packageLen)
        System.arraycopy(er.dataArray, 0, header.array(), 32, 163840)

        Log.d(tag, "发送的数据:${ByteUtil.bytes2HexString(header.array())}")

        ctx!!.writeAndFlush(Unpooled.copiedBuffer(header))*/

        Log.d(tag, "channelRead This is receive server:${msg.array()}")

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
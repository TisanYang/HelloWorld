package hf.car.wifi.video.processor

import android.util.Log
import com.niklaus.mvvm.utils.ByteUtil
import hf.car.wifi.video.callback.SocketCallback
import hf.car.wifi.video.model.GetVideoReplayInfo
import hf.car.wifi.video.model.SendFileInfo
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ReplayVideoHandler(private val er: GetVideoReplayInfo) :
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
        val header = ByteBuffer.allocate(20 + 36)
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
        header.putInt(16, 36)
        //要通过arrayCopy的形式存数据，不能直接用put
/*            u_8      ch;/*通道0~3，按照位掩码*/
//            u_8      downtype;/*0-错误 1-录像下载/回放，2-图片下载，3-日志下载4-配置文件下载  */
//            u_8      rev;
//            u_8      onlyKey;
//            u_32     startoff;       /*下载的文件名,前两个表示磁盘,后8位为文件名*/
//            u_32     datalen;       /*下载的长度*/
//            u_32     lIp;           /**/
//            u_16     wport;         /**/
//            u_16     wrate;         /*下载限速，默认填0*/
//            time_t   time_start;    /*起始时间*/  4
//            time_t   time_end;      /*结束时间*/ 4
//            time_t   time_cur;      /*当前下载时间*/ 4
//            u_32     sessionId;  /*非0*/
        *  val dataReplayBean = DataReplayBean(
                byteArrayOf(array[0], array[1], array[2], array[4]), //filename
                byteArrayOf(array[4], array[5], array[6], array[7]), //lIP
                byteArrayOf(array[8]),                               //channel
                byteArrayOf(array[9]),                               //type
                byteArrayOf(array[10]),                              //alm
                byteArrayOf(array[11]),                              //rev
                byteArrayOf(array[12], array[13], array[14], array[15]),//sttime
                byteArrayOf(array[16], array[17], array[18], array[19]),//etime
                byteArrayOf(array[20], array[21], array[22], array[23]) //size
            ) 底下的长度应该是36
        * */
        System.arraycopy(er.bean.channel,0,header.array(),20,er.bean.channel.size) //ch
        System.arraycopy(byteArrayOf(1),0,header.array(),21,er.bean.type.size)        //downtype
        System.arraycopy(er.bean.rev,0,header.array(),22,er.bean.rev.size)          //rev
        System.arraycopy(er.bean.channel,0,header.array(),23,er.bean.channel.size)  //onlyKey
        System.arraycopy(er.bean.filename,0,header.array(),24,er.bean.filename.size) // startoff 长度4
        System.arraycopy(er.bean.size,0,header.array(),28,er.bean.size.size) // datalen 长度4


        val ips = er.ip.split(".")
        //header.putInt(28 + i, ips[i].toInt())
        //System.arraycopy(ubyteArrayOf(ips[0].toUByte(),ips[1].toUByte(),ips[2].toUByte(),ips[3].toUByte()),0,header.array(),32,er.bean.lIP.size) // lIp 长度4
        //底下四个是ip,转成int来计算
        header.putInt(32,ips[0].toInt())
        header.putInt(33,ips[1].toInt())
        header.putInt(34,ips[2].toInt())
        header.putInt(35,ips[3].toInt())

        System.arraycopy(byteArrayOf(-7,16),0,header.array(),36,2) // wport 长度2

        System.arraycopy(byteArrayOf(0,0),0,header.array(),38,2) // wrate 下载限速 长度2
        System.arraycopy(er.bean.sttime,0,header.array(),40,er.bean.sttime.size) // time_start 长度4
        System.arraycopy(er.bean.etime,0,header.array(),44,er.bean.etime.size) // time_end 长度4
        System.arraycopy(er.bean.sttime,0,header.array(),48,er.bean.sttime.size) // time_cur 长度4 - 设为起始时间
        System.arraycopy(byteArrayOf(0,0,0,1),0,header.array(),52,4) // time_cur 长度4



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
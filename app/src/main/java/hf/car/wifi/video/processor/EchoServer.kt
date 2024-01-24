package hf.car.wifi.video.processor

import android.util.Log
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.timeout.ReadTimeoutHandler
import java.nio.ByteOrder
import java.util.concurrent.Executors

class EchoServer {

    private val tag: String = javaClass.simpleName

    private var channelFuture: ChannelFuture? = null

    fun start(ip: String, port: Int, handler: EchoServerHandler) {
        //new 一个主线程组
        val bossGroup: EventLoopGroup = NioEventLoopGroup(1)
        //new 一个工作线程组
        val workGroup: EventLoopGroup = NioEventLoopGroup(200)
        try {
            val bootstrap = ServerBootstrap()
            bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        ch?.let {
                            //设置超时15分钟，没有收到客户端的消息就关闭channel
                            ch.pipeline().addLast(ReadTimeoutHandler(60 * 5))
                            ch.pipeline().addLast(
                                LengthFieldBasedFrameDecoder(
                                    ByteOrder.LITTLE_ENDIAN, 500000,
                                    12, 4, 16,
                                    0, true
                                )
                            )
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
                //设置队列大小
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 两小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.SO_KEEPALIVE, true)
            //绑定端口,开始接收进来的连接
            channelFuture = bootstrap.bind(ip, port).sync()
            channelFuture?.addListener {
                if (it.isSuccess) {
                    Log.d(tag, "服务启动成功")
                } else {
                    Log.e(tag, "服务启动失败")
                }
            }
            Log.d(tag, "服务器启动开始监听端口:${port}")
            channelFuture?.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //关闭主线程组
            bossGroup.shutdownGracefully()
            //关闭工作线程组
            workGroup.shutdownGracefully()
        }
    }

    fun stop() {
        Executors.newSingleThreadScheduledExecutor().submit {
            channelFuture?.channel()?.close()
        }
    }
}
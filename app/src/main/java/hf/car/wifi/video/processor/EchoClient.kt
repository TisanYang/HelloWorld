package hf.car.wifi.video.processor

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.timeout.ReadTimeoutHandler
import java.nio.ByteOrder

class EchoClient {

    private var channelFuture: ChannelFuture? = null

    fun close() {
        channelFuture?.let {
            if (it.channel().isOpen) {
                it.channel().close()
            }
        }
    }

    /**
     * 发送简单数据信息
     */
    fun connect(ip: String, port: Int, handler: EchoClientHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        ch?.let {
                            ch.pipeline().addLast(
                                LengthFieldBasedFrameDecoder(
                                    ByteOrder.LITTLE_ENDIAN, Int.MAX_VALUE,
                                    16, 4, 0,
                                    0, true
                                )
                            )
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            channelFuture = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            channelFuture?.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    /**
     * 获取单个人脸数据信息
     */
    fun queryPersonFace(ip: String, port: Int, handler: QueryFaceHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        ch?.let {
                            ch.pipeline().addLast(ReadTimeoutHandler(60))
                            ch.pipeline().addLast(
                                LengthFieldBasedFrameDecoder(
                                    ByteOrder.LITTLE_ENDIAN, Int.MAX_VALUE,
                                    16, 4, 0,
                                    0, true
                                )
                            )
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
                .option(ChannelOption.SO_RCVBUF, 1024 * 200)
            //发起异步连接操作
            channelFuture = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            channelFuture?.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    /**
     * 上报人员人脸数据
     */
    fun updateFace(ip: String, port: Int, handler: UpdateFaceHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        ch?.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            channelFuture = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            channelFuture?.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    /**
     * 更新时间
     */
    fun getReplayData(ip: String, port: Int, handler: ReplayClientHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        ch?.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            channelFuture = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            channelFuture?.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }


    /**
     * 更新时间
     */
    fun updateTime(ip: String, port: Int, handler: UpdateTimeHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        ch?.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            channelFuture = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            channelFuture?.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    /**
     * 开启视频画面
     */
    fun openVideo(ip: String, port: Int, handler: QueryVideoHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        ch?.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            val future: ChannelFuture = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            future.channel().closeFuture().sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    /**
     * 准备系统升级
     */
    fun preUpdateSystem(ip: String, port: Int, handler: UpdateSystemHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            val future = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            future.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    fun sendPackageData(ip: String, port: Int, handler: SendFilePackageHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            val future = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            future.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    fun setBsdData(ip: String, port: Int, handler: SetBsdHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            val future = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            future.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    fun getDmsData(ip: String, port: Int, handler: GetDmsHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            val future = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            future.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    fun setDmsData(ip: String, port: Int, handler: SetDmsHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            val future = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            future.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    fun setLockCarTime(ip: String, port: Int, handler: SetLockCarHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            val future = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            future.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    fun requestReplay(ip: String, port: Int, handler: ReplayVideoHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            val future = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            future.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }

    fun controlPlay(ip: String, port: Int, handler: ControlVidoPlayHandler) {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.let {
                            ch.pipeline().addLast(handler)
                        }
                    }
                })
            //发起异步连接操作
            val future = bootstrap.connect(ip, port).sync()
            //等待客户端链路关闭
            future.channel()?.closeFuture()?.sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //优雅关闭线程组
            group.shutdownGracefully();
        }
    }
}
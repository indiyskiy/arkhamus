import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.JsonRequestDecoder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.JsonToObjectRequestDecoder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.ProcessingHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPipeline
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.string.StringEncoder
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class NettyServer {
    private var future: ChannelFuture? = null

    @Autowired
    lateinit var jsonDecoder: JsonRequestDecoder

    @Autowired
    lateinit var jsonToObjectDecoder: JsonToObjectRequestDecoder

    companion object {
        private val PORT: Int = 8081
        var logger: Logger = LoggerFactory.getLogger(NettyServer::class.java)
    }

    @PostConstruct
    fun start() {
        logger.info("Starting Netty on port $PORT")
        // Create boss & worker groups. Boss accepts connections from client. Worker
        // handles further communication through connections.
        val bossGroup: EventLoopGroup = NioEventLoopGroup(1)
        val workerGroup: EventLoopGroup = NioEventLoopGroup()

        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup) // Set boss & worker groups
                .channel(NioServerSocketChannel::class.java) // Use NIO to accept new connections.
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    public override fun initChannel(ch: SocketChannel) {
                        val p: ChannelPipeline = ch.pipeline()
                        /*
                         * Socket/channel communication happens in byte streams. String decoder &
                         * encoder helps conversion between bytes & String.
                         */
                        p.addLast(jsonDecoder)
                        p.addLast(jsonToObjectDecoder)
                        p.addLast(StringEncoder())
                        // This is our custom server handler which will have logic for chat.
                        p.addLast(ProcessingHandler())
                    }
                }
                )
            // Start the server.
            val f: ChannelFuture = b.bind(PORT).sync()
            println("Netty Server started. Ready to accept clients.")

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync()
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }

    @PreDestroy
    fun stop() {
        future?.channel()?.close()?.sync()
    }
}
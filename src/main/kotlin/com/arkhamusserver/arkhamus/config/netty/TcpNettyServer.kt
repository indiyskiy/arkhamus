package com.arkhamusserver.arkhamus.config.netty

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.util.concurrent.Executors


@Component
class TcpNettyServer {
    @Autowired
    lateinit var serverBootstrap: ServerBootstrap

    @Autowired
    lateinit var tcpPort: InetSocketAddress

    private var channel: Channel? = null

    companion object {
        var logger: Logger = LoggerFactory.getLogger(TcpNettyServer::class.java)
    }

    fun start() {
        Executors.newSingleThreadExecutor().submit {
            try {
                logger.info("Netty try to start : port {}", tcpPort.port)
                val serverChannelFuture = serverBootstrap.bind(tcpPort).sync()
                logger.info("Netty is started : port {}", tcpPort.port)
                channel = serverChannelFuture.channel().closeFuture().sync().channel()
            } catch (ex: Exception) {
                logger.error("Netty server failed to start", ex)
            }
        }
    }

    @PreDestroy
    fun stop() {
        channel?.close()?.sync()
        channel?.parent()?.close()
    }
}
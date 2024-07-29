package com.arkhamusserver.arkhamus.config.netty


import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.netty.NettyProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.net.InetSocketAddress

@Component
@EnableConfigurationProperties(NettyProperties::class)
class NettyConfiguration(val nettyProperties: ArkhamusNettyProperties) {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(NettyConfiguration::class.java)
    }

    @Bean(name = ["serverBootstrap"])
    fun bootstrap(
        initializer: ArkhamusChannelInitializer,
        bossGroup: NioEventLoopGroup,
        workerGroup: NioEventLoopGroup
    ): ServerBootstrap {
        val b = ServerBootstrap()
        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .handler(LoggingHandler(LogLevel.DEBUG))
            .childHandler(initializer)
        b.option(ChannelOption.SO_BACKLOG, nettyProperties.backlog)
        return b
    }


    @Bean(destroyMethod = "shutdownGracefully")
    fun bossGroup(): NioEventLoopGroup {
        return NioEventLoopGroup(nettyProperties.bossCount)
    }

    @Bean(destroyMethod = "shutdownGracefully")
    fun workerGroup(): NioEventLoopGroup {
        return NioEventLoopGroup(nettyProperties.workerCount)
    }

    @Bean
    fun tcpSocketAddress(): InetSocketAddress {
        return InetSocketAddress(nettyProperties.tcpPort)
    }
}
package com.arkhamusserver.arkhamus.config

import NettyServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NettyConfig {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(NettyConfig::class.java)
    }

    @Bean
    fun nettyServer(): NettyServer {
        logger.info("configuring Netty Server ")
        return NettyServer()
    }
}
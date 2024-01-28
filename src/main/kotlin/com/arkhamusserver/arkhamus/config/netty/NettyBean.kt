package com.arkhamusserver.arkhamus.config.netty

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component


@Component
class NettyBean {
    @Autowired
    private lateinit var tcpServer: TcpNettyServer

    @Bean
    fun readyEventApplicationListener(): ApplicationListener<ApplicationReadyEvent> {
        return ApplicationListener { tcpServer.start() }
    }
}
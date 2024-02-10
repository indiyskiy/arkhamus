package com.arkhamusserver.arkhamus.view.dto.netty.request


data class BaseRequestData(
    val tick: Long,
    val userPosition: UserPosition
)

data class UserPosition(
    val x: Double,
    val y: Double
)
package com.arkhamusserver.arkhamus.view.dto.netty.request

data class BaseRequestData(
    val gameId: Long,
    val userId: Long,
    val tick: Long
)
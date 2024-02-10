package com.arkhamusserver.arkhamus.view.dto.netty.response

data class MyGameUserResponseMessage(
    val id: Long,
    val nickName: String,
    val x: Double,
    val y: Double
)
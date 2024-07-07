package com.arkhamusserver.arkhamus.view.dto.admin

data class AdminNettyResourcesInfoDto(
    val size: Int,
    val nettyInfos: List<NettyInfo>
)

data class NettyInfo(
    val channelId: String,
    val gameSessionId: Long?,
    val userId: Long?,
    val userNickname: String,
)
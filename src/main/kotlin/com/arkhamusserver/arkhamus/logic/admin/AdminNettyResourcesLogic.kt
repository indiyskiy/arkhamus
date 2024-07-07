package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.view.dto.admin.AdminNettyResourcesInfoDto
import com.arkhamusserver.arkhamus.view.dto.admin.NettyInfo
import org.springframework.stereotype.Component

@Component
class AdminNettyResourcesLogic(
    private val channelRepository: ChannelRepository
) {
    fun info(): AdminNettyResourcesInfoDto {
        val all = channelRepository.allArkhamusChannels()
        return AdminNettyResourcesInfoDto(
            size = all.size,
            nettyInfos = all.map {
                NettyInfo(
                    channelId = it.channelId,
                    gameSessionId = it.gameSession?.id,
                    userId = it.userAccount?.id,
                    userNickname = it.userAccount?.nickName ?: ""
                )
            }
        )
    }
}
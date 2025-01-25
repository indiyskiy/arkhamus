package com.arkhamusserver.arkhamus.view.dto

import com.arkhamusserver.arkhamus.view.dto.user.UserDto

data class GameInvitationDto(
    val id: Long,
    val sourceUser: UserDto,
    val targetUser: UserDto,
    val shortGameInfo: ShortGameInfoDto
)
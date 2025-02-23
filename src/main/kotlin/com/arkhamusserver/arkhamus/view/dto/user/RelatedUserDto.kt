package com.arkhamusserver.arkhamus.view.dto.user

import com.arkhamusserver.arkhamus.config.CultpritsUserState
import com.arkhamusserver.arkhamus.model.enums.UserRelationType
import com.arkhamusserver.arkhamus.model.enums.steam.SteamPersonaState

data class RelatedUserDto(
    var steamId: String? = null,
    var steamState: SteamPersonaState? = null,
    var steamStateId: Int? = null,
    var nickName: String? = null,
    var userId: Long? = null,
    var cultpritsState: CultpritsUserState? = null,
    var lastActive: Long? = null,
    var relations: List<UserRelationType> = emptyList()
)
package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.InGameUserStatus
import com.arkhamusserver.arkhamus.view.dto.ingame.InGameUserStatusDto
import org.springframework.stereotype.Component

@Component
class InGameUserStatusLogic {
    fun listAllInUserGameStatuses(): List<InGameUserStatusDto> {
        return InGameUserStatus.values().map {
            InGameUserStatusDto(
                name = it.name,
                id = it.id,
                type = it.type,
            )
        }
    }

}
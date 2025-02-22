package com.arkhamusserver.arkhamus.view.maker

import com.arkhamusserver.arkhamus.model.database.entity.user.UserSkinSettings
import com.arkhamusserver.arkhamus.view.dto.UserSkinDto
import org.springframework.stereotype.Component

@Component
class UserSkinDtoMaker {
    fun toDto(from: UserSkinSettings): UserSkinDto {
        return UserSkinDto().apply {
            this.userId = from.userAccount!!.id
            this.skinColor = from.skinColor
        }
    }
}
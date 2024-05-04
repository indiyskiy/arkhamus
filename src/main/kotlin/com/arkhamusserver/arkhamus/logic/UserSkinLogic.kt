package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserSkinRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserSkinSettings
import com.arkhamusserver.arkhamus.model.enums.SkinColor
import com.arkhamusserver.arkhamus.view.dto.UserSkinDto
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrDefault
import kotlin.random.Random

@Component
class UserSkinLogic(
    private val repository: UserSkinRepository,
    private val currentUserService: CurrentUserService,
) {
    private val random = Random(System.currentTimeMillis())

    fun userSkin(): UserSkinDto {
        val player = currentUserService.getCurrentUserAccount()
        return repository.findByUserAccountId(player.id!!)
            .getOrDefault(skin(player))
            .toDto()
    }


    fun updateUserSkin(userSkin: UserSkinDto): UserSkinDto {
        val player = currentUserService.getCurrentUserAccount()
        return repository.findByUserAccountId(player.id!!)
            .getOrDefault(skin(player))
            .mergeSkin(userSkin)
            .save()
            .toDto()
    }

    private fun skin(user: UserAccount): UserSkinSettings {
        return UserSkinSettings().apply {
            skinColor = SkinColor.values().random(random)
            userAccount = user
        }
    }

    private fun UserSkinSettings.mergeSkin(userSkin: UserSkinDto): UserSkinSettings {
        this.skinColor = userSkin.skinColor
        return this
    }

    private fun UserSkinSettings.toDto(): UserSkinDto {
        val skin = this
        return UserSkinDto().apply {
            userId = skin.userAccount?.id
            skinColor = skin.skinColor
        }
    }

    private fun UserSkinSettings.save(): UserSkinSettings =
        repository.save(this)

}







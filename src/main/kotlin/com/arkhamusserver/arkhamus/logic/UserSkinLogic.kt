package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserSkinRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserSkinSettings
import com.arkhamusserver.arkhamus.model.enums.SkinColor
import com.arkhamusserver.arkhamus.view.dto.UserSkinDto
import com.arkhamusserver.arkhamus.view.maker.UserSkinDtoMaker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class UserSkinLogic(
    private val repository: UserSkinRepository,
    private val currentUserService: CurrentUserService,
    private val userSkinDtoMaker: UserSkinDtoMaker,
) {
    companion object {
        private val random = Random(System.currentTimeMillis())
        private val logger = LoggerFactory.getLogger(UserSkinLogic::class.java)
    }

    fun userSkin(): UserSkinDto {
        val player = currentUserService.getCurrentUserAccount()
        return player.userSkinSettings!!.toDto()
    }

    @Transactional
    fun updateUserSkin(userSkin: UserSkinDto): UserSkinDto {
        val player = currentUserService.getCurrentUserAccount()
        logger.info("user ${player.nickName} change skin color to ${userSkin.skinColor}")
        return player.userSkinSettings!!
            .mergeSkin(userSkin)
            .save()
            .toDto()
    }

    private fun UserSkinSettings.mergeSkin(userSkin: UserSkinDto): UserSkinSettings {
        this.skinColor = userSkin.skinColor!!
        return this
    }

    fun allSkinsOf(
        gameSession: GameSession,
    ): Map<Long, UserSkinSettings> {
        return gameSession
            .usersOfGameSession
            .map { it.userAccount }
            .map { user ->
                user.userSkinSettings!!
            }
            .associateBy { it.userAccount!!.id!! }
    }

    fun reshuffleSkins(skins: Collection<UserSkinSettings>): List<UserSkinSettings> {
        skins.forEach { skin ->
            if (skin.skinColor.isInUseMoreThenOnce(skins)) {
                val colorNotInUse = findColorNotInUse(skins, skin)
                skin.skinColor = colorNotInUse
            }
        }
        logger.info("reshuffled skins = ${skins.joinToString(", ") { it.skinColor.name }}")
        return repository.saveAll(skins).toList()
    }

    fun fixColors(session: GameSession, account: UserAccount) {
        val oldSkins = session.usersOfGameSession.map { user ->
            user.userAccount.userSkinSettings!!
        }
        val oldColors = oldSkins.map { it.skinColor }.toSet()
        logger.info("old colors = ${oldColors.joinToString(", ")}")
        val accountSkin = account.userSkinSettings!!
        val accountColor = accountSkin.skinColor
        logger.info("current user color = $accountColor")
        if (accountColor in oldColors) {
            logger.info("change color to random")
            val possibleColors = SkinColor.values().filter { it !in oldColors }
            accountSkin.skinColor = possibleColors.random(random)
            logger.info("change color to ${accountSkin.skinColor}")
            repository.save(accountSkin)
        }
    }

    private fun UserSkinSettings.toDto(): UserSkinDto =
        userSkinDtoMaker.toDto(this)

    private fun UserSkinSettings.save(): UserSkinSettings =
        repository.save(this)

    private fun findColorNotInUse(
        skins: Collection<UserSkinSettings>,
        oldSkin: UserSkinSettings,
    ): SkinColor {
        val allSkinColors = SkinColor.values().toSet()
        val usedSkinColors = skins.map { it.skinColor }.toSet()
        val availableSkinColors = allSkinColors - usedSkinColors
        logger.info("available skin colors = ${availableSkinColors.joinToString(", ")}")
        return availableSkinColors.takeIf { it.isNotEmpty() }?.random(random) ?: oldSkin.skinColor
    }

    private fun SkinColor.isInUseMoreThenOnce(skins: Collection<UserSkinSettings>): Boolean {
        return skins.count { it.skinColor == this } > 1
    }

}







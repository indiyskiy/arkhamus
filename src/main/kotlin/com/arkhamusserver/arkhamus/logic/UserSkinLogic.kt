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
import kotlin.jvm.optionals.getOrDefault
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
        return repository.findByUserAccountId(player.id!!)
            .getOrDefault(skin(player))
            .toDto()
    }

    @Transactional
    fun updateUserSkin(userSkin: UserSkinDto): UserSkinDto {
        val player = currentUserService.getCurrentUserAccount()
        logger.info("user ${player.nickName} change skin color to ${userSkin.skinColor}")
        return repository.findByUserAccountId(player.id!!)
            .getOrDefault(skin(player))
            .mergeSkin(userSkin)
            .save()
            .toDto()
    }

    private fun skin(user: UserAccount): UserSkinSettings {
        return UserSkinSettings(
            skinColor = SkinColor.values().random(random),
            userAccount = user
        )
    }

    private fun UserSkinSettings.mergeSkin(userSkin: UserSkinDto): UserSkinSettings {
        this.skinColor = userSkin.skinColor!!
        return this
    }

    private fun UserSkinSettings.toDto(): UserSkinDto =
        userSkinDtoMaker.toDto(this)

    private fun UserSkinSettings.save(): UserSkinSettings =
        repository.save(this)

    fun allSkinsOf(
        gameSession: GameSession,
        currentUserSkin: UserSkinSettings? = null,
    ): Map<Long, UserSkinSettings> {
        return gameSession
            .usersOfGameSession
            .map { it.userAccount }
            .map {
                if (currentUserSkin != null && currentUserSkin.userAccount?.id == it.id) {
                    currentUserSkin
                } else {
                    repository.findByUserAccountId(it.id!!).getOrDefault(skin(it))
                }
            }
            .associateBy { it.userAccount!!.id!! }
    }

    fun reshuffleSkins(skins: Collection<UserSkinSettings>): List<UserSkinSettings> {
        skins.forEach { skin ->
            if (skin.skinColor.isInUseMoreThenOnce(skins)) {
                val colorNotInUse = findColorNotInUse(skins)
                skin.skinColor = colorNotInUse
            }
        }
        logger.info("reshuffled skins = ${skins.joinToString(", ") { it.skinColor.name }}")
        return repository.saveAll(skins).toList()
    }

    private fun findColorNotInUse(skins: Collection<UserSkinSettings>): SkinColor {
        return SkinColor.values().first { it.isInUse(skins) }
    }

    private fun SkinColor.isInUseMoreThenOnce(skins: Collection<UserSkinSettings>): Boolean {
        return skins.count { it.skinColor == this } > 1
    }

    private fun SkinColor.isInUse(skins: Collection<UserSkinSettings>): Boolean {
        return skins.any { it.skinColor == this }
    }

    fun fixColors(session: GameSession, account: UserAccount) {
        val oldColors = session.usersOfGameSession.map { user ->
            repository.findByUserAccountId(user.userAccount.id!!)
                .getOrDefault(skin(user.userAccount)).skinColor
        }.toSet()
        logger.info("old colors = ${oldColors.joinToString(", ")}")
        val accountSkin = repository.findByUserAccountId(account.id!!).getOrDefault(skin(account))
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
}







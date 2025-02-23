package com.arkhamusserver.arkhamus.logic.user.relations

import com.arkhamusserver.arkhamus.logic.cache.OtherUserRelationCache
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserRelationRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.user.UserRelation
import com.arkhamusserver.arkhamus.model.enums.UserRelationType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType.*
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameEndRelationLogic(
    private val userRelationRepository: UserRelationRepository,
    private val otherUserRelationCache: OtherUserRelationCache
) {
    @Transactional
    fun saveGameEndedRelations(session: GameSession) {
        val type: UserRelationType? = getType(session)
        if (type == null) return
        val users = session.usersOfGameSession.map { it.userAccount }
        val userIds = users.map { it.id!! }.toSet()
        val relations = users.associate {
            it.id!! to userRelations(it, type, userIds)
        }
       val updatedRelations = users.map { user ->
            users.mapNotNull { otherUser ->
                if (user.id != otherUser.id) {
                    val existRelation = relations[user.id]?.firstOrNull{
                        it.targetUser?.id == otherUser.id
                    }
                    if(existRelation == null) {
                        createRelation(user, otherUser, type)
                    } else {
                         existRelation
                    }
                } else null
            }
        }.flatten()
        userRelationRepository.saveAll(updatedRelations)
        userIds.forEach{
            otherUserRelationCache.cleanCacheForUser(it)
        }
    }


    private fun createRelation(
        sourceAccount: UserAccount,
        targetAccount: UserAccount,
        type: UserRelationType
    ): UserRelation = UserRelation().apply {
        this.sourceUser = sourceAccount
        this.targetUser = targetAccount
        this.targetSteamId = targetAccount.steamId
        this.userRelationType = type
    }

    private fun userRelations(
        account: UserAccount,
        type: UserRelationType,
        userIds: Set<Long>
    ): List<UserRelation> {
        return userRelationRepository.findBySourceUserAndUserRelationType(account, type).filter {
            it.targetUser != null && it.targetUser?.id in userIds
        }
    }

    private fun getType(session: GameSession): UserRelationType? {
        return when (session.gameType) {
            DEFAULT -> UserRelationType.HAD_LADDER_GAME
            CUSTOM -> UserRelationType.HAD_CUSTOM_GAME
            SINGLE -> null
        }
    }
}
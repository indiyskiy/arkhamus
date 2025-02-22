package com.arkhamusserver.arkhamus.logic.user.relations

import com.arkhamusserver.arkhamus.logic.cache.*
import com.arkhamusserver.arkhamus.logic.user.CurrentUserService
import com.arkhamusserver.arkhamus.model.UserStateHolder
import com.arkhamusserver.arkhamus.model.dataaccess.UserStatusService
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.enums.UserRelationType
import com.arkhamusserver.arkhamus.model.enums.steam.SteamPersonaState
import com.arkhamusserver.arkhamus.view.dto.user.SteamUserShortDto
import org.springframework.stereotype.Component

@Component
class UserRelationLogic(
    private val steamUserRelationCache: SteamUserRelationCache,
    private val otherUserRelationCache: OtherUserRelationCache,
    private val currentUserService: CurrentUserService,
    private val userRepository: UserAccountRepository,
    private val userStatusService: UserStatusService,
    private val steamUserDataCache: SteamUserDataCache
) {
    fun readFriendList(steamIds: String): List<SteamUserShortDto> {
        val steamIdsList = steamIds.split(",").map { it.trim() }
        val currentUser = currentUserService.getCurrentUserAccount()

        val steamRelations = steamUserRelationCache.getCachedRelationsForUser(currentUser.id!!, steamIdsList)
        val otherRelations = otherUserRelationCache.getCachedRelationsForUser(currentUser.id!!)
        val allRelations = steamRelations + otherRelations

        val targetUsers = allRelations.mapNotNull { it.targetUserId }.let {
            userRepository.findByIdIn(it).associateBy { it.id }
        }
        val steamUsers = mapSteamUsers(allRelations)
        val states = mapUserStates(targetUsers)
        val trimRelations = allRelations.filterByUserId().filterBySteamId()
        return trimRelations.map {
            val steamUser = steamUsers[it.steamId]
            val user = targetUsers[it.targetUserId]
            val state = user?.let { states[user.id] }
            val relationTypes = allRelations.collectRelations(it.targetUserId, it.steamId)
            toDto(user, steamUser, state, relationTypes)
        }
    }

    fun List<CachedUserRelation>.filterByUserId(): List<CachedUserRelation> {
        val (withId, withoutId) = this.partition { it.targetUserId != null }
        val uniqueById = withId.groupBy { it.targetUserId }.map { it.value.first() }
        return uniqueById + withoutId
    }

    fun List<CachedUserRelation>.filterBySteamId(): List<CachedUserRelation> {
        val (withId, withoutId) = this.partition { it.steamId != null }
        val uniqueById = withId.groupBy { it.steamId }.map { it.value.first() }
        return uniqueById + withoutId
    }

    private fun List<CachedUserRelation>.collectRelations(userId: Long?, steamId: String?): List<UserRelationType> =
        this.filter {
            (it.targetUserId != null && it.targetUserId == userId) ||
                    (it.steamId != null && it.steamId == steamId)
        }.map { it.userRelationType }.distinct().sortedBy { it.ordinal }

    private fun mapUserStates(targetUsers: Map<Long?, UserAccount>): Map<Long, UserStateHolder> =
        targetUsers.mapNotNull {
            it.value.id
        }.map {
            userStatusService.getUserStatus(it)
        }.associateBy {
            it.userId
        }

    private fun mapSteamUsers(allRelations: List<CachedUserRelation>): Map<String, CachedSteamData> =
        allRelations.mapNotNull { it.steamId }
            .distinct()
            .mapNotNull {
                steamUserDataCache.getCachedSteamData(it)
            }.associateBy { it.steamId }


    private fun toDto(
        userAccount: UserAccount?,
        steamPlayer: CachedSteamData?,
        state: UserStateHolder?,
        userRelationTypes: List<UserRelationType>
    ) = SteamUserShortDto().apply {
            val steamState = steamPlayer?.steamPersonaState ?: SteamPersonaState.PERSONA_STATE_OFFLINE
            this.steamId = steamPlayer?.steamId
            this.steamState = steamState
            this.steamStateId = steamState.id
            this.nickName = userAccount?.nickName ?: steamPlayer?.name
            this.userId = userAccount?.id
            this.cultpritsState = state?.userState
            this.lastActive = state?.lastActive
            this.relations = userRelationTypes
        }
}

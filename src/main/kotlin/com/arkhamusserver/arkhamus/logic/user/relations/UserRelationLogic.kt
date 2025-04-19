package com.arkhamusserver.arkhamus.logic.user.relations

import com.arkhamusserver.arkhamus.logic.cache.*
import com.arkhamusserver.arkhamus.logic.user.CurrentUserService
import com.arkhamusserver.arkhamus.model.UserStateHolder
import com.arkhamusserver.arkhamus.model.dataaccess.UserStatusService
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserRelationRepository
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.user.UserRelation
import com.arkhamusserver.arkhamus.model.enums.UserRelationType
import com.arkhamusserver.arkhamus.model.enums.steam.SteamPersonaState
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.user.RelatedUserDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserRelationLogic(
    private val steamUserRelationCache: SteamUserRelationCache,
    private val otherUserRelationCache: OtherUserRelationCache,
    private val currentUserService: CurrentUserService,
    private val userRepository: UserAccountRepository,
    private val userStatusService: UserStatusService,
    private val steamUserDataCache: SteamUserDataCache,
    private val userRelationRepository: UserRelationRepository
) {

    companion object {
        private val logger = LoggingUtils.getLogger<UserRelationLogic>()
    }

    @Transactional
    fun makeFriend(newFriendId: Long): RelatedUserDto {
        val currentUser = currentUserService.getCurrentUserAccount()
        val targetUser =
            userRepository.findById(newFriendId).orElseThrow { IllegalStateException("user not found: $newFriendId") }
        val relations =
            userRelationRepository.findBySourceUserAndTargetUser(currentUser, targetUser)
        val result = relations.firstOrNull {
            it.userRelationType == UserRelationType.STEAM_FRIEND
        } ?: createFriendRelation(
            currentUser, targetUser
        )
        return toDto(
            userAccount = result.targetUser,
            steamPlayer = result.targetSteamId?.let { steamUserDataCache.getCachedSteamData(it) },
            state = result.targetUser?.let { userStatusService.getUserStatus(it.id!!) },
            userRelationTypes = relations.mapNotNull { it.userRelationType }
        )
    }

    private fun createFriendRelation(
        sourceUser: UserAccount, targetUser: UserAccount
    ): UserRelation {
        val relation = UserRelation().apply {
            this.sourceUser = sourceUser
            this.targetUser = targetUser
            this.userRelationType = UserRelationType.STEAM_FRIEND
            this.targetSteamId = targetUser.steamId
        }
        return userRelationRepository.save(relation)
    }

    fun readFriendList(steamIds: String): List<RelatedUserDto> {
        val steamIdsList = steamIds.split(",").map { it.trim() }
        val currentUser = currentUserService.getCurrentUserAccount()
        val steamRelations = steamUserRelationCache.getCachedRelationsForUser(currentUser.id!!, steamIdsList)
        val otherRelations = otherUserRelationCache.getCachedRelationsForUser(currentUser.id!!)
        val allRelations = steamRelations + otherRelations
        return processRelations(allRelations)
    }

    fun readFriendListForAdmin(userId: Long): List<RelatedUserDto> {
        val steamRelations = steamUserRelationCache.getCachedRelationsForUser(userId)
        val otherRelations = otherUserRelationCache.getCachedRelationsForUser(userId)
        val allRelations = steamRelations + otherRelations
        return processRelations(allRelations)
    }

    private fun processRelations(allRelations: List<CachedUserRelation>): List<RelatedUserDto> {
        logger.info("process relations: ${allRelations.size}")
        val targetUsers = allRelations.mapNotNull { it.targetUserId }.let {
            userRepository.findByIdIn(it).associateBy { it.id }
        }
        logger.info("read target users: ${targetUsers.size}")
        val steamUsers = mapSteamUsers(allRelations)
        logger.info("read steam users: ${steamUsers.size}")
        val states = mapUserStates(targetUsers)
        val trimRelations = allRelations.filterByUserId().filterBySteamId()
        logger.info("trim relations: ${trimRelations.size}")
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
            (it.targetUserId != null && it.targetUserId == userId) || (it.steamId != null && it.steamId == steamId)
        }.let {
            logger.info("relation types: ${it.joinToString { "${it.userRelationType}" }}")
            it
        }.map { it.userRelationType }

    private fun mapUserStates(targetUsers: Map<Long?, UserAccount>): Map<Long, UserStateHolder> =
        targetUsers.mapNotNull {
            it.value.id
        }.map {
            userStatusService.getUserStatus(it)
        }.associateBy {
            it.userId
        }

    private fun mapSteamUsers(allRelations: List<CachedUserRelation>): Map<String, CachedSteamData> =
        allRelations.mapNotNull { it.steamId }.distinct().mapNotNull {
            steamUserDataCache.getCachedSteamData(it)
        }.associateBy { it.steamId }


    private fun toDto(
        userAccount: UserAccount?,
        steamPlayer: CachedSteamData?,
        state: UserStateHolder?,
        userRelationTypes: List<UserRelationType>
    ) = RelatedUserDto().apply {
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

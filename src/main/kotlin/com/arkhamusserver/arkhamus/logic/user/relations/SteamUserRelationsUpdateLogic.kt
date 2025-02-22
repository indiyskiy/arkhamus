package com.arkhamusserver.arkhamus.logic.user.relations

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserRelationRepository
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.user.UserRelation
import com.arkhamusserver.arkhamus.model.enums.UserRelationType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SteamUserRelationsUpdateLogic(
    private val userAccountRepository: UserAccountRepository,
    private val userRelationRepository: UserRelationRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(SteamUserRelationsUpdateLogic::class.java)
    }

    @Transactional
    fun updateSteamUser(userId: Long, steamIds: List<String>): List<UserRelation> {
        logger.info("Updating Steam user relations for userId: $userId with steamIds: $steamIds")

        // 1. Fetch current user and relations
        val currentUser = fetchUser(userId)
        val currentRelations = fetchUserRelations(currentUser)
        val currentSteamIds = currentRelations.mapNotNull { it.targetSteamId }

        // 2. Handle new Steam IDs: Create new relations
        val newSteamIds = findNewSteamIds(steamIds, currentSteamIds)
        val newRelations = createNewRelations(currentUser, newSteamIds)

        // 3. Attempt to link UserAccounts to all relations (current + new)
        val allRelations = currentRelations + newRelations
        val linkedRelations = linkUserAccountToRelations(allRelations)
        val updatedRelationsSteamIds = linkedRelations.mapNotNull { it.targetSteamId }
        val createdNotLinkedRelations = newRelations.filter { it.targetSteamId !in updatedRelationsSteamIds }
        val merged = linkedRelations + createdNotLinkedRelations
        // 4. Save all modified relations in a single call
        saveUpdatedRelations(merged)

        // 5. Return updated list of distinct relations
        return merged
    }

    /**
     * Fetches the user from the database based on the provided ID.
     */
    private fun fetchUser(userId: Long): UserAccount {
        return userAccountRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User with id $userId not found") }
    }

    /**
     * Fetches all current relations of the user for STEAM relationship type.
     */
    private fun fetchUserRelations(user: UserAccount): List<UserRelation> {
        return userRelationRepository.findBySourceUserAndUserRelationType(user, UserRelationType.STEAM_FRIEND)
    }

    /**
     * Identifies new Steam IDs that are not already in the current relations.
     */
    private fun findNewSteamIds(steamIds: List<String>, currentSteamIds: List<String>): List<String> {
        return steamIds.filterNot { currentSteamIds.contains(it) }
    }

    /**
     * Creates new UserRelation entries for given Steam IDs.
     */
    private fun createNewRelations(user: UserAccount, newSteamIds: List<String>): List<UserRelation> {
        return newSteamIds.map { steamId ->
            logger.info("Creating new UserRelation for steamId $steamId")
            UserRelation(
                sourceUser = user,
                targetSteamId = steamId,
                userRelationType = UserRelationType.STEAM_FRIEND
            )
        }
    }

    /**
     * Links UserAccount to all relations (both existing and new) based on the Steam ID.
     * Fetches all UserAccounts in bulk and updates relations where possible.
     */
    private fun linkUserAccountToRelations(relations: List<UserRelation>): List<UserRelation> {
        // Collect all distinct targetSteamIds that need linking (where targetUser is null)
        val steamIdsToLink = relations
            .filter { it.targetUser == null }
            .mapNotNull { it.targetSteamId }
            .distinct()

        if (steamIdsToLink.isEmpty()) return emptyList()

        // Fetch all matching UserAccounts and map them by steamId
        val matchedUsersBySteamId = userAccountRepository.findBySteamIdIn(steamIdsToLink)
            .associateBy { it.steamId } // Create a map for quick lookup

        return relations.mapNotNull { relation ->
            relation.targetSteamId?.let { steamId ->
                val matchingUser = matchedUsersBySteamId[steamId]
                if (matchingUser != null && relation.targetUser == null) {
                    logger.info("Linking UserRelation with steamId $steamId to UserAccount with id ${matchingUser.id}")
                    relation.targetUser = matchingUser
                    relation
                } else {
                    null // Skip relations that don’t require updates
                }
            }
        }
    }


    /**
     * Saves all modified relations in a single saveAll call to improve performance.
     */
    private fun saveUpdatedRelations(updatedRelations: List<UserRelation>) {
        if (updatedRelations.isNotEmpty()) {
            userRelationRepository.saveAll(updatedRelations)
            logger.info("Saved ${updatedRelations.size} updated/new UserRelations to database")
        }
    }

}
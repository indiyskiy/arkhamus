package com.arkhamusserver.arkhamus.logic.user.relations

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserRelationRepository
import com.arkhamusserver.arkhamus.model.database.entity.user.UserRelation
import com.arkhamusserver.arkhamus.model.enums.UserRelationType.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OtherUserRelationUpdateLogic(
    private val userRelationRepository: UserRelationRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(OtherUserRelationUpdateLogic::class.java)
        private val RELATION_TYPE_SET =
            setOf(HAD_CUSTOM_GAME, HAD_LADDER_GAME, CULTPRITS_FRIEND)
    }

    fun updateRelations(userId: Long): List<UserRelation> {
        val relations = userRelationRepository.findBySourceUserIdAndUserRelationTypes(userId, RELATION_TYPE_SET)
        logger.info("got ${relations.size} relations for user $userId")
        updateOutdatedRelations(relations)
        return relations
    }

    private fun updateOutdatedRelations(relations: List<UserRelation>) {
        updateSteamlessRelations(relations)
    }

    private fun updateSteamlessRelations(relations: List<UserRelation>) {
        val steamless = relations.filter {
            val targetUser = it.targetUser
            it.targetSteamId == null &&
                    targetUser != null &&
                    targetUser.steamId != null
        }
        steamless.forEach { relation ->
            relation.targetSteamId = relation.targetUser!!.steamId
        }
        userRelationRepository.saveAll(steamless)
        logger.info("updated steam ID for ${steamless.size} relations")
    }

}
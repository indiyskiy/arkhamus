package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.user.UserRelation
import com.arkhamusserver.arkhamus.model.enums.UserRelationType
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*


interface UserRelationRepository : CrudRepository<UserRelation, Long> {

    fun findBySourceUserId(id: Long): List<UserRelation>

    fun findBySourceUser(sourceUser: UserAccount): List<UserRelation>

    fun findByTargetUserId(id: Long): List<UserRelation>

    fun findByTargetUser(targetUser: UserAccount): List<UserRelation>

    // Find all relationships where the targetSteamId matches a given Steam ID
    fun findByTargetSteamId(targetSteamId: String): List<UserRelation>

    // Find all relationships of a given RelationType for a source user
    fun findBySourceUserAndUserRelationType(sourceUser: UserAccount, userRelationType: UserRelationType): List<UserRelation>

    fun findBySourceUserIdAndUserRelationType(id: Long, userRelationType: UserRelationType): List<UserRelation>

    // Find all relationships between two users
    fun findBySourceUserAndTargetUser(
        sourceUser: UserAccount,
        targetUser: UserAccount
    ): Optional<UserRelation>

    @Query("SELECT ur FROM UserRelation ur WHERE ur.sourceUser = :sourceUser AND ur.userRelationType IN :userRelationType")
    fun findBySourceUserAndUserRelationTypes(
        @Param("sourceUser") sourceUser: UserAccount,
        @Param("userRelationType") userRelationTypes: Set<UserRelationType>
    ): List<UserRelation>

    @Query("SELECT ur FROM UserRelation ur WHERE ur.sourceUser.id = :id AND ur.userRelationType IN :userRelationTypes")
    fun findBySourceUserIdAndUserRelationTypes(
        @Param("id") id: Long,
        @Param("userRelationTypes") userRelationTypes: Set<UserRelationType>
    ): List<UserRelation>

}

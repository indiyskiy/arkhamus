package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.user.UserRelation
import com.arkhamusserver.arkhamus.model.enums.UserRelationType
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


interface UserRelationRepository : CrudRepository<UserRelation, Long> {

    // Find all relationships of a given RelationType for a source user
    fun findBySourceUserAndUserRelationType(sourceUser: UserAccount, userRelationType: UserRelationType): List<UserRelation>

    // Find all relationships between two users
    fun findBySourceUserAndTargetUser(
        sourceUser: UserAccount,
        targetUser: UserAccount
    ): List<UserRelation>

    @Query("SELECT ur FROM UserRelation ur WHERE ur.sourceUser.id = :id AND ur.userRelationType IN :userRelationTypes")
    fun findBySourceUserIdAndUserRelationTypes(
        @Param("id") id: Long,
        @Param("userRelationTypes") userRelationTypes: Set<UserRelationType>
    ): List<UserRelation>

}

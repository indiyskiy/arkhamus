package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.GameInvitation
import com.arkhamusserver.arkhamus.model.enums.InvitationState
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GameInvitationRepository : CrudRepository<GameInvitation, Long> {
    override fun findById(id: Long): Optional<GameInvitation>

    fun findByGameSessionIdAndState(
        id: Long,
        state: InvitationState
    ): List<GameInvitation>

    fun findByTargetUserAccountIdAndState(
        id: Long,
        state: InvitationState
    ): List<GameInvitation>

    fun findBySourceUserAccountIdAndState(
        id: Long,
        state: InvitationState
    ): List<GameInvitation>
}
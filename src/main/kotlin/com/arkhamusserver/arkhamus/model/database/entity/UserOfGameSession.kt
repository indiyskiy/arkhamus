package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import jakarta.persistence.*

@Entity
data class UserOfGameSession(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "userAccountId", nullable = false)
    var userAccount: UserAccount,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameSessionId", referencedColumnName = "id", nullable = false)
    var gameSession: GameSession,

    var host: Boolean,

    var roleInGame: RoleTypeInGame? = null
)
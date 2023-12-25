package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.ingame.RoleInGame
import jakarta.persistence.*
import java.sql.Timestamp

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

    var gameCreationTimestamp: Timestamp? = null,

    var roleInGame: RoleInGame? = null
)
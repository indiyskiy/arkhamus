package com.arkhamusserver.arkhamus.model.database.entity

import jakarta.persistence.*

@Entity
data class UserOfGameSession(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "userAccountId", nullable = false)
    var userAccount: UserAccount? = null,

    @ManyToOne
    @JoinColumn(name = "gameSessionId", nullable = false)
    var gameSession: GameSession? = null,

    var host: Boolean? = null
)
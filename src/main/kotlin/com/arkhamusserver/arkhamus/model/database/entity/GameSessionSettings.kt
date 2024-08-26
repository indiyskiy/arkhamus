package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import jakarta.persistence.*

/**
 * mutable part of game that user can actually change
 */
@Entity
data class GameSessionSettings(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var lobbySize: Int,
    var numberOfCultists: Int,
    var maxCallToArms: Int = 1,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = true)
    var level: Level? = null
)

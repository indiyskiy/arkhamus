package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
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
){
    // No-arg constructor for JPA
    constructor() : this(
        id = null,
        lobbySize = 8,
        numberOfCultists = 2,
        maxCallToArms = 1,
        level = null
    )
}

package com.arkhamusserver.arkhamus.model.database.entity

import jakarta.persistence.*

/**
 * mutable part of game that user can actually change
 */
@Entity
data class GameSessionSettings(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

// TODO we never use this, remove from postgres?
//    @OneToOne(
//        mappedBy = "gameSessionSettings",
//        cascade = [CascadeType.ALL],
//        fetch = FetchType.LAZY,
//        optional = false
//    )
//    private val gameSession: GameSession? = null,
    var lobbySize: Int,
    var numberOfCultists: Int,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = true)
    var level: Level? = null
)

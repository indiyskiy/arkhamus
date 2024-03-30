package com.arkhamusserver.arkhamus.model.database.entity

import jakarta.persistence.*

@Entity
data class Lantern(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long? = null,
    var lightRange: Double? = null,
    var x: Double? = null,
    var y: Double? = null,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null
)
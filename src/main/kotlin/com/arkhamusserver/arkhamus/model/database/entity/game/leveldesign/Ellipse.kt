package com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign

import jakarta.persistence.*

@Entity
data class Ellipse(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "levelZoneId", referencedColumnName = "id", nullable = false)
    var levelZone: LevelZone,
    var x: Double,
    var y: Double,
    var z: Double,
    var height: Double,
    var width: Double,
)
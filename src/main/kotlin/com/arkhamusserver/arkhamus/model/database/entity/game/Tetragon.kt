package com.arkhamusserver.arkhamus.model.database.entity.game

import jakarta.persistence.*

@Entity
data class Tetragon(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "levelZoneId", referencedColumnName = "id", nullable = false)
    var levelZone: LevelZone,
    var point0X: Double, var point0Y: Double, var point0Z: Double,
    var point1X: Double, var point1Y: Double, var point1Z: Double,
    var point2X: Double, var point2Y: Double, var point2Z: Double,
    var point3X: Double, var point3Y: Double, var point3Z: Double,
)
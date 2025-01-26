package com.arkhamusserver.arkhamus.model.database.entity.game

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import jakarta.persistence.*

@Entity
data class VisibilityDoor(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long?,
    var x1: Double,
    var y1: Double,
    var z1: Double,
    var x2: Double,
    var y2: Double,
    var z2: Double,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level
)
package com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign

import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import jakarta.persistence.*

@Entity
data class Lantern(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    var lightRange: Double? = null,
    var interactionRadius: Double? = null,
    var x: Double,
    var y: Double,
    var z: Double,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level
) : WithPoint {
    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }
}
package com.arkhamusserver.arkhamus.model.database.entity.game

import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import jakarta.persistence.*

@Entity
data class RitualArea(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    var radius: Double,
    var x: Double,
    var y: Double,
    var z: Double,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level
): WithPoint {
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
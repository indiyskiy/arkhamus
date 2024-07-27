package com.arkhamusserver.arkhamus.model.database.entity.game

import com.arkhamusserver.arkhamus.model.redis.WithPoint
import jakarta.persistence.*
import org.postgresql.geometric.PGpoint

@Entity
data class QuestGiver(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    var point: PGpoint,
    var interactionRadius: Double,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null,
    var name: String
) : WithPoint {
    override fun x(): Double {
        return point.x
    }

    override fun y(): Double {
        return point.y
    }
}
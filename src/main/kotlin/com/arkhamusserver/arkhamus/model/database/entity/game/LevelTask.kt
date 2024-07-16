package com.arkhamusserver.arkhamus.model.database.entity.game

import jakarta.persistence.*
import org.postgresql.geometric.PGpoint

@Entity
data class LevelTask(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null,
    var name: String,
    var point: PGpoint,
    var interactionRadius: Double,
)
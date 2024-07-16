package com.arkhamusserver.arkhamus.model.database.entity.game

import jakarta.persistence.*
import org.postgresql.geometric.PGpoint

@Entity
data class RitualArea(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    var radius: Double,
    var point: PGpoint,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level
)
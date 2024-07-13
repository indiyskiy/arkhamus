package com.arkhamusserver.arkhamus.model.database.entity

import jakarta.persistence.*
import org.postgresql.geometric.PGpoint

data class QuestGiver (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    var point: PGpoint,
    var interactionRadius: Double,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null
)
package com.arkhamusserver.arkhamus.model.database.entity.game

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.postgresql.geometric.PGpoint

@Entity
data class Door (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var point: PGpoint,
    var inGameId: Long,
    var zoneId: Long,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level
)
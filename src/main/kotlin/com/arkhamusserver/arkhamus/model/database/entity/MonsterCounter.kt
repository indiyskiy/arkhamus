package com.arkhamusserver.arkhamus.model.database.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.sql.Timestamp

@Entity
data class MonsterCounter(
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    var id: Long? = null,
    val gameId: Long,
    val lastUpdateTimeStump: Timestamp,
    val limeToEnd: Long,
    val isOver: Boolean = false
)
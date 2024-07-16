package com.arkhamusserver.arkhamus.model.database.entity.game

import com.arkhamusserver.arkhamus.model.enums.LevelState
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp

@Entity
data class Level(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @CreationTimestamp
    var creationTimestamp: Timestamp? = null,

    var version: Long,
    var levelId: Long,
    var levelHeight: Long,
    var levelWidth: Long,
    var state: LevelState
)
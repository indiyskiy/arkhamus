package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.LevelState
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp

@Entity
data class Level(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @CreationTimestamp
    var creationTimestamp: Timestamp? = null,

    var version: Long? = null,
    var levelId: Long? = null,
    var levelHeight: Long? = null,
    var levelWidth: Long? = null,
    var state: LevelState? = null
)
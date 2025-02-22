package com.arkhamusserver.arkhamus.model.database.entity.user

import com.arkhamusserver.arkhamus.model.enums.UserRelationType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp


@Entity
data class UserRelation(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinColumn(name = "sourceUserId", referencedColumnName = "id", nullable = false)
    var sourceUser: UserAccount? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinColumn(name = "targetUserId", referencedColumnName = "id", nullable = true)
    var targetUser: UserAccount? = null, // Nullable target user

    @Column(nullable = true)
    var targetSteamId: String? = null, // Nullable target Steam ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var userRelationType: UserRelationType? = null,

    @CreationTimestamp
    @Column(updatable = false)
    var creationTimestamp: Timestamp? = null,

    @UpdateTimestamp
    var lastUpdateTimestamp: Timestamp? = null
){
    constructor() : this(
        id = null,
        sourceUser = null,
        targetUser = null,
        targetSteamId = null,
        userRelationType = null,
        creationTimestamp = null,
        lastUpdateTimestamp = null
    )

}
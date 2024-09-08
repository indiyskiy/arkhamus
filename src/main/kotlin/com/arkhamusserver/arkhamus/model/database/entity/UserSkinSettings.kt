package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.SkinColor
import jakarta.persistence.*

@Entity
data class UserSkinSettings(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @Enumerated(EnumType.STRING)
    var skinColor: SkinColor,
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userAccountId")
    var userAccount: UserAccount? = null
)
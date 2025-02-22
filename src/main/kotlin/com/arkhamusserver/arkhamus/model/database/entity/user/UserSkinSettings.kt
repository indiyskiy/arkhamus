package com.arkhamusserver.arkhamus.model.database.entity.user

import com.arkhamusserver.arkhamus.model.enums.SkinColor
import jakarta.persistence.*

@Entity
data class UserSkinSettings(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @Enumerated(EnumType.STRING)
    var skinColor: SkinColor = SkinColor.SKY,
    @OneToOne
    @JoinColumn(
        name = "user_account_id",
        nullable = false
    )
    var userAccount: UserAccount? = null

) {
    constructor() : this(null, SkinColor.SKY, null)
}
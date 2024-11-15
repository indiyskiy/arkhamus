package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.TextKeyType
import jakarta.persistence.*

@Entity
data class TextKey(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var value: String? = null,
    @Enumerated(EnumType.STRING)
    var type: TextKeyType,
)
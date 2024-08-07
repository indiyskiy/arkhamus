package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.TextKeyType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class TextKey(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var value: String? = null,
    var type: TextKeyType,
)
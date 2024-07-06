package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.model.enums.LevelState

data class AdminGameLevelInfoDto (
    val levelId: Long,
    val latestVersion: Long,
    val state: LevelState,
    val levelHeight: Long,
    val levelWidth: Long,
)
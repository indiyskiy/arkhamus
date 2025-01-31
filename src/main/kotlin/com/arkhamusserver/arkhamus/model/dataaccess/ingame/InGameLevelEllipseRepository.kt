package com.arkhamusserver.arkhamus.model.dataaccess.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZoneEllipse
import org.springframework.stereotype.Repository

@Repository
class InGameLevelEllipseRepository : RamCrudRepository<InGameLevelZoneEllipse>()
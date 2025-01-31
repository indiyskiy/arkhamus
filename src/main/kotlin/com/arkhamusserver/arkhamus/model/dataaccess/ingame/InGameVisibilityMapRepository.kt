package com.arkhamusserver.arkhamus.model.dataaccess.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameVisibilityMap
import org.springframework.stereotype.Repository

@Repository
class InGameVisibilityMapRepository: RamCrudRepository<InGameVisibilityMap>()
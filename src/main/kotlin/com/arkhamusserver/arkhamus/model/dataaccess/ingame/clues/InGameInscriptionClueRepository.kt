package com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameInscriptionClue
import org.springframework.stereotype.Repository

@Repository
class InGameInscriptionClueRepository : RamCrudRepository<InGameInscriptionClue>()
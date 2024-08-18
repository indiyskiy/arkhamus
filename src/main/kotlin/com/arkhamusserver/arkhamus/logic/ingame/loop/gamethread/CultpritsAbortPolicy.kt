package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import org.slf4j.LoggerFactory
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor

class CultpritsAbortPolicy : RejectedExecutionHandler {

    companion object {
        private val logger = LoggerFactory.getLogger(CultpritsAbortPolicy::class.java)
    }

    private val abortPolicy = ThreadPoolExecutor.AbortPolicy()

    override fun rejectedExecution(r: Runnable, executor: ThreadPoolExecutor) {
        logger.warn("Task $r rejected from $executor")
        abortPolicy.rejectedExecution(r, executor)
    }
}
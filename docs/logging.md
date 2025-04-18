# Structured Logging in Arkhamus

This document describes the structured logging approach used in the Arkhamus application.

## Overview

Structured logging is a logging approach that treats log events as structured data rather than plain text. This makes logs easier to search, filter, and analyze. In Arkhamus, we use JSON-formatted logs with consistent fields and context information.

## Configuration

The logging configuration is defined in `src/main/resources/logback.xml`. This configuration:

- Uses the LogstashEncoder to format logs as JSON
- Outputs logs to both console and file
- Rotates log files daily with a 30-day retention period
- Sets appropriate log levels for different packages

## Logging Utility

The `LoggingUtils` class (`src/main/kotlin/com/arkhamusserver/arkhamus/util/logging/LoggingUtils.kt`) provides utility methods for structured logging:

- Methods to set context values in MDC (Mapped Diagnostic Context)
- Standardized logging methods for different log levels
- Event type constants for categorizing log events
- Context management utilities

## How to Use

### Basic Logging

Instead of using the logger directly:

```kotlin
logger.info("User logged in: $userId")
```

Use the LoggingUtils class:

```kotlin
LoggingUtils.info(
    logger,
    LoggingUtils.EVENT_USER_ACTION,
    "User logged in: {}",
    userId
)
```

### Adding Context

To add context to logs:

```kotlin
LoggingUtils.withContext(
    userId = user.id.toString(),
    sessionId = session.id,
    gameId = game.id.toString()
) {
    LoggingUtils.info(
        logger,
        LoggingUtils.EVENT_GAME_START,
        "Game started with {} players",
        players.size
    )
}
```

### Error Logging

For error logging with exceptions:

```kotlin
try {
    // Some code that might throw an exception
} catch (e: Exception) {
    LoggingUtils.error(
        logger,
        LoggingUtils.EVENT_ERROR,
        "Failed to process request: {}",
        e.message,
        e
    )
}
```

## Event Types

The following event types are defined:

- `EVENT_GAME_START`: Events related to starting a game
- `EVENT_GAME_END`: Events related to ending a game
- `EVENT_USER_ACTION`: Events triggered by user actions
- `EVENT_SYSTEM`: General system events
- `EVENT_ERROR`: Error events
- `EVENT_SECURITY`: Security-related events
- `EVENT_PERFORMANCE`: Performance-related events

## Context Fields

The following context fields are used:

- `SESSION_ID`: For tracking user sessions
- `USER_ID`: For identifying users
- `GAME_ID`: For identifying game instances
- `REQUEST_ID`: For tracking individual requests

## Best Practices

1. **Use Structured Format**: Always use the LoggingUtils methods instead of direct logger calls.
2. **Add Context**: Include relevant context information in logs.
3. **Use Appropriate Log Levels**:
   - ERROR: For errors that affect functionality
   - WARN: For potential issues that don't affect functionality
   - INFO: For important events
   - DEBUG: For detailed information useful during development
   - TRACE: For very detailed information
4. **Use Event Types**: Categorize logs with appropriate event types.
5. **Include Relevant Data**: Include all relevant data in logs, but be careful not to log sensitive information.
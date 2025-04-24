package com.arkhamusserver.arkhamus

/**
 * Test-specific implementation of getBuildType function.
 * This overrides the implementation in the main Application.kt file
 * to provide a consistent "test" build type for all tests.
 *
 * @return Always returns "test" for testing purposes
 */
fun getBuildType(): String {
    return "test"
}
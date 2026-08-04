package com.ghz.calculenity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MathEngine {
    init {
        // Loads the C++ library defined in CMakeLists.txt
        System.loadLibrary("calculenity_engine")
    }

    /**
     * External JNI function declaration.
     */
    private external fun calculateGiantPower(base: String, exponent: String): String

    /**
     * Suspend function to run the heavy JNI call on the IO thread.
     * This ensures the frosted glass UI remains perfectly smooth.
     */
    suspend fun power(base: String, exponent: String): String {
        return withContext(Dispatchers.IO) {
            calculateGiantPower(base, exponent)
        }
    }
}

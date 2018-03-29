package com.slelyuk.android.gqlviewer.util

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlin.coroutines.experimental.CoroutineContext

const val THREAD_COUNT = 3

/**
 * Global executor pools for the whole application.
 *
 * Grouping repositories like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
open class AppExecutors constructor(
        val ioContext: CoroutineContext = DefaultDispatcher,
        val networkContext: CoroutineContext = newFixedThreadPoolContext(THREAD_COUNT, "networkIO"),
        val uiContext: CoroutineContext = UI)
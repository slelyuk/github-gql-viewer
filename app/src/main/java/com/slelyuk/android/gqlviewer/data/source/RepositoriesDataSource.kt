package com.slelyuk.android.gqlviewer.data.source

import com.slelyuk.android.gqlviewer.fragment.RepositoryItem

/**
 * Main entry point for accessing repositories data.
 */
interface RepositoriesDataSource {

  suspend fun getRepositories(): Result<List<RepositoryItem>>

  suspend fun refreshRepositories()
}
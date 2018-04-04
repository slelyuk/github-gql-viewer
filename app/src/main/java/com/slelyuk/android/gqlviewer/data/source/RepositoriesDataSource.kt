package com.slelyuk.android.gqlviewer.data.source

import com.slelyuk.android.gqlviewer.data.Repo

/**
 * Main entry point for accessing repositories data.
 */
interface RepositoriesDataSource {

  suspend fun getRepositories(): Result<List<Repo>>

  suspend fun refreshRepositories()
}
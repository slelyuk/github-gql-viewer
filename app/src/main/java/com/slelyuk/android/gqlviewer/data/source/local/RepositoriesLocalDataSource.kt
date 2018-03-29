package com.slelyuk.android.gqlviewer.data.source.local

import com.slelyuk.android.gqlviewer.data.source.LocalDataNotFoundException
import com.slelyuk.android.gqlviewer.data.source.RepositoriesDataSource
import com.slelyuk.android.gqlviewer.data.source.Result
import com.slelyuk.android.gqlviewer.fragment.RepositoryItem
import com.slelyuk.android.gqlviewer.util.AppExecutors
import kotlinx.coroutines.experimental.withContext

/**
 * Implementation of a local data source (database).
 */
class RepositoriesLocalDataSource private constructor(
    private val appExecutors: AppExecutors
) : RepositoriesDataSource {

  override suspend fun getRepositories(): Result<List<RepositoryItem>> = withContext(
      appExecutors.ioContext) {
    // TODO
    return@withContext Result.Error(LocalDataNotFoundException())
  }

  override suspend fun refreshRepositories() {
    // TODO
  }

  companion object {
    private var INSTANCE: RepositoriesLocalDataSource? = null

    @JvmStatic
    fun getInstance(appExecutors: AppExecutors): RepositoriesLocalDataSource {
      if (INSTANCE == null) {
        synchronized(RepositoriesLocalDataSource::javaClass) {
          INSTANCE = RepositoriesLocalDataSource(appExecutors)
        }
      }
      return INSTANCE!!
    }

    @Suppress("unused")
    fun clearInstance() {
      INSTANCE = null
    }
  }
}

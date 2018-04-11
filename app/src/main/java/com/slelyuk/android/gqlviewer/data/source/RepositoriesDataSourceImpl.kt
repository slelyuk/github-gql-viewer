package com.slelyuk.android.gqlviewer.data.source

import com.slelyuk.android.gqlviewer.data.Repo
import java.util.ArrayList
import java.util.LinkedHashMap

/**
 * Concrete implementation to load repositories from the data sources into a cache.
 */
class RepositoriesDataSourceImpl(
    private val reposRemoteDataSource: RepositoriesDataSource,
    private val reposLocalDataSource: RepositoriesDataSource
) : RepositoriesDataSource {

  /**
   * This variable has public visibility so it can be accessed from tests.
   */
  private var cachedRepos: LinkedHashMap<String, Repo> = LinkedHashMap()

  /**
   * Marks the cache as invalid, to force an update the next time data is requested. This variable
   * has package local visibility so it can be accessed from tests.
   */
  private var cacheIsDirty = false

  /**
   * Gets repositories from cache, local data source (SQLite) or remote data source, whichever is
   * available first.
   */
  override suspend fun getRepositories(): Result<List<Repo>> {
    // Respond immediately with cache if available and not dirty
    if (cachedRepos.isNotEmpty() && !cacheIsDirty) {
      return Result.Success(cachedRepos.values.toList())
    }

    return if (cacheIsDirty) {
      // If the cache is dirty we need to fetch new data from the network.
      getTasksFromRemoteDataSource()
    } else {
      // Query the local storage if available. If not, query the network.
      val result = reposLocalDataSource.getRepositories()
      when (result) {
        is Result.Success -> {
          refreshCache(result.data)
          Result.Success(cachedRepos.values.toList())
        }
        is Result.Error -> getTasksFromRemoteDataSource()
      }
    }
  }

  override suspend fun refreshRepositories() {
    cacheIsDirty = true
  }

  private suspend fun getTasksFromRemoteDataSource(): Result<List<Repo>> {
    val result = reposRemoteDataSource.getRepositories()
    return when (result) {
      is Result.Success -> {
        refreshCache(result.data)
        Result.Success(ArrayList(cachedRepos.values))
      }
      is Result.Error -> Result.Error(RemoteDataNotFoundException())
    }

  }

  private fun refreshCache(repos: List<Repo>) {
    cachedRepos.clear()
    repos.forEach {
      cache(it)
    }
    cacheIsDirty = false
  }

  private fun cache(repo: Repo): Repo {
    cachedRepos[repo.name] = repo
    return repo
  }

  companion object {

    private var INSTANCE: RepositoriesDataSourceImpl? = null

    /**
     * Returns the single instance of this class, creating it if necessary.

     * @param reposRemoteDataSource the backend data source
     * *
     * @param reposLocalDataSource  the device storage data source
     * *
     * @return the [RepositoriesDataSourceImpl] instance
     */
    @JvmStatic
    fun getInstance(reposRemoteDataSource: RepositoriesDataSource,
        reposLocalDataSource: RepositoriesDataSource): RepositoriesDataSourceImpl {
      return INSTANCE ?: RepositoriesDataSourceImpl(reposRemoteDataSource, reposLocalDataSource)
          .apply { INSTANCE = this }
    }

    /**
     * Used to force [getInstance] to create a new instance
     * next time it's called.
     */
    @JvmStatic
    fun destroyInstance() {
      INSTANCE = null
    }
  }
}
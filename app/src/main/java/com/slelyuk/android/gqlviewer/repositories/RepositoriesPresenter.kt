package com.slelyuk.android.gqlviewer.repositories

import com.slelyuk.android.gqlviewer.data.Repo
import com.slelyuk.android.gqlviewer.data.source.RepositoriesDataSourceImpl
import com.slelyuk.android.gqlviewer.data.source.Result
import com.slelyuk.android.gqlviewer.util.launchSilent
import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Listens to user actions from the UI ([RepositoriesFragment]), retrieves the data and updates the
 * UI as required.
 */
class RepositoriesPresenter(private val dataSource: RepositoriesDataSourceImpl,
    private val repositoriesView: RepositoriesContract.View,
    private val uiContext: CoroutineContext = UI)
  : RepositoriesContract.Presenter {

  private var firstLoad = true

  init {
    repositoriesView.presenter = this
  }

  override fun start() {
    loadRepositories(false)
  }

  override fun result(requestCode: Int, resultCode: Int) {

  }

  override fun loadRepositories(forceUpdate: Boolean) {
    // Simplification for sample: a network reload will be forced on first load.
    loadRepositories(forceUpdate || firstLoad, true)
    firstLoad = false
  }

  /**
   * @param forceUpdate   true to refresh the cache data
   * *
   * @param showLoadingUI true to display a loading
   */
  private fun loadRepositories(forceUpdate: Boolean, showLoadingUI: Boolean) = launchSilent(
      uiContext) {
    if (showLoadingUI) {
      repositoriesView.setLoadingIndicator(true)
    }
    if (forceUpdate) {
      dataSource.refreshRepositories()
    }

    val result = dataSource.getRepositories()
    if (result is Result.Success) {
      // The view may not be able to handle UI updates anymore
      if (repositoriesView.isActive) {
        if (showLoadingUI) {
          repositoriesView.setLoadingIndicator(false)
        }

        processRepositories(result.data)
      }

    } else {
      // The view may not be able to handle UI updates anymore
      if (repositoriesView.isActive) {
        repositoriesView.showLoadingRepositoriesError()
      }
    }
  }

  private fun processRepositories(repos: List<Repo>) {
    if (repos.isEmpty()) {
      // Show a message indicating there are no repositories
      processEmptyRepositories()
    } else {
      // Show the list of repositories
      repositoriesView.showRepositories(repos)
    }
  }

  private fun processEmptyRepositories() {
    repositoriesView.showNoRepositories()
  }

  override fun openRepositoryDetails(repo: Repo) {
    // TODO
  }
}

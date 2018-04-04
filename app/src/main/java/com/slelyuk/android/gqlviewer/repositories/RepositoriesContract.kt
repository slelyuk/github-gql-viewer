package com.slelyuk.android.gqlviewer.repositories

import com.slelyuk.android.gqlviewer.BasePresenter
import com.slelyuk.android.gqlviewer.BaseView
import com.slelyuk.android.gqlviewer.data.Repo

/**
 * This specifies the contract between the view and the presenter.
 */
interface RepositoriesContract {

  interface View : BaseView<Presenter> {

    var isActive: Boolean

    fun setLoadingIndicator(active: Boolean)

    fun showRepositories(repos: List<Repo>)

    fun showLoadingRepositoriesError()

    fun showNoRepositories()
  }

  interface Presenter : BasePresenter {

    fun result(requestCode: Int, resultCode: Int)

    fun loadRepositories(forceUpdate: Boolean)

    fun openRepositoryDetails(repo: Repo)
  }
}

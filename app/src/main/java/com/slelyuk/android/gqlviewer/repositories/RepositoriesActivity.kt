package com.slelyuk.android.gqlviewer.repositories

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.slelyuk.android.gqlviewer.R
import com.slelyuk.android.gqlviewer.data.source.RepositoriesDataSourceImpl
import com.slelyuk.android.gqlviewer.data.source.local.RepositoriesLocalDataSource
import com.slelyuk.android.gqlviewer.data.source.remote.RepositoriesRemoteDataSource
import com.slelyuk.android.gqlviewer.util.AppExecutors
import com.slelyuk.android.gqlviewer.util.replaceFragmentInActivity
import com.slelyuk.android.gqlviewer.util.setupActionBar

class RepositoriesActivity : AppCompatActivity() {

  private lateinit var reposPresenter: RepositoriesPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.repos_act)

    // Set up the toolbar.
    setupActionBar(R.id.toolbar)

    val reposFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
        as RepositoriesFragment? ?: RepositoriesFragment.newInstance().also {
      replaceFragmentInActivity(it, R.id.contentFrame)
    }

    // Create the presenter
    reposPresenter = RepositoriesPresenter(RepositoriesDataSourceImpl(RepositoriesRemoteDataSource.getInstance(),
        RepositoriesLocalDataSource.getInstance(AppExecutors())), reposFragment)
  }
}

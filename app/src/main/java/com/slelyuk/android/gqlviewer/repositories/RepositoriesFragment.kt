package com.slelyuk.android.gqlviewer.repositories

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.VERTICAL
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.slelyuk.android.gqlviewer.R
import com.slelyuk.android.gqlviewer.R.string
import com.slelyuk.android.gqlviewer.data.Repo
import com.slelyuk.android.gqlviewer.fragment.RepositoryItem
import com.slelyuk.android.gqlviewer.repositories.RepositoriesFragment.RepositoriesAdapter.RepoViewHolder
import com.slelyuk.android.gqlviewer.util.showSnackBar
import java.util.ArrayList

/**
 * Display a list of [RepositoryItem]s.
 */
class RepositoriesFragment : Fragment(), RepositoriesContract.View {

  override lateinit var presenter: RepositoriesContract.Presenter

  override var isActive: Boolean = false
    get() = isAdded

  private lateinit var noRepositoriesView: View
  private lateinit var repositoriesListView: RecyclerView
  private lateinit var refreshLayout: SwipeRefreshLayout

  /**
   * Listener for clicks on repositories in the ListView.
   */
  private var itemListener: RepositoryItemListener = object : RepositoryItemListener {
    override fun onRepositoryClick(repo: Repo) {
      presenter.openRepositoryDetails(repo)
    }
  }

  private val listAdapter = RepositoriesAdapter(ArrayList(), itemListener)

  override fun onResume() {
    super.onResume()
    presenter.start()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    presenter.result(requestCode, resultCode)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.repos_frag, container, false)

    // Set up repositories view
    with(root) {
      noRepositoriesView = findViewById(R.id.noRepos)

      repositoriesListView = findViewById(R.id.reposList)
      repositoriesListView.adapter = listAdapter
      repositoriesListView.addItemDecoration(DividerItemDecoration(context, VERTICAL))

      refreshLayout = findViewById(R.id.refresh_layout)
      refreshLayout.isEnabled = false
    }

    return root
  }

  override fun setLoadingIndicator(active: Boolean) {
    with(refreshLayout) { isRefreshing = active }
  }

  override fun showRepositories(repos: List<Repo>) {
    listAdapter.repositories = repos

    repositoriesListView.visibility = View.VISIBLE
    noRepositoriesView.visibility = View.GONE
  }

  override fun showNoRepositories() {
    showNoTasksViews()
  }

  private fun showNoTasksViews() {
    repositoriesListView.visibility = View.GONE
    noRepositoriesView.visibility = View.VISIBLE
  }

  override fun showLoadingRepositoriesError() {
    showMessage(getString(string.message_network_error))
  }

  private fun showMessage(message: String) {
    view?.showSnackBar(message, Snackbar.LENGTH_LONG)
  }

  class RepositoriesAdapter(repositories: List<Repo>,
      private val itemListener: RepositoryItemListener)
    : Adapter<RepoViewHolder>() {

    var repositories: List<Repo> = repositories
      set(repos) {
        field = repos
        notifyDataSetChanged()
      }

    override fun getItemCount() = repositories.size

    private fun getItem(i: Int) = repositories[i]

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RepoViewHolder {
      return RepoViewHolder(
          LayoutInflater.from(parent?.context).inflate(R.layout.repo_item, parent, false))
    }

    override fun onBindViewHolder(holder: RepoViewHolder?, position: Int) {
      val repo = getItem(position)
      holder?.title?.text = repo.name
      holder?.description?.text = repo.description
      holder?.stars?.text = "${repo.starsCount}"
      holder?.forks?.text = "${repo.forksCount}"

      holder?.itemView?.setOnClickListener { itemListener.onRepositoryClick(repo) }
    }

    class RepoViewHolder(view: View?) : ViewHolder(view) {
      val title = view?.findViewById<TextView>(R.id.title)
      val description = view?.findViewById<TextView>(R.id.description)
      val stars = view?.findViewById<TextView>(R.id.stars)
      val forks = view?.findViewById<TextView>(R.id.forks)
    }
  }

  interface RepositoryItemListener {
    fun onRepositoryClick(repo: Repo)
  }

  companion object {
    fun newInstance() = RepositoriesFragment()
  }

}

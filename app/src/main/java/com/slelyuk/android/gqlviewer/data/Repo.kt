package com.slelyuk.android.gqlviewer.data

import com.slelyuk.android.gqlviewer.PublicRepositoriesQuery

data class Repo(
    val name: String,
    val description: String?,
    val forksCount: Long?,
    val starsCount: Long?)

fun PublicRepositoriesQuery.Data.toRepos(): List<Repo> {
  val edges = this.search().edges()
  return edges?.map { it.node()?.fragments()?.repositoryItem() }.orEmpty()
      .map {
        Repo(it?.nameWithOwner().orEmpty(),
            it?.description(),
            it?.forkCount(),
            it?.stargazers()?.totalCount())
      }
}
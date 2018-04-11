package com.slelyuk.android.gqlviewer.data.source.remote

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.CustomTypeAdapter
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.slelyuk.android.gqlviewer.PublicRepositoriesQuery
import com.slelyuk.android.gqlviewer.PublicRepositoriesQuery.Data
import com.slelyuk.android.gqlviewer.data.Repo
import com.slelyuk.android.gqlviewer.data.source.RepositoriesDataSource
import com.slelyuk.android.gqlviewer.data.source.Result
import com.slelyuk.android.gqlviewer.data.toRepos
import com.slelyuk.android.gqlviewer.type.CustomType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import java.net.URI
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine


/**
 * Implementation of the data source that gets data from network.
 */
class RepositoriesRemoteDataSource private constructor() : RepositoriesDataSource {

  private val apolloClient: ApolloClient = setupApollo()

  override suspend fun getRepositories(): Result<List<Repo>> {
    lateinit var result: Continuation<Result<List<Repo>>>

    apolloClient.query(
        PublicRepositoriesQuery.builder()
            .build()
    ).enqueue(object : ApolloCall.Callback<PublicRepositoriesQuery.Data>() {
      override fun onResponse(response: Response<Data>) {
        val repos = response.data()!!.toRepos()
        result.resume(Result.Success(repos))
      }

      override fun onFailure(e: ApolloException) {
        e.printStackTrace()
        result.resume(Result.Error(e))
      }
    })

    return suspendCoroutine { continuation -> result = continuation }
  }

  override suspend fun refreshRepositories() {
    // Not required because the {@link RepositoriesDataSource} handles the logic of refreshing the
    // repos from all the available data sources.
  }

  private fun setupApollo(): ApolloClient {
    val dateFormat: SimpleDateFormat = SimpleDateFormat.getDateTimeInstance() as SimpleDateFormat
    dateFormat.applyPattern("yyyy-MM-dd'T'HH:mm:ss");

    val dateTypeAdapter = object : CustomTypeAdapter<Date> {
      override fun decode(value: String): Date {
        try {
          return dateFormat.parse(value)
        } catch (e: ParseException) {
          throw RuntimeException(e)
        }

      }

      override fun encode(value: Date): String {
        return dateFormat.format(value)
      }
    }

    val uriTypeAdapter = object : CustomTypeAdapter<URI> {
      override fun decode(value: String): URI {
        return URI.create(value)
      }

      override fun encode(value: URI): String {
        return value.toASCIIString()
      }
    }

    val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor { chain ->
          chain.proceed(chain.request().newBuilder().addHeader(
              "Authorization",
              "bearer c81ae8a0063377586a8c44d3787ec4ff689fd9a6")
              .build())
        }
        .addInterceptor(HttpLoggingInterceptor().also { it.level = BODY })
        .build()

    return ApolloClient.builder()
        .serverUrl("https://api.github.com/graphql")
        .okHttpClient(okHttpClient)
        .addCustomTypeAdapter(CustomType.DATETIME, dateTypeAdapter)
        .addCustomTypeAdapter(CustomType.URI, uriTypeAdapter)
        .subscriptionTransportFactory(
            WebSocketSubscriptionTransport.Factory(
                "wss://api.github.com/subscriptions", okHttpClient))
        .build()
  }

  companion object {

    private lateinit var INSTANCE: RepositoriesRemoteDataSource
    private var needsNewInstance = true

    @JvmStatic
    fun getInstance(): RepositoriesRemoteDataSource {
      if (needsNewInstance) {
        INSTANCE = RepositoriesRemoteDataSource()
        needsNewInstance = false
      }
      return INSTANCE
    }
  }
}
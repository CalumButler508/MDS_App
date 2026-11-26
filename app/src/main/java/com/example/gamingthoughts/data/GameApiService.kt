package com.example.gamingthoughts.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// RAWG response (only the fields we use)
data class RawgResponse(val results: List<RawgGame>)
data class RawgGame(
    val name: String,
    val released: String?,                       // "2015-05-19"
    @com.google.gson.annotations.SerializedName("background_image") val backgroundImage: String?
)

interface GameApiService {
    @GET("games")
    suspend fun search(
        @Query("search") query: String,
        @Query("page_size") pageSize: Int = 5,
        @Query("key") apiKey: String
    ): RawgResponse

    companion object {
        val instance: GameApiService by lazy {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "GamingThoughtsApp/1.0")
                        .build()
                    chain.proceed(request)
                }
                .build()

            Retrofit.Builder()
                .baseUrl("https://api.rawg.io/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GameApiService::class.java)
        }
    }
}

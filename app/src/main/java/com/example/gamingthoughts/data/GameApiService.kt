package com.example.gamingthoughts.data

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
            Retrofit.Builder()
                .baseUrl("https://api.rawg.io/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GameApiService::class.java)
        }
    }
}

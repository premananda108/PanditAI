package ua.pp.soulrise.panditai.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import ua.pp.soulrise.panditai.data.config.ApiConfig

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("dew_ai/api")
    suspend fun sendMessage(
        @Body request: ApiRequest,
        @Query("key") apiKey: String = ApiConfig.API_KEY
    ): Response<ApiResponse>
}
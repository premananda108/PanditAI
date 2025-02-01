package ua.pp.soulrise.panditai.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("https://dewiar.com/dew_ai/api?key=Dwr_b916735cb47cc2203a695fd8c9b6e043164de92501915f397cdc7fd7824c0d58")
    suspend fun sendMessage(@Body request: ApiRequest): Response<ApiResponse>
}
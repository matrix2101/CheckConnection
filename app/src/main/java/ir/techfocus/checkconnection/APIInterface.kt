package ir.techfocus.checkconnection

import retrofit2.Call
import retrofit2.http.GET

interface APIInterface {

    @GET("ebills/GetBills")
    suspend fun getBills(): Call<List<Predefined>>
}
package ir.techfocus.checkconnection

import retrofit2.http.GET

interface APIInterface {
    @GET("matrix210/CheckConnection/refs/heads/main/Predefined.json")
    suspend fun getPredefined(): Items
}
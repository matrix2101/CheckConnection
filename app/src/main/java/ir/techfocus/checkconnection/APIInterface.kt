package ir.techfocus.checkconnection

import retrofit2.http.GET

interface APIInterface {
    @GET("matrix2101/CheckConnection/refs/heads/main/predefined.json")
    suspend fun getPredefined(): Items?
}
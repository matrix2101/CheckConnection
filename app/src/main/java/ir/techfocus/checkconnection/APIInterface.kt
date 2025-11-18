package ir.techfocus.checkconnection

import retrofit2.http.GET
import retrofit2.http.Url

interface APIInterface {
    @GET("matrix2101/CheckConnection/refs/heads/main/predefined.json")
    suspend fun getPredefined(): Items?

    @GET()
    suspend fun getIPInfo(@Url url: String): IPInfoLite?
}
package ir.techfocus.checkconnection

import com.google.gson.annotations.SerializedName

data class IPAddress(
    @SerializedName("ipAddress")
    val ipAddress: String,

    @SerializedName("port")
    val port: Int
)
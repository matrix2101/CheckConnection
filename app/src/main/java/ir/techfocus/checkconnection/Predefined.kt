package ir.techfocus.checkconnection

import com.google.gson.annotations.SerializedName


data class Predefined(
    @SerializedName("name")
    val name: String,

    @SerializedName("ipAddresses")
    val ipAddresses: List<String>
)

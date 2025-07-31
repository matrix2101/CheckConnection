package ir.techfocus.checkconnection

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("name")
    var name: String,

    @SerializedName("ipAddresses")
    var ipAddresses: List<IPAddress>
)

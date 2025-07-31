package ir.techfocus.checkconnection

import com.google.gson.annotations.SerializedName

data class Items(
    @SerializedName("items")
    var items: List<Item>
)
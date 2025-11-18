package ir.techfocus.checkconnection

import com.google.gson.annotations.SerializedName

data class IPInfoLite(
    @SerializedName("ip")
    val ip: String,

    @SerializedName("asn")
    val asn: String,

    @SerializedName("as_name")
    val asName: String,

    @SerializedName("as_domain")
    val asDomain: String,

    @SerializedName("country_code")
    val countryCode: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("continent_code")
    val continentCode: String,

    @SerializedName("continent")
    val continent: String
)